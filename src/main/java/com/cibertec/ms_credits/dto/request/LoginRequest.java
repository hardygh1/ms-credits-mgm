package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record LoginRequest(

        @NotBlank(message = ApiMessages.CORREO_REQUERIDO)
        @Email(message = ApiMessages.CORREO_INVALIDO)
        String correo,

        @NotBlank(message = ApiMessages.PASSWORD_REQUERIDO)
        String password
) {
}
