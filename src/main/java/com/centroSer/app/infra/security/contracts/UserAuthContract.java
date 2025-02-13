package com.centroSer.app.infra.security.contracts;

import com.centroSer.app.persistent.entities.enums.UserRole;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface UserAuthContract extends UserDetails {
    Long getUserId();
    UUID getUserPublicId();
    UserRole getRole();
}
