package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.constants.ParametroTipo;
import com.cibertec.ms_credits.dto.request.PrestamoRequest;
import com.cibertec.ms_credits.dto.response.PrestamoDetalleResponse;
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
import com.cibertec.ms_credits.service.ParametroService;
import com.cibertec.ms_credits.service.PrestamoService;
import com.cibertec.ms_credits.util.CalculadoraPrestamoUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    private final PrestamoRepository prestamoRepository;
    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final CuotaRepository cuotaRepository;
    private final ClienteRepository clienteRepository;
    private final PrestamoMapper prestamoMapper;
    private final CotizacionMapper cotizacionMapper;
    private final ParametroService parametroService;

    public PrestamoServiceImpl(PrestamoRepository prestamoRepository,
                                CotizacionRepository cotizacionRepository,
                                DetalleCotizacionRepository detalleCotizacionRepository,
                                CuotaRepository cuotaRepository,
                                ClienteRepository clienteRepository,
                                PrestamoMapper prestamoMapper,
                                CotizacionMapper cotizacionMapper,
                                ParametroService parametroService) {
        this.prestamoRepository = prestamoRepository;
        this.cotizacionRepository = cotizacionRepository;
        this.detalleCotizacionRepository = detalleCotizacionRepository;
        this.cuotaRepository = cuotaRepository;
        this.clienteRepository = clienteRepository;
        this.prestamoMapper = prestamoMapper;
        this.cotizacionMapper = cotizacionMapper;
        this.parametroService = parametroService;
    }

    @Override
    @Transactional
    public PrestamoResponse registrar(PrestamoRequest request) {
        Cliente cliente = clienteRepository.findById(request.clienteId())
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.CLIENTE_NO_ENCONTRADO));

        CalculadoraPrestamoUtil.ResultadoCalculo resultado =
                CalculadoraPrestamoUtil.calcular(request.monto(), request.tasaInteres(), request.plazoMeses());

        Parametro estadoPendiente = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.PENDIENTE);

        Cotizacion cotizacion = cotizacionRepository.save(
                cotizacionMapper.toEntity(cliente, request, resultado, estadoPendiente));
        detalleCotizacionRepository.saveAll(cotizacionMapper.toDetalleEntities(cotizacion, resultado.cronograma()));

        Prestamo prestamo = prestamoRepository.save(prestamoMapper.toEntity(cliente, cotizacion, estadoPendiente));

        return prestamoMapper.toResponse(prestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PrestamoResponse> listar() {
        return prestamoRepository.findAll().stream()
                .map(prestamoMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public PrestamoDetalleResponse obtenerDetalle(Integer prestamoId) {
        Prestamo prestamo = obtenerPrestamoOrThrow(prestamoId);
        List<DetalleCotizacion> detalles = detalleCotizacionRepository
                .findByCotizacionCotizacionIdOrderByNumeroCuotaAsc(prestamo.getCotizacion().getCotizacionId());

        return prestamoMapper.toDetalleResponse(prestamo, detalles);
    }

    @Override
    @Transactional
    public PrestamoResponse aprobar(Integer prestamoId) {
        Prestamo prestamo = obtenerPrestamoOrThrow(prestamoId);

        if (!EstadoCodigo.PENDIENTE.equals(prestamo.getEstado().getCodigo())) {
            throw new BusinessException(ApiMessages.PRESTAMO_NO_PENDIENTE);
        }

        Parametro estadoAprobado = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.APROBADO);
        prestamo.setEstado(estadoAprobado);
        prestamo.setFechaAprobacion(LocalDateTime.now());
        prestamoRepository.save(prestamo);

        List<DetalleCotizacion> detalles = detalleCotizacionRepository
                .findByCotizacionCotizacionIdOrderByNumeroCuotaAsc(prestamo.getCotizacion().getCotizacionId());

        Parametro estadoPendiente = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.PENDIENTE);
        cuotaRepository.saveAll(prestamoMapper.toCuotas(prestamo, detalles, estadoPendiente));

        return prestamoMapper.toResponse(prestamo);
    }

    private Prestamo obtenerPrestamoOrThrow(Integer prestamoId) {
        return prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.PRESTAMO_NO_ENCONTRADO));
    }
}
