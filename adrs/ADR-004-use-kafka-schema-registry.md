# ADR-004: Adopt Kafka with Avro and Schema Registry for Event Publishing

## Status
✅ Accepted

## Date
2025-10-01

## Context
The application currently uses Spring's ApplicationEventPublisher for event handling, which has the following limitations:
- Limited scalability in distributed environments
- No built-in persistence or replay capabilities
- Tight coupling with the application lifecycle
- No schema enforcement for event contracts
- No support for cross-service communication

## Decision
We will transition from Spring's ApplicationEventPublisher to Apache Kafka with Avro serialization and Schema Registry for event publishing, with the following key decisions:

1. **Event Transport**: Use Apache Kafka as the event streaming platform
2. **Serialization**: Use Avro for schema definition and serialization
3. **Schema Management**: Use Confluent Schema Registry for schema evolution
4. **Event Types**:
   - [LeadPromotedAvroEvent](cci:2://file:///home/jhon/workspace/java-spring-hexagonal-reactive-architecture/src/main/java/com/crm/validation/lead/avro/LeadPromotedAvroEvent.java:15:0-893:1): When a lead is successfully promoted
   - `LeadRejectedEvent`: When a lead is rejected during validation

## Technical Implementation

### Schema Definition
- Schemas are defined in [.avsc](cci:7://file:///home/jhon/workspace/java-spring-hexagonal-reactive-architecture/src/main/avro/com.crm.validation.lead.avro/Lead.avsc:0:0-0:0) files under `src/main/resources/avro/`
- Each event type has its own schema file
- Schema versioning is managed through the Schema Registry

### Producer Side
- [KafkaProducer](cci:2://file:///home/jhon/workspace/java-spring-hexagonal-reactive-architecture/src/main/java/com/crm/validation/lead/infrastructure/adapter/in/kafka/producer/KafkaProducer.java:6:0-20:1) service handles publishing events to Kafka
- Uses `KafkaTemplate` with Avro serializers
- Events are mapped from domain models to Avro records using [AvroMappers](cci:2://file:///home/jhon/workspace/java-spring-hexagonal-reactive-architecture/src/main/java/com/crm/validation/lead/infrastructure/adapter/in/commons/mappers/AvroMappers.java:8:0-102:1)
- Configured to automatically register schemas with the Schema Registry

### Consumer Side
- [KafkaConsumer](cci:2://file:///home/jhon/workspace/java-spring-hexagonal-reactive-architecture/src/main/java/com/crm/validation/lead/infrastructure/adapter/in/kafka/consumer/KafkaConsumer.java:10:0-22:1) listens to relevant topics
- Uses `@KafkaListener` for message consumption
- Deserializes messages using Avro deserializers
- Handles schema evolution through Schema Registry

## Consequences

### Benefits
- **Schema Evolution**: Backward and forward compatibility through Schema Registry
- **Decoupling**: Producers and consumers are decoupled
- **Scalability**: Horizontal scaling of event consumers
- **Durability**: Events are persisted and can be replayed
- **Cross-service**: Enables event-driven architecture across services

### Trade-offs
- **Complexity**: Added infrastructure complexity with Kafka and Schema Registry
- **Latency**: Increased latency compared to in-memory event publishing
- **Operational Overhead**: Requires monitoring and maintenance of Kafka clusters

## Migration Strategy
1. Deploy Schema Registry and Kafka infrastructure
2. Deploy updated services with dual-write (Spring Events + Kafka)
3. Migrate consumers to read from Kafka
4. Remove Spring Events implementation once migration is complete

## Related ADRs
- ADR-001: Adopt Hexagonal Architecture
- ADR-002: Domain Event Pattern

## Author
Jhon