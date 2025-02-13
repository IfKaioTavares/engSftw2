package com.centroSer.app.api.auth;

import com.centroSer.app.api.auth.contracts.AuthContract;
import com.centroSer.app.api.auth.dtos.RegisterDto;
import com.centroSer.app.api.auth.dtos.login.LoginRequestDto;
import com.centroSer.app.api.auth.dtos.login.LoginResponseDto;
import com.centroSer.app.api.user.UserResponseDto;
import com.centroSer.app.infra.exceptions.BadRequestException;
import com.centroSer.app.infra.exceptions.ResourceNotFoundException;
import com.centroSer.app.infra.security.UserAuthenticated;
import com.centroSer.app.infra.security.contracts.JwtContract;
import com.centroSer.app.persistent.entities.Session;
import com.centroSer.app.persistent.entities.User;
import com.centroSer.app.persistent.entities.enums.UserRole;
import com.centroSer.app.persistent.repositories.SessionRepository;
import com.centroSer.app.persistent.repositories.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class AuthService implements AuthContract {
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final JwtContract jwtContract;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void register(RegisterDto registerDto) {
        var user = userRepository.findByEmailAndDeletedFalse(registerDto.email());

        if(user.isPresent()){
            throw new BadRequestException("Email already in use");
        }

        var newUser = User.builder()
                .role(UserRole.USER)
                .email(registerDto.email())
                .username(registerDto.username())
                .password(passwordEncoder.encode(registerDto.password()))
                .active(true)
                .publicId(UUID.randomUUID())
                .build();
        userRepository.save(newUser);
    }

    @Override
    public LoginResponseDto login(LoginRequestDto loginRequestDto, HttpServletRequest request) {
        var user = userRepository.findByEmailAndDeletedFalse(loginRequestDto.email()).orElseThrow(
                () -> new BadRequestException("Email or password is incorrect")
        );

        if(!passwordEncoder.matches(loginRequestDto.password(), user.getPassword()) ||
                !user.getEmail().equals(loginRequestDto.email()) ||
                !user.isActive() ||
                user.isDeleted()
        ){
            throw new BadRequestException("Email or password is incorrect");
        }

        var jwtToken = jwtContract.generateToken(new UserAuthenticated(user));
        var newSession = Session.builder()
                .user(user)
                .token(jwtToken)
                .issuedAt(jwtContract.getIssuedAt(jwtToken))
                .expiresAt(jwtContract.getExpirationDate(jwtToken))
                .userAgent(getUserAgent(request))
                .ipAddress(getIpAddress(request))
                .active(true)
                .build();
        user.setLastAccess(ZonedDateTime.now());
        user.setDateUpdate(ZonedDateTime.now());
        userRepository.save(user);
        sessionRepository.save(newSession);
        return LoginResponseDto.builder()
                .token(jwtToken)
                .user(new UserResponseDto(user))
                .build();
    }

    @Override
    public void revokeTokens(UUID userPublicId) {
        var user = userRepository.findByPublicIdAndDeletedFalse(userPublicId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        sessionRepository.findAllByUserIdAndActiveTrue(user.getId())
                .forEach(session -> {
                    session.setDateUpdate(ZonedDateTime.now());
                    session.setActive(false);
                    session.setDeleted(true);
                    sessionRepository.save(session);
                });
    }

    private String getUserAgent(HttpServletRequest request) {
        return request.getHeader("User-Agent");

    }

    private String getIpAddress(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
}
