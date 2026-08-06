package dev.ravenhall.user.adapters.out.persistence.jpa.repository;

import dev.ravenhall.user.adapters.out.persistence.jpa.entites.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByUsername(String username);

    boolean existsByUsername(String username);
}
