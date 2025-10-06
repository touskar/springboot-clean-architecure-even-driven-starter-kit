# Spring Boot Clean Architecture Event-Driven Starter Kit

A production-ready Spring Boot starter implementing Clean Architecture with automatic event-driven system, ModelMapper integration, ULID-based IDs, and optimized database indexes.

## 🚀 Key Features

- **Auto-Discovery Event System**: Events and handlers found automatically
- **Clean Mapper Architecture**: Separate mapper classes with ModelMapper integration
- **ULID-based IDs**: Sortable, URL-safe identifiers instead of UUIDs
- **Database Indexes**: Optimized queries with strategic indexes
- **Type-Safe**: Class-based registration (no strings)
- **Clean Architecture**: Framework-agnostic domain layer with proper layering
- **Dual Repository Implementations**: Both in-memory and JPA implementations included

## 📦 Technology Stack

- **Java 17+**
- **Spring Boot 3.2.0**
- **ModelMapper 3.2.0** - Automatic object mapping
- **MapStruct 1.5.5** - Compile-time code generation (optional)
- **ULID Creator 5.2.3** - Universally Unique Lexicographically Sortable IDs
- **Lombok** - Boilerplate reduction
- **Spring Data JPA** - Database abstraction
- **H2 Database** - In-memory database for testing

## 🏗️ Architecture Overview

### Layer Structure

```
┌─────────────────────────────────────────────┐
│         Presentation Layer                   │
│  (Controllers, Presenters, ViewModels)       │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Application Layer                    │
│    (Use Cases, DTOs, Handlers)               │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│           Domain Layer                       │
│  (Entities, Repositories, Services)          │
│         ⚠️ NO framework dependencies          │
└─────────────────────────────────────────────┘
                    ↑
┌─────────────────────────────────────────────┐
│       Infrastructure Layer                   │
│ (JPA Entities, Mappers, Repositories,       │
│  Event Publishers, Configs)                  │
└─────────────────────────────────────────────┘
```

## 🗂️ Mapper Architecture

### IDomainMapper Interface

All mappers implement the `IDomainMapper<D, E>` interface:

```java
public interface IDomainMapper<D, E> {
    D toDomain(Optional<E> entity);
    E toEntity(Optional<D> domain);
}
```

### Example: UserMapper

```java
@Component
@RequiredArgsConstructor
public class UserMapper implements IDomainMapper<User, UserEntity> {
    private final ModelMapper modelMapper;

    @Override
    public User toDomain(Optional<UserEntity> entity) {
        return entity.map(e -> modelMapper.map(e, User.class)).orElse(null);
    }

    @Override
    public UserEntity toEntity(Optional<User> domain) {
        return domain.map(d -> modelMapper.map(d, UserEntity.class)).orElse(null);
    }
}
```

### ModelMapper Configuration

ModelMapper is pre-configured with strict matching:

```java
@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT)
                .setSkipNullEnabled(true)
                .setAmbiguityIgnored(false);
        return modelMapper;
    }
}
```

## 🆔 ULID Generation

### What is ULID?

ULID (Universally Unique Lexicographically Sortable Identifier) provides:
- **Sortable**: Can be sorted by generation time
- **URL-safe**: 26 characters (0-9, A-Z)
- **Compatible**: Works as String primary keys
- **Readable**: More human-friendly than UUIDs

### Usage

```java
// Automatic ULID generation in entities
@PrePersist
protected void onCreate() {
    if (id == null) {
        id = UlidGenerator.generate();
    }
    createdAt = Instant.now();
    updatedAt = Instant.now();
}

// Manual generation
String id = UlidGenerator.generate();
```

## 📊 Database Indexes

All entities include strategic indexes for optimal query performance:

```java
@Table(
    name = "users",
    indexes = {
        @Index(name = "idx_user_email", columnList = "email"),
        @Index(name = "idx_user_created_at", columnList = "createdAt")
    }
)
```

### Index Strategy

- **Foreign Keys**: All foreign key columns indexed
- **Unique Fields**: Email and other unique fields
- **Timestamps**: createdAt and updatedAt for time-based queries
- **Status Fields**: For filtering active/inactive records

## 📁 Repository Pattern

### Two Implementations Provided

#### 1. JPA Repository (Default - @Primary)

```java
@Repository
@Primary
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {
    private final JpaUserRepository jpaRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        var entity = userMapper.toEntity(Optional.of(user));
        var saved = jpaRepository.save(entity);
        return userMapper.toDomain(Optional.of(saved));
    }
}
```

#### 2. In-Memory Repository (For Testing)

```java
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UlidGenerator.generate());
            user.setCreatedAt(Instant.now());
        }
        users.put(user.getId(), user);
        return user;
    }
}
```

**To switch implementations**: Remove `@Primary` annotation from `UserRepositoryImpl`

