package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(

        @NotBlank(message = ApiMessages.NOMBRE_REQUERIDO)
        String nombre,

        @NotBlank(message = ApiMessages.CORREO_REQUERIDO)
        @Email(message = ApiMessages.CORREO_INVALIDO)
        String correo,

        @NotBlank(message = ApiMessages.PASSWORD_REQUERIDO)
        @Size(min = 6, max = 100, message = ApiMessages.PASSWORD_MIN_LENGTH)
        String password,

        @NotBlank(message = ApiMessages.ROL_REQUERIDO)
        String rol
) {
}
