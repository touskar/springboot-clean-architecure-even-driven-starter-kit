package com.example.cleanarch.domain.services;

import com.example.cleanarch.domain.entities.User;
import com.example.cleanarch.domain.repositories.UserRepository;
import java.util.UUID;

public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        User user = new User(name, email);
        user.setId(UUID.randomUUID().toString());
        return userRepository.save(user);
    }

    public java.util.List<User> getAllUsers() {
        return userRepository.findAll();
    }
}