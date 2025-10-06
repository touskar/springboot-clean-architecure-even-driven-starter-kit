package com.example.cleanarch.infrastructure.utils;

import com.github.f4b6a3.ulid.UlidCreator;

/**
 * Utility class for generating ULID (Universally Unique Lexicographically Sortable Identifier).
 * ULIDs are sortable, URL-safe, and more readable than UUIDs.
 */
public class UlidGenerator {

    private UlidGenerator() {
        // Utility class - prevent instantiation
    }

    /**
     * Generates a new ULID as a String.
     * Format: 26 characters (0-9, A-Z)
     *
     * @return ULID string
     */
    public static String generate() {
        return UlidCreator.getUlid().toString();
    }

    /**
     * Generates a monotonic ULID.
     * Monotonic ULIDs are guaranteed to be greater than previous ones
     * when generated in the same millisecond.
     *
     * @return Monotonic ULID string
     */
    public static String generateMonotonic() {
        return UlidCreator.getMonotonicUlid().toString();
    }
}
