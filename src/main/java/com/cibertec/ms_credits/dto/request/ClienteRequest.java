package com.cibertec.ms_credits.dto.request;

import com.cibertec.ms_credits.constants.ApiMessages;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotNull(message = ApiMessages.TIPO_DOCUMENTO_REQUERIDO)
        Integer tipoDocumentoId,

        @NotBlank(message = ApiMessages.NUMERO_DOCUMENTO_REQUERIDO)
        @Size(max = 15, message = ApiMessages.NUMERO_DOCUMENTO_MAX_LENGTH)
        String numeroDocumento,

        @NotBlank(message = ApiMessages.NOMBRES_REQUERIDO)
        @Size(max = 100, message = ApiMessages.NOMBRES_MAX_LENGTH)
        String nombres,

        @NotBlank(message = ApiMessages.APELLIDOS_REQUERIDO)
        @Size(max = 100, message = ApiMessages.APELLIDOS_MAX_LENGTH)
        String apellidos,

        @Size(max = 20)
        String telefono,

        @Size(max = 200)
        String direccion,

        @Email(message = ApiMessages.CORREO_INVALIDO)
        @Size(max = 150)
        String correo
) {
}
