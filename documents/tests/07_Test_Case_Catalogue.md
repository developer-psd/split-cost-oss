# Test Case Catalogue

## 1. Purpose
This catalogue lists the recommended automated test scenarios for the project.  
IDs are designed so they can be referenced from execution reports and defect logs.

---

## 2. Standalone core tests

### Participant and Trip
- `CORE-PART-001` Create participant from name only
- `CORE-PART-002` Create participant from name and explicit ID
- `CORE-TRIP-001` Create trip with valid participants
- `CORE-TRIP-002` Reject empty participant list
- `CORE-TRIP-003` Retrieve existing participant by ID
- `CORE-TRIP-004` Reject unknown participant lookup
- `CORE-TRIP-005` Add participants to trip
- `CORE-TRIP-006` Remove participants from trip

### BalanceSheet
- `CORE-BS-001` Add transactions to balance sheet
- `CORE-BS-002` Delegate settlement to chosen settler
- `CORE-BS-003` Empty balance sheet returns empty debts

### Basic Settler
- `CORE-SET-BASIC-001` Single transaction equal split
- `CORE-SET-BASIC-002` Payer included in beneficiaries
- `CORE-SET-BASIC-003` Merge repeated debtor-creditor debts
- `CORE-SET-BASIC-004` Ignore sponsored transactions
- `CORE-SET-BASIC-005` No self-debt generated
- `CORE-ROUND-001` Exact cent rounding across beneficiaries

### Simplified Settler
- `CORE-SET-SIM-001` Simple known optimum scenario
- `CORE-SET-SIM-002` Simplified count <= basic count
- `CORE-SET-SIM-003` Empty or zero-balance result
- `CORE-SET-SIM-004` Ignore sponsored transactions
- `CORE-SET-SIM-005` Zero-sum subgroup reconstruction scenario
- `CORE-SET-SIM-006` Reject >20 active non-zero participants if exact solver limit remains
- `CORE-SET-SIM-007` Apply debts and end with zero balances
- `CORE-SET-SIM-008` All debt amounts positive and non-zero
- `CORE-SET-SIM-009` No participant pays themselves

### Factory
- `CORE-FACT-001` BASIC returns BasicSettler
- `CORE-FACT-002` SIMPLIFIED returns SimplifiedSettler

---

## 3. Service tests

### TripServiceImpl
- `SVC-TRIP-001` Create trip maps string names to participants
- `SVC-TRIP-002` Add participants maps string names to participants
- `SVC-TRIP-003` Remove participants delegates IDs correctly
- `SVC-TRIP-004` Delete trip delegates correctly
- `SVC-TRIP-005` Get all trips delegates correctly
- `SVC-TRIP-006` Get trip by ID delegates correctly

### TripTransactionServiceImpl
- `SVC-TXN-001` Create transaction maps request to domain transaction
- `SVC-TXN-002` Create transaction parses valid date
- `SVC-TXN-003` Reject invalid date format
- `SVC-TXN-004` Reject invalid category
- `SVC-TXN-005` Reject unknown payer or beneficiary participant ID
- `SVC-TXN-006` Get transactions delegates correctly
- `SVC-TXN-007` Get single transaction delegates correctly
- `SVC-TXN-008` Delete transaction delegates correctly

### SettlementServiceImpl
- `SVC-SET-001` Settlement with simplify=false uses BASIC
- `SVC-SET-002` Settlement with simplify=true uses SIMPLIFIED
- `SVC-SET-003` Settlement persisted after computation
- `SVC-SET-004` Returned debts equal computed debts

---

## 4. Web MVC tests

### TripController
- `API-TRIP-001` POST /trip success
- `API-TRIP-002` POST /trip blank name returns 400
- `API-TRIP-003` POST /trip empty participants returns 400
- `API-TRIP-004` POST /trip invalid participant element returns 400
- `API-TRIP-010` POST /trip/{tripId}/participants success
- `API-TRIP-011` POST /trip/{tripId}/participants empty request returns 400
- `API-TRIP-012` POST /trip/{tripId}/participants duplicate participant path maps correctly
- `API-TRIP-020` DELETE /trip/{tripId}/participants success
- `API-TRIP-021` DELETE /trip/{tripId}/participants invalid participant path maps correctly
- `API-TRIP-030` GET /trip/{tripId}/details success
- `API-TRIP-031` GET /trip/{tripId}/details not found maps correctly
- `API-TRIP-040` GET /trip/all success
- `API-TRIP-050` DELETE /trip/{tripId} success

### TripTransactionController
- `API-TXN-001` POST /trip/{tripId}/transactions success
- `API-TXN-002` POST /trip/{tripId}/transactions empty transactions array returns 400
- `API-TXN-003` POST /trip/{tripId}/transactions blank spentBy returns 400
- `API-TXN-004` POST /trip/{tripId}/transactions empty benefittedBy returns 400
- `API-TXN-005` POST /trip/{tripId}/transactions service validation maps correctly
- `API-TXN-010` GET /trip/{tripId}/transactions success
- `API-TXN-020` GET /trip/{tripId}/transactions/{transactionId} success
- `API-TXN-021` GET single transaction runtime/domain failure maps correctly
- `API-TXN-030` DELETE /trip/{tripId}/transactions/{transactionId} success

### SettlementController
- `API-SET-001` POST settlement simplify=false success
- `API-SET-002` POST settlement simplify=true success
- `API-SET-003` POST settlement service error maps correctly

### Exception contract tests
- `API-ERR-001` ValidationException to expected HTTP status/payload
- `API-ERR-002` RuntimeException to expected HTTP status/payload
- `API-ERR-003` DuplicateInsertionException to expected HTTP status/payload

---

## 5. Repository integration tests

### PostgresTripRepository
- `DB-TRIP-001` Persist trip and fetch by ID
- `DB-TRIP-002` Persist multiple trips and fetch all
- `DB-TRIP-003` Add participants and re-fetch
- `DB-TRIP-004` Reject duplicate participant add
- `DB-TRIP-005` Remove/deactivate participant and re-fetch
- `DB-TRIP-006` Delete trip

### PostgresTripTransactionRepository
- `DB-TXN-001` Persist transactions and fetch all
- `DB-TXN-002` Fetch single transaction by ID
- `DB-TXN-003` Reject duplicate transaction ID
- `DB-TXN-004` Delete transaction
- `DB-TXN-005` Beneficiary ordering preserved after round-trip
- `DB-TXN-020` Create transaction clears current settlement snapshot
- `DB-TXN-030` Delete transaction clears current settlement snapshot

### PostgresSettlementRepository
- `DB-SET-001` Persist settlement rows
- `DB-SET-002` Retrieve trip aggregate for settlement
- `DB-SET-003` Reject settlement persist for unknown trip
- `DB-SET-010` Rewrite settlement rows on recompute

### Restart state
- `DB-STATE-001` Trip, participant, transaction state survives repository reconstruction
- `DB-STATE-002` Settlement snapshot survives retrieval until mutation clears it

---

## 6. Smoke tests
- `APP-SMOKE-001` Application context loads under test profile without unnecessary infra failures
