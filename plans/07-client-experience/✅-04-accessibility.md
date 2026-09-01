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

- [x] Unit-test accessibility preset mapping plus cross-setting clamping/defaults on the existing shared config.
- [x] Unit-level preset/HUD-model coverage proves `MINIMAL` forces a visible readable HUD and preserves explicit Deadly/passage warning translation keys; no live client-render smoke is part of the current CI.
- [x] Config/preset reload does not leave stale render/audio state.
- [x] Verify applying a preset does not create or persist a second configuration source.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke are GREEN.
- [x] No unresolved cross-stage contract was introduced; `plans/PENDING.md` requires no change for this task.
- [x] After implementation merge and verified post-merge hardening, this file is renamed with `✅-` and `plans/STATUS.md` is updated in the closeout checkpoint.

## Implementation record

- Branch: `feat/07-accessibility`.
- Structural TDD RED: HEAD `27d070fc885c61fdd1bf40ad75885db936381117`, workflow/job `33417094215` / `99570331965` — `compileTestJava` failed with 19 errors exclusively for the deliberately absent Stage 07.04 APIs.
- First complete implementation checkpoint: HEAD `75c16cb37052ded0aa40d574f53d584146771196`, workflow/job `33417436913` / `99571451556` — GREEN across unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Automated review found one valid P2 hot-path allocation defect: every presentation getter rebuilt the complete effective settings bundle. The final implementation lazy-caches one resolved bundle and invalidates it through the existing CLIENT config load/reload handlers.
- Final implementation HEAD: `84b3d0f9d8ce25afdfa9531200a218de89546af4`.
- PR: #56 — `Stage 07.04: accessibility profiles and validation`.
- Final exact PR-head verification: workflow/job `33418623603` / `99575240609` — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Accessibility verification is unit/model level for the lowest-effects presentation: `AccessibilityPresetControllerTest` asserts a visible `MINIMAL` HUD plus explicit `hud.enshrouded.deadly_shroud` and `hud.enshrouded.passage_blocked` model keys. The repository CI does not launch a live client-render smoke, so no such execution is claimed.
- The P2 thread was answered and resolved on the final HEAD before merge.
- Implementation merge SHA: `29ae2d9b7a13bbdffd3291d2fe4213e0705eb8e3`.
- First post-merge workflow `33421208877` exposed a pre-existing timing race in `EntityCorruptionReloadGameTests`: the persisted entity UUID could already be owned by the asynchronous entity-section manager while `ServerLevel.getEntity(UUID)` was not yet visible after the historical fixed 20-tick settle delay. This was a test-fixture defect, not an accessibility runtime regression, and Stage 07 closeout remained blocked until it was hardened.
- Hardening PR: #57 — `Harden entity corruption two-boot reload fixture`; final HEAD `6ab9b91c1b65dc18326ba84c063e3e160ffc71c3`, PR-head workflow/job `33422839116` / `99589178360` — GREEN. The test-only fix replaced the fixed sleep with bounded condition polling and retained fail-closed duplicate-UUID behavior.
- Hardening merge SHA / verified `main`: `a08ff919463cb6ce3ea2a8eda59d74feffa6b6b2`.
- Final independent post-merge verification: workflow/job `33471470436` / `99741928199` — `completed/success`, including unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

## Runtime result

`AccessibilityProfile` defines `CUSTOM`, `REDUCED_SENSORY` and `MINIMAL`. `AccessibilityPresetController` resolves coordinated effective settings entirely through the existing single `EnshroudedClientConfig`: `CUSTOM` preserves configured section values, `REDUCED_SENSORY` bounds fog/audio/Madness/particles/distortion while forcing screen-flash reduction, and `MINIMAL` disables Enshrouded fog/audio/Madness-audio/particles/distortion while retaining a visible readable HUD.

The shared client config remains the only presentation configuration owner. Effective settings are lazy-cached for render/audio/particle hot paths, and the cache is invalidated on the existing CLIENT config Loading/Reloading events before transient fog, ambient-audio and particle state is reset. Preset application therefore neither creates a second persisted configuration source nor leaves stale transient presentation state across config reload.

Accessibility remains presentation-only. The minimal profile cannot hide the authoritative exposure HUD; Deadly Shroud and passage warnings retain text/icon semantics independent of red coloration. No exposure duration, damage, progression, passage requirement, logical Shroud state or server authority is modified. No new cross-stage pending contract was introduced.

**Acceptance:** Players can reduce visual/sensory intensity and graphics cost through the single shared client-config seam without weakening server gameplay.
