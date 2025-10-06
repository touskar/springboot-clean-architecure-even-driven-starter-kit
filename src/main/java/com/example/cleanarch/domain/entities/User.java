package com.example.cleanarch.domain.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Domain model for User.
 * This is a pure domain object without any infrastructure dependencies.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id; // ULID
    private String name;
    private String email;
    private Instant createdAt;
    private Instant updatedAt;

    /**
     * Constructor for creating new users (without ID and timestamps)
     */
    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    /**
     * Business logic: Check if user has a valid email
     */
    public boolean hasValidEmail() {
        return email != null && email.contains("@");
    }

    /**
     * Business logic: Check if user is newly created
     */
    public boolean isNew() {
        return id == null;
    }
}
