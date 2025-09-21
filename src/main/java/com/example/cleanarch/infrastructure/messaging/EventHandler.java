package com.example.cleanarch.infrastructure.messaging;

import com.example.cleanarch.domain.events.contract.DomainEvent;

public interface EventHandler<T extends DomainEvent> {
    void handle(T event);
}