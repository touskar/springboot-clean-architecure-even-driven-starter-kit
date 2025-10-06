package com.example.cleanarch.common.domain.contracts;

import com.example.cleanarch.common.domain.events.contract.DomainEvent;

public interface IDomainEventPublisher {
    void publish(DomainEvent<?> event);
}