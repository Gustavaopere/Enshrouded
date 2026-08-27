# Enshrouded Plan — Exposure HUD

**Milestone:** Level 1 required.

**Goal:** establish the shared client-side configuration/state seam and display remaining Shroud time, severity, Madness stage and passage warning accurately.

**Planned types:** `EnshroudedClientConfig`, `ShroudHudOverlay`, `ExposureHudModel`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/config/EnshroudedClientConfig.java` as the single shared client-config registration/container for Stage 07.
- Create `src/main/java/com/gustavaopere/enshrouded/client/hud/*`.
- Create HUD textures/translations.

## Dependencies

- 03 Exposure payload/client state.

## Implementation contract

- Task 01 owns registration/persistence of the one shared Stage 07 client config. Later fog/audio/accessibility tasks extend or consume its documented settings; they must not register parallel client-config containers.
- The shared config is presentation-only. No option may change exposure duration, damage, passage requirements, progression or any other server-authoritative mechanic.
- HUD appears only when relevant and uses synchronized remaining/max exposure values.
- Ordinary vs Deadly state has distinct text/icon treatment independent of color alone.
- Countdown interpolation may be client-smooth but periodically snaps to authoritative snapshots and never predicts death.
- Madness warning thresholds mirror server-sent stage, not duplicate calculations.
- Config supports HUD scale/anchor/visibility without changing gameplay.
- The config seam must be usable by later Stage 07 tasks without importing HUD controller implementation classes.

## TDD / verification

- [ ] Unit-test shared client-config registration/defaults and HUD-specific clamping.
- [ ] Client/unit-test HUD model formatting at 300s, threshold boundaries, zero and Deadly warning.
- [ ] Integrated smoke verifies overlay disappears after returning to safe state.
- [ ] Network desync simulation confirms newer server snapshot overrides interpolation.
- [ ] Dedicated-server smoke confirms client config/HUD implementation classes are not loaded server-side.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The client has one authoritative presentation-config seam, and players always understand how much safe time remains and whether a zone is beyond their Flame passage level.
