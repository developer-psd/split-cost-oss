# Expense Splitter Webservice — Design Documentation

## 1. Purpose
The webservice module exposes the standalone expense-splitting engine as a **Spring Boot HTTP API**. Its job is to accept validated requests, orchestrate domain operations, persist state in PostgreSQL, and return API-friendly responses.

## 2. Main design idea
The webservice is organized as a **layered architecture**:
- **Controller layer**: HTTP endpoints and response shaping
- **Service layer**: application use cases and orchestration
- **Repository layer**: persistence contracts
- **Postgres adapter layer**: JPA-backed implementations and aggregate reconstruction
- **Domain layer**: reused standalone model and settlement strategies

This keeps HTTP, business flow, and persistence concerns separated.

## 3. Request flow
1. A controller receives HTTP input.
2. Request DTOs are validated with Jakarta Validation.
3. The controller calls a service.
4. The service coordinates repository calls and domain operations.
5. PostgreSQL repositories load or persist data.
6. `PostgresAggregateMapper` reconstructs domain aggregates.
7. The controller returns a lightweight JSON response.

## 4. Key classes and responsibilities

### Controllers

#### `TripController`
Handles trip lifecycle:
- create trip
- add/remove participants
- fetch one trip or all trips
- delete trip

#### `TripTransactionController`
Handles transaction lifecycle inside a trip:
- create transactions
- fetch all or one transaction
- delete transaction

#### `SettlementController`
Handles settlement computation:
- chooses basic or simplified mode through a query parameter
- returns debts as JSON

### Services

#### `TripServiceImpl`
Application service for trip use cases. Converts request participant names into domain `Participant` objects and delegates persistence work to `TripRepository`.

#### `TripTransactionServiceImpl`
Application service for transaction use cases. Validates category/date conversion, resolves participants against the trip aggregate, constructs domain `Transaction` objects, and persists them.

#### `SettlementServiceImpl`
Application service for settlement use cases. Loads the trip, chooses a settlement strategy, computes debts, and persists the latest settlement snapshot.

### Repository contracts
- `TripRepository`
- `TripTransactionReporitory`
- `SettlementRepository`

These interfaces define persistence needs without exposing JPA details to the service layer.

### Postgres repositories
- `PostgresTripRepository`
- `PostgresTripTransactionRepository`
- `PostgresSettlementRepository`

These are the concrete persistence adapters. They translate application operations into JPA operations and enforce persistence rules.

### `PostgresAggregateMapper`
Rebuilds a full domain `Trip` aggregate from normalized PostgreSQL tables:
- trip
- participants
- transactions
- transaction beneficiaries
- current settlement snapshot

This is the key class that keeps the domain model clean while the database stays normalized.

### Exception handling
- `GlobalExceptionHandler`
- `GlobalValidationExceptionHandler`

These classes convert exceptions into consistent HTTP responses.

## 5. GoF design patterns actually used

### Strategy
**Where:** reused from the standalone module in `SettlementServiceImpl`

`SettlementServiceImpl` selects a settlement strategy through `SettlerFactory` and the `simplify` flag. The web layer therefore benefits from the same extensible algorithm model without knowing algorithm details.

### Factory-style object selection
**Where:** `SettlerFactory` used by `SettlementServiceImpl`

The service asks the factory for the right settler rather than constructing algorithms inline.

### Adapter-like mapping
**Where:** controllers and `PostgresAggregateMapper`

Strictly speaking, the mapper/repository setup is closer to enterprise application patterns than pure GoF. But functionally, these classes adapt one representation to another:
- HTTP DTOs -> domain objects
- JPA entities -> domain aggregate
- domain objects -> API responses

This keeps each layer simple and focused.

## 6. Important non-GoF structural ideas

### Layered architecture
Controllers do not talk directly to JPA. Services do not expose HTTP concerns. Repositories do not contain endpoint logic. This is the main reason the code stays understandable.

### Repository pattern
The service layer depends on repository interfaces, not concrete database code. This allows in-memory and PostgreSQL implementations to coexist.

### Data Mapper style reconstruction
`PostgresAggregateMapper` is crucial because the database is normalized, while the domain wants a rich aggregate. This mapper bridges that gap cleanly.

### Dependency injection
Spring wires controllers, services, repositories, and handlers. This reduces construction noise and keeps components replaceable.

## 7. Extensibility points

### Add a new API operation
Add a controller method and corresponding service method. Existing repositories can be reused if the operation fits the current aggregate model.

### Add a new persistence backend
Implement the repository interfaces for another store, for example MongoDB or DynamoDB, and wire the desired implementation with Spring.

### Add a new settlement mode
No controller redesign is needed. Add a new settler in the standalone layer, extend `SettlementMode` and `SettlerFactory`, then expose it through the service/API contract.

### Add a richer response model
Controllers already map domain objects into `Map<String, Object>`. A cleaner next step is typed response DTOs, which can be introduced incrementally without changing the service layer.

### Add audit/history features
The current model persists the latest settlement snapshot. A history feature can be added by introducing versioned settlement entities without changing the settlement algorithm itself.

## 8. Why this design works well
- The domain logic is reused instead of duplicated.
- HTTP validation is pushed to DTOs and exception handlers.
- Services stay small and readable.
- Persistence is isolated behind interfaces.
- PostgreSQL is normalized, while the domain stays object-oriented.

## 9. Current limitations
- Controllers return `Map<String, Object>` instead of typed response DTOs.
- Service classes contain some mapping/validation code that could later be extracted.
- Repository naming has a typo (`TripTransactionReporitory`).
- The `simplify` flag is boolean-based; an enum request model would scale better for more modes.
- The system currently persists the latest settlement snapshot, not a full settlement history.

## 10. Suggested future refinement
The cleanest next improvements are:
- introduce typed response DTOs
- extract request-to-domain mappers from services
- replace boolean settlement selection with an enum-based request contract
- unify validation rules into dedicated application/domain validators
- add a versioned settlement history table if auditability matters

## 11. One-line summary
The webservice module is a **layered Spring Boot application** that reuses the standalone settlement engine, with **strategy-based settlement selection, repository abstraction, and aggregate mapping** to keep the API clear, testable, and extensible.
