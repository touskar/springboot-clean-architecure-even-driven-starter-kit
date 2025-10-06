package com.example.cleanarch.common.domain.entities;

import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Country {
    private String id; // ULID
    private String name; // Full name
    private String shortName; // Short name
    private String longName; // Long official name
    private String code; // ISO3 code (3 characters)
    private String iso2; // ISO2 code (2 characters)
    private String callCode; // Phone calling code (e.g., +1, +33)
    private String continent; // Continent name
    private StatusEntityEnum status;
    private Instant createdAt;
    private Instant updatedAt;
}
