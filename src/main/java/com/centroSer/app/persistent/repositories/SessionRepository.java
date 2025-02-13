package com.centroSer.app.persistent.repositories;

import com.centroSer.app.persistent.entities.Session;
import com.centroSer.app.persistent.repositories.contracts.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends BaseRepository<Session, Long> {
    Optional<Session> findByToken(String token);
    List<Session> findAllByUserIdAndActiveTrue(Long userId);

}
