package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record PagoRequest(

        @NotNull(message = ApiMessages.MONTO_PAGADO_REQUERIDO)
        @Positive(message = ApiMessages.MONTO_PAGADO_INVALIDO)
        @Digits(integer = 10, fraction = 2, message = ApiMessages.MONTO_PAGADO_INVALIDO)
        BigDecimal montoPagado,

        @NotBlank(message = ApiMessages.METODO_PAGO_REQUERIDO)
        @Size(max = 30, message = ApiMessages.METODO_PAGO_MAX_LENGTH)
        String metodoPago
) {
}
