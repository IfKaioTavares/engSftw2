package com.centroSer.app.persistent.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.ZonedDateTime;

@Entity
@Table(name = "sessions")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Session extends AbstractEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String token;
    private ZonedDateTime issuedAt;
    private ZonedDateTime expiresAt;
    private String userAgent;
    private String ipAddress;
    private boolean active;

    public Session(Long id){
        super(id);
        this.active = true;
    }
}
