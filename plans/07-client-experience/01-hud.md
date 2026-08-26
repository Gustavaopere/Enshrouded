# Enshrouded Plan — Exposure HUD

**Milestone:** Level 1 required.

**Goal:** display remaining Shroud time, severity, Madness stage and passage warning accurately.

**Planned types:** `ShroudHudOverlay`, `ExposureHudModel`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/client/hud/*`.
- Create HUD textures/translations.

## Dependencies

- 03 Exposure payload/client state.

## Implementation contract

- HUD appears only when relevant and uses synchronized remaining/max exposure values.
- Ordinary vs Deadly state has distinct text/icon treatment independent of color alone.
- Countdown interpolation may be client-smooth but periodically snaps to authoritative snapshots and never predicts death.
- Madness warning thresholds mirror server-sent stage, not duplicate calculations.
- Config supports HUD scale/anchor/visibility without changing gameplay.

## TDD / verification

- [ ] Client/unit-test HUD model formatting at 300s, threshold boundaries, zero and Deadly warning.
- [ ] Integrated smoke verifies overlay disappears after returning to safe state.
- [ ] Network desync simulation confirms newer server snapshot overrides interpolation.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players always understand how much safe time remains and whether a zone is beyond their Flame passage level.
