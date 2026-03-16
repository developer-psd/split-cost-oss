# Standalone Test Design Specification

## 1. Scope
This specification covers the non-Spring, core domain and algorithm layer in `expense-splitter.standalone`.

## 2. Components under test
- `Participant`
- `Trip`
- `BalanceSheet`
- `Transaction`
- `BasicSettler`
- `SimplifiedSettler`
- `SettlerFactory`

## 3. Core behavioral areas

### 3.1 Participant behavior
Test goals:
- participant ID generation occurs automatically for name-only construction
- generated IDs are non-null and fixed in the created record
- explicit `(name, participantId)` constructor preserves input exactly

Representative tests:
- `CORE-PART-001` create participant with name only
- `CORE-PART-002` create participant with explicit id
- `CORE-PART-003` string representation is readable and stable if relied on

### 3.2 Trip invariants
Test goals:
- trip creation rejects empty participant lists
- trip ID is generated
- `getParticipant` resolves by participant ID
- `getParticipant` fails for unknown participant ID
- participant add/remove behavior updates participant map and list coherently

Representative tests:
- `CORE-TRIP-001` create trip with valid participants
- `CORE-TRIP-002` reject empty participant list
- `CORE-TRIP-003` get known participant
- `CORE-TRIP-004` get unknown participant throws
- `CORE-TRIP-005` add participants
- `CORE-TRIP-006` remove participants

### 3.3 Balance sheet behavior
Test goals:
- transactions are appended
- settlement delegates to the provided settler
- empty transaction list produces empty settlement

Representative tests:
- `CORE-BS-001` add transactions
- `CORE-BS-002` delegate to settler
- `CORE-BS-003` empty balance sheet settlement

### 3.4 Basic settlement behavior
This is the direct settlement mode.

Test goals:
- equal-share transactions generate direct debts from beneficiaries to payer
- payer’s own share is excluded from debt generation
- multiple matching debts are merged
- sponsored transactions do not create debts
- rounding in cents is handled exactly

Representative tests:
- `CORE-SET-BASIC-001` one transaction, multiple beneficiaries
- `CORE-SET-BASIC-002` payer included among beneficiaries
- `CORE-SET-BASIC-003` repeated transactions merge same debtor-creditor pair
- `CORE-SET-BASIC-004` sponsored transaction ignored
- `CORE-ROUND-001` amount with remainder across beneficiaries

Expected assertion style:
- exact debtor
- exact creditor
- exact amount
- exact count of resulting debts

### 3.5 Simplified settlement behavior
This is the optimized settlement mode.

Test goals:
- net balances are computed correctly from transactions
- zero balances are removed before optimization
- result count is globally optimal for supported participant counts
- settlement is exact in value conservation
- each resulting debt moves money from a net debtor to a net creditor
- supported active non-zero participant limit is enforced
- zero-sum subgroup reconstruction preserves correctness

Representative tests:
- `CORE-SET-SIM-001` simple small exact scenario with known optimum
- `CORE-SET-SIM-002` compare count against basic mode; simplified should be <= basic
- `CORE-SET-SIM-003` all net balances zero => empty result
- `CORE-SET-SIM-004` sponsored transactions ignored
- `CORE-SET-SIM-005` scenario requiring zero-sum subgroup partitioning
- `CORE-SET-SIM-006` reject >20 active non-zero balances if exact solver contract remains
- `CORE-SET-SIM-007` each participant’s net effect equals original net balance

### 3.6 Factory routing
Test goals:
- factory returns `BasicSettler` for BASIC
- factory returns `SimplifiedSettler` for SIMPLIFIED

Representative tests:
- `CORE-FACT-001`
- `CORE-FACT-002`

## 4. Design patterns for standalone tests
- use explicit participant IDs, not auto-generated IDs, for deterministic assertions
- build transactions using helper fixtures
- compare settlements as normalized sets if order is not contractually guaranteed
- where count optimality matters, assert both total count and balance conservation

## 5. Mandatory helper assertions
Reusable helpers should validate:
- sum of outgoing and incoming debts per participant
- total settled amount equals total absolute creditor amount
- no debt has zero or negative amount
- no debt exists from a participant to themself
- final balances are zero after applying debts to original net balances

## 6. Negative-path design
The standalone suite should explicitly verify:
- null transaction handling where applicable
- non-positive transaction amounts rejected
- null payer rejected
- null share type rejected
- empty beneficiary list rejected
- invalid participant lookup rejected

## 7. Regression focus
These tests are the strongest regression shield against:
- financial rounding bugs
- settlement count regressions
- participant identity bugs
- sponsored/equal share rule drift
- changes to simplified solver internals that silently lose optimality
