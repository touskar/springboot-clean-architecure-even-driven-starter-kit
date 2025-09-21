package com.example.cleanarch.domain.contracts;

import com.example.cleanarch.domain.events.contract.DomainEvent;

public interface IDomainEventPublisher {
    void publish(DomainEvent<?> event);
}