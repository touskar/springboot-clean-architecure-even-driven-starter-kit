package com.example.cleanarch.infrastructure.messaging;

import com.example.cleanarch.domain.events.contract.DomainEvent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

@Component
public class EventRegistry {

    private final Map<Class<?>, List<Consumer<Object>>> eventHandlers = new HashMap<>();

    public <T> void registerHandler(Class<T> eventType, Consumer<T> handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add((Consumer<Object>) handler);
    }

    public <T extends DomainEvent> void registerHandler(Class<T> eventType, EventHandler<T> handler) {
        eventHandlers.computeIfAbsent(eventType, k -> new ArrayList<>())
                    .add((Consumer<Object>) event -> handler.handle((T) event));
    }

    public void publishEvent(Object event) {
        List<Consumer<Object>> handlers = eventHandlers.get(event.getClass());
        if (handlers != null) {
            handlers.forEach(handler -> handler.accept(event));
        }
    }

    public void registerEventType(Class<?> eventType) {
        eventHandlers.putIfAbsent(eventType, new ArrayList<>());
        System.out.println("Auto-registered event type: " + eventType.getSimpleName());
    }
}