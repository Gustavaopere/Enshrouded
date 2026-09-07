# Stage 10.08 — Advanced VFX pass

Status: TECHNICALLY COMPLETE / IMPLEMENTATION MERGED / POST-MERGE VERIFIED / ART APPROVED OPEN

## Scope

Stage 10.08 authors bounded transition/event sequences over the presentation seams already established by Stages 07 and 10. It does not add a second client gameplay model and it does not transfer authority from Shroud, Exposure, Madness, Flame, Sanctuary or Story state into rendering code.

The implementation covers the required Level-1 VFX families:

- `CLEAR → SHROUD`;
- `SHROUD → DEADLY`;
- entry into Sanctuary while latent Shroud remains present;
- local Core-proximity density ramp;
- authoritative Core destruction;
- authoritative successful Flame ritual;
- authoritative Lich manifestation;
- Madness-stage escalation.

## Authority boundary

**Stage 03 remains authoritative** for exposure, severity, reserve and Madness. Stage 05 remains authoritative for Flame ritual/progression and Sanctuary. Stage 01/02 remain authoritative for Shroud/Core lifecycle and terrain state. Stage 06 remains authoritative for manifestation lifecycle and Story state.

`AdvancedVfxController` is **presentation-only**. Snapshot-derived cues consume only synchronized `ClientExposureState`; discrete world-event cues arrive only as an ephemeral clientbound `AdvancedVfxPayload` after the corresponding canonical server transition has succeeded.

The VFX layer:

- owns no `SavedData`;
- has no serverbound mutation/query payload;
- does not spawn or replace the Lich provider entity;
- does not decide ritual success, Core destruction, Sanctuary state, Shroud severity or Madness stage;
- does not force chunks;
- does not scan loaded chunks/world state to reconstruct authority;
- does not broadcast effects per render tick.

The first synchronized exposure snapshot after connection establishes a presentation baseline and does not replay a transition. Logout/config reload clears the ephemeral advanced-VFX state so reconnect/reload cannot replay stale cues.

## Authored cue budgets

The canonical hard limits are declared by `AdvancedVfxCue` and additionally clamped by the existing client particle configuration.

| Cue | Max particles | Lifetime | Max distance | Cooldown | Authority source |
| --- | ---: | ---: | ---: | ---: | --- |
| CLEAR → SHROUD | 6 | 20 ticks | 16 blocks | 40 ticks | synchronized Exposure snapshot |
| SHROUD → DEADLY | 10 | 24 ticks | 18 blocks | 60 ticks | synchronized Exposure snapshot |
| Sanctuary entry | 6 | 18 ticks | 16 blocks | 40 ticks | synchronized Exposure snapshot |
| Core destroyed | 16 | 28 ticks | 32 blocks | 80 ticks | server-authored post-destruction cue |
| Flame ritual success | 12 | 28 ticks | 32 blocks | 80 ticks | server-authored post-`APPLIED` cue |
| Lich manifestation | 18 | 30 ticks | 48 blocks | 100 ticks | server-authored post-encounter-start cue |
| Madness escalation | 6 | 20 ticks | 16 blocks | 60 ticks | synchronized Madness stage |

`AdvancedVfxSequenceBudget` spreads each allowed budget across the bounded lifetime instead of emitting the full count in one tick. Per-cue cooldowns prevent rapid crossing/repeated server events from creating an unbounded active sequence stream.

The discrete server emitter iterates only the current `ServerLevel.players()` list and sends the cue only to players inside its declared radius. It never calls `sendParticles`, `getChunk`, chunk-forcing APIs or world scans.

## Core proximity

The existing `ShroudSourceParticlePlanner` remains the local Stage-07 particle source planner. Stage 10.08 changes only the Core density profile:

- outer local range: 2 requested particles per pulse;
- within 50% of configured particle distance: 3;
- within 25%: 4.

The result is always clamped by the single existing `EnshroudedClientConfig.ParticleSettings.maxCount()` and distance limit. No exposure/drain or Core-state calculation is performed client-side.

## Accessibility / reduced effects

Stage 10.08 uses the existing single `EnshroudedClientConfig` authority for presentation settings.

- `AccessibilityProfile.REDUCED_SENSORY` resolves to at most **4 particles** for the whole advanced sequence because the sequence budget is clamped to the resolved `maxCount`.
- `AccessibilityProfile.MINIMAL` resolves particles to disabled / zero, therefore advanced particle sequences emit **0 particles**.
- Madness audio continues to use the dedicated synchronized Madness-audio setting and intensity.
- Other cue audio obeys the ordinary Enshrouded client audio enable/volume settings.
- Config reload resets active/pending advanced sequences immediately.

`REDUCED_SENSORY` and `MINIMAL` change presentation only. They never modify server gameplay state.

## Lodestone decision — NOT ADOPTED for 10.08

