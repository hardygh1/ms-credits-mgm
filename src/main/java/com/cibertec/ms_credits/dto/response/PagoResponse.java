package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PagoResponse(
        Integer pagoId,
        Integer cuotaId,
        Integer prestamoId,
        Integer numeroCuota,
        LocalDateTime fechaPago,
        BigDecimal montoPagado,
        String metodoPago
) {
}
