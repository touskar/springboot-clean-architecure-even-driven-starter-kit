package com.example.cleanarch.common.infrastructure.database.entities;

import com.example.cleanarch.common.domain.entities.Hashtag;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.infrastructure.utils.UlidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.Nullable;
import java.time.Instant;

@Entity
@Table(
    name = "hashtags",
    indexes = {
        @Index(name = "idx_hashtag_status", columnList = "status"),
        @Index(name = "idx_hashtag_name", columnList = "name")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HashtagEntity {
    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String name; // Without # prefix

    @Nullable
    @Column(columnDefinition = "TEXT", nullable = true)
    private String description;

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
