package com.example.cleanarch.common.domain.entities;

import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import com.example.cleanarch.common.domain.utils.Maybe;

/**
 * Domain model for Role.
 * A role is a collection of permissions that can be assigned to users.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Role {
    private String id; // ULID
    private String name;
    @Builder.Default
    private Maybe<String> description = Maybe.empty();
    @Builder.Default
    private List<Permission> permissions = new ArrayList<>();
    private StatusEntityEnum status;
    private Instant createdAt;
    private Instant updatedAt;
}
