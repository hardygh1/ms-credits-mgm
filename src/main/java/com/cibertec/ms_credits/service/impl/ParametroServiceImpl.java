package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.response.ParametroResponse;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.ParametroMapper;
import com.cibertec.ms_credits.repository.ParametroRepository;
import com.cibertec.ms_credits.service.ParametroService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ParametroServiceImpl implements ParametroService {

    private final ParametroRepository parametroRepository;
    private final ParametroMapper parametroMapper;

    public ParametroServiceImpl(ParametroRepository parametroRepository, ParametroMapper parametroMapper) {
        this.parametroRepository = parametroRepository;
        this.parametroMapper = parametroMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Parametro obtenerPorTipoYCodigo(String tipo, String codigo) {
        return parametroRepository.findByTipoAndCodigo(tipo, codigo)
                .orElseThrow(() -> new IllegalStateException(ApiMessages.PARAMETRO_NO_CONFIGURADO));
    }

    @Override
    @Transactional(readOnly = true)
    public Parametro obtenerPorIdYTipo(Integer parametroId, String tipoEsperado, String mensajeError) {
        return Optional.ofNullable(parametroId)
                .flatMap(parametroRepository::findById)
                .filter(parametro -> tipoEsperado.equals(parametro.getTipo()))
                .orElseThrow(() -> new ResourceNotFoundException(mensajeError));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ParametroResponse> listar(String tipo) {
        List<Parametro> parametros = tipo == null
                ? parametroRepository.findAll()
                : parametroRepository.findByTipoOrderByCodigoAsc(tipo);

        return parametros.stream()
                .map(parametroMapper::toResponse)
                .toList();
    }
}
