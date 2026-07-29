package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.request.PagoRequest;
import com.cibertec.ms_credits.dto.response.PagoResponse;

import java.util.List;

public interface PagoService {

    PagoResponse registrarPago(Integer prestamoId, PagoRequest request);

    List<PagoResponse> listarPorPrestamo(Integer prestamoId);
}
