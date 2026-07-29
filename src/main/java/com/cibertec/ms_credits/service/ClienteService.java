package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.request.ClienteEstadoRequest;
import com.cibertec.ms_credits.dto.request.ClienteRequest;
import com.cibertec.ms_credits.dto.response.ClienteResponse;
import com.cibertec.ms_credits.dto.response.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ClienteService {

    ClienteResponse registrar(ClienteRequest request);

    PageResponse<ClienteResponse> listar(String documento, String nombre, Integer estadoId, Integer tipoDocumentoId,
                                          Pageable pageable);

    ClienteResponse obtenerPorId(Integer id);

    ClienteResponse actualizar(Integer id, ClienteRequest request);

    ClienteResponse cambiarEstado(Integer id, ClienteEstadoRequest request);
}
