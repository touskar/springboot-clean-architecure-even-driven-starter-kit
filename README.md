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

This project implements a **three-tier repository architecture** that provides clean separation between domain, persistence, and database layers.

### Repository Architecture Overview

```
┌─────────────────────────────────────────────────────────────┐
│                     DOMAIN LAYER                             │
│  ┌────────────────────────────────────────────────────┐    │
│  │  IUserRepository (interface)                       │    │
│  │  - Defines domain contract (business operations)   │    │
│  │  - Location: domain/repositories/                  │    │
│  │  - Prefix: "I" (e.g., IUserRepository)            │    │
│  │  - Returns: Domain entities (User, Role, etc.)    │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                           ↑
                           │ implements
                           │
┌─────────────────────────────────────────────────────────────┐
│              INFRASTRUCTURE - PERSISTENCE LAYER              │
│  ┌────────────────────────────────────────────────────┐    │
│  │  UserRepositoryImpl (implementation)               │    │
│  │  - Bridges domain and infrastructure              │    │
│  │  - Location: infrastructure/persistence/repository/│    │
│  │  - Suffix: "Impl" (e.g., UserRepositoryImpl)     │    │
│  │  - Uses: Mapper + JPA Repository                  │    │
│  │  - Converts: Entity ↔ Domain                      │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
                           ↓
                      uses / delegates to
                           ↓
┌─────────────────────────────────────────────────────────────┐
│              INFRASTRUCTURE - DATABASE LAYER                 │
│  ┌────────────────────────────────────────────────────┐    │
│  │  JpaUserRepository (Spring Data JPA)               │    │
│  │  - Spring Data JPA interface                       │    │
│  │  - Location: infrastructure/database/repository/   │    │
│  │  - Prefix: "Jpa" (e.g., JpaUserRepository)       │    │
│  │  - Extends: JpaRepository<UserEntity, String>     │    │
│  │  - Works with: JPA Entities (UserEntity)          │    │
│  └────────────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────────────┘
```

### Three Repository Types Explained

#### 1. Domain Repository Interface (IUserRepository)

**Purpose**: Defines the contract for data operations in business terms.

**Location**: `domain/repositories/IUserRepository.java`

**Naming Convention**: Prefix with "I" (Interface)

```java
/**
 * Domain repository interface - defines business operations
 * Note: "I" prefix indicates this is an interface
 */
public interface IUserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(String id);
}
```

**Key Characteristics**:
- ✅ Framework-independent (pure Java interface)
- ✅ Works with domain entities (`User`, `Role`, etc.)
- ✅ Defines business operations
- ✅ Part of the domain layer
- ❌ No JPA annotations
- ❌ No implementation details

#### 2. Repository Implementation (UserRepositoryImpl)

**Purpose**: Bridges domain layer with infrastructure (JPA).

**Location**: `infrastructure/persistence/repository/UserRepositoryImpl.java`

**Naming Convention**: Suffix with "Impl" (Implementation)

```java
/**
 * Repository implementation - bridges domain with JPA
 * Converts between domain entities and JPA entities using mapper
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {

    // Inject JPA repository and mapper
    private final JpaUserRepository jpaUserRepository;
    private final UserMapper userMapper;

    @Override
    public User save(User user) {
        // 1. Convert domain entity to JPA entity
        UserEntity entity = userMapper.toEntity(Optional.of(user));

        // 2. Save using JPA repository
        UserEntity saved = jpaUserRepository.save(entity);

        // 3. Convert back to domain entity
        return userMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<User> findById(String id) {
        return jpaUserRepository.findById(id)
                .map(entity -> userMapper.toDomain(Optional.of(entity)));
    }

    @Override
    public List<User> findAll() {
        return jpaUserRepository.findAll().stream()
                .map(entity -> userMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }
}
```

**Key Characteristics**:
- ✅ Implements domain repository interface
- ✅ Uses `@Component` (Spring bean)
- ✅ Delegates database operations to JPA repository
- ✅ Uses mapper to convert Entity ↔ Domain
- ✅ Returns domain entities to upper layers
- ✅ Location: `infrastructure/persistence/repository/`

#### 3. JPA Repository (JpaUserRepository)

**Purpose**: Spring Data JPA interface for database operations.

**Location**: `infrastructure/database/repository/JpaUserRepository.java`

**Naming Convention**: Prefix with "Jpa"

