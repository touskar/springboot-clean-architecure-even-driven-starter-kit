package com.example.cleanarch.common.domain.events.contract;

import java.time.LocalDateTime;

public abstract class DomainEvent<T> {
    protected final T data;

    public DomainEvent(T data) {
        this.data = data;
    }

    public abstract String getAggregateId();

    public T getData() {
        return data;
    }

    public LocalDateTime getOccurredOn() {
        return LocalDateTime.now();
    }
}