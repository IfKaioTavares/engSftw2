package com.centroSer.app.infra.security;

import com.centroSer.app.persistent.entities.enums.UserRole;

public record SecurityContextDto(
        Long userId,
        UserRole role
) {
}
