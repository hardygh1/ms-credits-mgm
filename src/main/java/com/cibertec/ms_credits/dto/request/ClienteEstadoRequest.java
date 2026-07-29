package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.NotNull;

public record ClienteEstadoRequest(

        @NotNull(message = ApiMessages.ESTADO_ID_REQUERIDO)
        Integer estadoId
) {
}
