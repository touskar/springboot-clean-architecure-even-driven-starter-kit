package com.example.cleanarch.infrastructure.database.entities;

import com.example.cleanarch.infrastructure.utils.UlidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * JPA Entity for User.
 * Includes ULID generation, timestamps, and database indexes.
 */
@Entity
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_created_at", columnList = "createdAt")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEntity {

    @Id
    private String id; // ULID

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    /**
     * JPA lifecycle callback - executed before entity is persisted
     */
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UlidGenerator.generate();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    /**
     * JPA lifecycle callback - executed before entity is updated
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
