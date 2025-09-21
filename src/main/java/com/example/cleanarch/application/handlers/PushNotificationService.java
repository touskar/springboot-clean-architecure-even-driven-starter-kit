package com.example.cleanarch.application.handlers;

import com.example.cleanarch.domain.events.UserCreatedEvent;
import com.example.cleanarch.infrastructure.messaging.AutoEventHandler;
import com.example.cleanarch.infrastructure.messaging.EventHandler;
import org.springframework.stereotype.Component;

@Component
@AutoEventHandler(UserCreatedEvent.class)
public class PushNotificationService implements EventHandler<UserCreatedEvent> {

    @Override
    public void handle(UserCreatedEvent event) {
        System.out.println("🔔 DEBUG: PushNotificationService handling UserCreatedEvent for: " + event.getData().getName());
        sendPushNotification(event.getData().getName(), event.getAggregateId());
        sendInAppNotification(event.getData().getName(), event.getData().getEmail());
    }

    private void sendPushNotification(String name, String userId) {
        System.out.println("🔔 Push Notification: Welcome " + name + "!");
        System.out.println("🔔 User ID: " + userId);
        System.out.println("🔔 Push notification sent to mobile device");
    }

    private void sendInAppNotification(String name, String email) {
        System.out.println("📱 In-App Notification: Account created for " + name);
        System.out.println("📱 Email: " + email);
        System.out.println("📱 In-app notification displayed");
    }
}