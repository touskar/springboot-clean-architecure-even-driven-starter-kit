package com.example.cleanarch.application.handlers;

import com.example.cleanarch.domain.events.UserCreatedEvent;
import com.example.cleanarch.infrastructure.messaging.AutoEventHandler;
import com.example.cleanarch.infrastructure.messaging.EventHandler;
import org.springframework.stereotype.Component;

@Component
@AutoEventHandler(UserCreatedEvent.class)
public class EmailService implements EventHandler<UserCreatedEvent> {

    @Override
    public void handle(UserCreatedEvent event) {
        System.out.println("📧 DEBUG: EmailService handling UserCreatedEvent for: " + event.getData().getName());
        sendWelcomeEmail(event.getData().getEmail(), event.getData().getName());
        sendVerificationEmail(event.getData().getEmail(), event.getData().getName());
    }

    private void sendWelcomeEmail(String email, String name) {
        System.out.println("📧 Sending welcome email to: " + email);
        System.out.println("📧 Subject: Welcome " + name + "!");
        System.out.println("📧 Welcome email sent successfully");
    }

    private void sendVerificationEmail(String email, String name) {
        System.out.println("✅ Sending verification email to: " + email);
        System.out.println("✅ Please verify your account, " + name);
        System.out.println("✅ Verification email sent successfully");
    }
}