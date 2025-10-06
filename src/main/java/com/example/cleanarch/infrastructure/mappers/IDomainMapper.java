package com.example.cleanarch.infrastructure.mappers;

import java.util.Optional;

/**
 * Generic interface for domain-entity bidirectional mapping.
 * Provides a contract for converting between domain models and JPA entities.
 *
 * @param <D> Domain model type
 * @param <E> Entity (JPA) type
 */
public interface IDomainMapper<D, E> {

    /**
     * Maps entity to domain model
     *
     * @param entity JPA entity wrapped in Optional
     * @return Domain model or null if Optional is empty
     */
    D toDomain(Optional<E> entity);

    /**
     * Maps domain model to entity
     *
     * @param domain Domain model wrapped in Optional
     * @return JPA entity or null if Optional is empty
     */
    E toEntity(Optional<D> domain);
}
