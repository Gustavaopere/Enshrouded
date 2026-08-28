# Enshrouded Plan — Level 1 Ritual Framework

**Milestone:** Level 1 required.

**Goal:** implement the generic Flame ritual engine and Level 1 checkpoint semantics before any altar UI/block adapter and without depending on the Lich Skull item created later in Stage 06.

**Planned types:** `FlameRitual`, `FlameRitualRegistry`, `FlameRitualExecutor`, `RitualOutcome`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/ritual/*`.
- Create tests under `src/test/java/.../flame/ritual/*`.

## Dependencies

- 01 Flame state.

This task intentionally has **no Flame Altar or Stage 06 dependency**. The engine is provider/UI-agnostic. Task `02-flame-altar` later invokes it as a physical adapter; the concrete `enshrouded:lich_manifestation_1` skull ritual is bound by `06-lich-story/04-lich-skull.md` after the authentic skull item/reward contract exists.

## Implementation contract

- `FlameRitual` describes a stable ritual ID, eligibility check, offering/intent contract and progression outcome without importing altar-menu or story-reward/item implementation classes.
- `FlameRitualRegistry` supports later ritual definitions without hard-coded switch statements and rejects duplicate stable IDs.
- `FlameRitualExecutor` resolves/receives the canonical `ProgressionOwner` once at invocation start and uses that immutable owner snapshot for eligibility, consumption, result application and progression mutation; it must not re-resolve the invoking player after the transaction begins.
- Executor writes are transactional/idempotent enough that reconnect, duplicate invocation or a team-membership change during the same operation cannot grant the ritual twice or redirect the result to a different owner.
- The executor exposes a server-side invocation seam that later physical adapters (Flame Altar) can call without becoming progression authorities.
- Level 1 checkpoint semantics support marking `nextLevelReady=true`/equivalent story-ready state while **not** granting Passage Level 2 until Level 2 content deliberately exists.
- The engine accepts a test-owned/synthetic ritual implementation in Stage 05 tests; it does not invent or register a fake production Lich Skull.
- Item authenticity/component checks for the real Lich Skull remain Stage 06 responsibility.

## TDD / verification

- [ ] Unit-test ritual registry stable IDs and duplicate registration rejection.
- [ ] Unit-test executor eligibility, idempotence and duplicate invocation behavior using a synthetic test ritual.
- [ ] Unit-test owner snapshot behavior: changing the resolver result after invocation starts cannot redirect or duplicate the in-flight ritual outcome.
- [ ] Unit-test Level 1 checkpoint outcome does not raise Passage Level above 1.
- [ ] Unit-test a fake server-side caller invokes a registered synthetic ritual exactly once without any altar classes present.
- [ ] Leave physical altar integration to `02-flame-altar.md` and concrete defeat -> skull -> altar offering to `06-lich-story/04-lich-skull.md`.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Stage 05 provides a complete, provider-agnostic ritual execution/checkpoint engine with immutable per-transaction owner semantics before the altar adapter exists; Stage 06 can later bind the authentic first Lich Skull without any causal dependency cycle.
