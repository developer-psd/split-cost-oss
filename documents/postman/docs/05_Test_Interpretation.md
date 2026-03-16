# 05 How to Interpret the Results

## Green run
A green run means:
- the main participant-management flow works
- the main settlement flow works
- validation failures are returned with the expected HTTP status and payload shape
- created data is cleaned up by the collection itself

## Simplified settlement assertion
The collection does not hardcode exact settlement values.
Instead it asserts that:
- the endpoint returns a well-formed non-empty array
- the simplified settlement is not longer than the basic settlement

This avoids brittle tests while still validating the contract and the high-level simplification goal.

## Negative tests
Negative tests are expected to pass by returning controlled failure responses such as `400`.
A negative test failing often means:
- the endpoint returned the wrong status
- validation messages changed
- the request did not hit the expected validation branch
