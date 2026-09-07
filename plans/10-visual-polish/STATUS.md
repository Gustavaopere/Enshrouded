# Stage 10 — Visual Polish Status

**State:** 10.05 TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED / P0 IN-GAME ART REVIEW STILL REQUIRED — NEXT: 10.06 SHROUD WORLD-ART FAMILY

**Planning PR:** #80 — `Stage 10 — Art Direction, Hero Assets and Visual Polish` — MERGED
**10.01 implementation PR:** #81 — `Stage 10.01 — Visual Bible and GeckoLib Runtime Contract` — MERGED
**10.01 closeout PR:** #82 — MERGED
**10.02 implementation PR:** #83 — `Stage 10.02 — Flame Altar Hero Asset` — MERGED as `00edf323936f6559c40f30b354ce57d64db152fe`
**10.02 closeout PR:** #84 — MERGED; final verified baseline before 10.03: `main@ccd003cec3cee5e652729fe9225d61ed5f08b54e`
**10.03 implementation PR:** #85 — `Stage 10.03 — Shroud Core Hero Asset` — MERGED as `aa3f96eaa387c49c286d0cf8978554fbd56067c5`
**10.03 closeout PR:** #86 — records final technical/post-merge verification only; no runtime changes.
**10.04 implementation PR:** #87 — `Stage 10.04 — Lich Skull and manifestation presentation` — MERGED as `a54e8f32e85c6b07dd3ace89a301743bfeb669ca`
**10.04 closeout PR:** #88 — documentation-only closeout for final technical/post-merge verification; no runtime changes.
**10.05 implementation PR:** #89 — `Stage 10.05 — Sanctuary / Purification presentation` — MERGED as `771341394045bdef09eb9d9fbb4743aaad1c39f6`.
**10.05 closeout PR:** #90 — documentation-only closeout for final technical/post-merge verification; no runtime changes.

## Planning checkpoint

- [x] Current modlist and Notion Enshrouded dossier consulted before Stage 10 planning.
- [x] Visual gap separated from the already-complete Level 1 gameplay/technical milestone.
- [x] Render/animation dependency ADR defined.
- [x] GeckoLib 4.9.2 selected as primary animated 3D runtime.
- [x] AzureLib evaluated and intentionally not selected as a second parallel Enshrouded animation engine.
- [x] Fusion defined as optional/soft environmental material enhancement with fallbacks.
- [x] Lodestone, OctoLib and Player Animator made task-gated rather than automatic dependencies.
- [x] Visual Bible defined.
- [x] Flame Altar, Shroud Core and Lich Skull marked as P0 hero-asset redesigns.
- [x] Sanctuary/purification and Lich presentation plan defined.
- [x] Multiblock authoritative-anchor contract defined, including 3×3/5×5 Flame Altar prototypes.
- [x] Player-built hero multiblocks must use explicit formation UX: place components → activate/validate → FORMED state.
- [x] FORMED multiblocks must visually read as one authored construction, with hidden/merged seams, coherent silhouette and controller-owned state; visible cube-grid/checkerboard assembly is a rejection criterion.
- [x] Formation animation remains presentation-only and cannot become gameplay authority.
- [x] Shroud world-art family and optional connected/continuous surface strategy defined.
- [x] HUD/UI, VFX and audio polish plan defined.
- [x] Performance, accessibility, provenance, renderer compatibility and dedicated-server gates defined.
- [x] Manual full 607-mod pack smoke retained as an external release gate.
- [x] Canonical implementation sequence 10.01 → 10.10 defined.

## 10.01 — Visual Bible + dependency ADR — COMPLETE

