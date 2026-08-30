# 06 — Lich and Story

**Goal:** implement persistent recurring-antagonist story state, provider abstraction, first manifestation encounter, unique skull reward and the concrete Level 1 Flame ritual binding.

## Task order

1. [`✅ 01 story state`](✅-01-story-state.md)
2. [`✅ 02 boss provider`](✅-02-boss-provider.md)
3. [`03 first manifestation`](03-first-manifestation.md)
4. [`04 Lich Skull + Flame binding`](04-lich-skull.md)

Current state: **2/4 tasks verified and merged**. The next Stage 06 implementation task is **03 First Manifestation Encounter**.

## Runtime contracts

- External boss entities are encounter actors, never progression authorities.
- Defeating a manifestation cannot kill the canonical Lich story entity permanently.
- Every reward is idempotent and encounter-ID scoped.
- Story persistence is server-global, version-aware and keyed by stable Foundation `ProgressionOwner` identity; physical entity UUIDs are transient encounter fields only.
- Stage 06 owns the authentic first Lich Skull item/component validation and binds it into the generic Stage 05 ritual engine.
- Stage 05 never depends back on Stage 06; the end-to-end defeat -> skull -> altar checkpoint closes here.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
