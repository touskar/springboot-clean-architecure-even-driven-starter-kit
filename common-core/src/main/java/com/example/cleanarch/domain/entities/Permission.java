package com.example.cleanarch.common.domain.entities;

import com.example.cleanarch.common.domain.enums.Plateform;
import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import com.example.cleanarch.common.domain.utils.Maybe;

/**
 * Domain model for Permission.
 * Represents a specific permission that can be granted to roles.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Permission {
    private String id; // ULID
    private String code;
    private String name;
    @Builder.Default
    private Maybe<String> targetEntity = Maybe.empty(); // Entity this permission applies to (e.g., "User", "Campaign")
    @Builder.Default
    private Maybe<String> description = Maybe.empty();
    @Builder.Default
    private Maybe<String> group = Maybe.empty(); // Logical grouping (e.g., "user_management", "campaign_management")
    @Builder.Default
    private Maybe<String> groupName = Maybe.empty(); // Human-readable group name

    private Plateform plateform;
    private StatusEntityEnum status;
    private Instant createdAt;
    private Instant updatedAt;
}
