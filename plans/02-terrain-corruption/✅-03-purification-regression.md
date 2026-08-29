# Enshrouded Plan — Purification and Regression

**Milestone:** Level 1 required.

**Goal:** turn core destruction into gradual logical retreat and safe visual cleanup.

**Planned types:** `ShroudRegressionScheduler`, `PurificationPolicy`, `TerrainRestorationService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/purification/*`.
- Extend terrain rule codec with inverse/cleanup semantics.

## Dependencies

- `04 terrain safety` merged.
- `01 materialization rules` merged.
- 01 core lifecycle.

## Implementation contract

- Destroyed-core regions stop expansion first, then intensity decays over time under a separate bounded regression budget.
- Regression ordering is deterministic and preferably retreats from frontier toward former core center.
- Stage 02 regression is the sole runtime owner of the `CoreLifecycleState.DESTROYED -> PURIFIED` trigger. It performs that transition only when the core no longer owns any effective logical Shroud cells/frontier work in its dimension.
- `PURIFIED` is a logical terminal state, not a guarantee that every physical corrupted block has been restored. The transition never waits for unloaded chunks, safely skipped player-edited blocks, protected claims/containers or unknown/unreversible mappings.
- Native growths are removed gradually when their logical cell clears; cleanup/restoration may continue lazily after the owning core is already logically `PURIFIED`.
- Replaced natural blocks are restored only where a rule has an explicit safe reverse mapping and the current block still matches the expected corrupted state.
- Every cleanup/restoration world mutation passes through the already-merged `MutationAuthority`; purification may be more conservative than corruption but may never bypass claim/container/player-edit safety.
- Sanctuary is **not** a blanket veto for `MutationKind.PURIFICATION`. Once the logical field regresses, authorized restoration/growth cleanup may proceed inside an active ward so stale corrupted visuals are not stranded indefinitely. The same ward still prevents new `CORRUPTION`/`CORE_PLACEMENT`, and purification never edits the latent Shroud field merely because a ward exists.
- If a player changed a corrupted block after corruption, restoration fails closed rather than overwriting the player change. Such a safely skipped visual cannot resurrect logical Shroud or keep the core out of `PURIFIED`.

## TDD / verification

- [x] Unit-test intensity decay and deterministic frontier-to-center regression.
- [x] Unit-test a destroyed core transitions to `PURIFIED` exactly once when its last effective logical cell/frontier entry retires, and cannot re-enter `ACTIVE`/`DESTROYED` work afterward.
- [x] Unit-test/fixture proves a core can become logically `PURIFIED` even when a visual restoration candidate is intentionally skipped for player-edit/protection safety.
- [x] Static/test scan proves restoration/removal mutation sinks route through `MutationAuthority`.
- [x] GameTest player-modified corrupted block is not overwritten during cleanup.
- [x] GameTest native growth disappears as the cell clears only when mutation is authorized.
- [x] GameTest warded cleared cell can purify while new corruption remains vetoed at the same position.
- [x] Save/reload mid-purification resumes from persistent logical state without re-expansion; save/reload after `PURIFIED` never resurrects frontier work even if safe visual leftovers remain.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; GameTests and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; existing Flame Ward/claim adapter pendings remain explicitly cross-stage.
- [x] After merge, this file is renamed with `✅-` and `plans/STATUS.md` is updated in the checkpoint.

## Verification record

- Branch: `feat/02-purification-regression`
- Observed TDD RED: commit `a123cf900c15e9d87835a88786fb9dae31f98504`, workflow `33229924692` — failed at Unit tests before production regression existed.
- Final implementation HEAD: `e7190fc2ad7aaaaa77abb0ef9cd0bf2e04b48d54`.
- Final push verification: workflow `33230760825`, job `99043064368` — GREEN complete.
- PR: #18 — `02 — Purification and Regression`.
- Final PR-head verification: workflow `33230950508`, job `99043572562` — GREEN complete.
- Exact PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.
- Squash merge SHA: `a3b598f9f40e4ad7e0445fb61feea639cba2533b`.

**Acceptance:** Destroyed cores regress to a one-way logical `PURIFIED` terminal state independently of best-effort terrain cleanup, while visible healing remains bounded, safe, mutation-authorized and unable to overwrite later player/protected changes.
