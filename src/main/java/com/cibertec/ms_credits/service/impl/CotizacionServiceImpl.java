package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.constants.ParametroTipo;
import com.cibertec.ms_credits.dto.request.CotizacionRequest;
import com.cibertec.ms_credits.dto.response.CotizacionDetalleResponse;
import com.cibertec.ms_credits.dto.response.CotizacionResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;
import com.cibertec.ms_credits.entity.Cliente;
import com.cibertec.ms_credits.entity.Cotizacion;
import com.cibertec.ms_credits.entity.DetalleCotizacion;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.entity.Prestamo;
import com.cibertec.ms_credits.exception.BusinessException;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.CotizacionMapper;
import com.cibertec.ms_credits.mapper.PrestamoMapper;
import com.cibertec.ms_credits.repository.ClienteRepository;
import com.cibertec.ms_credits.repository.CotizacionRepository;
import com.cibertec.ms_credits.repository.CuotaRepository;
import com.cibertec.ms_credits.repository.DetalleCotizacionRepository;
import com.cibertec.ms_credits.repository.PrestamoRepository;
import com.cibertec.ms_credits.service.CotizacionService;
import com.cibertec.ms_credits.service.ParametroService;
import com.cibertec.ms_credits.util.CalculadoraPrestamoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final ClienteRepository clienteRepository;
    private final PrestamoRepository prestamoRepository;
    private final CuotaRepository cuotaRepository;
    private final CotizacionMapper cotizacionMapper;
    private final PrestamoMapper prestamoMapper;
    private final ParametroService parametroService;

    public CotizacionServiceImpl(CotizacionRepository cotizacionRepository,
                                  DetalleCotizacionRepository detalleCotizacionRepository,
                                  ClienteRepository clienteRepository,
                                  PrestamoRepository prestamoRepository,
                                  CuotaRepository cuotaRepository,
                                  CotizacionMapper cotizacionMapper,
                                  PrestamoMapper prestamoMapper,
                                  ParametroService parametroService) {
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.clienteRepository = clienteRepository;
        this.prestamoRepository = prestamoRepository;
        this.cuotaRepository = cuotaRepository;
        this.cotizacionMapper = cotizacionMapper;
        this.prestamoMapper = prestamoMapper;
        this.parametroService = parametroService;
    }

    @Override
    @Transactional
    public CotizacionResponse registrar(CotizacionRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.CLIENTE_NO_ENCONTRADO));

        CalculadoraPrestamoUtil.ResultadoCalculo resultado =
                CalculadoraPrestamoUtil.calcular(request.monto(), request.tasaInteres(), request.plazoMeses());

        Parametro estadoPendiente = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.PENDIENTE);

        Cotizacion cotizacion = cotizacionRepository.save(
                cotizacionMapper.toEntity(cliente, request, resultado, estadoPendiente));
        detalleCotizacionRepository.saveAll(cotizacionMapper.toDetalleEntities(cotizacion, resultado.cronograma()));

        return cotizacionMapper.toResponse(cotizacion);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CotizacionResponse> listar() {
        return cotizacionRepository.findAll().stream()
                .map(cotizacionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CotizacionDetalleResponse obtenerDetalle(Integer cotizacionId) {
        Cotizacion cotizacion = obtenerCotizacionOrThrow(cotizacionId);
        List<DetalleCotizacion> detalles = detalleCotizacionRepository
                .findByCotizacionCotizacionIdOrderByNumeroCuotaAsc(cotizacion.getCotizacionId());

        return cotizacionMapper.toDetalleResponse(cotizacion, detalles);
    }

    @Override
    @Transactional
    public PrestamoResponse aprobar(Integer cotizacionId) {
        Cotizacion cotizacion = obtenerCotizacionOrThrow(cotizacionId);
        validarAprobable(cotizacion);

        Parametro estadoAprobado = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.APROBADO);
        cotizacion.setEstado(estadoAprobado);
        cotizacionRepository.save(cotizacion);

        Prestamo prestamo = prestamoMapper.toEntity(cotizacion.getCliente(), cotizacion, estadoAprobado);
        prestamo.setFechaAprobacion(LocalDateTime.now());
        prestamo = prestamoRepository.save(prestamo);

        List<DetalleCotizacion> detalles = detalleCotizacionRepository
                .findByCotizacionCotizacionIdOrderByNumeroCuotaAsc(cotizacion.getCotizacionId());

        Parametro estadoCuotaPendiente = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.PENDIENTE);
        cuotaRepository.saveAll(prestamoMapper.toCuotas(prestamo, detalles, estadoCuotaPendiente));

        return prestamoMapper.toResponse(prestamo);
    }

    @Override
    @Transactional
    public CotizacionResponse eliminar(Integer cotizacionId) {
        Cotizacion cotizacion = obtenerCotizacionOrThrow(cotizacionId);
        String codigoActual = cotizacion.getEstado().getCodigo();

        if (EstadoCodigo.APROBADO.equals(codigoActual)) {
            throw new BusinessException(ApiMessages.COTIZACION_APROBADA_NO_ELIMINAR);
        }
        if (EstadoCodigo.ANULADO.equals(codigoActual)) {
            throw new BusinessException(ApiMessages.COTIZACION_YA_ELIMINADA);
        }

        Parametro estadoAnulado = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.ANULADO);
        cotizacion.setEstado(estadoAnulado);

        return cotizacionMapper.toResponse(cotizacionRepository.save(cotizacion));
    }

    private void validarAprobable(Cotizacion cotizacion) {
        String codigoActual = cotizacion.getEstado().getCodigo();
        if (EstadoCodigo.PENDIENTE.equals(codigoActual)) {
            return;
        }
        if (EstadoCodigo.APROBADO.equals(codigoActual)) {
            throw new BusinessException(ApiMessages.COTIZACION_YA_APROBADA);
        }
        throw new BusinessException(ApiMessages.COTIZACION_ELIMINADA_NO_APROBAR);
    }

    private Cotizacion obtenerCotizacionOrThrow(Integer cotizacionId) {
        return cotizacionRepository.findById(cotizacionId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.COTIZACION_NO_ENCONTRADA));
    }
}
