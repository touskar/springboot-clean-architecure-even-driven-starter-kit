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
 * Generic User entity for Clean Architecture starter kit
 * Includes basic authentication/authorization fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private String id; // ULID
    private String username;
    private String name;
    private String email;
    private String password; // BCrypt hashed
    private String phoneNumber;

    @Builder.Default
    private Maybe<String> address = Maybe.empty();

    // Relationships - Object references instead of IDs
    private Country country;
    private StatusEntityEnum status;

    @Builder.Default
    private List<Role> roles = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;

    // Business logic
    public boolean isActive() {
        return status == StatusEntityEnum.ACTIVE;
    }

    public boolean hasRole(String roleName) {
        return roles.stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(roleName));
    }

    public boolean hasPermission(String permissionCode) {
        return roles.stream()
            .flatMap(role -> role.getPermissions().stream())
            .anyMatch(permission -> permission.getCode().equalsIgnoreCase(permissionCode));
    }
}
