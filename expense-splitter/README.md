# Expense Splitter
## Context
- Trip: In the context of a defined trip, there can be multiple transactions.
- Transaction: Defines the amount of money spent on a specific activity and the details of who spent it. ```(cost, spentBy, spentOn)```

## Actors
- Individuals involved in the trip.

## Available Features
- Split the expenses between the participants rudimentarily such that each knows how much to pay the other.
- Compute the split such that settlements are simplified and there is the least number of transactions to settle debts among participants.

## APIs
- You can find the API documentation in the following folder:
    - root/docs/postman/Postman/Expense Splitter.postman_collection.json
    - Follow the details given in "start-with-me.md" to setup the postman environment.
    - You can find the API documentation in the Postman collection.
    - Flows are made available in the Collection's folders which can be viewed in Postman.

## Design Documentations
- Design documentations are available in the root folder's docs folder.
- high-level-design-description.md
- inside root/expense-splitter/docs/design.md