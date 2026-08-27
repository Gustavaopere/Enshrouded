# Enshrouded Plan — Accessibility Profiles and Cross-Setting Validation

**Milestone:** Level 1 required.

**Goal:** finalize reduced-distortion presets, color-independent warnings and performance knobs over the shared client config while preserving mechanics.

**Planned types:** `AccessibilityProfile`, `AccessibilityPresetController`.

## Files

- Extend the shared `EnshroudedClientConfig` created in Task 01 with cross-setting accessibility presets/validation; do not introduce another config registration/container.
- Integrate preset application into existing HUD/fog/audio/particle controllers.

## Dependencies

- 01 shared client config/HUD.
- 02 fog/rendering.
- 03 audio/particles.

## Implementation contract

- Task 04 owns accessibility preset semantics and cross-setting validation, not client-config registration.
- Existing shared options cover HUD scale, fog intensity, hallucination/distortion intensity, particle density, ambient audio intensity and screen-flash reduction.
- Presets apply coordinated values through the shared config without bypassing each controller's normal setting path.
- Deadly Shroud warning always has non-color cue (icon/text/audio optional) even when red effects are reduced.
- Client config cannot modify exposure duration, damage, passage requirement or progression.
- Lowest visual preset remains mechanically readable.

## TDD / verification

- [ ] Unit-test accessibility preset mapping plus cross-setting clamping/defaults on the existing shared config.
- [ ] Client smoke lowest-effects preset still displays timer and Deadly warning.
- [ ] Config/preset reload does not leave stale render/audio state.
- [ ] Verify applying a preset does not create or persist a second configuration source.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players can reduce visual/sensory intensity and graphics cost through the single shared client-config seam without weakening server gameplay.
