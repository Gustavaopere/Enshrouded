# 05 — Flame Progression

**Goal:** implement owner-scoped Flame state, altar interaction, sanctuary and the provider-agnostic Level 1 ritual framework.

## Task order

1. [`01 Flame state`](01-flame-state.md)
2. [`02 Flame Altar`](02-flame-altar.md)
3. [`03 sanctuary`](03-sanctuary.md)
4. [`04 Level 1 ritual framework`](04-level1-ritual.md)

## Runtime contracts

- Progression state is persistent and independent of altar block survival.
- Flame Passage is queried through one service used by exposure.
- Sanctuary protection is queried through one ward service used by exposure and mutation.
- Stage 05 owns generic ritual registry/execution/checkpoint semantics only.
- The authentic first Lich Skull and its concrete `enshrouded:lich_manifestation_1` ritual binding are owned by Stage 06, preventing a Stage 05 <-> Stage 06 dependency cycle.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
