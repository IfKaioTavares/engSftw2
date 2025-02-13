package com.centroSer.app.infra.security;

import com.centroSer.app.persistent.entities.User;
import com.centroSer.app.infra.security.contracts.UserAuthContract;
import com.centroSer.app.persistent.entities.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
public class UserAuthenticated implements UserAuthContract {
    private final User user;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.user.isAdmin()
                ? List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER"))
                : List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() {
        return this.user.getPassword();
    }

    @Override
    public String getUsername() {
        return this.getUsername();
    }

    @Override
    public Long getUserId() {
        return this.user.getId();
    }

    @Override
    public UUID getUserPublicId() {
        return this.user.getPublicId();
    }

    @Override
    public UserRole getRole() {
        return this.user.getRole();
    }


}