```java
/**
 * Spring Data JPA repository - handles database operations
 * Note: "Jpa" prefix distinguishes from domain repository interface
 */
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {

    // Spring Data JPA automatically implements these methods
    Optional<UserEntity> findByEmail(String email);
    List<UserEntity> findByStatus(String status);

    // Custom query methods
    @Query("SELECT u FROM UserEntity u WHERE u.createdAt > :date")
    List<UserEntity> findRecentUsers(@Param("date") Instant date);
}
```

**Key Characteristics**:
- ✅ Extends `JpaRepository<UserEntity, String>`
- ✅ Works with JPA entities (`UserEntity`)
- ✅ Uses `@Repository` annotation
- ✅ Provides automatic CRUD operations
- ✅ Supports custom query methods
- ✅ Location: `infrastructure/database/repository/`
- ❌ NOT injected directly into use cases (only into repository implementations)

### Why Three Layers?

#### Separation of Concerns

1. **Domain Layer** (`IUserRepository`)
   - Defines WHAT operations are needed (business contract)
   - No knowledge of HOW data is stored
   - Framework-independent

2. **Persistence Layer** (`UserRepositoryImpl`)
   - Implements HOW domain operations work
   - Translates between domain and database
   - Uses mappers for conversion

3. **Database Layer** (`JpaUserRepository`)
   - Handles actual database operations
   - Spring Data JPA magic
   - Only knows about JPA entities

#### Clean Architecture Benefits

```
Use Case → IUserRepository → UserRepositoryImpl → JpaUserRepository → Database
   ↓            ↓                   ↓                    ↓
 Domain      Domain            Infrastructure      Infrastructure
  Layer      Contract           Implementation         (Spring)
```

- **Testability**: Mock `IUserRepository` in use case tests
- **Flexibility**: Swap implementations without changing domain
- **Independence**: Domain doesn't depend on JPA or Spring

### Naming Conventions Summary

| Repository Type | Prefix/Suffix | Example | Location |
|----------------|---------------|---------|----------|
| Domain Interface | **I** prefix | `IUserRepository` | `domain/repositories/` |
| Repository Implementation | **Impl** suffix | `UserRepositoryImpl` | `infrastructure/persistence/repository/` |
| JPA Repository | **Jpa** prefix | `JpaUserRepository` | `infrastructure/database/repository/` |

### Alternative Implementation: In-Memory Repository

You can also provide alternative implementations for testing:

```java
/**
 * In-memory repository implementation for testing
 * Implements same domain interface but uses HashMap
 */
@Component
@RequiredArgsConstructor
public class InMemoryUserRepository implements IUserRepository {

    private final Map<String, User> users = new ConcurrentHashMap<>();

    @Override
    public User save(User user) {
        if (user.getId() == null) {
            user.setId(UlidGenerator.generate());
            user.setCreatedAt(Instant.now());
        }
        user.setUpdatedAt(Instant.now());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}
```

**To switch implementations**: Use `@Primary` annotation on the implementation you want to be the default, or use `@Qualifier` when injecting.

### Data Flow Example

```java
// 1. Use Case calls domain repository interface
@Component
@RequiredArgsConstructor
public class CreateUserUseCase {
    private final IUserRepository userRepository; // Domain interface

    public User execute(CreateUserRequest request) {
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .build();

        // Calls domain interface - no knowledge of JPA
        return userRepository.save(user);
    }
}

// 2. UserRepositoryImpl handles the call
@Component
public class UserRepositoryImpl implements IUserRepository {

    public User save(User user) {
        // Convert: Domain → Entity
        UserEntity entity = userMapper.toEntity(Optional.of(user));

        // Call JPA repository
        UserEntity saved = jpaUserRepository.save(entity);

        // Convert: Entity → Domain
        return userMapper.toDomain(Optional.of(saved));
    }
}

// 3. JpaUserRepository executes database operation
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    // Spring Data JPA handles the actual SQL
}
```

### Key Takeaways

✅ **Domain interfaces** (`IUserRepository`) define business operations
✅ **Repository implementations** (`UserRepositoryImpl`) bridge domain and infrastructure
✅ **JPA repositories** (`JpaUserRepository`) handle database operations
✅ **Mappers** convert between domain entities and JPA entities
✅ **Use cases** only depend on domain interfaces, never on JPA repositories directly

This architecture ensures clean separation, testability, and flexibility to change implementations without affecting business logic.

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

### 4. Create Repository (Three Layers)

#### a. Domain Repository Interface

**Location**: `domain/repositories/IProductRepository.java`

