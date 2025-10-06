# Spring Boot Clean Architecture & DDD Starter Kit

A production-ready multi-module Spring Boot starter implementing **Clean Architecture** and **Domain-Driven Design** principles with automatic event-driven system.

> **📌 Important Note on Branches:**
> - **`main` branch**: Single-service architecture (simpler, one module)
> - **`modular-monolith` branch**: Multi-module architecture (this branch)(in dev) - supports common-core + multiple service modules for scalable modular monolith patterns

## 🎯 Architecture Principles

- **Clean Architecture**: Framework-agnostic domain layer, dependency inversion
- **Domain-Driven Design**: Entities, repositories, domain events, ubiquitous language
- **Multi-Module Maven**: Modular monolith (common-core + service modules)
- **Event-Driven**: Domain events with automatic handler discovery
- **Three-Tier Repository**: IRepository → RepositoryImpl → JpaRepository

## 🏗️ Project Structure

```
├── common-core/                        # Shared Domain & Infrastructure
│   └── com/example/cleanarch/common/
│       ├── domain/                     # ✅ PURE DOMAIN (No framework deps)
│       │   ├── entities/               # User, Role, Permission, Country
│       │   ├── enums/                  # StatusEntityEnum, Plateform
│       │   ├── events/                 # TestSharedEvent (shared across modules)
│       │   └── repositories/           # IUserRepository (interfaces only)
│       │
│       └── infrastructure/             # Framework-specific implementations
│           ├── database/
│           │   ├── entities/           # UserEntity (JPA)
│           │   └── repository/         # JpaUserRepository (Spring Data)
│           ├── mappers/                # UserMapper (Domain ↔ Entity)
│           ├── messaging/              # Event publisher, registry
│           └── persistence/repository/ # UserRepositoryImpl
│
└── service-api/                        # REST API Module
    └── com/example/cleanarch/api/
        ├── application/                # Use Cases, DTOs, Event Handlers
        ├── domain/events/              # UserRegisteredEvent (module-specific)
        └── presentation/               # REST Controllers
```

## 🔷 Clean Architecture Layers

```
┌──────────────────────────────────────────────┐
│  Presentation (Controllers, ViewModels)      │
│            ↓ depends on ↓                    │
│  Application (Use Cases, DTOs, Handlers)     │
│            ↓ depends on ↓                    │
│  DOMAIN (Entities, IRepositories, Events)    │  ← CORE: No framework deps
│            ↑ implemented by ↑                │
│  Infrastructure (JPA, Mappers, Impl)         │
└──────────────────────────────────────────────┘
```

**Dependency Rule**: Outer layers depend on inner layers, never the reverse.

## 📁 Three-Tier Repository Pattern

### Why Three Tiers?

**Clean Architecture requires domain independence from infrastructure.**

| Tier | Name | Prefix/Suffix | Location | Purpose |
|------|------|--------------|----------|---------|
| 1 | Domain Interface | **I**Prefix | `domain/repositories/` | Business contract (pure Java) |
| 2 | Implementation | **Impl**Suffix | `infrastructure/persistence/repository/` | Bridge domain ↔ JPA |
| 3 | JPA Repository | **Jpa**Prefix | `infrastructure/database/repository/` | Spring Data JPA |

### Example: User Repository

#### 1. Domain Interface (IUserRepository)
```java
// domain/repositories/IUserRepository.java
public interface IUserRepository {
    User save(User user);
    Optional<User> findById(String id);
    Optional<User> findByEmail(String email);
    List<User> findAll();
    void deleteById(String id);

    // Business methods
    User assignRoles(String userId, List<Role> roles);
    boolean hasPermission(String userId, String permissionCode);
}
```

#### 2. Repository Implementation (UserRepositoryImpl)
```java
// infrastructure/persistence/repository/UserRepositoryImpl.java
@Component
@RequiredArgsConstructor
public class UserRepositoryImpl implements IUserRepository {
    private final JpaUserRepository jpaRepo;
    private final UserMapper mapper;

    public User save(User user) {
        UserEntity entity = mapper.toEntity(Optional.of(user));
        UserEntity saved = jpaRepo.save(entity);
        return mapper.toDomain(Optional.of(saved));
    }
}
```

#### 3. JPA Repository (JpaUserRepository)
```java
// infrastructure/database/repository/JpaUserRepository.java
@Repository
public interface JpaUserRepository extends JpaRepository<UserEntity, String> {
    Optional<UserEntity> findByEmail(String email);

    @Query("SELECT u FROM UserEntity u WHERE u.status = 'ACTIVE'")
    List<UserEntity> findAllActive();
}
```

