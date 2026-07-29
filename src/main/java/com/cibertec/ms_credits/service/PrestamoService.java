package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.request.PrestamoRequest;
import com.cibertec.ms_credits.dto.response.PrestamoDetalleResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;

import java.util.List;

public interface PrestamoService {

    PrestamoResponse registrar(PrestamoRequest request);

    List<PrestamoResponse> listar();

    PrestamoDetalleResponse obtenerDetalle(Integer prestamoId);

    PrestamoResponse aprobar(Integer prestamoId);
}
