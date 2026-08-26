# Enshrouded Plan — Accessibility and Client Configuration

**Milestone:** Level 1 required.

**Goal:** provide reduced distortion, color-independent warnings and performance knobs while preserving mechanics.

**Planned types:** `EnshroudedClientConfig`, `AccessibilityProfile`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/config/EnshroudedClientConfig.java`.
- Integrate settings into HUD/fog/audio/particle controllers.

## Dependencies

- 01-03 client tasks.

## Implementation contract

- Options include HUD scale, fog intensity, hallucination/distortion intensity, particle density, ambient audio intensity and screen-flash reduction.
- Deadly Shroud warning always has non-color cue (icon/text/audio optional) even when red effects are reduced.
- Client config cannot modify exposure duration, damage, passage requirement or progression.
- Lowest visual preset remains mechanically readable.

## TDD / verification

- [ ] Unit-test config clamping/defaults.
- [ ] Client smoke lowest-effects preset still displays timer and Deadly warning.
- [ ] Config reload does not leave stale render/audio state.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players can reduce visual/sensory intensity and graphics cost without weakening server gameplay.
