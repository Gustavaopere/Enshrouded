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
- The persisted ownership graph is bidirectionally consistent: every core references an existing region owned by that core, and every region references an existing core whose `regionId` points back to it.

## TDD / verification

- [x] Round-trip an empty world state, one core/region and a sparse multi-cell region through codec/NBT serialization.
- [x] Prove idempotent save/load does not duplicate cells or cores.
- [x] Prove two `ServerLevel`/dimension fixtures with identical local coarse coordinates remain isolated and never share cores/cells/index entries.
- [x] Prove corrupted input with duplicate IDs, inconsistent core↔region ownership or impossible radii is rejected deterministically.
- [x] Run a save/reload GameTest after unit GREEN and confirm data is recovered only from the same dimension's storage.

## TDD evidence

- Initial RED contract commit `9e3f1a4a5295a9d749ca7edf8a0610ec98de72f0` ran in workflow `33167249451`; implementation followed in `5c522e1bf150bd5e69e2a050b8dfaebb20bfdb9b`.
- Two-boot Shroud SavedData reload harness entered in `0463d556df13c5de045199e2add29d31ea309bc4` / `d0cb58dc88f91afb8fc89391f138131e706a3bc4` and completed GREEN in workflow `33167818552`.
- Review found an additional ownership-graph gap. RED commit `e4b6b61e7bee713ba5deeb7596d620a47f403b67` produced exactly 2 failing tests (`rejectsCoreWhoseRegionIsMissing`, `rejectsRegionWhoseCoreDoesNotMatchItsOwner`) out of 41 in workflow `33168707273`. Production fix `6b8a4fbd550721b74bccc7f1b705d99c62ff054d` enforced bidirectional core↔region integrity.

## Final implementation checkpoint — 2026-08-28

Implementation HEAD `6b8a4fbd550721b74bccc7f1b705d99c62ff054d` passed workflow `33168930216`, job `98841011566` GREEN through:

- wrapper provenance/integrity;
- 43 unit tests;
- diff sanity;
- NeoForge build;
- built-JAR verification;
- GameTest server;
- Shroud SavedData two-boot reload GameTest;
- dedicated-server two-boot save/reload smoke.

The exact merged PR head `3b893d9c54a4252b551f130715dfef7cc6210aab` then passed workflow `33169285643`, job `98842192376` with every committed gate GREEN before PR #8 merged.

## Merge gate

- [x] All task-specific tests are GREEN on the verified implementation HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, Shroud SavedData reload and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; existing later-stage boundaries remain tracked in `plans/PENDING.md`.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

## Merge record

- Branch: `feat/01-shroud-state`
- PR: #8 — `Stage 01: persist dimension-local Shroud state`
- Final PR head: `3b893d9c54a4252b551f130715dfef7cc6210aab`
- Final PR-head CI: workflow `33169285643`, job `98842192376` — GREEN
- Merge SHA on `main`: `46250e44e119c4a7e7adb4d7c23ecf5afe5a5434`

**Acceptance:** A server restart preserves the exact dimension-local logical Shroud field with bounded serialized data, no duplicate/inconsistent core identities and no cross-dimension state leakage.
