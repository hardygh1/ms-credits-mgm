package com.cibertec.ms_credits.service;

import com.cibertec.ms_credits.dto.request.LoginRequest;
import com.cibertec.ms_credits.dto.request.RegisterRequest;
import com.cibertec.ms_credits.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request);
}
