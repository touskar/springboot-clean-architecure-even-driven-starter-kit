package com.example.cleanarch.common.infrastructure.persistence.repository;

import com.example.cleanarch.common.domain.entities.Role;
import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.domain.repositories.IUserRepository;
import com.example.cleanarch.common.infrastructure.database.entities.UserEntity;
import com.example.cleanarch.common.infrastructure.database.repository.JpaUserRepository;
import com.example.cleanarch.common.infrastructure.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {
    private final JpaUserRepository jpaRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public User save(User user) {
        UserEntity entity = userMapper.toEntity(Optional.of(user));
        UserEntity saved = jpaRepository.save(entity);
        return userMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id).map(e -> userMapper.toDomain(Optional.of(e)));
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email).map(e -> userMapper.toDomain(Optional.of(e)));
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> userMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findAllActive() {
        return jpaRepository.findAllActive().stream()
                .map(e -> userMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    

    

    

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsByEmail(String email) {
        return jpaRepository.existsByEmail(email);
    }

    // Business methods
    @Override
    public User assignRoles(String userId, List<Role> roles) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        List<Role> currentRoles = new ArrayList<>(user.getRoles());
        roles.forEach(role -> {
            if (currentRoles.stream().noneMatch(r -> r.getId().equals(role.getId()))) {
                currentRoles.add(role);
            }
        });

        user.setRoles(currentRoles);
        user.setUpdatedAt(Instant.now());
        return save(user);
    }

    @Override
    public User removeRole(String userId, String roleId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        user.setUpdatedAt(Instant.now());
        return save(user);
    }

    @Override
    public User activateUser(String userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setStatus(StatusEntityEnum.ACTIVE);
        user.setUpdatedAt(Instant.now());
        return save(user);
    }

    @Override
    public User deactivateUser(String userId) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setStatus(StatusEntityEnum.DISABLED);
        user.setUpdatedAt(Instant.now());
        return save(user);
    }

    @Override
    public User changePassword(String userId, String newPassword) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(Instant.now());
        return save(user);
    }

    @Override
    public boolean hasRole(String userId, String roleName) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return user.getRoles().stream()
                .anyMatch(r -> r.getName().equals(roleName));
    }

    @Override
    public boolean hasPermission(String userId, String permissionCode) {
        User user = findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + userId));

        return user.getRoles().stream()
                .flatMap(role -> role.getPermissions().stream())
                .anyMatch(p -> p.getCode().equals(permissionCode));
    }
}
