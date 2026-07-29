package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DetalleCuotaResponse(
        Integer numeroCuota,
        LocalDate fechaPago,
        BigDecimal capital,
        BigDecimal interes,
        BigDecimal cuota,
        BigDecimal saldo
) {
}
