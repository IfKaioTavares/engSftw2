package com.centroSer.app.infra.api;

import org.springframework.http.HttpStatus;

public record RestErrorMessage(
        HttpStatus status,
        String message
) {
}