Freshness reconciliation on 2026-09-07 confirms the physical 612-mod pack contains **Lodestone 1.8.2** (`lodestone-1.21.1-1.8.2.jar`). The pack also contains AAA Particles `2.2.3` and AAA Particles: World `2.0.0`.

The Stage-10 ADR required the hardest advanced sequences to be prototyped first on the existing Stage-07/10 NeoForge particle/audio/fog/GeckoLib seams. That native prototype is now implemented and has passed the complete repository CI matrix without requiring ribbons, a custom shader pipeline or a second VFX runtime.

Therefore Lodestone is **not adopted** as an Enshrouded dependency for Stage 10.08. There is **no Lodestone compile/runtime dependency** in `build.gradle` or `neoforge.mods.toml`, and production code imports no Lodestone API. AAA Particles likewise remains neighboring pack infrastructure rather than becoming an Enshrouded dependency merely because it is installed.

This is a task-local dependency decision, not a ban on Lodestone forever. A future task may reopen the gate only with a concrete effect that materially requires its API and with fresh API/version/performance evidence.

### Compatibility evidence boundary

Automated evidence proves that the native implementation:

- compiles on NeoForge 21.1.248 / Java 21;
- preserves the existing required GeckoLib dependency only;
- passes unit/authority contracts;
- passes the production build/JAR checks;
- passes GameTests and persistence/reload profiles;
- remains safe on dedicated server.

Automated CI does **not** prove subjective rendering quality or real client composition with Sodium/shader/resource-pack combinations. A Sodium compatibility result therefore remains a **manual** visual gate; it is not inferred from compilation or from Sodium merely being present in the 612-mod pack.

## TDD / validation evidence

Implementation development used multiple explicit RED → GREEN checkpoints:

- initial planner/budget RED: `cc643fe4b60bfad91e2471822212e5b765da64dc`;
- bounded planner/budget GREEN: `d9d00a5449c4fa06f51855da6e833d585d526e43`, which passed both Release Readiness and full Enshrouded CI;
- discrete-cue RED: `89e28ff0e64f783c88d1807c2279db5cba3f7376`, failing at `compileTestJava` before `AdvancedVfxPayload` / `ClientAdvancedVfxState` existed;
- clientbound cue implementation then passed the complete matrix, including dedicated-server save/reload;
- renderer/temporal-budget implementation HEAD `35bfaacee1a2f3fe5b78e0e618922dc3347c502e` passed Release Readiness `34150710640` and Enshrouded CI `34150710595`;
- closeout-contract RED HEAD `8aea9abe6520ddbb8162029db23a3418c49e7cd0` failed Release Readiness `34153754176 / 101841172426` exactly because this canonical 10.08 contract did not yet exist, after the preceding provenance checks passed.

`scripts/ci/test_stage10_advanced_vfx.py` is included in both Enshrouded CI and Level 1 Release Readiness. It locks the task-local Lodestone decision, clientbound-only presentation boundary, hard cue limits and presence of this contract.

### Final implementation and post-merge evidence

- implementation PR: #95 — `Stage 10.08 — Advanced VFX pass`;
- final implementation HEAD: `9a4ff6ef53ff192bb1d28cf1c0e2c5dc8662e5a7`;
- final PR-head Level 1 Release Readiness: `34153873230 / 101841530155` — `completed/success`;
- final PR-head Enshrouded CI: `34153873227 / 101841529843` — `completed/success` across provenance, Stage 10 contracts, unit tests, performance baselines, diff sanity, NeoForge build, GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke;
- PR #95 had no unresolved review threads and no pending review submission at the final gate;
- implementation merge: `00439c1593cf8e0699f6abe4e19d4848d1e97149`;
- post-merge Level 1 Release Readiness: `34154332957 / 101842896487` — `completed/success` on exact `main@00439c1593cf8e0699f6abe4e19d4848d1e97149`;
- post-merge Enshrouded CI: `34154332819 / 101842895697` — `completed/success` across the complete matrix on that same `main` baseline.

The Stage 10.08 technical implementation checkpoint is therefore closed. This documentation closeout records verified evidence only and does not change runtime behavior or promote visual quality to `ART APPROVED`.

## Manual art / compatibility gates

**ART APPROVED remains open.** Technical correctness and CI do not certify final visual quality.

The following are **manual pending gates**:

- full **612-mod** client smoke;
- Sodium coexistence visual check on the current installed version;
- shader-off / shader-on comparison where applicable;
- particles full vs `REDUCED_SENSORY` vs `MINIMAL` comparison;
- fog enabled/disabled comparison;
- day/night review;
- reconnect and dimension-switch review;
- rapid Shroud/Deadly/Sanctuary boundary crossing to check duplicate/spam behavior visually;
- Core destruction, Flame ritual and Lich manifestation review at near/far distances;
- final art-direction approval.

These manual gates are deliberately carried forward to Stage 10.10 where the complete visual QA/release-art matrix is owned. They do not become fabricated automated evidence in Stage 10.08.
