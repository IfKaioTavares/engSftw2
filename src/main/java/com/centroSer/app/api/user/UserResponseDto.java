package com.centroSer.app.api.user;

import com.centroSer.app.persistent.entities.User;
import lombok.Builder;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record UserResponseDto(
        UUID publicId,
        String username,
        String email,
        Boolean active,
        int role,
        ZonedDateTime lastAccess,
        ZonedDateTime dateCreate
) {

    public UserResponseDto(User user) {
            this(
                    user.getPublicId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.isActive(),
                    user.getRole().getId(),
                    user.getLastAccess(),
                    user.getDateCreate()
            );
    }
}
