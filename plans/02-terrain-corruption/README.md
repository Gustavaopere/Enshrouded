# 02 — Terrain Corruption

**Goal:** materialize the logical field into visible, reversible and claim-aware world corruption under strict budgets.

## Task order

1. [`01 materialization rules`](01-materialization-rules.md)
2. [`02 corruption growths`](02-corruption-growths.md)
3. [`03 purification/regression`](03-purification-regression.md)
4. [`04 terrain safety`](04-terrain-safety.md)

## Runtime contracts

- Logical Shroud state remains authority; terrain appearance never drives spread.
- Unknown blocks are not destructively transformed by default.
- Every mutation passes through `MutationAuthority` and a per-tick budget.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