### Data Flow
```
UseCase → IUserRepository → UserRepositoryImpl → JpaUserRepository → Database
  (app)      (domain)         (infrastructure)      (Spring Data)
```

## 🎯 Event-Driven Architecture

### Dual Event System

**1. Shared Events** (common-core): Available to all modules
```java
// common-core: com.example.cleanarch.common.domain.events
public class TestSharedEvent extends DomainEvent<TestData> {
    // Accessible from any module
}
```

**2. Module-Specific Events** (service-api): Local to module
```java
// service-api: com.example.cleanarch.api.domain.events
public class UserRegisteredEvent extends DomainEvent<User> {
    // Only for service-api module
}
```

### Auto-Discovery

Events and handlers are **automatically discovered** at startup:
- Scans `com.example.cleanarch.common.domain.events` (shared)
- Scans `{module}.domain.events` (module-specific)
- Registers all `@AutoEventHandler` annotated handlers

### Publishing Events

```java
@Component
@RequiredArgsConstructor
public class RegisterUserUseCase {
    private final IDomainEventPublisher eventPublisher;
    private final IUserRepository userRepository;

    public User execute(RegisterUserRequest request) {
        User user = User.builder()
            .name(request.getName())
            .email(request.getEmail())
            .build();

        User saved = userRepository.save(user);

        // Publish domain event
        eventPublisher.publish(new UserRegisteredEvent(saved));

        return saved;
    }
}
```

### Handling Events

```java
@Component
@AutoEventHandler(UserRegisteredEvent.class)
public class SendWelcomeEmailHandler implements EventHandler<UserRegisteredEvent> {
    @Override
    public void handle(UserRegisteredEvent event) {
        User user = event.getData();
        // Send welcome email, audit log, etc.
    }
}
```

## 🔑 Domain-Driven Design

### Entities

**Domain Entity** (Pure Java POJO):
```java
// domain/entities/User.java
@Data @Builder
public class User {
    private String id;              // ULID (26-char sortable)
    private String name;
    private String email;
    private String password;
    private Country country;        // Rich object, not ID
    private StatusEntityEnum status;
    private List<Role> roles;

    // Business logic in domain
    public boolean isActive() {
        return status == StatusEntityEnum.ACTIVE;
    }

    public boolean hasPermission(String code) {
        return roles.stream()
            .flatMap(r -> r.getPermissions().stream())
            .anyMatch(p -> p.getCode().equals(code));
    }
}
```

**JPA Entity** (Infrastructure):
```java
// infrastructure/database/entities/UserEntity.java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_email", columnList = "email")
})
public class UserEntity implements UserDetails {
    @Id
    private String id;  // ULID generated via @PrePersist

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private CountryEntity country;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles")
    private List<RoleEntity> roles;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UlidGenerator.generate();
        createdAt = Instant.now();
    }
}
```

### Mappers (Domain ↔ Infrastructure)

```java
@Component
@RequiredArgsConstructor
public class UserMapper implements IDomainMapper<User, UserEntity> {
    private final ModelMapper modelMapper;

    public User toDomain(Optional<UserEntity> entity) {
        return entity.map(e -> modelMapper.map(e, User.class)).orElse(null);
    }

    public UserEntity toEntity(Optional<User> domain) {
        return domain.map(d -> modelMapper.map(d, UserEntity.class)).orElse(null);
    }
}
```

### Value Objects

**Maybe<T>**: Custom optional type with Jackson serialization
```java
@Builder.Default
private Maybe<String> address = Maybe.empty();

// Usage
user.getAddress().orElse("Unknown");
```

### Enums

```java
public enum StatusEntityEnum {
    ACTIVE, DISABLED, DELETED
}

public enum Plateform {
    BACKOFFICE_ADMIN,
    APP_MOBILE_CLIENT,
    APP_MOBILE_MERCHANT,
    BACKOFFICE_COMPANY
}
```

## 🚀 Quick Start

### 1. Start Infrastructure
```bash
docker-compose up -d
```

### 2. Configure Environment
```bash
# Edit .env file
DB_HOST=localhost
DB_PORT=26257
REDIS_HOST=localhost
```

### 3. Run Application
```bash
./run_server.sh
# or
mvn spring-boot:run -pl service-api
```

### 4. Test
```bash
curl http://localhost:8081/actuator/health
```

## 📋 Adding New Entities (DDD Checklist)

