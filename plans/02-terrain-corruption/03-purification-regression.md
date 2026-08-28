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
- Native growths are removed gradually when their logical cell clears.
- Replaced natural blocks are restored only where a rule has an explicit safe reverse mapping and the current block still matches the expected corrupted state.
- Every cleanup/restoration world mutation passes through the already-merged `MutationAuthority`; purification may be more conservative than corruption but may never bypass claim/container/player-edit safety.
- Sanctuary is **not** a blanket veto for `MutationKind.PURIFICATION`. Once the logical field regresses, authorized restoration/growth cleanup may proceed inside an active ward so stale corrupted visuals are not stranded indefinitely. The same ward still prevents new `CORRUPTION`/`CORE_PLACEMENT`, and purification never edits the latent Shroud field merely because a ward exists.
- If a player changed a corrupted block after corruption, restoration fails closed rather than overwriting the player change.

## TDD / verification

- [ ] Unit-test intensity decay and deterministic frontier-to-center regression.
- [ ] Static/test scan proves restoration/removal mutation sinks route through `MutationAuthority`.
- [ ] GameTest player-modified corrupted block is not overwritten during cleanup.
- [ ] GameTest native growth disappears as the cell clears only when mutation is authorized.
- [ ] GameTest warded cleared cell can purify while new corruption remains vetoed at the same position.
- [ ] Save/reload mid-purification resumes from persistent logical state without re-expansion.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Destroyed cores cause visible gradual healing with no whole-region block snapshot, no unsafe overwrite of subsequent player edits, no cleanup path that bypasses canonical terrain safety and no Sanctuary-created pocket of permanently stranded corruption after logical regression.
