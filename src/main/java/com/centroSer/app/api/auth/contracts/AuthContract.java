package com.centroSer.app.api.auth.contracts;

import com.centroSer.app.api.auth.dtos.RegisterDto;
import com.centroSer.app.api.auth.dtos.login.LoginRequestDto;
import com.centroSer.app.api.auth.dtos.login.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface AuthContract {
    void register(RegisterDto registerDto);
    LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request);
    void revokeTokens(UUID userPublicId);
}
