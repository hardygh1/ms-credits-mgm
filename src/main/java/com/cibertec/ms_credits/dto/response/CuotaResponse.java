package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record CuotaResponse(
        Integer cuotaId,
        Integer prestamoId,
        Integer numeroCuota,
        LocalDate fechaVencimiento,
        BigDecimal capital,
        BigDecimal interes,
        BigDecimal monto,
        BigDecimal saldo,
        Integer estadoId,
        String estadoDesc
) {
}
