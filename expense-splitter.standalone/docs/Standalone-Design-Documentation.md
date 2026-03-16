# Expense Splitter Standalone — Design Documentation

## 1. Purpose
The standalone module contains the **core domain model and settlement engine**. Its job is simple: represent a trip, collect transactions, and compute who owes whom using interchangeable settlement algorithms.

## 2. Main design idea
The design keeps the **business model independent of the user interface and persistence**. `Trip`, `Participant`, `Transaction`, `BalanceSheet`, and `Debt` form the domain core. Settlement logic is plugged in through a `Settler` interface, so the core model does not depend on one fixed algorithm.

## 3. Core flow
1. A `Trip` owns participants and a `BalanceSheet`.
2. Transactions are added to the `BalanceSheet`.
3. `Trip.settle(Settler)` delegates to `BalanceSheet.settle(Settler)`.
4. The chosen settler computes a `List<Debt>`.
5. The result can be shown or persisted by another layer.

## 4. Key classes and responsibilities

### `Trip`
- Aggregate root of the standalone model.
- Owns participants, transactions, and current settlements.
- Provides participant lookup by `participantId`.
- Delegates settlement to the balance sheet.

### `BalanceSheet`
- Stores the list of transactions.
- Delegates settlement to a supplied strategy.
- Acts as the boundary between stored transactions and computed debts.

### `Transaction`
- Immutable-style business record of one expense.
- Captures payer, beneficiaries, category, share type, date, and amount.

### `Debt`
- Output value object representing one payment from one participant to another.

### `Settler`
- Abstraction for settlement algorithms.
- Enables multiple settlement policies without changing the domain model.

### `BasicSettler`
- Computes direct beneficiary-to-payer debts.
- Merges identical debtor-creditor pairs.
- Easy to understand and deterministic.

### `SimplifiedSettler`
- Computes net balances first.
- Uses an exact dynamic-programming approach to maximize disjoint zero-sum groups.
- Produces a globally optimal transfer count for supported input size.

### `SettlerFactory`
- Creates a settler from `SettlementMode`.
- Centralizes algorithm selection.

## 5. GoF design patterns actually used

### Strategy
**Where:** `Settler`, `BasicSettler`, `SimplifiedSettler`

**Why it is used:**
The system supports more than one settlement algorithm. The domain model asks for a `Settler`, not a concrete class. This lets the same `Trip` and `BalanceSheet` work with different settlement behaviors.

**Benefit:**
- new algorithms can be added without rewriting `Trip` or `BalanceSheet`
- testing is easier because each algorithm is isolated
- controller/service layers can choose the algorithm at runtime

### Simple Factory / Factory-style creation
**Where:** `SettlerFactory.create(SettlementMode)`

**Why it is used:**
Object creation for settlement strategies is kept in one place instead of being spread across the codebase.

**Benefit:**
- selection logic is centralized
- callers stay small and readable
- adding a new strategy only changes the factory and enum

## 6. Important non-GoF structural ideas
These are not GoF patterns, but they are important to understanding the design.

### Aggregate-oriented model
`Trip` behaves like the aggregate root: participant changes, transaction changes, and settlement computation all happen through the trip-centric model.

### Value objects
`Participant`, `Transaction`, and `Debt` are used as domain data carriers with clear meaning and low ceremony.

### Separation of stored state and computed state
Transactions are stored as input state; settlements are computed output state. This separation makes recomputation straightforward when transactions change.

## 7. Extensibility points

### Add a new settlement algorithm
Add:
- a new class implementing `Settler`
- a new enum value in `SettlementMode`
- one new branch in `SettlerFactory`

No change is needed in `Trip` or `BalanceSheet`.

### Add a new sharing model
Today the code mainly handles equal sharing and skips sponsored transactions. To add percentage-based or weighted splits, the clean next step is to extract **share calculation** behind its own strategy interface, similar to `Settler`.

### Add richer debt optimization rules
Examples:
- minimize number of creditors touched
- prefer same-currency settlements
- prefer round-number settlements

These rules can be implemented in a new `Settler` without disturbing the rest of the model.

## 8. Why this design works well
- The domain model is small and readable.
- Algorithm choice is explicit.
- The core logic is testable without Spring or a database.
- The model is reusable by CLI, web, batch, or API layers.

## 9. Current limitations
- `SettlerFactory` is a factory-style switch, not a pluggable registry yet.
- Share-type behavior is not fully generalized into its own strategy family.
- `SimplifiedSettler` intentionally limits exact optimization to a bounded number of active participants.
- Validation is partly embedded in settler classes rather than centralized in a domain validation component.

## 10. Suggested future refinement
If the standalone engine grows further, the clean next refactor is:
- extract **share calculation** into a `ShareCalculator` strategy family
- make `SettlerFactory` registry-based for plug-and-play algorithms
- move repeated validation into a dedicated domain validator

## 11. One-line summary
The standalone module is a **domain-first settlement engine** built around a **Strategy pattern for settlement algorithms** and a **factory-based selection mechanism**, designed to stay small, testable, and easy to extend.
