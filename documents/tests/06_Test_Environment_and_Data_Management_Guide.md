# Test Environment and Data Management Guide

## 1. Purpose
This guide explains how to set up, run, and stabilize the test environments used by the Expense Splitter project.

## 2. Baseline environment
Recommended baseline:
- Java 21
- Maven
- Docker
- PostgreSQL available either through:
  - Testcontainers, or
  - a local Docker container for manual integration verification

## 3. Environment types

### 3.1 Standalone local test environment
Used for:
- pure algorithm tests
- domain model tests
- fast regression loops

Requirements:
- Java 21
- Maven only
- no database needed

### 3.2 MVC test environment
Used for:
- MockMvc controller tests
- validation contract tests
- exception handler tests

Requirements:
- Java 21
- Maven
- no live database required
- services/repositories mocked or overridden

### 3.3 Repository integration environment
Used for:
- JPA mapping tests
- persistence round-trip tests
- settlement snapshot rewrite/clear tests

Requirements:
- Docker
- Testcontainers PostgreSQL support

## 4. Data profile strategy
Use at least these profiles:

### `test`
For:
- smoke tests
- MVC tests
- non-database Spring tests

Purpose:
- avoid debug-only runtime beans
- avoid unwanted production-style infra dependencies

### `integration`
Optional but recommended
For:
- repository integration tests
- data-source-backed scenarios

## 5. Database strategy

### Preferred approach
Use Testcontainers PostgreSQL for repository integration tests because it provides:
- isolated database instances
- repeatable state
- reduced manual setup
- better CI portability

### Manual fallback
If you must use a local PostgreSQL Docker container, keep:
- explicit schema scripts under version control
- deterministic cleanup scripts
- clear separation between developer data and test data

## 6. Schema expectations
The PostgreSQL schema should support:
- trips
- participants
- transactions
- transaction beneficiaries
- current settlement snapshot rows

Documented repository behavior indicates:
- settlement snapshot rows are deleted and rewritten on recompute
- settlement snapshot rows are deleted when transactions are created or deleted

## 7. Test data design principles
- use fixed UUIDs where explicit control is needed
- use readable participant IDs such as `P1`, `P2`, `P3` in unit tests
- use deterministic dates
- use compact datasets for exact financial assertions
- keep one or two large patterned datasets for regression stress

## 8. Cleanup strategy
### Standalone and MVC tests
No cleanup typically needed beyond test isolation.

### Repository integration tests
Use either:
- transaction rollback strategy when appropriate, or
- fresh Testcontainers database instance per test class

Avoid sharing mutable database state across unrelated integration tests.

## 9. Seed data patterns
Recommended reusable fixture patterns:
1. trip with 2 participants
2. trip with 5 participants
3. trip with removed participant
4. trip with multiple transactions
5. trip with existing settlement snapshot
6. transaction with payer in beneficiary list
7. transaction with sponsored share type

## 10. Logging and diagnostics
When diagnosing failures:
- capture the first root cause, not just repeated threshold failures
- for MVC tests, inspect response body and resolved exception
- for repository tests, log the SQL or repository result state when needed
- for settlement tests, print computed net balances only when debugging, not as a permanent assertion strategy

## 11. CI recommendations
- split fast tests from database-backed tests
- fail fast on compilation and standalone logic
- run repository integration tests in a second stage
- archive surefire reports and RTM coverage summary as build artifacts