## 🎯 Event System

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
        String userId = event.getAggregateId();
        LocalDateTime occurredOn = event.getOccurredOn();

        // Your logic here - all event data is available
    }
}
```

**3. Publish from Use Case:**
```java
UserDeletedEvent event = new UserDeletedEvent(user);
eventPublisher.publish(event);
```

## 🏛️ Clean Architecture Configuration

You have **two options** for configuring domain services:

### Option 1: Framework-Independent (Recommended for Pure Clean Architecture)

Keep domain services completely free of framework dependencies:

```java
// Domain service - NO Spring annotations
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        // Pure business logic
    }
}
```

Then configure in infrastructure layer:

```java
@Configuration
public class DomainConfig {
    @Bean
    public UserService userService(UserRepository userRepository) {
        return new UserService(userRepository);
    }
}
```

**Benefits:**
- **Framework Independence**: Domain works with any framework
- **Better Testability**: No Spring context needed for tests
- **Pure Clean Architecture**: Infrastructure depends on domain, not vice versa

### Option 2: Spring Annotations (Simpler, but couples to Spring)

Use Spring annotations directly on domain services:

```java
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User createUser(String name, String email) {
        // Business logic
    }
}
```

**Benefits:**
- **Simpler**: Less configuration needed
- **Familiar**: Standard Spring approach

**Trade-offs:**
- **Framework Coupling**: Domain depends on Spring
- **Less Flexible**: Harder to switch frameworks later

### Choose Based on Your Needs

- **Pure Clean Architecture projects**: Use Option 1
- **Spring-focused projects**: Option 2 is acceptable

## 🔧 Adding New Entities

### 1. Create Domain Entity

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    private String id; // ULID
    private String name;
    private BigDecimal price;
    private Instant createdAt;
    private Instant updatedAt;
}
```

### 2. Create JPA Entity with Indexes

```java
@Entity
@Table(
    name = "products",
    indexes = {
        @Index(name = "idx_product_name", columnList = "name"),
        @Index(name = "idx_product_created_at", columnList = "createdAt")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductEntity {
    @Id
    private String id; // ULID

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        if (id == null) {
            id = UlidGenerator.generate();
        }
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}
```

### 3. Create Mapper

```java
@Component
@RequiredArgsConstructor
public class ProductMapper implements IDomainMapper<Product, ProductEntity> {
    private final ModelMapper modelMapper;

    @Override
    public Product toDomain(Optional<ProductEntity> entity) {
        return entity.map(e -> modelMapper.map(e, Product.class)).orElse(null);
    }

    @Override
    public ProductEntity toEntity(Optional<Product> domain) {
        return domain.map(d -> modelMapper.map(d, ProductEntity.class)).orElse(null);
    }
}
```

### 4. Create Repository

```java
// Domain interface
public interface ProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
}

// JPA Repository
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
}

// Implementation
@Repository
@Primary
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final JpaProductRepository jpaRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        var entity = productMapper.toEntity(Optional.of(product));
        var saved = jpaRepository.save(entity);
        return productMapper.toDomain(Optional.of(saved));
    }
}
```

## 🚀 Running the Application

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

## 🧪 Test API

```bash
# Create user (triggers events)
curl -X POST http://localhost:8080/api/v1/register \
  -H "Content-Type: application/json" \
  -d '{"name": "John Doe", "email": "john@example.com"}'
```

## 📋 Requirements

- Java 17+
- Maven 3.6+

## 📚 Best Practices Included

1. ✅ **Separation of Concerns**: Clear layer boundaries
2. ✅ **Dependency Inversion**: Domain layer has no framework dependencies
3. ✅ **ULID over UUID**: Better performance and sortability
4. ✅ **Database Optimization**: Strategic indexes on all entities
5. ✅ **Mapper Pattern**: Clean separation between domain and persistence
6. ✅ **Event-Driven**: Decoupled business logic with domain events
7. ✅ **Timestamp Tracking**: createdAt/updatedAt on all entities
8. ✅ **Lombok Integration**: Reduced boilerplate code
9. ✅ **Optional Handling**: Safe null handling in mappers
10. ✅ **Dual Implementations**: Flexibility between in-memory and JPA

## 📝 Project Structure

```
src/main/java/com/example/cleanarch/
├── application/                    # Application layer
│   ├── dto/                       # Data Transfer Objects
│   ├── handlers/                  # Event handlers
│   └── usecases/                  # Use case implementations
├── domain/                        # Domain layer (framework-free)
│   ├── entities/                  # Domain entities
│   ├── events/                    # Domain events
│   ├── repositories/              # Repository interfaces
│   └── services/                  # Domain services
├── infrastructure/                # Infrastructure layer
│   ├── config/                    # Spring configurations
│   ├── database/entities/         # JPA entities
│   ├── mappers/                   # Domain-Entity mappers
│   ├── messaging/                 # Event system implementation
│   ├── persistence/               # Repository implementations
│   └── utils/                     # Utilities (ULID, etc.)
└── presentation/                  # Presentation layer
    ├── controllers/               # REST controllers
    ├── presenters/                # Response formatters
    └── viewmodels/                # View models
```

## 🔄 Migration from Older Versions

If you have an existing project using the old mapper pattern:

1. Add ModelMapper dependency
2. Create `IDomainMapper` interface
3. Create separate mapper classes for each entity
4. Remove embedded `toDomain()` and `fromDomain()` methods from entities
5. Update repository implementations to use mapper classes
6. Add ULID generation to `@PrePersist` methods
7. Add database indexes to `@Table` annotations

## 📄 License

MIT License - feel free to use this starter kit for your projects!

## 🤝 Contributing

Contributions are welcome! This is a starter kit meant to be copied and customized for your needs.

---

**Happy Coding!** 🚀
