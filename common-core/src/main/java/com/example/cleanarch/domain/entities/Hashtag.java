package com.example.cleanarch.common.domain.entities;

import com.example.cleanarch.common.domain.enums.StatusEntityEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import com.example.cleanarch.common.domain.utils.Maybe;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hashtag {
    private String id; // ULID
    private String name; // Without # prefix
    @Builder.Default
    private Maybe<String> description = Maybe.empty();
    private StatusEntityEnum status;
    private Instant createdAt;
    private Instant updatedAt;

    public boolean isActive() {
        return status == StatusEntityEnum.ACTIVE;
    }

    public String getHashtagWithPrefix() {
        return "#" + name;
    }
}
