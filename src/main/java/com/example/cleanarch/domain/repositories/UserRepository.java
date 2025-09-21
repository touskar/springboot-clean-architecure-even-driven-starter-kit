package com.example.cleanarch.domain.repositories;

import com.example.cleanarch.domain.entities.User;
import java.util.List;
import java.util.Optional;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(String id);
    List<User> findAll();
    List<User> findByNameContaining(String name);
    Optional<User> findByEmail(String email);
    void deleteById(String id);
    boolean existsById(String id);
    long count();
}