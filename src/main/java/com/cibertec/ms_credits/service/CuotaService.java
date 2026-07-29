package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.response.CuotaResponse;

import java.util.List;

public interface CuotaService {

    List<CuotaResponse> listarPorPrestamo(Integer prestamoId);
}
