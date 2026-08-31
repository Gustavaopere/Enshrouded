# Enshrouded Plan — Audio and Particles

**Milestone:** Level 1 required.

**Goal:** add atmospheric cues, Madness escalation and core/Deadly effects with bounded particle/sound rates.

**Planned types:** `ShroudParticleController`, `ShroudAmbientAudioController`, `MadnessAudioCue`.

## Files

- Create particle/sound registrations and client controllers under `client/effects/*`.
- Create original sound-event definitions and particle assets.
- Add effects-specific values to the shared `EnshroudedClientConfig` established by Task 01; do not register a separate config owner.

## Dependencies

- 01 shared client-config/client-state seam.
- 02 fog/rendering for the established visual-state pipeline.
- 03 Exposure / 02 Madness server-provided stage.

## Implementation contract

- Ambient emission is local/player-centered and rate-limited; no server broadcasts every tick.
- Madness cues scale by server-provided stage and can be reduced/disabled independently.
- Core, growth and Red Sludge particles are bounded by distance and client settings.
- Particle density and ambient-audio intensity are read from the single shared client config; effects controllers never persist an independent copy.
- External AmbientSounds/Particular mods are not required; coexistence avoids duplicate event ownership where possible.
- No proprietary Enshrouded-game audio assets are copied.

## TDD / verification

- [x] Unit tests cover ordinary/Deadly ambient rate limiting, independent Madness cue stage/cooldown behavior, particle source planning and aggregate hard caps.
- [x] Shared-config tests cover audio, Madness-audio and particle clamping; runtime controllers read the shared config at emission time and logout resets transient budgets/cursors.
- [x] Audio is emitted as bounded non-looping one-shot pulses, so leaving/resetting presentation state cannot leave an Enshrouded-owned persistent sound loop; connection-boundary reset is covered by the client lifecycle contract.
- [x] Source-local particle tests/controllers enforce per-source counts, one aggregate `maxCount` budget, distance culling and a maximum of 192 loaded-position samples per pulse.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, SavedData two-boot and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract was introduced; `plans/PENDING.md` requires no change for this task.
- [x] After implementation merge, this file is renamed with `✅-` and `plans/STATUS.md` is updated in the closeout checkpoint.

## Implementation record

- Branch: `feat/07-audio-particles`.
- Baseline asset checkpoint: `ae1562e075d57ca92dc682731768027a889b379a`; workflow `33401136621` — GREEN before the final missing contracts were added.
- Final TDD RED contract commit: `f9368288ed8a10043c0f4141e63fbaf410a9c4f5`; workflow `33407355136` — failed at test compilation with 44 errors exclusively for deliberately absent `MadnessAudioCue`, `MadnessAudioSettings`, distance-aware `ParticleSettings` and `ShroudSourceParticlePlanner` contracts.
- Final implementation HEAD: `13cabf913ff81addb68778e2c6257de5e0f457fb`.
- Final push verification: workflow `33408412199`, job `99541653083` — GREEN.
- PR: #54 — `Stage 07.03: audio and particles`.
- Exact PR-head verification: workflow `33410349784`, job `99548076424` — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- The automated Codex review service reported an account usage-limit condition and produced no technical finding/thread. Manual review of the critical authority, lifecycle, registry and bounded-sampling paths found no merge blocker; the PR had no unresolved review threads.
- Merge SHA: `6b92a99c035237e9e5bc774b20b6619440a1faa0`.
- Post-merge `main` workflow: `33411460873`, job `99551777488` — GREEN on the implementation merge SHA, including all standard gates and 74/74 GameTests.

## Runtime result

`ShroudAmbientController` consumes only the synchronized server-authored `ClientExposureState`. Ordinary/Deadly environmental ambience remains a short non-looping, cooldown-bounded presentation channel. Madness audio is separate: `MadnessAudioCue` derives directly from the synchronized `MadnessStage`, has its own enable/volume configuration and its own cooldown budget, and introduces no client gameplay authority or second Madness progression state.

The task adds project-owned `shroud_core`, `shroud_growth` and `red_sludge` particle registrations, JSON descriptions and original textures. `ShroudParticleController` performs bounded client-side source sampling only around the player, rejects unloaded positions with `hasChunkAt`, caps each pulse to 192 candidate samples, applies configured distance culling and spends one aggregate configured particle budget across all discovered sources. It does not force chunks, enumerate all entities, scan the world globally or persist source state.

The existing single Stage 07 `EnshroudedClientConfig` now owns ordinary ambient audio, independent Madness-audio and particle presentation settings. Connection logout resets audio budgets and the source-sampling cursor. Common bootstrap owns only sound/particle registry declarations while particle providers/controllers remain under the physical `Dist.CLIENT` bootstrap, as verified by the dedicated-server smoke gate.

All sound and particle assets introduced by this task are project-owned/original. The audio provenance record describes deterministic procedural synthesis and explicitly excludes third-party samples or proprietary Enshrouded-game audio. AmbientSounds and Particular remain optional presentation neighbors rather than authorities or dependencies. No new cross-stage pending contract was created.

**Acceptance:** The Shroud feels alive and threatening through bounded, configurable original audio and source-local particle cues without unbounded spam, external dependencies, duplicated gameplay authority or a second client-config owner.