- [x] Reconciled implementation start against current 607-mod pack and Notion Enshrouded dossier.
- [x] Promoted GeckoLib `4.9.2` from compatibility-fixture-only usage to the Enshrouded primary production animation runtime.
- [x] Declared GeckoLib as a required external NeoForge dependency with accepted range `[4.9.2,5.0.0)`; it is not shaded into the Enshrouded JAR.
- [x] Reused the same version property for the Ars Zero real-distribution fixture to prevent dependency drift.
- [x] Kept Fusion soft/optional: no compile/runtime dependency and no gameplay authority.
- [x] Added CI contract tests for GeckoLib versioning, metadata, external provenance and Fusion softness.
- [x] PR #81 final HEAD `33a5da3b734055004323c9789622c52741d5a26d` passed Level 1 Release Readiness `34049582677` and Enshrouded CI `34049582642`.
- [x] PR #81 merged to `main` as `abd938361697d20b431334cff47053a3a2787342`.
- [x] Post-merge Level 1 Release Readiness `34049961298` passed.
- [x] Post-merge Enshrouded CI `34049961432 / 101531662766` passed the full matrix, including Stage 10 visual-stack contract, NeoForge build, GameTests, two-boot SavedData reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.

## 10.02 — Flame Altar hero asset — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Runtime and authority

- [x] Existing `FlameAltarBlockEntity` remains the single inventory/ward/gameplay controller.
- [x] Altar block uses GeckoLib animated BlockEntity rendering instead of the previous vanilla cube placeholder.
- [x] Renderer registration is physical-client-only.
- [x] GeckoLib animation controller contains `idle`, `ritual_available`, `ritual_charge`, `ritual_success`, `level_transition` and `inactive` presentation clips.
- [x] `ritual_success` / `level_transition` are triggered only after canonical `FlameAltarService.Status.APPLIED`.
- [x] Level-transition selection compares canonical `FlameAltarRuntime` progression snapshots before/after ritual execution; menu/client state is not authoritative.
- [x] Animation completion never mutates ritual, Flame Level, passage or ward authority.

### Hero asset package

- [x] Original GeckoLib geo asset with stepped pedestal, four-direction cradle, rune channels, flame volume, broken ritual halo and floating fragments.
- [x] Polished first-party material atlas using dark stone/charcoal, bronze/brass, amber runes and hot flame hierarchy.
- [x] Selective first-party glowmask for runes, flame, halo seams and fragment fissures rather than full-model emissive wash.
- [x] At the 10.02 closeout the editable Blockbench source covered all 26 then-runtime cuboids and both base/emissive texture references; Stage 10.05 subsequently extended both runtime geometry and editable source together, and the CI contract now compares their counts dynamically.
- [x] First-party PNGs registered in provenance.
- [x] Old polished-blackstone-bricks + magma placeholder removed from the Flame Altar presentation.
- [x] Automated Stage 10 contract covers render path, model/animation assets, provenance, editable-source completeness and authority → presentation ordering.

### TDD / automated evidence

- [x] Initial Stage 10.02 RED intentionally failed for missing hero-asset pieces before implementation.
- [x] Authority/presentation RED commit `93d2a941b558c842af894f9e00ad1e3124d95dda` failed exactly at the Stage 10 visual-stack contract before authoritative trigger wiring existed.
- [x] Pre-polish implementation HEAD `ffa824256ebb45e06b1c49e4b6412539a23c8996` passed Level 1 Release Readiness and the complete Enshrouded CI matrix including dedicated-server save/reload smoke.
- [x] Final PR #83 HEAD `6d55a14d1715bf569c6e2dc73ef472e39003f859` passed Level 1 Release Readiness `34057379811 / 101551634235` and Enshrouded CI `34057379719 / 101551633757`.
- [x] PR #83 merged to `main` as `00edf323936f6559c40f30b354ce57d64db152fe`.
- [x] Post-merge Level 1 Release Readiness `34058152231 / 101553745612` passed.
- [x] Post-merge Enshrouded CI `34058152183 / 101553745486` passed the complete matrix: Stage 10 contract, wrapper provenance, unit tests, performance baselines, diff sanity, NeoForge build, GameTests, two-boot SavedData reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.

### Visual/manual acceptance still open

- [ ] P0 approval requires in-game client screenshots/review at realistic FOV and distance, per `03-hero-assets.md`.
- [ ] Reduced-effects view must remain readable.
- [ ] Full 607-mod-pack visual smoke remains an external/manual gate because the complete pack is not vendored into CI.
- [ ] Technical merge does **not** waive the later 3×3/5×5 multiblock formation requirement.

