# 03 Request Flow

## Participant Management Flow
Purpose: validate participant add/remove behavior independently of settlement.

Sequence:
1. create a small trip
2. add a participant by name
3. verify the trip now contains four participants
4. remove the added participant by participant ID
5. verify the trip returns to three participants
6. delete the trip

## Settlement E2E Flow
Purpose: validate the main business flow from trip creation through settlement.

Sequence:
1. create an eight-participant trip
2. capture all generated participant IDs by participant name
3. construct a transaction payload dynamically using those IDs
4. post the transactions
5. compute basic settlement
6. compute simplified settlement
7. assert the simplified result is not longer than the basic result
8. delete one created transaction
9. recompute settlement
10. delete the trip

## Negative Validation Flow
Purpose: validate error handling without polluting the happy-path flow.

Sequence:
1. send a blank-name trip request and expect `400`
2. create a temporary trip
3. send an invalid transaction payload and expect `400`
4. delete the temporary trip
