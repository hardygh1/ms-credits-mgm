package com.cibertec.ms_credits.security;

import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.entity.Usuario;
import com.cibertec.ms_credits.repository.UsuarioRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException(correo));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getCorreo())
                .password(usuario.getPasswordHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol())))
                .disabled(!EstadoCodigo.ACTIVO.equals(usuario.getEstado().getCodigo()))
                .build();
    }
}
