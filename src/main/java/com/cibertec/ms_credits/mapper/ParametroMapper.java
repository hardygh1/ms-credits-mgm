package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.response.ParametroResponse;
import com.cibertec.ms_credits.entity.Parametro;
import org.springframework.stereotype.Component;

@Component
public class ParametroMapper {

    public ParametroResponse toResponse(Parametro parametro) {
        return new ParametroResponse(
                parametro.getParametroId(),
                parametro.getTipo(),
                parametro.getCodigo(),
                parametro.getDescripcion()
        );
    }
}
