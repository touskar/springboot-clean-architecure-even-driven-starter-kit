package com.example.cleanarch.api.application.handlers;

import com.example.cleanarch.common.domain.events.TestSharedEvent;
import com.example.cleanarch.common.infrastructure.messaging.EventHandler;
import com.example.cleanarch.common.infrastructure.messaging.contract.AutoEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@AutoEventHandler(TestSharedEvent.class)
public class TestSharedEventHandler implements EventHandler<TestSharedEvent> {

    @Override
    public void handle(TestSharedEvent event) {
        TestSharedEvent.TestData data = event.getData();
        String id = event.getAggregateId();
        LocalDateTime occurredOn = event.getOccurredOn();

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  [API SERVICE] SHARED EVENT RECEIVED: TestSharedEvent     ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("  📝 Test ID: " + id);
        System.out.println("  💬 Message: " + data.getMessage());
        System.out.println("  🕐 Occurred: " + occurredOn);
        System.out.println("╚════════════════════════════════════════════════════════════╝");
    }
}
