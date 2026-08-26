# Enshrouded Plan — Core Lifecycle

**Milestone:** Level 1 required.

**Goal:** make a physical Level 1 Shroud Core create, own and retire a persistent region.

**Planned types:** `ShroudCoreBlock`, `ShroudCoreBlockEntity`, `ShroudCoreService`, `CoreLifecycleState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/core/*`.
- Create block/block-entity registrations and assets/data for `enshrouded:shroud_core`.
- Test under `src/test` and `src/gametest`.

## Dependencies

- 01 state/persistence.

## Implementation contract

- Placing/seeding a valid core registers exactly one persistent `ShroudCoreState` with a stable UUID.
- Core block entity stores only the reference/state needed for local interaction; authoritative expansion data remains in SavedData.
- Breaking a live core through a permitted server action transitions it to `DESTROYED`, stops future expansion immediately and emits one core-destroy event.
- Piston movement, duplicate placement, explosions and chunk unload/reload cannot duplicate or resurrect a destroyed core.
- Level 1 core maximum radius and growth rate are server-configurable with hard safety clamps.

## TDD / verification

- [ ] Unit-test legal lifecycle transitions `DORMANT -> ACTIVE -> DESTROYED -> PURIFIED` and reject resurrection.
- [ ] GameTest core placement/register/reload and destruction/unregister behavior.
- [ ] GameTest explosion/piston edge cases fail closed.
- [ ] Verify no expansion work is scheduled for a destroyed core.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** One physical core has one persistent identity, can be destroyed exactly once, and its lifecycle is authoritative after reload.
