package com.cibertec.ms_credits.mapper;

import com.cibertec.ms_credits.dto.request.RegisterRequest;
import com.cibertec.ms_credits.dto.response.AuthResponse;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.entity.Usuario;
import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(RegisterRequest request, String passwordHash, Parametro estado) {
        return Usuario.builder()
                .nombre(request.nombre())
                .correo(request.correo())
                .passwordHash(passwordHash)
                .rol(request.rol())
                .estado(estado)
                .build();
    }

    public AuthResponse toAuthResponse(Usuario usuario, String token) {
        return new AuthResponse(token, usuario.getUsuarioId(), usuario.getNombre(), usuario.getCorreo(), usuario.getRol());
    }
}
