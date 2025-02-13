package com.centroSer.app.persistent.entities;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.ZonedDateTime;

@Getter
@Setter
@MappedSuperclass
@ToString
public abstract class AbstractEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private boolean deleted;
    private ZonedDateTime dateCreate;
    private ZonedDateTime dateUpdate;

    public AbstractEntity() {
        this.dateUpdate = ZonedDateTime.now();
        this.dateCreate = ZonedDateTime.now();
    }

    public AbstractEntity(Long id) {
        this();
        this.id = id;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractEntity && this.getId() != null && ((AbstractEntity) obj).getId() != null)
            return (this.getId() != null && this.getId().equals(((AbstractEntity) obj).getId()))
                    || (((AbstractEntity) obj).getId() != null && ((AbstractEntity) obj).getId().equals(this.getId()));
        return super.equals(obj);
    }
}
