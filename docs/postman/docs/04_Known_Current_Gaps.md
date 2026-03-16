# 04 Known Current Gaps

The current source code contains two transaction-query endpoint problems:

## 1. GET /trip/{tripId}/transactions
The controller contains a stubbed `@GetMapping` that returns `null`.
Because of that, this route is not included in the active automated path.

## 2. GET /trip/{tripId}/transactions/transaction/{transactionId}
The controller route exists, but the `transactionId` parameter is not correctly bound in the current source.
Because of that, this route is also excluded from the active automated path.

Both requests are preserved in the collection under:
`99 - Known Current API Gaps (disabled)`

Enable them only after the controller is fixed.
