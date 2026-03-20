# Expense Splitter

A backend REST application to manage trips, participants, expenses, and settlements.

## What this project does

This project lets users:

- create and manage trips
- add participants to a trip
- record trip expenses
- view all recorded transactions
- compute settlements between participants
- compute both:
  - **basic settlement** — direct balances
  - **simplified settlement** — reduced number of payments
- persist all state in PostgreSQL so the service survives restarts

## Project structure

- `expense-splitter.standalone` — core Java domain and settlement logic
- `expense-splitter.webservice` — Spring Boot REST API and persistence layer

## Tech stack

- Java
- Spring Boot
- PostgreSQL
- Maven
- Docker / Docker Compose

## Notes

- This is a **backend-only** application.
- There is **no frontend/UI** in this repository.
- Settlement state is persisted and recomputed when transactions change.

## Getting started

To run the application locally, please go directly to:

## **[START-WITH-ME.md](START-WITH-ME.md)**

That file contains the deployment steps, setup flow, and commands needed to start the application.

## Documentation

Additional project notes and supporting documents can be placed under the `docs/` folder.
