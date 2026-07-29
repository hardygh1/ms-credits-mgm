package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.request.CotizacionRequest;
import com.cibertec.ms_credits.dto.response.CotizacionDetalleResponse;
import com.cibertec.ms_credits.dto.response.CotizacionResponse;
import com.cibertec.ms_credits.dto.response.PrestamoResponse;

import java.util.List;

public interface CotizacionService {

    CotizacionResponse registrar(CotizacionRequest request);

    List<CotizacionResponse> listar();

    CotizacionDetalleResponse obtenerDetalle(Integer cotizacionId);

    PrestamoResponse aprobar(Integer cotizacionId);

    CotizacionResponse eliminar(Integer cotizacionId);
}
