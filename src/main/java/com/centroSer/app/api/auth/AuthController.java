package com.centroSer.app.api.auth;

import com.centroSer.app.api.auth.contracts.AuthContract;
import com.centroSer.app.api.auth.dtos.RegisterDto;
import com.centroSer.app.api.auth.dtos.login.LoginRequestDto;
import com.centroSer.app.api.auth.dtos.login.LoginResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthContract contract;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterDto registerDto) {
        contract.register(registerDto);
        URI location = URI.create("api/v1/auth/login");
        return ResponseEntity.created(location).build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto, HttpServletRequest request) {
        return ResponseEntity.ok(contract.login(loginRequestDto, request));
    }

    @GetMapping("/revokeTokens/{userPublicId}")
    public ResponseEntity<String> revokeTokens(@PathVariable String userPublicId) {
        contract.revokeTokens(UUID.fromString(userPublicId));
        return ResponseEntity.ok("Tokens revoked");
    }
}
