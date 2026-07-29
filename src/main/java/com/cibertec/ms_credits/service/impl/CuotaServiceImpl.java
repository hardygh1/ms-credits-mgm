package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.response.CuotaResponse;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.CuotaMapper;
import com.cibertec.ms_credits.repository.CuotaRepository;
import com.cibertec.ms_credits.repository.PrestamoRepository;
import com.cibertec.ms_credits.service.CuotaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CuotaServiceImpl implements CuotaService {

    private final CuotaRepository cuotaRepository;
    private final PrestamoRepository prestamoRepository;
    private final CuotaMapper cuotaMapper;

    public CuotaServiceImpl(CuotaRepository cuotaRepository, PrestamoRepository prestamoRepository, CuotaMapper cuotaMapper) {
        this.cuotaRepository = cuotaRepository;
        this.prestamoRepository = prestamoRepository;
        this.cuotaMapper = cuotaMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuotaResponse> listarPorPrestamo(Integer prestamoId) {
        if (!prestamoRepository.existsById(prestamoId)) {
            throw new ResourceNotFoundException(ApiMessages.PRESTAMO_NO_ENCONTRADO);
        }

        return cuotaRepository.findByPrestamoPrestamoIdOrderByNumeroCuotaAsc(prestamoId).stream()
                .map(cuotaMapper::toResponse)
                .toList();
    }
}
