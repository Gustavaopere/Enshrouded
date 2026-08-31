# 06 — Lich and Story

**Goal:** implement persistent recurring-antagonist story state, provider abstraction, first manifestation encounter, unique skull reward and the concrete Level 1 Flame ritual binding.

## Task order

1. [`✅ 01 story state`](✅-01-story-state.md)
2. [`✅ 02 boss provider`](✅-02-boss-provider.md)
3. [`✅ 03 first manifestation`](✅-03-first-manifestation.md)
4. [`✅ 04 Lich Skull + Flame binding`](✅-04-lich-skull.md)

Current state: **4/4 tasks verified and merged. Stage 06 is complete.**

## Runtime contracts

- External boss entities are encounter actors, never progression authorities.
- Defeating a manifestation cannot kill the canonical Lich story entity permanently.
- Every reward is idempotent and encounter-ID scoped.
- Story persistence is server-global, version-aware and keyed by stable Foundation `ProgressionOwner` identity; physical entity UUIDs are transient encounter fields only.
- Stage 06 owns the authentic first Lich Skull item/component validation and binds it into the generic Stage 05 ritual engine.
- The first-manifestation reward commits `rewardIssued=true` only after physical skull delivery succeeds; failed insertion leaves the reward pending and retryable.
- Stage 05 never depends back on Stage 06; the end-to-end defeat -> skull -> altar checkpoint is closed here.
- Completing the Level 1 skull offering records the ritual checkpoint and `nextLevelReady=true` while Flame Level and Passage Level remain 1.

All four Stage 06 tasks are verified and merged. Later stages may consume these contracts but must not redefine their ownership or persistence semantics.
