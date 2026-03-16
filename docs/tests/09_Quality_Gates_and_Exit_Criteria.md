# Quality Gates and Exit Criteria

## 1. Purpose
This document defines the minimum acceptance gates for the Expense Splitter system from a testing perspective.

## 2. Mandatory gates

### Gate A - Build integrity
- project compiles
- test sources compile
- no duplicate or malformed Maven dependency declarations
- smoke test can run under the defined test profile

### Gate B - Standalone correctness
All critical standalone financial logic tests must pass, especially:
- equal split correctness
- sponsored-share handling
- rounding correctness
- basic settlement merging
- simplified settlement optimality and balance conservation

### Gate C - API contract correctness
All controller contract tests must pass for:
- trip endpoints
- transaction endpoints
- settlement endpoint
- validation response shape
- exception mapping

### Gate D - Persistence integrity
All PostgreSQL repository integration tests must pass for:
- trip persistence and retrieval
- participant persistence lifecycle
- transaction persistence and retrieval
- settlement snapshot persistence
- settlement snapshot rewrite/clear behavior

### Gate E - Traceability completeness
All in-scope requirements in the RTM must map to at least one automated test case.

## 3. Exit criteria for submission or release candidate
The system is considered test-complete when:
- no Sev-1 defects remain open
- no Sev-2 defects remain open unless explicitly accepted in writing
- all critical automated tests pass
- all known issues are documented in `10_Assumptions_Risks_and_Known_Issues.md`
- the traceability matrix is current

## 4. Conditional pass criteria
A conditional pass may be accepted only when:
- the failure is non-critical
- the issue is well understood
- a workaround exists
- the interviewer/reviewer/stakeholder explicitly accepts the risk

## 5. Automatic fail conditions
The build should be considered failed if any of the following occur:
- incorrect financial result in standalone tests
- simplified settlement no longer globally optimizes transfer count within supported limits
- controller mapping regression causes endpoint contract breakage
- validation handlers return the wrong contract shape
- transaction mutation no longer clears stale settlement snapshots
- repository reconstruction depends on in-memory state

## 6. Coverage guidance for acceptance
Minimum acceptable evidence:
- core logic thoroughly covered
- each endpoint has success and negative paths
- each repository has create/read/update-delete path coverage as applicable
- restart-state behavior is represented by repository reconstruction tests

## 7. Review sign-off checklist
Before sign-off confirm:
- strategy reviewed
- plan reviewed
- RTM reviewed
- test suite executed
- defects triaged
- quality gate result recorded
