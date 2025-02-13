package com.centroSer.app.api.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record RegisterDto(
        @NotBlank
        String username,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password
) {

}
