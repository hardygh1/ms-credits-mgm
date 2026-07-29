package com.cibertec.ms_credits.dto.response;

public record ParametroResponse(
        Integer parametroId,
        String tipo,
        String codigo,
        String descripcion
) {
}
