# Enshrouded Plan — Shroud Fog and Color

**Milestone:** Level 1 required.

**Goal:** render ordinary and Deadly Shroud as distinct volumetric-feeling fog/color states using safe NeoForge hooks.

**Planned types:** `ShroudFogController`, `ShroudColorProfile`, `ShroudRenderState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/client/render/*`.
- Create client config/data profiles for ordinary and Deadly visuals.

## Dependencies

- 01 HUD/client Shroud state.

## Implementation contract

- Ordinary Shroud uses desaturated/arcane fog; Deadly uses stronger red/crimson cues.
- Fog density/color interpolates across zone edges to avoid single-tick flashing.
- Implementation uses supported NeoForge 1.21.1 fog/render events and remains compatible with Sodium-style renderer replacements as far as those hooks allow.
- No mandatory dependency on transitive Veil.
- Disabling enhanced fog leaves HUD/particles sufficient to play.

## TDD / verification

- [ ] Client smoke crosses zone boundaries repeatedly without render-state leakage.
- [ ] Performance test records frame/render-hook overhead with fog enabled/disabled.
- [ ] Dedicated server verifies no client render class is loaded server-side.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Ordinary and Deadly Shroud are visually unmistakable, smooth at boundaries and server-safe.
