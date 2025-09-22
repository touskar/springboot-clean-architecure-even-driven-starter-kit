package com.example.cleanarch.infrastructure.config;

import com.example.cleanarch.domain.repositories.UserRepository;
import com.example.cleanarch.domain.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}