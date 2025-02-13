package com.centroSer.app.infra.security;

import com.centroSer.app.infra.exceptions.UnauthorizedException;
import com.centroSer.app.persistent.repositories.SessionRepository;
import com.centroSer.app.infra.security.contracts.JwtContract;
import com.centroSer.app.infra.security.contracts.UserAuthContract;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Component;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtService implements JwtContract {
    private final SessionRepository sessionRepository;
    @Value("${security.jwt.private}")
    private RSAPrivateKey privateKey;
    @Value("${security.jwt.public}")
    private RSAPublicKey publicKey;

    @Override
    public String generateToken(UserAuthContract userAuth) {
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(3600);
        String scopes = userAuth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(" "));
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("login-api")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(userAuth.getUserPublicId().toString())
                .claim("scope", scopes)
                .build();
        return encoder().encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }

    @Override
    public String validateTokenAndGetSubject(String token) throws UnauthorizedException {
        return sessionRepository.findByToken(token)
                .filter(session -> !session.isDeleted() && session.isActive())
                .map(session -> {
                    if (getExpirationDate(token).isBefore(ZonedDateTime.now())) {
                        session.setActive(false);
                        sessionRepository.save(session);
                        throw new UnauthorizedException("Expired token.");
                    }
                    return decoder().decode(token).getSubject();
                })
                .orElseThrow(() -> new UnauthorizedException("Invalid token"));
    }

    @Override
    public ZonedDateTime getExpirationDate(String token) {
        return tokenIsnstantToZonedDateTime(Objects.requireNonNull(decoder().decode(token).getExpiresAt()));
    }

    @Override
    public ZonedDateTime getIssuedAt(String token) {
        return tokenIsnstantToZonedDateTime(Objects.requireNonNull(decoder().decode(token).getIssuedAt()));
    }

    private ZonedDateTime tokenIsnstantToZonedDateTime(Instant instant){
        return instant.atZone(ZoneId.of("UTC"));
    }

    private JwtDecoder decoder() {
        return NimbusJwtDecoder.withPublicKey(this.publicKey).build();
    }


    private JwtEncoder encoder() {
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(this.privateKey).build();
        JWKSource<SecurityContext> jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }
}
