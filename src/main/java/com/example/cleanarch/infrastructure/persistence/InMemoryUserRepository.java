package com.example.cleanarch.infrastructure.persistence;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.repositories.UserRepository;
import com.example.cleanarch.infrastructure.utils.UlidGenerator;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of UserRepository.
 * Useful for testing and demos. Uses ULID for IDs.
 */
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UlidGenerator.generate());
            user.setCreatedAt(Instant.now());
            user.setUpdatedAt(Instant.now());
        } else {
            user.setUpdatedAt(Instant.now());
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public List<User> findByNameContaining(String name) {
        return users.values().stream()
                .filter(user -> user.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return users.values().stream()
                .filter(user -> email.equals(user.getEmail()))
                .findFirst();
    }

    @Override
    public void deleteById(String id) {
        users.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return users.containsKey(id);
    }

    @Override
    public long count() {
        return users.size();
    }
}