## 10.03 — Shroud Core hero asset — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### TDD and authority

- [x] RED commit `688496e0130a27db7f66063283a88eea45cd25ac` executed 333 tests and failed exactly the six new 10.03 contract clauses before the implementation existed (`Enshrouded CI 34062099651 / job 101564316011`).
- [x] Existing `ShroudCoreBlockEntity` remains the physical/core identity anchor and continues to delegate lifecycle to `ShroudSavedData` / `ShroudCoreService`.
- [x] Existing recovery API, persistent UUID fail-closed behavior, registration queue and configured `EnshroudedConfig.coreMaxInfluenceRadius()` are preserved.
- [x] Shroud Core rendering is GeckoLib `ENTITYBLOCK_ANIMATED`; renderer registration remains client-only.
- [x] `PresentationProfile.ORDINARY|DEADLY` is read-only presentation state. It is not persisted as gameplay authority.
- [x] Deadly presentation is derived only from canonical `ShroudSeverity.DEADLY` and a matching canonical `sourceId`; core `tier` is never used as a visual severity authority.
- [x] Presentation refresh is server-only, position-staggered and bounded to one check per 20 ticks per loaded core; client updates are emitted only when the profile changes.
- [x] Collapse animation trigger is downstream of a successful authoritative ACTIVE → DESTROYED transition and `ShroudCoreDestroyedEvent`; authoritative block/lifecycle removal is never delayed for animation.

### Hero asset package

- [x] Separate Ordinary and Deadly GeckoLib geometry resources rather than a hue-only recolor.
- [x] Ordinary silhouette includes `core_root`, `roots`, asymmetric `outer_husk`, exposed `inner_heart`, `tendrils` and membranes.
- [x] Deadly silhouette opens the husk further, enlarges/exposes the heart and adds a dedicated `deadly_thorns` bone with eight outward spikes.
- [x] `idle`, `threat` and `collapse` animation clips animate heart/husk/tendrils independently; Deadly thorns participate in threat/collapse.
- [x] Separate first-party 128×128 Ordinary/Deadly material atlases and selective glowmasks are present and registered in provenance.
- [x] Editable Blockbench source and technical design sheet are present.
- [x] No Fusion hard dependency was added; optional environmental continuity remains a later world-art concern.

### Final gates

- [x] Final PR #85 HEAD `6eec2983da14edea4c2e7f21fe4a23c45aa29ec8` passed Level 1 Release Readiness `34065431339`.
- [x] Final PR #85 HEAD passed the full Enshrouded CI matrix in `34065429077 / 101573262686`, including provenance, unit/contract tests, NeoForge build, GameTests, two-boot reload, real Ars Zero profile and dedicated-server smoke.
- [x] PR #85 merged to `main` as `aa3f96eaa387c49c286d0cf8978554fbd56067c5`.
- [x] Post-merge Enshrouded CI `34065822861 / 101574272097` passed the complete matrix on `main@aa3f96eaa387c49c286d0cf8978554fbd56067c5`.
- [x] Post-merge Level 1 Release Readiness `34065822757 / 101574271832` passed on the same `main@aa3f96eaa387c49c286d0cf8978554fbd56067c5` baseline.
- [ ] P0 **ART APPROVED** still requires in-game screenshots for Ordinary and Deadly at realistic FOV/distance, reduced-effects readability and external full 607-mod-pack visual smoke.

## 10.04 — Lich Skull + manifestation presentation — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Runtime and authority

- [x] Existing Stage 06 provider, Story State, defeat routing and exactly-once Lich Skull reward path remain canonical and unchanged.
- [x] `LichSkullItem` is now a GeckoLib `GeoItem` rather than a `StandingAndWallBlockItem`/vanilla-skull presentation.
- [x] GeckoLib split-source pattern is used: common item code contains no `net.minecraft.client.*`; the physical client injects `LichSkullRenderProvider` from `EnshroudedClient`.
- [x] `LichSkullRenderer` + `LichSkullGeoModel` own rendering only and add selective emissive treatment through `AutoGlowingGeoLayer`.
- [x] Manifestation spawn VFX occurs only after provider spawn, canonical encounter activation and optional arena activation succeed.
- [x] Defeat VFX occurs only after the persisted encounter validates the dead bound actor and `StorySavedData.defeatEncounter(encounterId)` succeeds.
- [x] `LichManifestationPresentation` has no provider, StorySavedData mutation, actor spawning, reward issuance, chunk forcing or global scan authority.

