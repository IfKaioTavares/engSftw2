package com.centroSer.app.api.auth.dtos.login;

import com.centroSer.app.api.user.UserResponseDto;
import lombok.Builder;

@Builder
public record LoginResponseDto(
        UserResponseDto user,
        String token
) {
}
