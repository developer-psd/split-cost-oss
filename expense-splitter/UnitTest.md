# Expanded JUnit 5 suite for expense-splitter

This suite expands coverage across:

- Web MVC controller slice tests
- Global exception-handler contract tests
- Service unit tests
- PostgreSQL repository integration tests using Testcontainers

## Maven test dependencies

For Spring Boot 4.0.3, keep these test dependencies:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>

<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>testcontainers-postgresql</artifactId>
    <scope>test</scope>
</dependency>
```

Remove duplicate `spring-boot-starter-test` declarations.

## Important expected failures against current code

Two tests intentionally assert the intended HTTP contract and will fail until the production code is fixed:

1. `TripTransactionControllerWebMvcTest.getTransaction_shouldReturnSingleTransaction`
    - controller method is missing `@PathVariable` on `transactionId`
2. `TripTransactionControllerWebMvcTest.getTransactions_withoutTrailingSlash_shouldReturnAllTransactions`
    - base GET handler currently returns `null`