### Hero asset + VFX package

- [x] Original first-party `geometry.lich_skull_manifestation_1` with elongated ritual mask/skull, broken crown, fragmented halo, detached fragments and arcane fracture layer.
- [x] Editable `art/blockbench/lich_skull_manifestation_1.bbmodel` source is present.
- [x] First-party base texture + selective glowmask are present and provenance-tracked.
- [x] Item model uses `builtin/entity` with authored GUI, ground, fixed/display, first-person and third-person transforms.
- [x] `animation.lich_skull.idle` and `animation.lich_skull.ritual_resonance` are authored; animation completion cannot mutate Story/reward authority.
- [x] First-party `lich_arcana` particle is registered for spawn/defeat presentation.
- [x] Hard VFX budgets are 28 spawn particles, 36 defeat particles and 48-block maximum audience/audio radius.
- [x] `10-04-lich-skull-design-sheet.md` records visual language, render architecture, authority boundaries, budgets and manual review requirements.
- [x] `09-asset-review-matrix.md` now records all Stage 10.04 touched assets explicitly instead of allowing silent compile-to-approval promotion.

### TDD / final gates

- [x] TDD RED commit `7f45cbafa94c438919569acc419ddc2686c607ca` failed exactly at the new Stage 10.04 visual contract (`Enshrouded CI 34068621021 / 101581789264`).
- [x] Initial GREEN work exposed a real NeoForge 1.21.1 `SoundEvent` holder signature mismatch; it was fixed without changing authority semantics.
- [x] The repository architecture test then rejected the first renderer wiring because common code referenced client classes; the implementation was corrected to GeckoLib split-source rather than weakening the boundary test.
- [x] Final PR #87 HEAD `2c79c7e9b6673b89117f38154267aa323962b50c` passed Release Readiness `34071068581 / 101588392524`.
- [x] Final PR #87 HEAD passed Enshrouded CI `34071068613 / 101588392587`: Stage 10 contracts, 333 unit tests, performance baselines, diff sanity, NeoForge build, GameTests, two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- [x] PR #87 merged to `main` as `a54e8f32e85c6b07dd3ace89a301743bfeb669ca`.
- [x] Post-merge Release Readiness `34071344201 / 101589147118` passed on `main@a54e8f32e85c6b07dd3ace89a301743bfeb669ca`.
- [x] Post-merge Enshrouded CI `34071344252 / 101589147239` passed the complete matrix on the same main baseline.

### Visual/manual acceptance still open

- [ ] P0 **ART APPROVED** requires in-game GUI, first-person, third-person, ground/fixed-display and manifestation screenshots at realistic FOV/distance.
- [ ] Reduced-effects readability remains to be reviewed in-game.
- [ ] Full 607-mod-pack visual smoke remains an external/manual gate because the complete pack is not vendored into CI.

## 10.05 — Sanctuary / Purification presentation — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Runtime and authority

- [x] `FlameWardRuntime` remains the loaded-altar Sanctuary lifecycle owner and `FlameWardService` remains the server-authoritative ward query provider.
- [x] `ShroudPurificationRuntime` remains the only logical purification/regression owner; `TerrainRestorationService` remains bounded best-effort terrain healing.
- [x] Client presentation consumes only synchronized `ShroudSample.sanctuarySuppressed` plus the underlying authoritative intensity from `ClientShroudState`; it has no server-write path.
- [x] Sanctuary suppression does not delete latent Shroud state: protected contamination remains visually expressible while gameplay exposure is suppressed by the canonical ward.
- [x] No second Sanctuary provider, second purification state, second terrain pipeline or chunk-forcing path was added.
- [x] Purification Shrine gameplay/multiblock authority is explicitly deferred to 10.09 rather than silently invented in 10.05.