```java
/**
 * Domain repository interface for Product
 * Note: "I" prefix following naming convention
 */
public interface IProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
    List<Product> findAll();
    void deleteById(String id);
}
```

#### b. JPA Repository

**Location**: `infrastructure/database/repository/JpaProductRepository.java`

```java
/**
 * Spring Data JPA repository for ProductEntity
 * Note: "Jpa" prefix distinguishes from domain repository
 */
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
    // Custom query methods
    List<ProductEntity> findByPriceGreaterThan(BigDecimal price);
}
```

#### c. Repository Implementation

**Location**: `infrastructure/persistence/repository/ProductRepositoryImpl.java`

```java
/**
 * Repository implementation - bridges domain and JPA
 * Note: "Impl" suffix indicates implementation class
 */
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements IProductRepository {

    private final JpaProductRepository jpaProductRepository;
    private final ProductMapper productMapper;

    @Override
    public Product save(Product product) {
        // Convert domain → entity
        ProductEntity entity = productMapper.toEntity(Optional.of(product));

        // Save via JPA
        ProductEntity saved = jpaProductRepository.save(entity);

        // Convert entity → domain
        return productMapper.toDomain(Optional.of(saved));
    }

    @Override
    public Optional<Product> findById(String id) {
        return jpaProductRepository.findById(id)
                .map(entity -> productMapper.toDomain(Optional.of(entity)));
    }

    @Override
    public List<Product> findAll() {
        return jpaProductRepository.findAll().stream()
                .map(entity -> productMapper.toDomain(Optional.of(entity)))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteById(String id) {
        jpaProductRepository.deleteById(id);
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
│
├── domain/                        # Domain layer (framework-free)
│   ├── entities/                  # Domain entities (User, Role, etc.)
│   ├── events/                    # Domain events
│   ├── repositories/              # Repository INTERFACES
│   │   └── IUserRepository.java   # Interface with "I" prefix
│   └── services/                  # Domain services
│
├── infrastructure/                # Infrastructure layer
│   ├── config/                    # Spring configurations
│   │   ├── DomainConfig.java     # Domain bean configuration
│   │   └── ModelMapperConfig.java # ModelMapper configuration
│   │
│   ├── database/                  # Database-related infrastructure
│   │   ├── entities/              # JPA entities (UserEntity, etc.)
│   │   └── repository/            # Spring Data JPA repositories
│   │       └── JpaUserRepository.java  # JPA interface with "Jpa" prefix
│   │
│   ├── mappers/                   # Domain ↔ Entity mappers
│   │   ├── IDomainMapper.java     # Generic mapper interface
│   │   └── UserMapper.java        # Entity-Domain converter
│   │
│   ├── messaging/                 # Event system implementation
│   │   ├── DomainEventPublisher.java
│   │   ├── EventRegistry.java
│   │   └── EventRegistrationProcessor.java
│   │
│   ├── persistence/               # Repository implementations
│   │   ├── repository/            # Repository implementation classes
│   │   │   └── UserRepositoryImpl.java  # Implementation with "Impl" suffix
│   │   └── InMemoryUserRepository.java  # Alternative implementation
│   │
│   └── utils/                     # Utility classes
│       └── UlidGenerator.java     # ULID generation
│
└── presentation/                  # Presentation layer
    ├── controllers/               # REST controllers
    ├── presenters/                # Response formatters
    └── viewmodels/                # View models
```

### Directory Organization Explained

#### Domain Layer (`domain/`)
- **`entities/`**: Pure Java domain models (no JPA annotations)
- **`repositories/`**: Repository interfaces (prefixed with "I")
  - Example: `IUserRepository`, `IRoleRepository`
  - Defines business operations
  - No implementation details

#### Infrastructure Layer (`infrastructure/`)
- **`database/`**: Database-specific code
  - **`entities/`**: JPA entities with database annotations
  - **`repository/`**: Spring Data JPA repositories (prefixed with "Jpa")
    - Example: `JpaUserRepository`, `JpaRoleRepository`
    - Extends `JpaRepository<Entity, String>`

- **`persistence/`**: Repository implementations
  - **`repository/`**: Implementation classes (suffixed with "Impl")
    - Example: `UserRepositoryImpl`, `RoleRepositoryImpl`
    - Bridges domain and JPA
    - Uses mappers for conversion

- **`mappers/`**: Conversion between domain and JPA entities
  - All implement `IDomainMapper<D, E>`
  - Uses ModelMapper for automatic field mapping

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
