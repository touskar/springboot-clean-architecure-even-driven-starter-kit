# Spring Boot Clean Architecture Event-Driven Starter Kit

A Spring Boot starter implementing Clean Architecture and automatic event-driven system.

## Event System

### How It Works

1. **Events** extend `DomainEvent<T>` - automatically discovered at startup
2. **Handlers** implement `EventHandler<T>` with `@AutoEventHandler(EventClass.class)` - automatically registered
3. **Publishing** - Use `IDomainEventPublisher` in your use cases

### Creating Events & Handlers

#### Quick Generator Script

```bash
# Make executable (first time only)
chmod +x generate-event.sh

# Show help
./generate-event.sh --help

# Create event with single handler
./generate-event.sh --event UserDeleted --entity User --handler Audit

# Create event with multiple handlers
./generate-event.sh --event OrderPlaced --entity Order --handler Email --handler Payment --handler Analytics

# Add handlers to existing event
./generate-event.sh --handler-only --event UserCreated --handler Notification --handler Slack
```

#### Manual Creation

**1. Create Event Class:**
```java
public class UserDeletedEvent extends DomainEvent<User> {
    public UserDeletedEvent(User user) {
        super(user);
    }

    @Override
    public String getAggregateId() {
        return data.getId();
    }
}
```

**2. Create Handler Class:**
```java
@Component
@AutoEventHandler(UserDeletedEvent.class)
public class AuditHandler implements EventHandler<UserDeletedEvent> {
    @Override
    public void handle(UserDeletedEvent event) {
        User user = event.getData();
        // Your logic here - user is now available
    }
}
```

**3. Publish from Use Case:**
```java
UserDeletedEvent event = new UserDeletedEvent(user);
eventPublisher.publish(event);
```

## Key Features

- **Auto-Discovery**: Events and handlers found automatically
- **Type-Safe**: Class-based registration (no strings)
- **Dynamic Package Detection**: Works with any package structure
- **Multiple Handlers**: Many handlers can listen to one event

## Running the Application

```bash
mvn spring-boot:run
```

## Test API

```bash
# Create user (triggers events)
curl -X POST http://localhost:8080/api/v1/register \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

## Requirements

- Java 17+
- Maven 3.6+