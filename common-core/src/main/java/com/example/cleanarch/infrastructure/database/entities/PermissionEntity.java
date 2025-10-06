package com.example.cleanarch.common.infrastructure.database.entities;

import com.example.cleanarch.common.domain.enums.Plateform;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.infrastructure.utils.UlidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;

import java.time.Instant;

/**
 * JPA Entity for Permission.
 * Includes ULID generation, timestamps, and database indexes.
 */
@Entity
@Table(
    name = "permissions",
    indexes = {
        @Index(name = "idx_permission_code", columnList = "code"),
        @Index(name = "idx_permission_plateform", columnList = "plateform"),
        @Index(name = "idx_permission_group", columnList = "groupCode"),
        @Index(name = "idx_permission_status", columnList = "status"),
        @Index(name = "idx_permission_target_entity", columnList = "targetEntity")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionEntity {

    @Id
    private String id; // ULID

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String name;

    @Nullable
    @Column(nullable = true)
    private String targetEntity; // Entity this permission applies to

    @Nullable
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

    @Nullable
    @Column(name = "groupCode", nullable = true)
    private String group; // Logical grouping code

    @Nullable
    @Column(nullable = true)
    private String groupName; // Human-readable group name

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Plateform plateform;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEntityEnum status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UlidGenerator.generate();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
