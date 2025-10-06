package com.example.cleanarch.infrastructure.mappers;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.infrastructure.database.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Mapper for converting between User domain model and UserEntity JPA entity.
 * Uses ModelMapper for automatic field mapping.
 */
@Component
@RequiredArgsConstructor
public class UserMapper implements IDomainMapper<User, UserEntity> {

    private final ModelMapper modelMapper;

    @Override
    public User toDomain(Optional<UserEntity> entity) {
        return entity.map(e -> modelMapper.map(e, User.class)).orElse(null);
    }

    @Override
    public UserEntity toEntity(Optional<User> domain) {
        return domain.map(d -> modelMapper.map(d, UserEntity.class)).orElse(null);
    }
}
