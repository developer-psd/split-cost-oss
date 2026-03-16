# Test Strategy

## 1. Document purpose
This document defines the overall testing strategy for the Expense Splitter system. It establishes:
- what is in scope
- what quality attributes matter most
- which test levels are required
- how standalone and webservice testing complement one another
- which risks drive prioritization

## 2. System under test
The system has two main parts:

### 2.1 Standalone/core module
A Java domain library that models:
- trips
- participants
- transactions
- balance sheets
- settlement generation

The core business-critical logic lives here, especially:
- equal-share splitting
- sponsored-share handling
- merging/normalizing debts
- simplified settlement minimization
- zero-sum grouping for globally optimal transfer reduction

### 2.2 Webservice module
A Spring Boot application exposing:
- trip lifecycle APIs
- participant management APIs
- transaction management APIs
- settlement APIs
- validation and error-mapping behavior
- PostgreSQL-backed persistence behavior

## 3. Quality goals
The testing program prioritizes the following quality goals:

1. **Functional correctness**  
   Settlements, validations, and CRUD flows must produce correct results.

2. **Deterministic financial behavior**  
   Expense splitting must remain stable under rounding and repeated recomputation.

3. **Persistence correctness**  
   State must survive restart via PostgreSQL-backed repositories. Settlement snapshots must be rewritten or cleared correctly when transactions change.

4. **API contract reliability**  
   The HTTP contract must be predictable, especially around success payloads, validation failures, and domain failures.

5. **Regression resistance**  
   Refactoring controllers, repositories, or settlement algorithms must not silently break business rules.

## 4. Test objectives
The test objectives are to prove that:
- trips can be created, queried, modified, and deleted correctly
- participants are added and removed correctly
- transactions are created, queried, and deleted correctly
- invalid payloads are rejected cleanly
- business exceptions are surfaced through the global handlers with expected HTTP semantics
- basic settlement is correct and merges debts properly
- simplified settlement returns a globally optimal number of transfers for supported participant counts
- settlement snapshots are recomputed and rewritten after transaction changes
- settlement snapshots are cleared when transactions are added/deleted before the next recompute
- repository-backed state survives application restart
- database mappings preserve aggregate integrity

## 5. Scope

### 5.1 In scope
- standalone domain objects
- settlement algorithms
- service-layer transformations
- controller request/response behavior
- validation behavior
- exception handling behavior
- PostgreSQL repository behavior
- repository integration with JPA mappings
- persisted settlement rewrite/clear semantics
- smoke-level Spring Boot context loading

### 5.2 Out of scope
- performance benchmarking at production scale
- load testing
- security testing beyond basic contract validation
- UI/browser testing
- distributed systems concerns
- multi-node concurrency correctness
- observability dashboards and runtime telemetry validation

## 6. Test levels
The strategy uses layered verification.

### 6.1 Standalone unit and component tests
Purpose:
- verify domain logic in isolation
- keep algorithmic failures easy to debug
- catch rounding, grouping, and participant-mapping bugs early

Primary targets:
- `Trip`
- `BalanceSheet`
- `BasicSettler`
- `SimplifiedSettler`
- settlement factory logic
- participant/transaction invariants

### 6.2 Web MVC slice tests
Purpose:
- verify endpoint mappings
- verify request validation
- verify response shape
- verify exception-to-HTTP mapping
- verify service interaction boundaries

Primary targets:
- `TripController`
- `TripTransactionController`
- `SettlementController`
- `GlobalExceptionHandler`
- `GlobalValidationExceptionHandler`

### 6.3 Service unit tests
Purpose:
- verify request-to-domain mapping
- verify repository orchestration
- verify settlement mode routing
- verify correct recomputation semantics

Primary targets:
- `TripServiceImpl`
- `TripTransactionServiceImpl`
- `SettlementServiceImpl`

### 6.4 Repository integration tests
Purpose:
- verify PostgreSQL persistence and retrieval
- verify JPA mappings
- verify active/inactive participant handling
- verify transaction/beneficiary persistence
- verify settlement snapshot rewrite/clear behavior

Primary targets:
- `PostgresTripRepository`
- `PostgresTripTransactionRepository`
- `PostgresSettlementRepository`

### 6.5 Application smoke tests
Purpose:
- prove the application context can start under a controlled test profile
- ensure infrastructure/debug-only beans do not break testability

## 7. Test design principles
The following principles apply across the suite:
- test public behavior, not private implementation details
- prefer exact financial assertions for settlement outputs
- preserve deterministic ordering where business logic depends on it
- isolate algorithm tests from Spring
- isolate MVC tests from the database
- use real PostgreSQL for repository integration tests
- use clear IDs and known participant sets in assertions
- model tests around business scenarios, not only code branches

## 8. Risk-based focus areas
The highest-risk areas are:
1. simplified settlement global optimality
2. equal-share rounding in cents
3. participant identity mismatches
4. validation path consistency
5. deletion side effects on persisted settlement snapshots
6. restart-state behavior after transaction mutations
7. controller mapping regressions
8. persistence of beneficiary ordering

## 9. Test data strategy
Test data should emphasize:
- small deterministic examples for exact expected settlements
- repeated-pattern datasets for regression stability
- invalid payload variants for validation coverage
- persistence scenarios with multiple trips and overlapping participant names
- date parsing success and failure cases
- duplicate and not-found scenarios

## 10. Tooling strategy
Recommended tool stack:
- JUnit 5
- Mockito or Spring bean override mocks for unit/MVC tests
- Spring MockMvc for controller tests
- Testcontainers PostgreSQL for repository integration tests
- Maven Surefire for execution
- CI pipeline with separate fast and slow stages

## 11. Coverage guidance
Coverage target is not merely line coverage. The target is business-rule coverage:
- every major endpoint path covered
- every validation family covered
- every repository responsibility covered
- both settlement modes covered
- zero/one/many transaction paths covered
- recompute/rewrite/clear persistence paths covered

## 12. Exit philosophy
A build is considered acceptable only when:
- no critical standalone financial logic tests fail
- no controller contract tests fail
- no repository integration tests fail
- known issues are explicitly documented and accepted
