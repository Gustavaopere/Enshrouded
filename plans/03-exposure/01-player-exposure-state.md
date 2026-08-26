# Enshrouded Plan — Player Exposure State

**Milestone:** Level 1 required.

**Goal:** give each player a persistent/synchronized Shroud exposure reserve with deterministic drain/recovery.

**Planned types:** `ShroudExposureAttachment`, `ExposureService`, `ExposureSnapshot`, `ExposurePayload`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/exposure/*`.
- Register a NeoForge data attachment or equivalent 1.21.1 player persistence mechanism.
- Create tests under `src/test` and `src/gametest`.

## Dependencies

- 01 Shroud Field query service.

## Implementation contract

- Default ordinary-Shroud maximum exposure reserve is configurable and initially 300 seconds, clamped to safe bounds.
- `CLEAR` regenerates reserve; `SHROUD` drains it; `DEADLY` delegates to Deadly policy.
- Drain/recovery is based on server ticks/authoritative delta and remains deterministic through lag spikes within configured caps.
- Death/respawn policy is explicit: respawn restores a safe baseline; disconnect/reconnect cannot reset an active unsafe exploitably without server rules.
- Sync sends changed snapshots rather than every tick.

## TDD / verification

- [ ] Unit-test drain/recovery boundary math and configured max clamping.
- [ ] GameTest crossing zone boundaries changes exposure exactly once per sampled interval.
- [ ] GameTest disconnect/save/reload preserves expected state policy.
- [ ] Network test confirms client cannot submit exposure values.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Ordinary Shroud starts a reliable server timer, safe air recovers it, and state survives normal multiplayer lifecycle correctly.
