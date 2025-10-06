package com.example.cleanarch.common.infrastructure.database.repository;

import com.example.cleanarch.common.domain.enums.Plateform;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.infrastructure.database.entities.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for PermissionEntity.
 * This interface belongs to the infrastructure layer.
 */
@Repository
public interface JpaPermissionRepository extends JpaRepository<PermissionEntity, String> {

    /**
     * Find permission by code
     */
    Optional<PermissionEntity> findByCode(String code);

    /**
     * Find permissions by platform
     */
    List<PermissionEntity> findByPlateform(Plateform plateform);

    /**
     * Find permissions by status
     */
    List<PermissionEntity> findByStatus(StatusEntityEnum status);

    /**
     * Find permissions by group
     */
    List<PermissionEntity> findByGroup(String group);

    /**
     * Find permissions by target entity
     */
    List<PermissionEntity> findByTargetEntity(String targetEntity);

    /**
     * Check if permission exists by code
     */
    boolean existsByCode(String code);

    /**
     * Find permissions by platform and status
     */
    List<PermissionEntity> findByPlateformAndStatus(Plateform plateform, StatusEntityEnum status);
}
