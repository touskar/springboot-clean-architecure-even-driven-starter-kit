package com.example.cleanarch.api.application.handlers;

import com.example.cleanarch.api.domain.events.UserRegisteredEvent;
import com.example.cleanarch.common.domain.entities.User;
import com.example.cleanarch.common.infrastructure.messaging.EventHandler;
import com.example.cleanarch.common.infrastructure.messaging.contract.AutoEventHandler;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * API-specific handler for UserRegisteredEvent (module-specific event)
 *
 * Handles user registration events in the API service context.
 * Examples: Send welcome email, create API session, initialize user preferences
 */
@Component
@AutoEventHandler(UserRegisteredEvent.class)
public class UserServiceHandlerApi implements EventHandler<UserRegisteredEvent> {

    @Override
    public void handle(UserRegisteredEvent event) {
        User user = event.getData();
        String userId = event.getAggregateId();
        LocalDateTime occurredOn = event.getOccurredOn();

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  [API SERVICE] MODULE EVENT: UserRegisteredEvent          ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("  👤 User ID: " + userId);
        System.out.println("  📝 Name: " + user.getName());
        System.out.println("  📧 Email: " + user.getEmail());
        System.out.println("  🕐 Timestamp: " + occurredOn);
        System.out.println("╚════════════════════════════════════════════════════════════╝");

        // TODO: Add API-specific logic here
        // Examples:
        // - Send welcome email notification
        // - Create default user preferences
        // - Initialize API session/token
        // - Track user registration in analytics
    }
}
