package com.centroSer.app.persistent.repositories;

import com.centroSer.app.persistent.entities.User;
import com.centroSer.app.persistent.repositories.contracts.BaseRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends BaseRepository<User, Long> {
    Optional<User> findByPublicIdAndDeletedFalse(UUID publicId);
    Optional<User> findByEmailAndDeletedFalse(String email);
    List<User> findAllByDeletedFalse();
}
