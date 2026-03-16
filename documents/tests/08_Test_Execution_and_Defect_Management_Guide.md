# Test Execution and Defect Management Guide

## 1. Purpose
This guide defines how to run the tests, record outcomes, and manage defects in a repeatable way.

## 2. Execution order
Recommended order:
1. standalone/core tests
2. service tests
3. MVC/controller tests
4. exception contract tests
5. repository integration tests
6. full-context smoke tests

This order gives the shortest feedback loop and makes root-cause isolation easier.

## 3. Local execution model
### Fast suite
Run during normal development:
- standalone
- service
- MVC
- exception contract

### Full suite
Run before merge/submission:
- all of the above
- repository integration
- context smoke

## 4. Maven execution examples
Typical commands:
- `mvn test`
- `mvn -Dtest=*WebMvcTest test`
- `mvn -Dtest=*RepositoryIT test`

Adapt these to your naming conventions.

## 5. Result recording
For each run capture:
- date/time
- code revision
- environment
- total tests
- passed
- failed
- errors
- skipped
- first root cause for each distinct failure class

## 6. Failure triage rules
When a test fails, classify it as one of:
1. product defect
2. stale test expectation
3. environment/configuration issue
4. flaky/non-deterministic test
5. known accepted issue

Always triage using the **first root cause** rather than repeated threshold or wrapper failures.

## 7. Defect logging template
For each defect record:
- defect ID
- summary
- discovered by test ID
- layer (standalone, MVC, service, repository, context)
- severity
- priority
- reproducibility
- steps to reproduce
- expected result
- actual result
- suspected component
- status
- owner

## 8. Severity model
### Sev-1
Critical financial correctness or data loss issue

### Sev-2
Major functional issue affecting core flows or persistence reliability

### Sev-3
Contract mismatch or non-critical feature defect

### Sev-4
Cosmetic/documentation-only issue

## 9. Pass/fail interpretation
A run is not “green” if:
- there are zero assertion failures but context load errors remain
- repository tests are skipped due to missing Docker support without explicit approval
- known expected failures are not documented in the known issues register

## 10. Reporting format
Recommended summary format:
- build identifier
- scope executed
- overall status
- requirements covered
- defects opened/closed
- gate recommendation: pass / conditional pass / fail

## 11. Evidence to retain
Retain:
- surefire reports
- failing response payloads for MVC tests
- database logs for repository failures where useful
- traceability matrix status snapshot
- defect report references
