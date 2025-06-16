# ADR-001: Adopt Hexagonal Architecture (Ports and Adapters)

## Status
**Accepted** - 2025-06-09

## Context

Our company uses a custom CRM where sales-qualified leads are stored with basic personal data (national ID, birthdate, name, email, etc.). To convert a lead into a prospect, agents currently run manual checks against several external systems. We need to automate and expose these checks as a reactive, on-demand service.

- **Lead Data Source**
    - Reads lead records (ID, name, birthdate, email, …) from the CRM database.

- **Parallel External Validations**
    1. **National Registry Check**
        - Verify the person exists in the government registry and that key fields match our local DB.
    2. **Judicial Records Check**
        - Ensure there are no active judicial records in the national archives.

  Both checks run concurrently to minimize response time.

- **Sequential Scoring Validation**
    3. **Internal Qualification Score**
        - Once the first two checks succeed, send aggregated data to our internal scoring service (returns a 0–100 score).
        - Only leads scoring > 60 are eligible for prospect conversion.

- **Service Requirements**
    - Expose a non-blocking API endpoint that agents can call to trigger validations on demand.
    - Implement a Hexagonal (Ports & Adapters) design so each external system is a swappable adapter (stubbed for local dev, real HTTP client for production).
    - Use Project Reactor (`Mono`/`Flux`) to orchestrate parallel and dependent calls.
    - Ensure high testability with JUnit 5, Mockito, and Reactor Test.
    - Build with Maven, deployable to containers, cloud, or on-prem environments without major refactoring.

---

### Current Challenges
1. **Technology Uncertainty**: We may need to switch from MySQL to PostgreSQL or NoSQL databases based on scaling requirements
2. **Integration Complexity**: Multiple third-party services with different APIs and potential vendor changes
3. **Testing Difficulties**: Current monolithic approach makes unit testing complex due to tight coupling
4. **Deployment Flexibility**: Need to support different infrastructure setups for various clients
5. **Team Growth**: Expecting to scale the development team, requiring clear architectural boundaries

### Evaluated Alternatives

#### 1. Layered Architecture (N-Tier)
- **Pros**: Simple, well-understood, good separation of concerns
- **Cons**: Risk of business logic leaking into presentation/data layers, tight coupling between layers, difficult to test in isolation

#### 2. Clean Architecture
- **Pros**: Excellent separation of concerns, testable, framework independence
- **Cons**: More complex setup, potential over-engineering for current scope, steep learning curve

#### 3. Microservices Architecture
- **Pros**: Technology diversity, independent deployments, scalability
- **Cons**: Premature for current team size, operational complexity, distributed system challenges

#### 4. Hexagonal Architecture (Ports and Adapters)
- **Pros**: Clear separation between business logic and external concerns, excellent testability, technology agnostic, supports multiple interfaces
- **Cons**: Initial learning curve, more upfront design work, potential over-engineering for simple CRUD operations

## Decision

We will adopt **Hexagonal Architecture (Ports and Adapters)** for the User Management Service.

### Rationale

#### Primary Drivers
1. **Testability Requirements**: Business logic can be tested in complete isolation without external dependencies
2. **Technology Flexibility**: Core business logic remains unchanged when switching databases, message queues, or external APIs
3. **Integration Needs**: Clear contracts (ports) for all external integrations make vendor switching seamless
4. **Multiple Interface Support**: Architecture naturally supports REST, GraphQL, CLI, and future interfaces without code duplication

#### Technical Benefits
- **Dependency Inversion**: Core business logic depends only on abstractions, not implementations
- **Single Responsibility**: Each adapter has one reason to change (the external system it integrates with)
- **Open/Closed Principle**: New adapters can be added without modifying existing code
- **Framework Independence**: Business logic is not coupled to Spring Boot, JPA, or any specific framework

#### Business Benefits
- **Faster Development**: Once established, new features can be developed and tested rapidly
- **Risk Mitigation**: Vendor lock-in risks are minimized through clear abstraction layers
- **Team Scaling**: Clear architectural boundaries enable multiple teams to work independently
- **Maintenance**: Bugs in external integrations don't affect core business logic

## Implementation Plan

### Phase 1: Core Setup (Sprint 1-2)
- Define domain models and business rules
- Create primary ports (UserService interface)
- Implement core business logic
- Set up comprehensive unit tests

### Phase 2: Initial Adapters (Sprint 3-4)
- REST API adapter for user management
- JPA adapter for MySQL persistence
- Email notification adapter
- Integration tests

### Phase 3: Additional Interfaces (Sprint 5-6)
- GraphQL adapter
- Audit logging adapter
- Performance monitoring

### Architecture Guidelines

#### Port Design Principles
- Ports should be designed from the application's perspective, not the adapter's
- Use domain language in port interfaces, not technical terminology
- Keep ports focused and cohesive (Interface Segregation Principle)

#### Adapter Responsibilities
- Handle all technology-specific concerns (serialization, protocol handling, error mapping)
- Translate between domain models and external system formats
- Manage external system lifecycle (connections, retries, circuit breakers)

#### Testing Strategy
- **Unit Tests**: Test business logic through primary ports with mock secondary ports
- **Integration Tests**: Test individual adapters against real external systems
- **End-to-End Tests**: Test complete flows through primary adapters

## Consequences

### Positive
- **High Testability**: Business logic can achieve 100% test coverage without external dependencies
- **Technology Agnostic**: Can easily migrate between frameworks, databases, and external services
- **Clear Boundaries**: Explicit separation between business logic and technical concerns
- **Parallel Development**: Teams can work on different adapters simultaneously
- **Documentation**: Port interfaces serve as clear contracts and documentation

### Negative
- **Initial Complexity**: More interfaces and classes than simpler architectures
- **Learning Curve**: Team needs to understand hexagonal principles and dependency inversion
- **Over-Engineering Risk**: Simple CRUD operations may seem unnecessarily complex
- **Upfront Design**: Requires more architectural planning before coding begins

### Mitigation Strategies
- **Training**: Conduct architecture workshops and code review sessions
- **Documentation**: Maintain clear examples and patterns for common scenarios
- **Gradual Adoption**: Start with core features and expand the pattern incrementally
- **Code Generation**: Use templates/generators for repetitive adapter code

## Monitoring and Review

### Success Metrics
- **Test Coverage**: Maintain >90% unit test coverage for business logic
- **Integration Time**: New external system integrations should take <2 days
- **Bug Isolation**: <10% of external system issues should affect core business logic
- **Development Velocity**: Feature development time should decrease after initial setup

### Review Schedule
- **3-month review**: Assess team adoption and identify pain points
- **6-month review**: Evaluate architecture effectiveness and consider refinements
- **Annual review**: Compare with alternative architectures and industry trends

## References
- [Hexagonal Architecture by Alistair Cockburn](https://alistair.cockburn.us/hexagonal-architecture/)
- [Clean Architecture by Robert C. Martin](https://blog.cleancoder.com/uncle-bob/2012/08/13/the-clean-architecture.html)
- [Ports and Adapters Pattern](https://herbertograca.com/2017/09/14/ports-adapters-architecture/)
