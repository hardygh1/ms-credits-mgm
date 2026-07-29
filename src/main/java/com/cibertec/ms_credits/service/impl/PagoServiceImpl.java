package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.constants.ParametroTipo;
import com.cibertec.ms_credits.dto.request.PagoRequest;
import com.cibertec.ms_credits.dto.response.PagoResponse;
import com.cibertec.ms_credits.entity.Cuota;
import com.cibertec.ms_credits.entity.Pago;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.entity.Prestamo;
import com.cibertec.ms_credits.exception.BusinessException;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.PagoMapper;
import com.cibertec.ms_credits.repository.CuotaRepository;
import com.cibertec.ms_credits.repository.PagoRepository;
import com.cibertec.ms_credits.repository.PrestamoRepository;
import com.cibertec.ms_credits.service.PagoService;
import com.cibertec.ms_credits.service.ParametroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PagoServiceImpl implements PagoService {

    private final PrestamoRepository prestamoRepository;
    private final CuotaRepository cuotaRepository;
    private final PagoRepository pagoRepository;
    private final PagoMapper pagoMapper;
    private final ParametroService parametroService;

    public PagoServiceImpl(PrestamoRepository prestamoRepository,
                            CuotaRepository cuotaRepository,
                            PagoRepository pagoRepository,
                            PagoMapper pagoMapper,
                            ParametroService parametroService) {
        this.prestamoRepository = prestamoRepository;
        this.cuotaRepository = cuotaRepository;
        this.pagoRepository = pagoRepository;
        this.pagoMapper = pagoMapper;
        this.parametroService = parametroService;
    }

    @Override
    @Transactional
    public PagoResponse registrarPago(Integer prestamoId, PagoRequest request) {
        Prestamo prestamo = prestamoRepository.findById(prestamoId)
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.PRESTAMO_NO_ENCONTRADO));

        if (!EstadoCodigo.APROBADO.equals(prestamo.getEstado().getCodigo())) {
            throw new BusinessException(ApiMessages.PRESTAMO_NO_APROBADO);
        }

        Cuota cuota = cuotaRepository
                .findFirstByPrestamoPrestamoIdAndEstadoCodigoOrderByNumeroCuotaAsc(prestamoId, EstadoCodigo.PENDIENTE)
                .orElseThrow(() -> new BusinessException(ApiMessages.PRESTAMO_SIN_CUOTAS_PENDIENTES));

        if (request.montoPagado().compareTo(cuota.getMonto()) != 0) {
            throw new BusinessException(ApiMessages.MONTO_PAGADO_NO_COINCIDE);
        }

        Pago pago = pagoRepository.save(pagoMapper.toEntity(cuota, request));

        Parametro estadoPagado = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.PAGADO);
        cuota.setEstado(estadoPagado);
        cuotaRepository.save(cuota);

        return pagoMapper.toResponse(pago);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PagoResponse> listarPorPrestamo(Integer prestamoId) {
        if (!prestamoRepository.existsById(prestamoId)) {
            throw new ResourceNotFoundException(ApiMessages.PRESTAMO_NO_ENCONTRADO);
        }

        return pagoRepository.findByCuotaPrestamoPrestamoIdOrderByFechaPagoAsc(prestamoId).stream()
                .map(pagoMapper::toResponse)
                .toList();
    }
}
