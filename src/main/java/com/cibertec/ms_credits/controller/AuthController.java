package com.cibertec.ms_credits.controller;

import com.cibertec.ms_credits.common.ApiResponse;
import com.cibertec.ms_credits.constants.ApiMessages;
import com.cibertec.ms_credits.dto.request.LoginRequest;
import com.cibertec.ms_credits.dto.request.RegisterRequest;
import com.cibertec.ms_credits.dto.response.AuthResponse;
import com.cibertec.ms_credits.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(response, ApiMessages.USUARIO_REGISTRADO));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success(response, ApiMessages.LOGIN_EXITOSO));
    }
}
