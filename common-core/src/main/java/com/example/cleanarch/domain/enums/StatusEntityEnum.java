package com.example.cleanarch.common.domain.enums;

/**
 * Entity status for soft delete and disable functionality
 */
public enum StatusEntityEnum {
    /**
     * Entity is active and operational
     */
    ACTIVE,

    /**
     * Entity is soft-deleted (can be restored)
     */
    DELETED,

    /**
     * Entity is disabled (temporary suspension)
     */
    DISABLED
}
