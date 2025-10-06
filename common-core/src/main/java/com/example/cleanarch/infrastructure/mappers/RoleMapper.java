package com.example.cleanarch.common.infrastructure.mappers;

import com.example.cleanarch.common.domain.entities.Role;
import com.example.cleanarch.common.infrastructure.database.entities.RoleEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Mapper for Role domain entity and RoleEntity JPA entity.
 * Handles mapping of role permissions list.
 */
@Component
@RequiredArgsConstructor
public class RoleMapper implements IDomainMapper<Role, RoleEntity> {

    private final ModelMapper modelMapper;
    private final PermissionMapper permissionMapper;

    @Override
    public Role toDomain(Optional<RoleEntity> entity) {
        if (entity.isEmpty()) return null;

        RoleEntity roleEntity = entity.get();
        Role role = modelMapper.map(roleEntity, Role.class);

        // Map permissions list
        if (roleEntity.getPermissions() != null) {
            role.setPermissions(
                roleEntity.getPermissions().stream()
                    .map(p -> permissionMapper.toDomain(Optional.ofNullable(p)))
                    .collect(Collectors.toList())
            );
        }

        return role;
    }

    @Override
    public RoleEntity toEntity(Optional<Role> domain) {
        if (domain.isEmpty()) return null;

        Role role = domain.get();
        RoleEntity roleEntity = modelMapper.map(role, RoleEntity.class);

        // Map permissions list
        if (role.getPermissions() != null) {
            roleEntity.setPermissions(
                role.getPermissions().stream()
                    .map(p -> permissionMapper.toEntity(Optional.ofNullable(p)))
                    .collect(Collectors.toList())
            );
        }

        return roleEntity;
    }
}
