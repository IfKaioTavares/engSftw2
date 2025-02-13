package com.centroSer.app.persistent.entities;

import com.centroSer.app.persistent.converters.UserRoleConverter;
import com.centroSer.app.persistent.entities.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends AbstractEntity {
    private UUID publicId;
    private String username;
    private String email;
    private String password;

    @Convert(converter = UserRoleConverter.class)
    @Column(name = "role_id")
    private UserRole role;

    private boolean active;
    private ZonedDateTime lastAccess;

    public boolean isAdmin() {
        return this.role == UserRole.ADMIN;
    }

    public User(Long id){
        super(id);
    }
}
