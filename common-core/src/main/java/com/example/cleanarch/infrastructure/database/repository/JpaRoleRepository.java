package com.example.cleanarch.common.infrastructure.database.repository;

import com.example.cleanarch.common.infrastructure.database.entities.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaRoleRepository extends JpaRepository<RoleEntity, String> {
    Optional<RoleEntity> findByName(String name);

    @Query("SELECT r FROM RoleEntity r WHERE r.status = 'ACTIVE'")
    List<RoleEntity> findAllActive();

    List<RoleEntity> findByIdIn(List<String> ids);

    boolean existsByName(String name);
}
