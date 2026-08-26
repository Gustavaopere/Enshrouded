# Enshrouded Plan — Audio and Particles

**Milestone:** Level 1 required.

**Goal:** add atmospheric cues, Madness escalation and core/Deadly effects with bounded particle/sound rates.

**Planned types:** `ShroudParticleController`, `ShroudAmbientAudioController`, `MadnessAudioCue`.

## Files

- Create particle/sound registrations and client controllers under `client/effects/*`.
- Create original sound-event definitions and particle assets.

## Dependencies

- 02 fog/rendering.
- 02 Madness.

## Implementation contract

- Ambient emission is local/player-centered and rate-limited; no server broadcasts every tick.
- Madness cues scale by server-provided stage and can be reduced/disabled independently.
- Core, growth and Red Sludge particles are bounded by distance and client settings.
- External AmbientSounds/Particular mods are not required; coexistence avoids duplicate event ownership where possible.
- No proprietary Enshrouded-game audio assets are copied.

## TDD / verification

- [ ] Unit-test rate limiter/state transitions.
- [ ] Client smoke verifies leaving Shroud stops loops and clears stale sounds.
- [ ] Performance test caps particle spawn count in a dense test scene.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The Shroud feels alive and threatening without unbounded particle/audio spam or external dependencies.
