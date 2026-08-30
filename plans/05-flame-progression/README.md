# 05 — Flame Progression

**Goal:** implement owner-scoped Flame state, provider-agnostic ritual execution, altar interaction and sanctuary in causal order.

## Task order

The file numbering is historical; implementation order is causal:

1. [`✅ 01 Flame state`](✅-01-flame-state.md)
2. [`✅ 04 Level 1 ritual framework`](✅-04-level1-ritual.md)
3. [`✅ 02 Flame Altar`](✅-02-flame-altar.md)
4. [`03 sanctuary`](03-sanctuary.md)

Current state: **3/4 tasks verified and merged**. The remaining Stage 05 implementation task is **03 Sanctuary**.

## Runtime contracts

- Progression state is persistent and independent of altar block survival.
- Flame Passage is queried through one service used by exposure.
- The generic ritual registry/executor exists before the altar block/menu that invokes it.
- The altar is a physical interaction adapter over the already-merged ritual engine; it does not own ritual/progression truth.
- Sanctuary protection is queried through one ward service used by exposure and mutation.
- Stage 05 owns generic ritual registry/execution/checkpoint semantics only.
- The authentic first Lich Skull and its concrete `enshrouded:lich_manifestation_1` ritual binding are owned by Stage 06, preventing a Stage 05 <-> Stage 06 dependency cycle.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
