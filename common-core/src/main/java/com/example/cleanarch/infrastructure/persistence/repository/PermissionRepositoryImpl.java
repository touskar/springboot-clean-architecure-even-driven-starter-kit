package com.example.cleanarch.common.infrastructure.persistence.repository;

import com.example.cleanarch.common.domain.entities.Permission;
import com.example.cleanarch.common.domain.enums.Plateform;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.domain.repositories.IPermissionRepository;
import com.example.cleanarch.common.infrastructure.database.entities.PermissionEntity;
import com.example.cleanarch.common.infrastructure.mappers.PermissionMapper;
import com.example.cleanarch.common.infrastructure.database.repository.JpaPermissionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of PermissionRepository using JPA.
 * This class bridges the domain layer (PermissionRepository interface)
 * with the infrastructure layer (JpaPermissionRepository).
 */
@Component
@RequiredArgsConstructor
public class PermissionRepositoryImpl implements IPermissionRepository {

    private final JpaPermissionRepository jpaPermissionRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public Permission save(Permission permission) {
        PermissionEntity entity = permissionMapper.toEntity(Optional.ofNullable(permission));
        PermissionEntity saved = jpaPermissionRepository.save(entity);
        return permissionMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<Permission> findById(String id) {
        return jpaPermissionRepository.findById(id)
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)));
    }

    @Override
    public Optional<Permission> findByCode(String code) {
        return jpaPermissionRepository.findByCode(code)
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)));
    }

    @Override
    public List<Permission> findAll() {
        return jpaPermissionRepository.findAll().stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByPlateform(Plateform plateform) {
        return jpaPermissionRepository.findByPlateform(plateform).stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByStatus(StatusEntityEnum status) {
        return jpaPermissionRepository.findByStatus(status).stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByGroup(String group) {
        return jpaPermissionRepository.findByGroup(group).stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Permission> findByTargetEntity(String targetEntity) {
        return jpaPermissionRepository.findByTargetEntity(targetEntity).stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaPermissionRepository.deleteById(id);
    }

    @Override
    public boolean existsByCode(String code) {
        return jpaPermissionRepository.existsByCode(code);
    }

    // Business methods
    @Override
    public Permission activatePermission(String permissionId) {
        Permission permission = findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        permission.setStatus(StatusEntityEnum.ACTIVE);
        permission.setUpdatedAt(Instant.now());
        return save(permission);
    }

    @Override
    public Permission deactivatePermission(String permissionId) {
        Permission permission = findById(permissionId)
                .orElseThrow(() -> new IllegalArgumentException("Permission not found with id: " + permissionId));

        permission.setStatus(StatusEntityEnum.DISABLED);
        permission.setUpdatedAt(Instant.now());
        return save(permission);
    }

    @Override
    public List<Permission> findAllActive() {
        return findByStatus(StatusEntityEnum.ACTIVE);
    }

    @Override
    public List<Permission> findActiveByPlateform(Plateform plateform) {
        return jpaPermissionRepository.findByPlateformAndStatus(plateform, StatusEntityEnum.ACTIVE).stream()
                .map(entity -> permissionMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }
}
