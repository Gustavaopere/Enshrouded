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

- [ ] Unit-test intensity decay and deterministic frontier-to-center regression.
- [ ] Unit-test a destroyed core transitions to `PURIFIED` exactly once when its last effective logical cell/frontier entry retires, and cannot re-enter `ACTIVE`/`DESTROYED` work afterward.
- [ ] Unit-test/fixture proves a core can become logically `PURIFIED` even when a visual restoration candidate is intentionally skipped for player-edit/protection safety.
- [ ] Static/test scan proves restoration/removal mutation sinks route through `MutationAuthority`.
- [ ] GameTest player-modified corrupted block is not overwritten during cleanup.
- [ ] GameTest native growth disappears as the cell clears only when mutation is authorized.
- [ ] GameTest warded cleared cell can purify while new corruption remains vetoed at the same position.
- [ ] Save/reload mid-purification resumes from persistent logical state without re-expansion; save/reload after `PURIFIED` never resurrects frontier work even if safe visual leftovers remain.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Destroyed cores regress to a one-way logical `PURIFIED` terminal state independently of best-effort terrain cleanup, while visible healing remains bounded, safe, mutation-authorized and unable to overwrite later player/protected changes.