### Presentation package

- [x] Existing Flame Altar hero asset is extended with `ward_focus`, `ward_ring`, `purification_aperture` and `ward_fragments`, keeping the canonical altar as the visible Sanctuary focus.
- [x] Editable Blockbench source and runtime GeckoLib geometry were extended together; the visual-stack contract now checks source element count against actual runtime cube count instead of a stale hardcoded count.
- [x] `animation.flame_altar.sanctuary_active` supplies continuous ward motion and `animation.flame_altar.purification_release` is authored as a presentation-only one-shot reserved for an explicit canonical trigger.
- [x] `SanctuaryPresentationController` emits at most 6 `sanctuary_mote` particles every 6 ticks, respects existing particle settings and performs no global scan or server packet emission.
- [x] `ShroudPurificationPresentation` emits at most 24 motes exactly downstream of a persisted canonical `DESTROYED -> PURIFIED` transition and skips the burst when the core center is not loaded.
- [x] `enshrouded:sanctuary_mote` reuses an existing first-party particle sprite, adding no new third-party binary/provenance surface.
- [x] `09-asset-review-matrix.md` records every 10.05 touched visual surface as `REVIEW_IN_GAME` rather than promoting compile success to art approval.

### TDD / final gates

- [x] TDD RED was observed in Release Readiness `34074014415 / 101596544083` before the required 10.05 implementation files existed.
- [x] The initial RED-state review concern was answered with the subsequent GREEN implementation evidence and its only inline thread was resolved.
- [x] Final PR #89 HEAD `aafc70f24e52e3dd5b70be0188f01eae6cbbbe67` passed Release Readiness `34074557757 / 101598031506`.
- [x] The same PR HEAD passed Enshrouded CI `34074557737 / 101598073190`: Stage 10 contracts, unit tests, performance baselines, diff sanity, NeoForge build, GameTests, two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- [x] PR #89 merged to `main` as `771341394045bdef09eb9d9fbb4743aaad1c39f6`.
- [x] Post-merge Release Readiness `34074967881 / 101599175586` passed on `main@771341394045bdef09eb9d9fbb4743aaad1c39f6`.
- [x] Post-merge Enshrouded CI `34074967864 / 101599175335` passed the complete matrix on the same baseline.

### Visual/manual acceptance still open

- [ ] P0 **ART APPROVED** requires an in-game screenshot of the active Sanctuary ward focus.
- [ ] Sanctuary over latent Shroud must be captured and reviewed at realistic FOV/distance.
- [ ] Terminal purification release must be captured in-game.
- [ ] Reduced-effects / particles-disabled readability remains to be reviewed.
- [ ] Full 607-mod-pack visual smoke remains an external/manual gate because the complete pack is not vendored into CI.

## Multiblock boundary carried forward

Full player-built 3×3/5×5 formation/validation is deliberately not implemented inside 10.02, 10.03, 10.04 or 10.05. The future formation task must preserve the Flame Altar BlockEntity as the authoritative anchor and use the agreed UX: components placed by the player → explicit activation/validation → FORMED state. A FORMED altar that still reads as a Minecraft cube grid/checkerboard is rejected even if mechanically correct. A Purification Shrine may only be introduced in 10.09 if it preserves one canonical authority path with bounded, idempotent, fail-closed formation logic.

## Hard boundaries carried into subsequent tasks

- GeckoLib receives presentation state only; animation completion never mutates gameplay authority.
- AzureLib remains intentionally unused by Enshrouded unless a future ADR replaces the current decision.
- Fusion may improve environmental materials only when a valid base/fallback resource path exists.
- Lodestone, OctoLib and Player Animator remain task-gated rather than automatic dependencies.
- Player-built hero multiblocks are rejected if their FORMED presentation still reads as a normal Minecraft block grid.
- Manual full 607-mod pack smoke remains an external release gate before distribution.
- Do not mark the Flame Altar/Sanctuary focus, Shroud Core or Lich Skull P0 **ART APPROVED** until the required in-game evidence exists.
