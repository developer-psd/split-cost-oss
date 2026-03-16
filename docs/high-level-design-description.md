# High-Level Design Description

## Purpose
The project is intentionally split into **two architectural parts**:
- **`expense-splitter.standalone`**: the pure business engine
- **`expense-splitter`**: the Spring Boot webservice and persistence adapter

The split exists so that the **core expense-splitting model and settlement algorithms remain independent of HTTP, Spring, PostgreSQL, and deployment concerns**.

## Architectural Boundary
### 1. Standalone = the business core
The standalone module owns the **domain language and the settlement behavior** of the system.
It contains the classes that define what the system *means*:
- `Trip`
- `Participant`
- `Transaction`
- `BalanceSheet`
- `Debt`
- settlement algorithms such as `BasicSettler` and `SimplifiedSettler`
- `SettlerFactory` and `SettlementMode`

This layer answers questions like:
- What is a trip?
- Who are the participants?
- How is a transaction represented?
- How are debts computed?
- How does “basic” settlement differ from “simplified” settlement?

This layer should **not** know:
- HTTP endpoints
- request/response DTOs
- Spring annotations
- repositories or JPA entities
- database schema
- deployment/runtime configuration

In short: **standalone is the product logic**.

### 2. Webservice = the delivery and persistence layer
The webservice module exposes the standalone core through a deployable API.
It contains the classes that define how the system is *used and stored*:
- controllers (`TripController`, `TripTransactionController`, `SettlementController`)
- request DTOs (`POSTTripRequest`, `PUTTripRequest`, `PostTransactionRequest`)
- application services (`TripServiceImpl`, `TripTransactionServiceImpl`, `SettlementServiceImpl`)
- repository interfaces and PostgreSQL implementations
- exception handlers
- Spring Boot configuration

This layer answers questions like:
- Which API creates a trip?
- How is a transaction request validated?
- How do we load and persist a trip in PostgreSQL?
- How do we translate HTTP input into domain objects?
- How do we return domain results as JSON?

In short: **webservice is the adapter around the core**.

## What Belongs in Each Part
## Standalone module
Put code here when it is:
- business-rule heavy
- reusable outside Spring
- independent of storage and transport
- testable as plain Java

Examples:
- settlement algorithms
- domain objects and invariants
- balance/debt calculation
- settlement mode selection

## Webservice module
Put code here when it is:
- API-specific
- persistence-specific
- framework-specific
- integration/orchestration-focused

Examples:
- controllers and URL mappings
- JSON request parsing and validation
- repository implementations
- JPA entities and mappers
- exception-to-HTTP translation
- startup/configuration code

## Design Intent of the Standalone Boundary
The standalone boundary is trying to achieve **portability, replaceability, and long-term safety**.

### Portability
The same expense-splitting engine can be reused by:
- a web API
- a CLI tool
- batch processing
- another UI later

### Replaceability
If the delivery layer changes, the core should survive unchanged.
For example, PostgreSQL can be replaced, or Spring Boot can be replaced, without rewriting settlement logic.

### Testability
The most important logic — debt computation — can be tested without bringing up Spring, Docker, or a database.
That keeps the core tests fast and trustworthy.

### Change isolation
Feature changes are easier to reason about:
- a new settlement algorithm should mostly change the standalone module
- a new endpoint or persistence detail should mostly change the webservice module

## Runtime Interaction Between the Two Parts
The runtime flow is:
1. A controller receives an HTTP request.
2. A service validates/maps the request.
3. The service loads or builds domain objects.
4. The service calls the standalone core (`Trip`, `BalanceSheet`, `SettlerFactory`, settler implementations).
5. The computed result is persisted or returned.
6. The controller serializes the response back to JSON.

So the dependency direction is:
- **webservice depends on standalone**
- **standalone does not depend on webservice**

That is the key architectural rule in this project.

## Patterns Visible at This Level
- **Layered architecture** in the webservice: controller -> service -> repository
- **Strategy pattern** in the standalone core: `Settler` with `BasicSettler` and `SimplifiedSettler`
- **Factory-style selection** via `SettlerFactory`
- **Adapter/mapper role** in the webservice when translating between DTOs, database rows/entities, and domain objects

## Extensibility Guidance
### Add a new settlement algorithm
Add it in the **standalone** module.
Only the selection wiring in the webservice should need minor changes.

### Add a new API
Add it in the **webservice** module.
Do not move HTTP logic into the standalone core.

### Add a new persistence technology
Change the **webservice repository implementations**.
The standalone core should remain untouched.

### Add new request/response formats
Keep that in the **webservice** layer.
The domain model should not become shaped by JSON concerns.

## One-line Summary
The project is designed so that **`expense-splitter.standalone` is the pure expense-sharing engine**, while **`expense-splitter` is the Spring Boot adapter that validates requests, persists state, and exposes the core through HTTP**.
