# Enshrouded Plan — Level 1 Ritual Framework

**Milestone:** Level 1 required.

**Status:** ✅ Complete and merged.

**Implementation branch:** `feat/05-level1-ritual`

**Final implementation HEAD:** `b3f6c0cabb3f4aa91191cf4a1197cadc68214a35`

**PR:** #32 — `05.04 — Generic Flame ritual framework`

**Final exact-head CI:** workflow `33290135711` / run #895 — GREEN.

**Merge SHA:** `37d100e7ea6511a76d954b640f6347bbb598d1d9`

**Goal:** implement the generic Flame ritual engine and Level 1 checkpoint semantics before any altar UI/block adapter and without depending on the Lich Skull item created later in Stage 06.

**Implemented types:** `FlameRitual`, `FlameRitualRegistry`, `FlameRitualExecutor`, `RitualOutcome`.

## Files

- `src/main/java/com/gustavaopere/enshrouded/flame/ritual/*`.
- Unit tests under `src/test/java/.../flame/ritual/*`.
- Real NeoForge server integration coverage under `src/gameTest/java/.../flame/ritual/*`.

## Dependencies

- ✅ 01 Flame state.

This task intentionally has **no Flame Altar or Stage 06 dependency**. The engine is provider/UI-agnostic. Task `02-flame-altar` invokes it later as a physical adapter; the concrete `enshrouded:lich_manifestation_1` skull ritual remains owned by `06-lich-story/04-lich-skull.md` after the authentic skull item/reward contract exists.

## Implementation contract

- [x] `FlameRitual` describes a stable ritual ID, eligibility check, offering/intent contract and progression outcome without importing altar-menu or story-reward/item implementation classes.
- [x] `FlameRitualRegistry` supports later ritual definitions without hard-coded switch statements and rejects duplicate stable IDs.
- [x] `FlameRitualExecutor` resolves the canonical `ProgressionOwner` exactly once at invocation start and uses that immutable owner snapshot for eligibility, consumption, result application and progression mutation.
- [x] Executor writes are serialized/idempotent so duplicate invocation cannot grant the same ritual twice or redirect the result to a different owner during the same operation.
- [x] The executor exposes a real server-side invocation seam through `FlameRitualExecutor.forServer(...)` that later physical adapters can call without becoming progression authorities.
- [x] Level 1 checkpoint semantics persist `nextLevelReady=true` while keeping Flame Level 1 and Passage Level 1.
- [x] Persistent schema advanced from v1 to v2 with explicit v1 migration/default handling for the new readiness flag; future/invalid schemas continue to fail closed.
- [x] The engine accepts only test-owned/synthetic ritual implementations in Stage 05 verification; no fake production Lich Skull is registered.
- [x] Item authenticity/component checks for the real Lich Skull remain Stage 06 responsibility.

## TDD / verification

- [x] Unit-test ritual registry stable IDs and duplicate registration rejection.
- [x] Unit-test executor eligibility, idempotence and duplicate invocation behavior using a synthetic test ritual.
- [x] Unit-test owner snapshot behavior: changing the resolver result after invocation starts cannot redirect or duplicate the in-flight ritual outcome.
- [x] Unit-test Level 1 checkpoint outcome does not raise Passage Level above 1.
- [x] Unit-test a fake server-side caller invokes a registered synthetic ritual exactly once without any altar classes present.
- [x] GameTest a real NeoForge server-side caller invokes the synthetic ritual through `FlameRitualExecutor.forServer(...)`, consumes its offering once, persists the checkpoint and leaves Flame/Passage at Level 1.
- [x] Physical altar integration remains in `02-flame-altar.md`; concrete defeat -> skull -> altar offering remains in `06-lich-story/04-lich-skull.md`.

TDD RED evidence: commit `db7df85374a8065f435b8650f97c7b024eb3ebe7`, workflow `33289604543`, failed at test compilation because the planned ritual framework and readiness API did not yet exist.

The final HEAD received two complete GREEN runs on identical code: workflow `33289912453` / run #894 and workflow `33290135711` / run #895. The latter is the merge gate of record.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build and production JAR verification are GREEN.
- [x] GameTest server is GREEN, including the real server invocation seam.
- [x] SavedData two-boot reload GameTest is GREEN.
- [x] Dedicated-server save/reload smoke is GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` records that the Stage 05 side of `ENSH-L1-OWNER-SNAPSHOT-001` is now proven while Stage 06/08 closure remains open.
- [x] This task file is renamed with the `✅-` prefix and `plans/STATUS.md` is updated in the same closeout checkpoint.

**Acceptance:** Stage 05 now provides a complete, provider-agnostic ritual execution/checkpoint engine with immutable per-transaction owner semantics before the altar adapter exists; Stage 06 can later bind the authentic first Lich Skull without any causal dependency cycle.
