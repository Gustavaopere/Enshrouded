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

- Placing/seeding a valid core registers exactly one persistent `ShroudCoreState` with a stable UUID in the owning `ServerLevel`/dimension.
- Core block entity stores only the reference/state needed for local interaction; authoritative expansion data remains in that dimension's SavedData.
- Breaking a live core through a permitted server action transitions it to `DESTROYED`, stops future expansion immediately and emits one core-destroy event.
- Stage 01 defines the legal lifecycle shape `DORMANT -> ACTIVE -> DESTROYED -> PURIFIED`, but does **not** decide when a destroyed core has fully regressed. Stage 02 `ShroudRegressionScheduler` owns the runtime `DESTROYED -> PURIFIED` trigger.
- `PURIFIED` means logical threat is terminal: no effective owned Shroud cells/frontier work remain. It does not assert that every physical block has been restored; lazy/safely-skipped terrain cleanup is allowed to outlive the logical transition.
- Piston movement, duplicate placement, explosions and chunk unload/reload cannot duplicate or resurrect a destroyed/purified core.
- Level 1 core maximum radius and growth rate are server-configurable with hard safety clamps.

## TDD / verification

- [x] Unit-test legal lifecycle transitions `DORMANT -> ACTIVE -> DESTROYED -> PURIFIED` and reject resurrection, while keeping the Stage 02 terminal trigger outside Stage 01 runtime behavior.
- [x] GameTest core placement/register/reload and destruction/unregister behavior.
- [x] GameTest explosion/piston edge cases fail closed.
- [x] Verify no expansion work is scheduled for a destroyed or purified core.
- [x] Contract test proves a logically `PURIFIED` core cannot reactivate merely because physical cleanup is incomplete or skipped.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; GameTests and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; Stage 02 remains owner of the runtime `DESTROYED -> PURIFIED` trigger.
- [x] Merged to `main` and renamed with the `✅-` prefix.

## Verified merge record

- Branch: `feat/01-core-lifecycle`
- Initial RED: `c0e03e6c0c5dd00adacf981bdc76da21f79bb254`
- Final implementation HEAD: `950c75b39c0c5c9c3cb2f9fc9203dc3597208647`
- PR: #10 — `Stage 01: implement Shroud core lifecycle`
- Final workflow: `33188789656`
- Final job: `98908621757`
- Verification: GREEN
- Merge SHA: `4eac58f685592e518a6d36c24591bc498003b361`

**Acceptance:** One physical core has one persistent dimension-local identity, can be destroyed exactly once, cannot resurrect, and later reaches the logical terminal `PURIFIED` state independently of best-effort terrain cleanup.
