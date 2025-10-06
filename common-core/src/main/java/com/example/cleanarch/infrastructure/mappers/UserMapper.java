package com.example.cleanarch.common.infrastructure.mappers;

import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.infrastructure.database.entities.UserEntity;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserMapper implements IDomainMapper<User, UserEntity> {

    private final ModelMapper modelMapper;
    private final CountryMapper countryMapper;
    private final RoleMapper roleMapper;

    @Override
    public User toDomain(Optional<UserEntity> entity) {
        if (entity.isEmpty()) {
            return null;
        }

        UserEntity userEntity = entity.get();
        User user = modelMapper.map(userEntity, User.class);

        // Map related entities
        user.setCountry(countryMapper.toDomain(Optional.ofNullable(userEntity.getCountry())));
        user.setRoles(userEntity.getRoles().stream()
                .map(role -> roleMapper.toDomain(Optional.of(role)))
                .collect(Collectors.toList()));

        // Note: AdvertiserCompany and ContentCreator mappings will be handled separately
        // to avoid circular dependencies. Use lazy loading or separate service calls.

        return user;
    }

    @Override
    public UserEntity toEntity(Optional<User> domain) {
        if (domain.isEmpty()) {
            return null;
        }

        User user = domain.get();
        UserEntity userEntity = modelMapper.map(user, UserEntity.class);

        // Map related entities
        userEntity.setCountry(countryMapper.toEntity(Optional.ofNullable(user.getCountry())));
        userEntity.setRoles(user.getRoles().stream()
                .map(role -> roleMapper.toEntity(Optional.of(role)))
                .collect(Collectors.toList()));

        return userEntity;
    }
}
