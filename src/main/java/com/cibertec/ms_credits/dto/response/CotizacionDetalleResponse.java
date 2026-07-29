package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CotizacionDetalleResponse(
        Integer cotizacionId,
        Integer clienteId,
        BigDecimal monto,
        BigDecimal tasaInteres,
        Integer plazoMeses,
        BigDecimal cuotaMensual,
        BigDecimal totalInteres,
        BigDecimal totalPagar,
        Integer estadoId,
        String estadoDesc,
        LocalDateTime fechaRegistro,
        List<DetalleCuotaResponse> cronograma
) {
}
