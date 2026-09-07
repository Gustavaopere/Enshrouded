# Stage 10.05 — Sanctuary / Purification presentation

Status: **TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED — P0 IN-GAME ART REVIEW STILL REQUIRED**.

## Authority and scope

Stage 10.05 is presentation-only. It **does not create** a second Sanctuary state, a second purification lifecycle, a second terrain-restoration pipeline, or a second Flame provider.

Canonical ownership remains:

- `FlameWardRuntime` owns the loaded-altar Sanctuary lifecycle.
- `FlameWardService` is the server-authoritative `FlameWardQuery` provider.
- `ShroudPurificationRuntime` owns logical regression and the `DESTROYED -> PURIFIED` terminal transition.
- `TerrainRestorationService` remains best-effort loaded-world visual healing.
- `ShroudSample.sanctuarySuppressed` is the synchronized presentation fact consumed by the physical client.

The Stage 10 code may render or emit VFX downstream of those facts. It may not mutate them.

## Sanctuary visual language

The existing Flame Altar is the physical Sanctuary focus because every loaded altar already activates exactly one canonical ward through `FlameWardRuntime.onAltarLoaded(...)`. Stage 10.05 therefore extends the existing first-party Flame Altar GeckoLib hero asset instead of registering a decorative block that could falsely imply authority.

The ward-focus assembly adds:

- `ward_ring`: low horizontal runic boundary rotating slowly around the altar;
- `purification_aperture`: four vertical clean-light posts framing the central Flame;
- `ward_fragments`: small floating fragments below the primary halo;
- `animation.flame_altar.sanctuary_active`: continuous low-amplitude ward motion;
- `animation.flame_altar.purification_release`: authored one-shot art state retained for a future explicit altar-local trigger if canonical gameplay ever exposes one.

The assembly reuses the project-authored Flame Altar texture/glowmask and remains visually subordinate to the primary Flame/halo silhouette.

## Latent Shroud behavior

A Sanctuary over **latent Shroud** must remain legible as a protected contaminated place, not look as if the Shroud were deleted.

`SanctuaryPresentationController` reads only `ClientShroudState.INSTANCE.sample()`. While `sanctuarySuppressed == true` and underlying `sample.intensity() > 0`, it emits a small number of upward clean motes around the local player. Shroud geometry, growth, terrain residue and the underlying synchronized intensity remain visible. The controller neither scans the world nor asks the server for state.

If particles are disabled or capped by the existing client accessibility/performance configuration, the enhancement fails soft and gameplay remains unchanged.

## Purification sequence

Purification success presentation is attached directly downstream of the canonical terminal transition. `ShroudPurificationRuntime` first persists the new state and proves `DESTROYED -> PURIFIED`; only then `ShroudPurificationPresentation.onPurified(...)` emits a bounded release burst at the canonical core center and the existing `ShroudCorePurifiedEvent` is posted.

The presentation bridge:

- emits at most 24 particles for one terminal transition;
- does not force chunks;
- skips the burst when the core center is not loaded;
- does not mutate `ShroudSavedData`;
- does not run regression or terrain restoration;
- does not grant rewards or progression.

The particle type is `enshrouded:sanctuary_mote`. Its descriptor intentionally reuses the existing project-authored Shroud particle sprite in this checkpoint, avoiding a redundant binary asset while the ward geometry supplies the primary visual identity.

## Optional Purification Shrine multiblock decision

**DEFERIDO PARA 10.09.** Stage 10.05 does not register a Purification Shrine multiblock, controller, satellite, recipe, validator or gameplay provider.

Reason: the current canonical Sanctuary authority is already the loaded Flame Altar ward. Introducing a second physical controller in 10.05 would either duplicate authority or require a new gameplay contract. Stage 10.09 is the dedicated multiblock/set-piece pass and may revisit a shrine only if it remains a presentation/activation shell over one canonical provider with bounded, idempotent, fail-closed validation.

## Performance and compatibility invariants

- client Sanctuary motes: pulse every 6 ticks, at most 6 motes per pulse;
- terminal purification burst: at most 24 motes, once per canonical transition;
- no per-frame global scans;
- no chunk forcing;
- no server packet emitted from the client controller;
- Sodium compatibility is preserved because the effect uses vanilla/NeoForge particle rendering and the existing GeckoLib renderer path;
- Fusion remains optional; Stage 10.05 adds no hard dependency;
- dedicated server never loads `SanctuaryPresentationController` because it is referenced only by the Dist.CLIENT bootstrap.

## Automated acceptance

Contract gate: `scripts/ci/test_stage10_sanctuary_presentation.py`.

Required repository verification after implementation:

1. Stage 10 Sanctuary contract test GREEN;
2. existing Stage 10 visual-stack and Lich presentation tests GREEN;
3. unit tests GREEN;
4. NeoForge build GREEN;
5. GameTests GREEN;
6. SavedData two-boot reload GREEN;
7. real Ars Zero distribution profile GREEN;
8. dedicated-server smoke GREEN;
9. PR CI / Release Readiness GREEN.

## Verification / closeout evidence

- TDD RED was observed before implementation in Release Readiness `34074014415 / 101596544083`; the new 10.05 contract failed because the required presentation files did not yet exist.
- Final implementation PR #89 HEAD `aafc70f24e52e3dd5b70be0188f01eae6cbbbe67` passed Level 1 Release Readiness `34074557757 / 101598031506`.
- The same PR HEAD passed Enshrouded CI `34074557737 / 101598073190`, including Stage 10 contracts, unit tests, performance baselines, diff sanity, NeoForge build, GameTests, two-boot SavedData reload, the real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- PR #89 merged to `main` as `771341394045bdef09eb9d9fbb4743aaad1c39f6`.
- Post-merge Level 1 Release Readiness `34074967881 / 101599175586` passed on `main@771341394045bdef09eb9d9fbb4743aaad1c39f6`.
- Post-merge Enshrouded CI `34074967864 / 101599175335` passed the complete matrix on the same baseline.
- Automated technical verification does not promote the art to P0 `ART APPROVED`; the manual evidence below remains required.

## Manual art gates

The following remain visual review gates and must not be falsely marked automated:

- [ ] in-game screenshot of active Sanctuary ward focus;
- [ ] in-game screenshot of Sanctuary over latent Shroud;
- [ ] in-game capture of terminal purification release;
- [ ] reduced-effects/particles-disabled verification;
- [ ] full 607-mod client smoke.
