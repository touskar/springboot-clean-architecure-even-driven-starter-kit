package com.example.cleanarch.domain.entities;

import com.example.cleanarch.domain.events.contract.DomainEvent;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseEntity {
    private final List<DomainEvent> domainEvents = new ArrayList<>();

    protected void addDomainEvent(DomainEvent domainEvent) {
        domainEvents.add(domainEvent);
    }

    public List<DomainEvent> getDomainEvents() {
        return List.copyOf(domainEvents);
    }

    public void clearDomainEvents() {
        domainEvents.clear();
    }
}