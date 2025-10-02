# crm-validation java hexagonal architecture & reactive

## How to start the project

### Start Docker containers

```bash
docker compose up -d
```

Otheer commands to manage the containers:

```bash
docker compose ps
docker logs -f leads-db
```

### Stop Docker containers

```bash
docker compose down
```

### Clean up Docker containers

```bash
docker compose down --volumes --remove-orphans
docker compose down --rmi all
```


## Connect to the database

- contraseña: secret

```bash
psql -h localhost -p 5432 -U leaduser -d leadsdb
```

# Run the application


Run maven install to generate the objects

```bash
mvn clean install
```

Compile and package the source code, and then you will be able to run the generated jar,
for see those fo to the target directory. 

```bash
mvn clean package
java -jar target/crm-validation-1.0-SNAPSHOT.jar
```

The application has 2 main entry points:

## CLI client

The command line interface will start automatically when the application starts.

It will start asking you for the lead information, then it will validate it, and finally show you the results by console messages.

To execute the CLI client, you can run the application with the following command:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--app.cli.enabled=true"
```

## REST API

You can find the SWAGGER specification once the application is running at:

```bash
http://localhost:8080/swagger-ui.html
```

You can also use the REST API to send leads to the application. The API is documented in the SWAGGER specification.

```bash
curl -X 'POST' \
'http://localhost:8080/api/leads/validate' \
-H 'accept: */*' \
-H 'Content-Type: application/json' \
-d '{
    "id": "12",
    "name": "j",
    "birthdate": "1990-06-13",
    "email": "a@gmail.com",
    "phoneNumber": "+23342",
    "documentType": "CC",
    "documentNumber": 1234
}'
```

In a success execution you will se a response like this:

```json
{
  "lead": {
    "id": "12",
    "name": "j",
    "birthdate": "1990-06-13",
    "state": "PROSPECT",
    "email": "a@gmail.com",
    "phoneNumber": "+23342",
    "documentType": "CC",
    "documentNumber": 1234
  },
  "validations": {
    "errors": [],
    "valid": true,
    "allErrosInString": ""
  }
}
```

### Validate schema registry

You can validate the schema registry at:

```bash

// Get all subjects
curl -s http://localhost:8081/subjects | jq .

// Get all versions of a subject
curl -s http://localhost:8081/subjects/leads-value/versions | jq .

// Get latest version metadata (shows the schema and its ID)
curl -s http://localhost:8081/subjects/leads-value/versions/latest | jq .

// If the latest metadata says "id": 21 (example), fetch by ID:
curl -s http://localhost:8081/schemas/ids/21 | jq .

```

### Manually execute flyway migrations

If you want to manually execute the flyway migrations, you can do it with the following command:

```bash
mvn flyway:migrate
```

## High Level Architecture

| Layer                  | Responsibilities                                                                   |
| ---------------------- |------------------------------------------------------------------------------------|
| **Domain Layer**       | - Core business logic (entities, validation rules, value objects)                  |
| **Application Layer**  | - Orchestrates use cases (e.g., `ValidateLeadUseCase`)                             |
| **Ports (Interfaces)** | - Abstractions for interacting with external systems (e.g., `JudicialRecordsPort`) |
| **Adapters (Infra)**   | - Implementations of ports (stubbed HTTP, mock services, etc.)                     |
| **Presentation Layer** | - CLI input/output and REST uses the application service                           |
| **Testing Layer**      | - Unit tests, validation flow tests, edge case checks                              |


## Relevant links

- [Business Assumptions and requirements](adr/ADR-000-assumptions-and-requirements.md)
- [Hexagonal Architecture](adr/ADR-001-adopt-hexagonal-architecture.md)
- [Internal Design Patterns](adr/ADR-002-internal-design-patterns.md)