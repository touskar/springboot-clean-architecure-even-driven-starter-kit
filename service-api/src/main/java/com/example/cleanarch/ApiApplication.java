package com.example.cleanarch.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {
    "com.example.cleanarch.api",
    "com.example.cleanarch.common"
})
@EntityScan(basePackages = "com.example.cleanarch.common.infrastructure.database.entities")
@EnableJpaRepositories(basePackages = "com.example.cleanarch.common.infrastructure.persistence")
public class ApiApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiApplication.class, args);
    }
}
