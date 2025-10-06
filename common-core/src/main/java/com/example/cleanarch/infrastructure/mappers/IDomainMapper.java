package com.example.cleanarch.common.infrastructure.mappers;

import java.util.Optional;

/**
 * Generic interface for domain-entity bidirectional mapping.
 *
 * @param <D> Domain model type
 * @param <E> Entity (JPA) type
 */
public interface IDomainMapper<D, E> {

    /**
     * Maps entity to domain model
     *
     * @param entity JPA entity
     * @return Domain model
     */
    D toDomain(Optional<E> entity);

    /**
     * Maps domain model to entity
     *
     * @param domain Domain model
     * @return JPA entity
     */
    E toEntity(Optional<D> domain);
}
