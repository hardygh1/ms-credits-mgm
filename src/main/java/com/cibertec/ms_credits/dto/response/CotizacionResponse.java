package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CotizacionResponse(
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
        LocalDateTime fechaRegistro
) {
}
