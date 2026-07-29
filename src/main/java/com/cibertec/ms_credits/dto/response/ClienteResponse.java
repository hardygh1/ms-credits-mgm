package com.cibertec.ms_credits.dto.response;

import java.time.LocalDateTime;

public record ClienteResponse(
        Integer clienteId,
        Integer tipoDocumentoId,
        String tipoDocumentoDesc,
        String numeroDocumento,
        String nombres,
        String apellidos,
        String telefono,
        String direccion,
        String correo,
        Integer estadoId,
        String estadoDesc,
        LocalDateTime fechaRegistro
) {
}
