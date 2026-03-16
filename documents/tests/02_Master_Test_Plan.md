# Master Test Plan

## 1. Plan objective
This plan defines how the Expense Splitter system will be tested, executed, controlled, and reported for both the standalone core and the Spring Boot webservice.

## 2. Test items
The following test items are covered:

### Standalone/core
- `Participant`
- `Trip`
- `BalanceSheet`
- `Transaction`
- `BasicSettler`
- `SimplifiedSettler`
- `SettlerFactory`

### Webservice
- `TripController`
- `TripTransactionController`
- `SettlementController`
- `GlobalExceptionHandler`
- `GlobalValidationExceptionHandler`
- `TripServiceImpl`
- `TripTransactionServiceImpl`
- `SettlementServiceImpl`
- PostgreSQL repositories and entity mappings

## 3. Features to be tested
1. Trip creation
2. Participant addition
3. Participant removal / soft deactivation behavior
4. Trip retrieval
5. All-trips retrieval
6. Trip deletion
7. Transaction creation
8. Transaction retrieval (single and all)
9. Transaction deletion
10. Basic settlement generation
11. Simplified settlement generation
12. Settlement snapshot persistence
13. Settlement snapshot invalidation on transaction mutation
14. Validation of malformed payloads
15. Exception mapping to HTTP status and error payloads
16. Persistence round-trips through PostgreSQL

## 4. Features not planned for this phase
- performance/stress testing
- API security/authorization
- chaos testing
- browser UI testing
- production deployment validation
- backward-compatibility guarantees for undocumented payload fields

## 5. Test approach by layer

### 5.1 Fast lane
Runs on every local build and CI push:
- standalone unit/component tests
- service unit tests
- MockMvc controller tests
- exception handler contract tests

### 5.2 Slow lane
Runs on merge/PR gate or nightly:
- PostgreSQL repository integration tests via Testcontainers
- full-context smoke tests
- restart-state and persistence-oriented integration checks

The “slow lane” in this document set is currently a **conceptual execution tier**, not a separately wired Maven/JUnit lane with its own tags, profile, or pipeline step. That means it describes the intended classification of heavier tests, but the project does not yet enforce that split automatically during execution. At present, these tests are part of the normal test suite unless you manually isolate them. So the distinction is architectural and documentation-led rather than tool-enforced.

In the current suite, the slow-lane behavior is represented by the **heavier persistence-oriented tests**, especially the PostgreSQL repository integration tests that run against a real database using Testcontainers. These tests start an ephemeral PostgreSQL container, wire the repositories to it, execute real persistence and retrieval flows, and then validate that state is stored and reconstructed correctly from the database. The restart-state aspect is covered indirectly through persistence/reload checks, while full-context coverage is only lightly represented by the application smoke test rather than a full end-to-end `@SpringBootTest` flow suite.

## 6. Entry criteria
Testing may begin when:
- project compiles
- test dependencies resolve
- a stable test profile exists
- known controller mappings are not in a partially broken state
- schema is available for repository integration tests

## 7. Suspension criteria
Execution should be suspended if:
- the application does not compile
- database schema and repository contracts diverge
- financial outputs become non-deterministic due to uncontrolled timestamps/IDs in assertions
- the test environment becomes inconsistent across runs

## 8. Resumption criteria
Testing resumes when:
- the build is restored
- schema and entity model are aligned
- failing tests have been triaged into real defects vs. stale expectations
- the environment is reproducible again

## 9. Roles and responsibilities
### Developer
- maintain test code
- keep fixtures deterministic
- fix code regressions
- update documentation when contracts change

## 10. Deliverables
The test documentation deliverables are:
- test strategy
- master plan
- traceability matrix
- standalone design spec
- webservice design spec
- environment and data guide
- execution and defect guide
- quality gates
- assumptions/risks/known issues
- test case catalogue

The implementation deliverables are:
- JUnit 5 standalone tests
- JUnit 5 service tests
- MockMvc controller tests
- exception contract tests
- Testcontainers PostgreSQL repository tests

## 11. Environment needs
### Local developer environment
- Java 21
- Maven
- Docker
- PostgreSQL container or Testcontainers support

### CI environment
- Java 21
- Docker daemon support for Testcontainers
- enough resources for database-backed integration tests

## 12. Test schedule recommendation
### Phase 1
Core standalone algorithm and model tests

### Phase 2
Service and controller contract tests

### Phase 3
Repository integration tests

### Phase 4
Regression, cleanup, quality-gate certification

## 13. Metrics to capture
- total tests
- pass/fail count
- defect count by severity
- defects by layer (standalone, MVC, service, repository)
- requirement coverage ratio from RTM
- percentage of critical business rules with automated verification

## 14. Test completion criteria
Testing is complete for a release candidate when:
- all critical tests pass
- no Sev-1/Sev-2 open defects remain
- traceability matrix shows coverage for all in-scope requirements
- known issues are documented and accepted
