# Enshrouded Plan — Level 1 Ritual Framework

**Milestone:** Level 1 required.

**Goal:** implement the generic Flame ritual engine and Level 1 checkpoint semantics without depending on the Lich Skull item that is created later in Stage 06.

**Planned types:** `FlameRitual`, `FlameRitualRegistry`, `FlameRitualExecutor`, `RitualOutcome`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/ritual/*`.
- Create tests under `src/test/java/.../flame/ritual/*`.

## Dependencies

- 01 Flame state.
- 02 Flame Altar.

This task intentionally has **no Stage 06 dependency**. The concrete `enshrouded:lich_manifestation_1` skull ritual is bound by `06-lich-story/04-lich-skull.md` after the authentic skull item/reward contract exists.

## Implementation contract

- `FlameRitual` describes a stable ritual ID, eligibility check and progression outcome without importing story reward/item implementation classes.
- `FlameRitualRegistry` supports later ritual definitions without hard-coded switch statements and rejects duplicate stable IDs.
- `FlameRitualExecutor` performs eligibility, consumption/result application and progression mutation transactionally/idempotently enough that reconnect/double-click cannot grant the same ritual twice.
- Level 1 checkpoint semantics support marking `nextLevelReady=true`/equivalent story-ready state while **not** granting Passage Level 2 until Level 2 content deliberately exists.
- The engine accepts a test-owned/synthetic ritual implementation in Stage 05 tests; it does not invent or register a fake production Lich Skull.
- Item authenticity/component checks for the real Lich Skull remain Stage 06 responsibility.

## TDD / verification

- [ ] Unit-test ritual registry stable IDs and duplicate registration rejection.
- [ ] Unit-test executor eligibility, idempotence and duplicate activation behavior using a synthetic test ritual.
- [ ] Unit-test Level 1 checkpoint outcome does not raise Passage Level above 1.
- [ ] GameTest altar invokes a registered synthetic ritual exactly once and persists the checkpoint.
- [ ] Leave the concrete defeat -> skull -> altar offering test to `06-lich-story/04-lich-skull.md`.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Stage 05 provides a complete, provider-agnostic ritual execution/checkpoint engine; Stage 06 can bind the authentic first Lich Skull without creating a causal dependency cycle.
