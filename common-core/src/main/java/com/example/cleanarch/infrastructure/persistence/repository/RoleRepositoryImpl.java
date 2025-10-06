package com.example.cleanarch.common.infrastructure.persistence.repository;

import com.example.cleanarch.common.domain.entities.Permission;
import com.example.cleanarch.common.domain.entities.Role;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.domain.repositories.IRoleRepository;
import com.example.cleanarch.common.infrastructure.database.entities.RoleEntity;
import com.example.cleanarch.common.infrastructure.database.repository.JpaRoleRepository;
import com.example.cleanarch.common.infrastructure.mappers.RoleMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RoleRepositoryImpl implements IRoleRepository {
    private final JpaRoleRepository jpaRepository;
    private final RoleMapper roleMapper;

    @Override
    public Role save(Role role) {
        RoleEntity entity = roleMapper.toEntity(Optional.of(role));
        RoleEntity saved = jpaRepository.save(entity);
        return roleMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<Role> findById(String id) {
        return jpaRepository.findById(id).map(e -> roleMapper.toDomain(Optional.of(e)));
    }

    @Override
    public Optional<Role> findByName(String name) {
        return jpaRepository.findByName(name).map(e -> roleMapper.toDomain(Optional.of(e)));
    }

    @Override
    public List<Role> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> roleMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(e -> roleMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<Role> findByIds(List<String> ids) {
        return jpaRepository.findByIdIn(ids).stream()
                .map(e -> roleMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByName(String name) {
        return jpaRepository.existsByName(name);
    }

    // Business methods
    @Override
    public Role assignPermissions(String roleId, List<Permission> permissions) {
        Role role = findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        List<Permission> currentPermissions = new ArrayList<>(role.getPermissions());
        permissions.forEach(permission -> {
            if (currentPermissions.stream().noneMatch(p -> p.getCode().equals(permission.getCode()))) {
                currentPermissions.add(permission);
            }
        });

        role.setPermissions(currentPermissions);
        role.setUpdatedAt(Instant.now());
        return save(role);
    }

    @Override
    public Role removePermission(String roleId, String permissionCode) {
        Role role = findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        role.getPermissions().removeIf(p -> p.getCode().equals(permissionCode));
        role.setUpdatedAt(Instant.now());
        return save(role);
    }

    @Override
    public Role activateRole(String roleId) {
        Role role = findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        role.setStatus(StatusEntityEnum.ACTIVE);
        role.setUpdatedAt(Instant.now());
        return save(role);
    }

    @Override
    public Role deactivateRole(String roleId) {
        Role role = findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        role.setStatus(StatusEntityEnum.DISABLED);
        role.setUpdatedAt(Instant.now());
        return save(role);
    }

    @Override
    public boolean hasPermission(String roleId, String permissionCode) {
        Role role = findById(roleId)
                .orElseThrow(() -> new IllegalArgumentException("Role not found with id: " + roleId));

        return role.getPermissions().stream()
                .anyMatch(p -> p.getCode().equals(permissionCode));
    }
}