### 1. Domain Entity (Pure Java)
```java
// domain/entities/Product.java
@Data @Builder
public class Product {
    private String id;  // ULID
    private String name;
    private BigDecimal price;

    // Business logic
    public boolean isExpensive() {
        return price.compareTo(new BigDecimal("1000")) > 0;
    }
}
```

### 2. JPA Entity (Infrastructure)
```java
// infrastructure/database/entities/ProductEntity.java
@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_name", columnList = "name")
})
public class ProductEntity {
    @Id private String id;
    private String name;
    private BigDecimal price;

    @PrePersist
    protected void onCreate() {
        if (id == null) id = UlidGenerator.generate();
    }
}
```

### 3. Domain Repository Interface
```java
// domain/repositories/IProductRepository.java
public interface IProductRepository {
    Product save(Product product);
    Optional<Product> findById(String id);
}
```

### 4. JPA Repository
```java
// infrastructure/database/repository/JpaProductRepository.java
@Repository
public interface JpaProductRepository extends JpaRepository<ProductEntity, String> {
}
```

### 5. Repository Implementation
```java
// infrastructure/persistence/repository/ProductRepositoryImpl.java
@Component
@RequiredArgsConstructor
public class ProductRepositoryImpl implements IProductRepository {
    private final JpaProductRepository jpaRepo;
    private final ProductMapper mapper;

    public Product save(Product product) {
        ProductEntity entity = mapper.toEntity(Optional.of(product));
        ProductEntity saved = jpaRepo.save(entity);
        return mapper.toDomain(Optional.of(saved));
    }
}
```

### 6. Mapper
```java
// infrastructure/mappers/ProductMapper.java
@Component
@RequiredArgsConstructor
public class ProductMapper implements IDomainMapper<Product, ProductEntity> {
    private final ModelMapper modelMapper;

    public Product toDomain(Optional<ProductEntity> entity) {
        return entity.map(e -> modelMapper.map(e, Product.class)).orElse(null);
    }

    public ProductEntity toEntity(Optional<Product> domain) {
        return domain.map(d -> modelMapper.map(d, ProductEntity.class)).orElse(null);
    }
}
```

## 🧪 Testing

### Unit Test (Domain Logic)
```java
@ExtendWith(MockitoExtension.class)
class RegisterUserUseCaseTest {
    @Mock private IUserRepository repo;
    @Mock private IDomainEventPublisher publisher;
    @InjectMocks private RegisterUserUseCase useCase;

    @Test
    void shouldRegisterUser() {
        // Given
        RegisterUserRequest request = new RegisterUserRequest("John", "john@example.com");
        when(repo.save(any())).thenReturn(User.builder().id("01ARZ3N").build());

        // When
        User result = useCase.execute(request);

        // Then
        assertThat(result).isNotNull();
        verify(publisher).publish(any(UserRegisteredEvent.class));
    }
}
```

## 🔧 Configuration

### Maven Modules
```xml
<!-- Parent POM -->
<groupId>com.example</groupId>
<artifactId>spring-clean-architecture-starter</artifactId>
<packaging>pom</packaging>

<modules>
    <module>common-core</module>
    <module>service-api</module>
</modules>
```

### Package Structure Convention
- **Domain**: `com.example.cleanarch.{module}.domain`
- **Application**: `com.example.cleanarch.{module}.application`
- **Infrastructure**: `com.example.cleanarch.common.infrastructure`
- **Presentation**: `com.example.cleanarch.{module}.presentation`

## 📚 Key Principles Applied

### 1. Dependency Inversion Principle
✅ Domain defines interfaces (IUserRepository)
✅ Infrastructure implements them (UserRepositoryImpl)
✅ Domain never depends on infrastructure

### 2. Single Responsibility Principle
✅ Domain entities: Business logic only
✅ JPA entities: Persistence only
✅ Mappers: Conversion only
✅ Use cases: One business operation

### 3. Open/Closed Principle
✅ Add new event handlers without modifying publisher
✅ Swap repository implementations without changing domain
✅ Extend entities via composition

### 4. Interface Segregation
✅ Repository interfaces define only needed methods
✅ Event handlers implement specific event types
✅ Mappers have single conversion responsibility

## 🎓 Learn More

- [Clean Architecture (Uncle Bob)](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Domain-Driven Design (Martin Fowler)](https://martinfowler.com/bliki/DomainDrivenDesign.html)
- [Event-Driven Architecture](https://martinfowler.com/articles/201701-event-driven.html)

## 📄 License

MIT License - Free to use for any project

---

**Built with Clean Architecture & DDD principles** 🏛️
