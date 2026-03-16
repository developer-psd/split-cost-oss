# Webservice Test Design Specification

## 1. Scope
This specification covers the Spring Boot application layer, including:
- controllers
- validation
- exception mapping
- service implementations
- PostgreSQL repositories
- persistence-backed state behavior

## 2. API surface under test

### Trip APIs
- `POST /trip`
- `POST /trip/{tripId}/participants`
- `DELETE /trip/{tripId}/participants`
- `GET /trip/{tripId}/details`
- `GET /trip/all`
- `DELETE /trip/{tripId}`

### Transaction APIs
- `POST /trip/{tripId}/transactions`
- `GET /trip/{tripId}/transactions`
- `GET /trip/{tripId}/transactions/{transactionId}`
- `DELETE /trip/{tripId}/transactions/{transactionId}`

### Settlement API
- `POST /trip/{tripId}/settlement?simplify=true|false`

## 3. Test layers

### 3.1 MockMvc controller tests
Purpose:
- verify routing
- verify payload binding
- verify validation outcomes
- verify response JSON shape
- verify mapping to service contracts

### 3.2 Exception handler contract tests
Purpose:
- verify global mapping of business and validation exceptions
- isolate handler semantics from the full application context

Recommendation:
- use standalone MockMvc builder with explicit controller advice

### 3.3 Service unit tests
Purpose:
- verify request-to-domain mapping
- verify participant resolution against trip aggregate
- verify date parsing
- verify settlement mode dispatch
- verify repository orchestration

### 3.4 Repository integration tests
Purpose:
- verify JPA mappings and aggregate reconstruction
- verify active participant handling
- verify beneficiary persistence
- verify settlement snapshot rewrite and clear behavior

## 4. Controller design expectations

### 4.1 TripController
Must be tested for:
- successful trip creation
- 400 on blank trip name
- 400 on empty participant list
- participant add/remove success paths
- not-found and duplicate cases as mapped by the global handlers
- single-trip payload shape
- all-trips payload shape
- delete-trip acknowledgement

### 4.2 TripTransactionController
Must be tested for:
- successful transaction creation
- validation failures on blank `spentBy`
- validation failures on empty `benefittedBy`
- validation failures on empty `transactions`
- correct base collection retrieval mapping
- correct single-resource retrieval mapping
- transaction delete acknowledgement
- not-found or runtime exception mapping

### 4.3 SettlementController
Must be tested for:
- `simplify=false` routes to basic settlement behavior through the service
- `simplify=true` routes to simplified settlement behavior through the service
- payload shape is list of `{from, to, amount}`
- service exceptions are mapped correctly

## 5. Validation design
The project currently surfaces validation errors as a flat JSON map keyed by field path.  
Therefore tests must assert paths like:
- `$['participants[1]']`
- `$['transactions[0].spentBy']`
- `$['transactions[0].benefittedBy']`

Do not write tests assuming nested JSON validation output unless the handler contract is deliberately changed.

## 6. Service test design

### 6.1 TripServiceImpl
Test goals:
- create trip maps string names to `Participant`
- add participants maps new names to `Participant`
- remove delegates IDs as-is
- get/delete/all delegate correctly
- repository exceptions propagate as domain validation exceptions

### 6.2 TripTransactionServiceImpl
Test goals:
- validates category conversion
- validates date parsing
- resolves payer and beneficiaries from the trip aggregate
- creates domain `Transaction` objects correctly
- delegates retrieval and deletion correctly
- rejects unknown participant IDs through trip aggregate lookup

### 6.3 SettlementServiceImpl
Test goals:
- `simplify=false` selects BASIC mode
- `simplify=true` selects SIMPLIFIED mode
- repository trip retrieval invoked
- result persisted using settlement repository
- returned debt list matches the trip settlement result

## 7. Repository integration design

### 7.1 PostgresTripRepository
Test goals:
- create trip persists aggregate
- get trip reconstructs active participants
- add participants persists new participant rows
- duplicate participant add rejected
- remove participant deactivates rather than hard-deletes if that is current contract
- get all trips returns persisted trips
- delete trip removes trip

### 7.2 PostgresTripTransactionRepository
Test goals:
- create transaction persists transaction row plus beneficiary rows
- get all transactions reconstructs domain transactions accurately
- get single transaction returns the correct item
- delete transaction removes the row
- create/delete transaction clears current settlement snapshot rows
- duplicate transaction ID rejected
- trip-not-found rejected
- beneficiary order preserved after round-trip

### 7.3 PostgresSettlementRepository
Test goals:
- settlement persistence writes rows in expected order
- recompute rewrites rows for the same trip
- getTrip reconstructs current trip aggregate with transactions
- invalid trip update rejected

## 8. Full-context smoke testing
The project should also keep one minimal context smoke test under a test profile.  
This test should not pull in debug-only infrastructure beans that are not essential to the application contract.

## 9. Key webservice regression risks
- accidental controller mapping breakage
- missing `@PathVariable` or invalid binding annotations
- validation output shape drift
- repository side effects on settlement snapshot not being preserved
- schema/entity drift after refactors
- accidental dependence on in-memory state after moving to PostgreSQL
