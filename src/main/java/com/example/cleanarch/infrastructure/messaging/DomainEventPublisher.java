package com.example.cleanarch.infrastructure.messaging;

import com.example.cleanarch.domain.contracts.IDomainEventPublisher;
import com.example.cleanarch.domain.events.contract.DomainEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class DomainEventPublisher implements IDomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;
    private final EventRegistry eventRegistry;

    public DomainEventPublisher(ApplicationEventPublisher applicationEventPublisher, EventRegistry eventRegistry) {
        this.applicationEventPublisher = applicationEventPublisher;
        this.eventRegistry = eventRegistry;
    }

    @Override
    public void publish(DomainEvent<?> event) {
        // Publish to both Spring's event system and our custom EventRegistry
        applicationEventPublisher.publishEvent(event);
        eventRegistry.publishEvent(event);
    }
}