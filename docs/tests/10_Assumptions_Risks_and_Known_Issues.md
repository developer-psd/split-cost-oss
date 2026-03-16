# Assumptions, Risks, and Known Issues

## 1. Assumptions
This documentation assumes:
- the archive reflects the intended current codebase
- the standalone module is the source of truth for financial settlement rules
- the Spring Boot module uses PostgreSQL-backed repositories as the primary runtime repository implementation
- the webservice persists a “current settlement snapshot” and rewrites or clears it appropriately
- exact simplified settlement currently supports up to 20 active non-zero participants, as indicated in the current `SimplifiedSettler`

## 2. Key risks
1. **Algorithmic regression risk**  
   Refactoring simplified settlement may preserve correctness of balances while losing global optimality.

2. **Contract drift risk**  
   Validation handlers return a flat map. Tests that assume nested JSON structures will become stale.

3. **Persistence side-effect risk**  
   Settlement snapshot clearing on transaction mutation is easy to break silently.

4. **Context-load fragility risk**  
   Debug-only beans such as datasource verifiers can break testability if not isolated from the test profile.

5. **Identity mapping risk**  
   Transactions depend on participant ID resolution within the trip aggregate. Small mapping mistakes cause large correctness failures.

## 3. Known code/documentation alignment notes
- The project README describes some APIs at a higher level than the current implemented controller surface.
- Current repository behavior indicates participant removal is effectively a soft deactivate in PostgreSQL-backed storage.
- Current validation assertions should target flat field-path keys.
- Current simplified settlement documentation should explicitly state supported participant-count limits for exact optimization.

## 4. Documentation maintenance advice
Update this file whenever:
- an endpoint contract changes
- repository behavior changes from soft-delete to hard-delete
- settlement algorithm complexity or supported limits change
- the validation payload format changes
- smoke-test requirements change

## 5. What this pack does not claim
This pack does not claim:
- load/performance readiness
- security certification
- UI testing completeness
- production deployment certification
