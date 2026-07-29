package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PrestamoRequest(

        @NotNull(message = ApiMessages.CLIENTE_ID_REQUERIDO)
        Integer clienteId,

        @NotNull(message = ApiMessages.MONTO_REQUERIDO)
        @Positive(message = ApiMessages.MONTO_INVALIDO)
        @Digits(integer = 10, fraction = 2, message = ApiMessages.MONTO_INVALIDO)
        BigDecimal monto,

        @NotNull(message = ApiMessages.TASA_INTERES_REQUERIDA)
        @Positive(message = ApiMessages.TASA_INTERES_INVALIDA)
        @Digits(integer = 3, fraction = 2, message = ApiMessages.TASA_INTERES_INVALIDA)
        BigDecimal tasaInteres,

        @NotNull(message = ApiMessages.PLAZO_MESES_REQUERIDO)
        @Min(value = 1, message = ApiMessages.PLAZO_MESES_INVALIDO)
        @Max(value = 360, message = ApiMessages.PLAZO_MESES_INVALIDO)
        Integer plazoMeses
) {
}
