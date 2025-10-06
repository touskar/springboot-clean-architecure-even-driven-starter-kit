package com.example.cleanarch.common.infrastructure.utils;

import com.github.f4b6a3.ulid.UlidCreator;

/**
 * ULID (Universally Unique Lexicographically Sortable Identifier) Generator
 *
 * ULIDs are:
 * - 128-bit compatible with UUID
 * - Lexicographically sortable
 * - Canonically encoded as a 26 character string
 * - URL safe (Base32)
 * - Case insensitive
 * - No special characters
 */
public class UlidGenerator {

    /**
     * Generate a new ULID as String
     * @return ULID string (26 characters)
     */
    public static String generate() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * Generate a monotonic ULID (same timestamp = increment)
     * Useful for high-frequency ID generation
     * @return Monotonic ULID string
     */
    public static String generateMonotonic() {
        return UlidCreator.getMonotonicUlid().toString();
    }

    /**
     * Generate ULID with specific timestamp
     * @param timestamp Unix timestamp in milliseconds
     * @return ULID string
     */
    public static String generate(long timestamp) {
        return UlidCreator.getUlid(timestamp).toString();
    }
}
