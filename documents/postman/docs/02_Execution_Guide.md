# 02 Execution Guide

## Postman Runner
1. Import the collection and environment.
2. Set `baseUrl` in the environment if your API is not on `http://localhost:8080`.
3. Run the full collection in the listed order.
4. Review request-level assertions in the runner summary.

## Newman
### Install dependencies
```bash
npm install
```

### Run locally
```bash
npm run run:local
```

### Generated outputs
- CLI summary in the terminal
- JUnit XML in `reports/junit/results.xml`
- HTML report in `reports/html/report.html`

## Recommended usage pattern
- Use Postman Runner when you want to inspect individual requests interactively.
- Use Newman when you want repeatable automated execution and exportable reports.

## Failure triage
When a request fails:
1. check the HTTP status assertion first
2. inspect the response payload in the Newman or Postman output
3. confirm that the previous request successfully created and stored the required collection variables
4. confirm the application and database were running before the test started
