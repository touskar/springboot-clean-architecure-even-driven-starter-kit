package com.example.cleanarch.infrastructure.config;

import com.example.cleanarch.domain.repositories.IUserRepository;
import com.example.cleanarch.domain.services.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DomainConfig {

    @Bean
    public UserService userService(IUserRepository userRepository) {
        return new UserService(userRepository);
    }
}