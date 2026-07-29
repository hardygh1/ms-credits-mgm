package com.cibertec.ms_credits.service.impl;

import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.constants.EstadoCodigo;
import com.cibertec.ms_credits.constants.ParametroTipo;
import com.cibertec.ms_credits.dto.request.LoginRequest;
import com.cibertec.ms_credits.dto.request.RegisterRequest;
import com.cibertec.ms_credits.dto.response.AuthResponse;
import com.cibertec.ms_credits.entity.Parametro;
import com.cibertec.ms_credits.entity.Usuario;
import com.cibertec.ms_credits.exception.BusinessException;
import com.cibertec.ms_credits.exception.ResourceNotFoundException;
import com.cibertec.ms_credits.mapper.UsuarioMapper;
import com.cibertec.ms_credits.repository.UsuarioRepository;
import com.cibertec.ms_credits.security.JwtUtil;
import com.cibertec.ms_credits.service.AuthService;
import com.cibertec.ms_credits.service.ParametroService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ParametroService parametroService;

    public AuthServiceImpl(UsuarioRepository usuarioRepository,
                            UsuarioMapper usuarioMapper,
                            PasswordEncoder passwordEncoder,
                            AuthenticationManager authenticationManager,
                            JwtUtil jwtUtil,
                            ParametroService parametroService) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.parametroService = parametroService;
    }

    @Override
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.correo())) {
            throw new BusinessException(ApiMessages.CORREO_YA_REGISTRADO);
        }

        Parametro estadoActivo = parametroService.obtenerPorTipoYCodigo(ParametroTipo.ESTADO, EstadoCodigo.ACTIVO);
        String passwordHash = passwordEncoder.encode(request.password());
        Usuario usuario = usuarioRepository.save(usuarioMapper.toEntity(request, passwordHash, estadoActivo));
        String token = jwtUtil.generateToken(usuario.getCorreo());

        return usuarioMapper.toAuthResponse(usuario, token);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.correo(), request.password()));

        Usuario usuario = usuarioRepository.findByCorreo(request.correo())
                .orElseThrow(() -> new ResourceNotFoundException(ApiMessages.UNAUTHORIZED));
        String token = jwtUtil.generateToken(usuario.getCorreo());

        return usuarioMapper.toAuthResponse(usuario, token);
    }
}
