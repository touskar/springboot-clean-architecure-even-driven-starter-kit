package com.example.cleanarch.common.infrastructure.mappers;

import com.example.cleanarch.common.domain.entities.Permission;
import com.example.cleanarch.common.infrastructure.database.entities.PermissionEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper for Permission domain entity and PermissionEntity JPA entity.
 */
@Component
@RequiredArgsConstructor
public class PermissionMapper implements IDomainMapper<Permission, PermissionEntity> {

    private final ModelMapper modelMapper;

    @Override
    public Permission toDomain(Optional<PermissionEntity> entity) {
        return entity.map(e -> modelMapper.map(e, Permission.class)).orElse(null);
    }

    @Override
    public PermissionEntity toEntity(Optional<Permission> domain) {
        return domain.map(d -> modelMapper.map(d, PermissionEntity.class)).orElse(null);
    }
}
