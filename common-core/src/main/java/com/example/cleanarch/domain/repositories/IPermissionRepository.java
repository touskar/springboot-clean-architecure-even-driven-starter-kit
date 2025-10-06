package com.example.cleanarch.common.domain.repositories;

import com.example.cleanarch.common.domain.entities.Permission;
import com.example.cleanarch.common.domain.enums.Plateform;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Permission domain entity.
 * This interface belongs to the domain layer and defines business operations.
 */
public interface IPermissionRepository {

    // Basic CRUD
    /**
     * Save or update a permission
     */
    Permission save(Permission permission);

    /**
     * Find permission by ID
     */
    Optional<Permission> findById(String id);

    /**
     * Find permission by code
     */
    Optional<Permission> findByCode(String code);

    /**
     * Find all permissions
     */
    List<Permission> findAll();

    /**
     * Find permissions by platform
     */
    List<Permission> findByPlateform(Plateform plateform);

    /**
     * Find permissions by status
     */
    List<Permission> findByStatus(StatusEntityEnum status);

    /**
     * Find permissions by group
     */
    List<Permission> findByGroup(String group);

    /**
     * Find permissions by target entity
     */
    List<Permission> findByTargetEntity(String targetEntity);

    /**
     * Delete permission by ID
     */
    void deleteById(String id);

    /**
     * Check if permission exists by code
     */
    boolean existsByCode(String code);

    // Business methods
    /**
     * Activate a permission
     */
    Permission activatePermission(String permissionId);

    /**
     * Deactivate a permission
     */
    Permission deactivatePermission(String permissionId);

    /**
     * Find all active permissions
     */
    List<Permission> findAllActive();

    /**
     * Find active permissions by platform
     */
    List<Permission> findActiveByPlateform(Plateform plateform);
}
