package com.cibertec.ms_credits.dto.response;

public record AuthResponse(
        String token,
        Integer usuarioId,
        String nombre,
        String correo,
        String rol
) {
}
