package com.cibertec.ms_credits.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PrestamoDetalleResponse(
        Integer prestamoId,
        Integer clienteId,
        Integer cotizacionId,
        BigDecimal monto,
        BigDecimal tasaInteres,
        Integer plazoMeses,
        BigDecimal cuotaMensual,
        BigDecimal totalInteres,
        BigDecimal totalPagar,
        Integer estadoId,
        String estadoDesc,
        LocalDateTime fechaAprobacion,
        LocalDate fechaDesembolso,
        List<DetalleCuotaResponse> cronograma
) {
}
