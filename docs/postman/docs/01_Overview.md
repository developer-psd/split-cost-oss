# 01 Overview

This automation pack converts the existing manual Expense Splitter Postman work into a repeatable, end-to-end, assertion-backed API test flow.

The collection is intentionally stateful within a single run:
- it creates fresh trips
- captures returned trip and participant identifiers
- builds downstream requests dynamically from those identifiers
- validates both success and validation-error contracts
- cleans up the test data it creates

This makes the pack suitable for:
- local smoke testing
- developer regression runs
- CI/CD API checks
- interview/demo walkthroughs

The design goal was to keep the active run green against the current codebase while still documenting known controller gaps for future activation.
