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

- Use one `ShroudSavedData` state per authoritative `ServerLevel`/dimension. There is no global cross-dimension logical field or spatial index.
- Every core/region/cell/frontier identity lookup is scoped to the level that owns the data. A raw core/region UUID is never resolved against another dimension by accident; any future persisted cross-dimension reference must include the dimension key explicitly.
- Serialize schema version explicitly from the first write.
- Cells are sparse and keyed by a coarse grid coordinate within their owning dimension, not one object per block.
- Persist core UUID, center, tier, lifecycle state, maximum influence radius, expansion seed/epoch and region membership.
- Persist intensity/severity inputs without storing client presentation state.
- Loading an unknown future schema version fails safely with a diagnostic rather than silently corrupting data.

## TDD / verification

- [ ] Round-trip an empty world state, one core/region and a sparse multi-cell region through codec/NBT serialization.
- [ ] Prove idempotent save/load does not duplicate cells or cores.
- [ ] Prove two `ServerLevel`/dimension fixtures with identical local coarse coordinates remain isolated and never share cores/cells/index entries.
- [ ] Prove corrupted input with duplicate IDs or impossible radii is rejected/normalized deterministically.
- [ ] Run a save/reload GameTest after unit GREEN and confirm data is recovered only from the same dimension's storage.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A server restart preserves the exact dimension-local logical Shroud field with bounded serialized data, no duplicate core identities and no cross-dimension state leakage.
