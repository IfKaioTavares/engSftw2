package com.centroSer.app.infra.security;

import com.centroSer.app.infra.exceptions.UnauthorizedException;
import com.centroSer.app.infra.security.contracts.JwtContract;
import com.centroSer.app.persistent.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.JwtValidationException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;


@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;
    private final JwtContract jwtContract;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException, UnauthorizedException {
        var token = extractToken(request);
        if (!token.isEmpty()) {
            try {
                var tokenSubject = jwtContract.validateTokenAndGetSubject(token);
                UUID userPublicId = UUID.fromString(tokenSubject);
                var user = userRepository.findByPublicIdAndDeletedFalse(userPublicId);
                if (user.isPresent()) {
                    var userAuthenticated = new UserAuthenticated(user.get());
                    Authentication authentication = new UsernamePasswordAuthenticationToken(new SecurityContextDto(
                            userAuthenticated.getUserId(),
                            userAuthenticated.getRole()
                    ), null, userAuthenticated.getAuthorities());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }catch (JwtValidationException e){
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Credentials not allowed\"}");
                return;
            }
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return (authHeader != null && authHeader.startsWith("Bearer ")) ? authHeader.substring(7) : "";
    }
}
