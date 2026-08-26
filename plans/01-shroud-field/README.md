# 01 — Shroud Field

**Goal:** implement the authoritative persistent corruption field, core lifecycle, bounded spread, querying and initial world seeding.

## Task order

1. [`01 state/persistence`](01-shroud-state-persistence.md)
2. [`02 core lifecycle`](02-core-lifecycle.md)
3. [`03 frontier expansion`](03-frontier-expansion.md)
4. [`04 zone query/sync`](04-zone-query-sync.md)
5. [`05 core seeding`](05-core-seeding.md)

## Runtime contracts

- Logical state is authoritative even when no affected chunk is loaded.
- No query or expansion path forces chunk loads.
- Every field cell is attributable to an owning core/region and schema-versioned for save upgrades.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
