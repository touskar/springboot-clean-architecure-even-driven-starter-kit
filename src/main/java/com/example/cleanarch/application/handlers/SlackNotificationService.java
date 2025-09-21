package com.example.cleanarch.application.handlers;

import com.example.cleanarch.domain.events.UserCreatedEvent;
import com.example.cleanarch.infrastructure.messaging.AutoEventHandler;
import com.example.cleanarch.infrastructure.messaging.EventHandler;
import org.springframework.stereotype.Component;

@Component
@AutoEventHandler(UserCreatedEvent.class)
public class SlackNotificationService implements EventHandler<UserCreatedEvent> {

    @Override
    public void handle(UserCreatedEvent event) {
        System.out.println("💬 DEBUG: SlackNotificationService handling UserCreatedEvent for: " + event.getData().getName());
        sendSlackMessage(event.getData().getName(), event.getData().getEmail(), event.getOccurredOn().toString());
    }

    private void sendSlackMessage(String name, String email, String timestamp) {
        System.out.println("💬 Slack Notification:");
        System.out.println("💬 New user registered: " + name + " (" + email + ")");
        System.out.println("💬 Time: " + timestamp);
        System.out.println("💬 Message sent to #registrations channel");
    }
}