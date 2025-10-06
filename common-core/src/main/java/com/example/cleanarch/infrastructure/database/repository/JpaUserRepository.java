package com.example.cleanarch.common.infrastructure.database.repository;

import com.example.cleanarch.common.infrastructure.database.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.status = 'ACTIVE' AND u.enabled = true")
    List<UserEntity> findAllActive();




    boolean existsByEmail(String email);
}
