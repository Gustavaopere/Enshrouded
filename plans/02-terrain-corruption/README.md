# 02 — Terrain Corruption

**Goal:** materialize the logical field into visible, reversible and claim-aware world corruption under strict budgets.

## Task order

The file numbering is historical; implementation order is causal and safety-first:

1. [`✅ 04 terrain safety`](✅-04-terrain-safety.md)
2. [`01 materialization rules`](01-materialization-rules.md)
3. [`02 corruption growths`](02-corruption-growths.md)
4. [`03 purification/regression`](03-purification-regression.md)

## Runtime contracts

- Logical Shroud state remains authority; terrain appearance never drives spread.
- Unknown blocks are not destructively transformed by default.
- `DefaultMutationAuthority` exists before the first world-mutating Stage 02 branch.
- Every mutation passes through the Foundation `MutationAuthority` contract and a per-tick budget.
- `MutationKind` is Foundation-owned and is reused by all Stage 02 services; Stage 02 must not introduce a duplicate mutation-kind model.
- Sanctuary/claim/protection inputs feed the same `MutationAuthority`; no later task may add a parallel terrain gate.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
