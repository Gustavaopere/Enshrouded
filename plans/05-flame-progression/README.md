# 05 — Flame Progression

**Goal:** implement owner-scoped Flame state, altar ritual, sanctuary and the Level 1 completion ritual.

## Task order

1. [`01 Flame state`](01-flame-state.md)
2. [`02 Flame Altar`](02-flame-altar.md)
3. [`03 sanctuary`](03-sanctuary.md)
4. [`04 Level 1 ritual`](04-level1-ritual.md)

## Runtime contracts

- Progression state is persistent and independent of altar block survival.
- Flame Passage is queried through one service used by exposure.
- Sanctuary protection is queried through one ward service used by exposure and mutation.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
