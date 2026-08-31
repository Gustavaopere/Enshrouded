# Enshrouded Plan — Shroud Fog and Color

**Milestone:** Level 1 required.

**Goal:** render ordinary and Deadly Shroud as distinct volumetric-feeling fog/color states using safe NeoForge hooks.

**Planned types:** `ShroudFogController`, `ShroudColorProfile`, `ShroudRenderState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/client/render/*`.
- Create data/color profiles for ordinary and Deadly visuals.
- Add fog-specific values to the shared `EnshroudedClientConfig` established by Task 01; do not register a second client config.

## Dependencies

- 01 HUD/client Shroud state and shared client-config seam.

## Implementation contract

- Ordinary Shroud uses desaturated/arcane fog; Deadly uses stronger red/crimson cues.
- Fog density/color interpolates across zone edges to avoid single-tick flashing.
- Implementation uses supported NeoForge 1.21.1 fog/render events and remains compatible with Sodium-style renderer replacements as far as those hooks allow.
- No mandatory dependency on transitive Veil.
- Fog intensity/enabled settings are read from the already-registered shared client config; fog code does not own config persistence or gameplay state.
- Disabling enhanced fog leaves HUD/particles sufficient to play.

## TDD / verification

- [x] Client/render-state boundary crosses CLEAR/SHROUD/DEADLY targets repeatedly without render-state leakage; interpolation advances exactly once per rendered frame through `RenderFrameEvent.Pre`.
- [x] Unit/client tests cover shared-config fog clamping, live runtime config reads, zero-intensity compatibility and logout reset.
- [x] Performance test records enabled/disabled render-state hot-path overhead.
- [x] Dedicated-server smoke verifies client render classes remain physically client-only.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, SavedData two-boot and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract was introduced; `plans/PENDING.md` requires no change for this task.
- [x] After implementation merge, this file is renamed with `✅-` and `plans/STATUS.md` is updated in the closeout checkpoint.

## Implementation record

- Branch: `feat/07-fog-rendering`.
- Structural RED: `516e4b002756e2949afc41e4786a23f024fe1e1d` — planned render/config APIs absent.
- Hook-boundary RED: `97155c89cdf80d9e825fdc16b0db6fdef55fcffb` — 233 tests, exactly two expected failures before real fog hooks/client-only registration existed.
- NeoForge/Mojmap 1.21.1 compatibility correction: the initial accessor assumption `Minecraft#getDeltaTracker()` was invalid; the intermediate implementation used the real `Minecraft#getTimer()` API before interpolation ownership moved to `RenderFrameEvent.Pre`.
- Zero-intensity compatibility RED: `02c3d11d660675e655f52eef0b56c8f556cf3469` — 233 tests, exactly one expected failure proving intensity `0` still overrode vanilla/other-mod fog.
- Pre-review candidate: `dedcd67f24946eb41eebdef6d48c78455085b0c4`, workflow/job `33363850922` / `99400419223` — GREEN.
- PR: #52 — `Stage 07.02: Shroud fog rendering`.
- Automated review found two valid P2 defects on the pre-review candidate: `RenderFog` could advance interpolation more than once per rendered frame, and static fog weights could survive logout into another connection.
- Review-regression RED: `73a495535d2186fcd329438e9378b5421e5d4ca0`, workflow/job `33384470174` / `99463863007` — 234 tests, exactly two failures matching the two review findings.
- Once-per-frame fix: `193cc3c03aae44e61b8538a22853bd1523c071bb` moves interpolation advancement to `RenderFrameEvent.Pre` and consumes `event.getPartialTick().getGameTimeDeltaTicks()`.
- Logout-state fix/final implementation HEAD: `68aaf638a92cc775ffa30df570ef1727712c9c36`; `ClientExposureLifecycle` resets both exposure presentation state and `ShroudFogController` state at the connection boundary.
- Final exact PR-head verification: workflow `33384653329`, job `99464456432` — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Both P2 review threads were answered and resolved. Fresh Codex re-review on the final HEAD returned no new suggestions and recorded a positive reaction.
- Merge SHA: `08ae3e6d55db798fecd683a671f0414b2d212e2e`.
- Post-merge `main` workflow: `33385717698` — GREEN on the implementation merge SHA.

## Runtime result

`ShroudFogController` is registered only from the physical `Dist.CLIENT` bootstrap and consumes the synchronized `ClientExposureState`; it never writes Exposure, Madness, progression, Shroud cells or any other gameplay authority. `ShroudRenderState` owns only transient presentation weights. `ShroudColorProfile` projects those weights into bounded fog color/plane factors for CLEAR, ordinary SHROUD and DEADLY.

Interpolation advances once per rendered frame through NeoForge `RenderFrameEvent.Pre`, using game-time delta rather than wall-clock time. `ViewportEvent.RenderFog` applies only the already-advanced state and cancels the event only when enhanced fog is actually active. `ViewportEvent.ComputeFogColor` applies the corresponding presentation tint. Underwater/lava/powder-snow fog remains untouched, and fog intensity `0` leaves vanilla/other-mod fog uncancelled.

Sanctuary suppression targets the clear visual state without mutating latent logical Shroud. Logout resets the transient fog state directly, preventing cross-server presentation leakage even if the next connection receives its first Exposure packet before another fog pass.

The single Stage 07 `EnshroudedClientConfig` now owns `fog.enabled` and bounded `fog.intensity`; no parallel client config or mandatory Veil dependency was introduced. No new cross-stage pending contract was created.

**Acceptance:** Ordinary and Deadly Shroud are visually distinct, transitions are bounded/smooth, state does not leak across render passes or connections, and dedicated-server authority remains isolated from client rendering.
