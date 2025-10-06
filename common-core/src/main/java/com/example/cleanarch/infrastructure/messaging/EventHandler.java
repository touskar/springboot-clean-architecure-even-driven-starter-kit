package com.example.cleanarch.common.infrastructure.messaging;

import com.example.cleanarch.common.domain.events.contract.DomainEvent;

public interface EventHandler<T extends DomainEvent> {
    void handle(T event);
}