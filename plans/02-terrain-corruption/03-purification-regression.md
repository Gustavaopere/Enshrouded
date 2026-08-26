# Enshrouded Plan — Purification and Regression

**Milestone:** Level 1 required.

**Goal:** turn core destruction into gradual logical retreat and safe visual cleanup.

**Planned types:** `ShroudRegressionScheduler`, `PurificationPolicy`, `TerrainRestorationService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/purification/*`.
- Extend terrain rule codec with inverse/cleanup semantics.

## Dependencies

- 01 materialization rules.
- 01 core lifecycle.

## Implementation contract

- Destroyed-core regions stop expansion first, then intensity decays over time under a separate bounded regression budget.
- Regression ordering is deterministic and preferably retreats from frontier toward former core center.
- Native growths are removed gradually when their logical cell clears.
- Replaced natural blocks are restored only where a rule has an explicit safe reverse mapping and the current block still matches the expected corrupted state.
- If a player changed a corrupted block after corruption, restoration fails closed rather than overwriting the player change.

## TDD / verification

- [ ] Unit-test intensity decay and deterministic frontier-to-center regression.
- [ ] GameTest player-modified corrupted block is not overwritten during cleanup.
- [ ] GameTest native growth disappears as the cell clears.
- [ ] Save/reload mid-purification resumes from persistent logical state without re-expansion.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Destroyed cores cause visible gradual healing with no whole-region block snapshot and no unsafe overwrite of subsequent player edits.
