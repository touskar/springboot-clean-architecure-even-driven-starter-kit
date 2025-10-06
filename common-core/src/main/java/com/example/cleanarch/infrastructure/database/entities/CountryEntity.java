package com.example.cleanarch.common.infrastructure.database.entities;

import com.example.cleanarch.common.domain.entities.Country;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import com.example.cleanarch.common.infrastructure.utils.UlidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(
    name = "countries",
    indexes = {
        @Index(name = "idx_country_code", columnList = "code"),
        @Index(name = "idx_country_iso2", columnList = "iso2"),
        @Index(name = "idx_country_status", columnList = "status"),
        @Index(name = "idx_country_continent", columnList = "continent")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CountryEntity {
    @Id
    private String id;

    @Column(nullable = false)
    private String name; // Full name

    @Column(nullable = false)
    private String shortName; // Short name

    @Column(nullable = false)
    private String longName; // Long official name

    @Column(nullable = false, unique = true, length = 3)
    private String code; // ISO3 code

    @Column(nullable = false, unique = true, length = 2)
    private String iso2; // ISO2 code

    @Column(nullable = false)
    private String callCode; // Phone calling code

    @Column(nullable = false)
    private String continent; // Continent name

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
