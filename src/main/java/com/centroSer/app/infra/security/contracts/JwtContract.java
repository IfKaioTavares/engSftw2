package com.centroSer.app.infra.security.contracts;

import java.time.ZonedDateTime;

public interface JwtContract {
    String generateToken(UserAuthContract userAuth);
    String validateTokenAndGetSubject(String token);
    ZonedDateTime getExpirationDate(String token);
    ZonedDateTime getIssuedAt(String token);
}
