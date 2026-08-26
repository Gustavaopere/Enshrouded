# 04 — Corrupted Ecology

**Goal:** corrupt existing living entities persistently, alter hostility/attributes, classify magic resistance and provide reversible presentation.

## Task order

1. [`01 entity state`](01-entity-corruption.md)
2. [`02 hostility and buffs`](02-hostility-buffs.md)
3. [`03 magic resistance`](03-magic-resistance.md)
4. [`04 visuals/drops/restoration`](04-ecology-visuals.md)

## Runtime contracts

- Original entity type/identity is preserved where possible.
- Corruption is server state; visual layers are client presentation.
- AI changes must be reversible and avoid permanently rewriting third-party goal selectors where a safer target hook exists.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
