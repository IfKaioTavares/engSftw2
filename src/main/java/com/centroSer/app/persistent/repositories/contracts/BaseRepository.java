package com.centroSer.app.persistent.repositories.contracts;

import com.centroSer.app.persistent.entities.AbstractEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;

@NoRepositoryBean
public interface BaseRepository <T extends AbstractEntity, B> extends JpaRepository<T, B> {

    Optional<T> findByIdAndDeletedFalse(B id);
    List<T> findAllByIdAndDeletedFalse(B id);

    default void softDelete(T entity) {
       entity.setDateUpdate(ZonedDateTime.now());
       entity.setDeleted(true);
       this.save(entity);
    }

    default void softDeleteById(B id) {
        Optional<T> entity = this.findByIdAndDeletedFalse(id);
        entity.ifPresent(this::softDelete);
    }
}
