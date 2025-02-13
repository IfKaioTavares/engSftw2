package com.centroSer.app;

import com.centroSer.app.api.auth.AuthService;
import com.centroSer.app.api.auth.dtos.RegisterDto;
import com.centroSer.app.api.auth.dtos.login.LoginRequestDto;
import com.centroSer.app.api.auth.dtos.login.LoginResponseDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private JwtContract jwtContract;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterDto registerDto;
    private User user;

    @BeforeEach
    void setUp() {
        registerDto = new RegisterDto("test@example.com", "testuser", "password");
        user = User.builder()
                .publicId(UUID.randomUUID())
                .email("test@example.com")
                .username("testuser")
                .password("encodedPassword")
                .role(UserRole.USER)
                .active(true)
                .build();
    }

    @Test
    void shouldRegisterUserSuccessfully() {
        when(userRepository.findByEmailAndDeletedFalse(registerDto.email())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(registerDto.password())).thenReturn("encodedPassword");
        when(userRepository.save(Mockito.<User>any())).thenReturn(user);

        assertDoesNotThrow(() -> authService.register(registerDto));

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertNotNull(userCaptor.getValue());
    }

    @Test
    void shouldThrowExceptionIfEmailAlreadyExists() {
        when(userRepository.findByEmailAndDeletedFalse(registerDto.email())).thenReturn(Optional.of(user));
        assertThrows(BadRequestException.class, () -> authService.register(registerDto));
    }

    @Test
    void shouldLoginSuccessfully() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "password");
        HttpServletRequest request = mock(HttpServletRequest.class);

        when(userRepository.findByEmailAndDeletedFalse(loginRequestDto.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(eq(loginRequestDto.password()), eq(user.getPassword()))).thenReturn(true);

        UserAuthenticated userAuthenticated = new UserAuthenticated(user);

        ArgumentCaptor<UserAuthenticated> userAuthCaptor = ArgumentCaptor.forClass(UserAuthenticated.class);
        when(jwtContract.generateToken(userAuthCaptor.capture())).thenReturn("jwtToken");

        when(jwtContract.getIssuedAt(anyString())).thenReturn(ZonedDateTime.now());
        when(jwtContract.getExpirationDate(anyString())).thenReturn(ZonedDateTime.now().plusHours(1));

        LoginResponseDto response = authService.login(loginRequestDto, request);

        assertNotNull(response);
        assertEquals("jwtToken", response.token());

        assertNotNull(userAuthCaptor.getValue());
        assertTrue(userAuthCaptor.getValue() instanceof UserAuthenticated);
    }


    @Test
    void shouldThrowExceptionForInvalidLogin() {
        LoginRequestDto loginRequestDto = new LoginRequestDto("test@example.com", "wrongpassword");
        when(userRepository.findByEmailAndDeletedFalse(loginRequestDto.email())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(loginRequestDto.password(), user.getPassword())).thenReturn(false);

        assertThrows(BadRequestException.class, () -> authService.login(loginRequestDto, mock(HttpServletRequest.class)));
    }

    @Test
    void shouldRevokeTokensSuccessfully() {
        UUID userPublicId = user.getPublicId();
        when(userRepository.findByPublicIdAndDeletedFalse(userPublicId)).thenReturn(Optional.of(user));
        when(sessionRepository.findAllByUserIdAndActiveTrue(user.getId())).thenReturn(java.util.List.of(mock(Session.class)));

        assertDoesNotThrow(() -> authService.revokeTokens(userPublicId));
        verify(sessionRepository, times(1)).findAllByUserIdAndActiveTrue(user.getId());
    }

    @Test
    void shouldThrowExceptionWhenRevokingTokensForNonExistentUser() {
        UUID userPublicId = UUID.randomUUID();
        when(userRepository.findByPublicIdAndDeletedFalse(userPublicId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> authService.revokeTokens(userPublicId));
    }
}
