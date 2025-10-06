package com.example.cleanarch.infrastructure.persistence;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.repositories.UserRepository;
import com.example.cleanarch.infrastructure.mappers.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * JPA-based implementation of UserRepository.
 * Uses UserMapper to convert between domain and entity models.
 *
 * Note: @Primary annotation makes this the default implementation
 * when both InMemoryUserRepository and UserRepositoryImpl are available.
 * Remove @Primary to use InMemoryUserRepository instead.
 */
@Repository
@Primary
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final JpaUserRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(Optional.of(user));
        var saved = jpaRepository.save(entity);
        return userMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaRepository.findById(id)
                .map(e -> userMapper.toDomain(Optional.of(e)));
    }

    @Override
    public List<User> findAll() {
        return jpaRepository.findAll().stream()
                .map(e -> userMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findByNameContaining(String name) {
        return jpaRepository.findByNameContainingIgnoreCase(name).stream()
                .map(e -> userMapper.toDomain(Optional.of(e)))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return jpaRepository.findByEmail(email)
                .map(e -> userMapper.toDomain(Optional.of(e)));
    }

    @Override
    public void deleteById(String id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }
}
