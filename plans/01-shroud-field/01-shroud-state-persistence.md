# Enshrouded Plan — Shroud State and Persistence

**Milestone:** Level 1 required.

**Goal:** create sparse, versioned server persistence for Shroud cores, regions, cells and future tier metadata.

**Planned types:** `ShroudSavedData`, `ShroudWorldState`, `ShroudRegionState`, `ShroudCoreState`, `ShroudCellState`, `ShroudSchema`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/state/*`.
- Create `src/test/java/com/gustavaopere/enshrouded/shroud/state/*`.

## Dependencies

- 00 Foundation complete.

## Implementation contract

- Use `SavedData` per dimension/server level as appropriate for NeoForge 1.21.1; serialize schema version explicitly.
- Cells are sparse and keyed by a coarse grid coordinate, not one object per block.
- Persist core UUID, center, tier, lifecycle state, maximum influence radius, expansion seed/epoch and region membership.
- Persist intensity/severity inputs without storing client presentation state.
- Loading an unknown future schema version fails safely with a diagnostic rather than silently corrupting data.

## TDD / verification

- [ ] Round-trip an empty world state, one core/region and a sparse multi-cell region through codec/NBT serialization.
- [ ] Prove idempotent save/load does not duplicate cells or cores.
- [ ] Prove corrupted input with duplicate IDs or impossible radii is rejected/normalized deterministically.
- [ ] Run a save/reload GameTest after unit GREEN.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A server restart preserves the exact logical Shroud field with bounded serialized data and no duplicate core identities.
