package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.constants.ParametroTipo;
import com.cibertec.ms_credits.dto.request.ClienteEstadoRequest;
import com.cibertec.ms_credits.dto.request.ClienteRequest;
import com.cibertec.ms_credits.dto.response.ClienteResponse;
import com.cibertec.ms_credits.dto.response.PageResponse;
import com.cibertec.ms_credits.entity.Cliente;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.exception.BusinessException;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.ClienteMapper;
import com.cibertec.ms_credits.repository.ClienteRepository;
import com.cibertec.ms_credits.service.ClienteService;
import com.cibertec.ms_credits.service.ParametroService;
import com.cibertec.ms_credits.util.ClienteSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;
    private final ParametroService parametroService;

    public ClienteServiceImpl(ClienteRepository clienteRepository,
                               ClienteMapper clienteMapper,
                               ParametroService parametroService) {
        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
        this.parametroService = parametroService;
    }

    @Override
    public ClienteResponse registrar(ClienteRequest request) {
        Parametro tipoDocumento = parametroService.obtenerPorIdYTipo(
                request.tipoDocumentoId(), ParametroTipo.TIPO_DOCUMENTO, ApiMessages.TIPO_DOCUMENTO_NO_ENCONTRADO);

        if (clienteRepository.existsByTipoDocumentoParametroIdAndNumeroDocumento(
                tipoDocumento.getParametroId(), request.numeroDocumento())) {
            throw new BusinessException(ApiMessages.DOCUMENTO_YA_REGISTRADO);
        }

        Parametro estadoActivo = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.ACTIVO);
        Cliente cliente = clienteRepository.save(clienteMapper.toEntity(request, tipoDocumento, estadoActivo));
        return clienteMapper.toResponse(cliente);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<ClienteResponse> listar(String documento, String nombre, Integer estadoId,
                                                 Integer tipoDocumentoId, Pageable pageable) {
        Specification<Cliente> filtro = ClienteSpecifications.conNumeroDocumento(documento)
                .and(ClienteSpecifications.conNombreOApellido(nombre))
                .and(ClienteSpecifications.conEstadoId(estadoId))
                .and(ClienteSpecifications.conTipoDocumentoId(tipoDocumentoId));

        Page<ClienteResponse> pagina = clienteRepository.findAll(filtro, pageable).map(clienteMapper::toResponse);
        return PageResponse.from(pagina);
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorId(Integer id) {
        return clienteMapper.toResponse(obtenerClienteOrThrow(id));
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(Integer id, ClienteRequest request) {
        Cliente cliente = obtenerClienteOrThrow(id);
        Parametro tipoDocumento = parametroService.obtenerPorIdYTipo(
                request.tipoDocumentoId(), ParametroTipo.TIPO_DOCUMENTO, ApiMessages.TIPO_DOCUMENTO_NO_ENCONTRADO);

        if (clienteRepository.existsByTipoDocumentoParametroIdAndNumeroDocumentoAndClienteIdNot(
                tipoDocumento.getParametroId(), request.numeroDocumento(), id)) {
            throw new BusinessException(ApiMessages.DOCUMENTO_YA_REGISTRADO);
        }

        clienteMapper.actualizar(cliente, request, tipoDocumento);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    @Override
    @Transactional
    public ClienteResponse cambiarEstado(Integer id, ClienteEstadoRequest request) {
        Cliente cliente = obtenerClienteOrThrow(id);
        Parametro estado = parametroService.obtenerPorIdYTipo(
                request.estadoId(), ParametroTipo.ESTADO, ApiMessages.ESTADO_NO_ENCONTRADO);

        cliente.setEstado(estado);
        return clienteMapper.toResponse(clienteRepository.save(cliente));
    }

    private Cliente obtenerClienteOrThrow(Integer id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.CLIENTE_NO_ENCONTRADO));
    }
}
