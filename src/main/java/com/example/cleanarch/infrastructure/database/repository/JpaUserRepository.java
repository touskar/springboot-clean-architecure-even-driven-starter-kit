package com.example.cleanarch.infrastructure.database.repository;

import com.example.cleanarch.infrastructure.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * JPA Repository interface for UserEntity.
 * Spring Data JPA will automatically implement this interface.
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {

    /**
     * Find users by name containing the given string (case-insensitive)
     */
    List<UserEntity> findByNameContainingIgnoreCase(String name);

    /**
     * Find user by exact email match
     */
    Optional<UserEntity> findByEmail(String email);

    /**
     * Check if user exists by email
     */
    boolean existsByEmail(String email);
}
