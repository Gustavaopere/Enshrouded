# Stage 10 — Visual Polish Status

**State:** 10.09 LOGIC/AUTHORITY IMPLEMENTED / ACQUISITION FIX + RECONCILIATION MERGED + VERIFIED / SHELL ART + SET-PIECE CONSUMERS OPEN — 10.10 NOT STARTED

**Current physical-pack authority:** 602 mods on NeoForge `21.1.248` from the latest 2026-09-08 physical modlist. References to 603/607/612 below are retained as checkpoint-time evidence for earlier Stage 10 tasks, not as the current pack count.

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
**10.06 implementation PR:** #91 — `Stage 10.06 — Shroud world-art family` — MERGED as `19b9cee08cf5b5f369497ad4c8c0329eff65253d`.
**10.06 closeout PR:** #92 — documentation-only closeout for final technical/post-merge verification; no runtime changes.
**10.07 implementation PR:** #93 — `Stage 10.07 — HUD / UI art` — MERGED as `341aa508bcd997cf32b7d550c9a37fca19f36ff3`.
**10.07 closeout PR:** #94 — documentation-only closeout for final technical/post-merge verification; no runtime changes.
**10.08 implementation PR:** #95 — `Stage 10.08 — Advanced VFX pass` — MERGED as `00439c1593cf8e0699f6abe4e19d4848d1e97149`.
**10.08 closeout PR:** #96 — documentation-only closeout for final technical/post-merge verification; no runtime changes.
**10.09 implementation PR:** #97 — `Stage 10.09 — Multiblock and set-piece pass` — MERGED as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`.
**10.09 restart-harness correction PR:** #99 — MERGED into the current lineage as `650c5c99308415a94ed51c9abd9abea1b5b26c10`.
**10.09 survival-acquisition correction PR:** #100 — MERGED as `ed122c42f0eee0e706219361e30c9e1f05416a6d`; independent post-merge Release Readiness `34226166892` and Enshrouded CI `34226166913 / 102060710827` GREEN.
**10.09 reconciliation PR:** #98 — final HEAD `43bf73df5d4c3c93a731ee5d88397b5633badd1c`; MERGED as `fa9552daa0372bf5061708a93ce5c263257df5d2`; independent post-merge Release Readiness `34234079530` and Enshrouded CI `34234079616 / 102087197147` GREEN. It records the actual implemented checkpoint and the still-open art/production-consumer handoffs without changing runtime authority.

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
- [x] Manual full 607-mod pack smoke retained as an external release gate at the original Stage 10 planning checkpoint.
- [x] Canonical implementation sequence 10.01 → 10.10 defined.

## 10.01 — Visual Bible + dependency ADR — COMPLETE

- [x] Reconciled implementation start against the then-current 607-mod pack and Notion Enshrouded dossier.
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

## 10.06 — Shroud world-art family — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Authority and resource architecture

- [x] Stage 02 remains the sole terrain mutation/materialization authority; no gameplay Java authority was added by 10.06.
- [x] `shroud_growth`, `shroud_vein` and `withered_growth` use exactly three bounded vanilla weighted baked variants each.
- [x] No visual-only SavedData, packet, block entity, ticker, runtime randomizer, neighbor scan, chunk forcing or parallel spread state was introduced.
- [x] `withered_growth` is explicitly Deadly/Red ecology presentation, not purification.
- [x] Purification/regression continues to use canonical restoration/cleanup and no persistent purified-residue provider exists.
- [x] Fusion remains optional; vanilla blockstates/models constitute the complete fallback.

### World-art package

- [x] Ordinary growth and vein families use authored multi-element geometry instead of single `minecraft:block/cross` placeholders.
- [x] Separate Ordinary membrane/crust materials are present.
- [x] Deadly withered-growth variants use a distinct silhouette/material composition and separate Deadly membrane/crust materials.
- [x] Red Sludge still/flow textures were upgraded within the same Deadly-family language without changing `RedSludgeFluidType` gameplay behavior.
- [x] New first-party PNGs are explicitly registered in the provenance ledger.
- [x] Stage 10.06 material textures are 32×32 and the contract bounds them to at most 64×64.
- [x] `09-asset-review-matrix.md` records all 10.06 surfaces as `REVIEW_IN_GAME`/optional enhancement rather than treating CI as art approval.

### TDD and validation evidence

- [x] RED HEAD `af8f6140491e3304f1f4b60bf2a40f7affb51b29`: Enshrouded CI `34077847446 / 101607351530` failed at the new Stage 10 visual contract; Release Readiness `34077847501 / 101607351673` failed at the new presentation contract.
- [x] First GREEN implementation HEAD `90b81e1d4df50b5c2b4956b0a0a1ce7384dace2e` exposed an actual provenance failure for new PNGs; the ledger was fixed rather than weakening the gate.
- [x] Reconciled checkpoint `aa7ec50a0bc61de80eda9a4e838106603897f76e` passed Release Readiness `34078546785 / 101609292301` and full Enshrouded CI `34078546777 / 101609292946`.
- [x] Before final merge, the then-latest attached modlist was rechecked: that checkpoint pack was 612 mods; GeckoLib was `4.9.2`; Sodium was `0.8.13+mc1.21.1`; Fusion was `1.3.15+a` / `fusion-1.3.15a-neoforge-mc1.21.1.jar`.
- [x] Two P2 review findings hardened the contract before merge: exact unique existing `_a/_b/_c` weighted model references, plus decoded-RGBA alpha-topology comparison for Ordinary/Deadly materials instead of file-hash comparison.
- [x] Final PR #91 HEAD `1bf6dca57f8ff3548ef41955b0db06b8eb46b1c8` passed Release Readiness `34079531152` and full Enshrouded CI `34079531285 / 101612091620`.
- [x] Both P2 review threads were resolved only after the corrected final HEAD was green.
- [x] PR #91 merged to `main` as `19b9cee08cf5b5f369497ad4c8c0329eff65253d`.
- [x] Post-merge Release Readiness `34079910949` passed on exact `main@19b9cee08cf5b5f369497ad4c8c0329eff65253d`.
- [x] Post-merge Enshrouded CI `34079910911 / 101613106937` passed the complete matrix on the same baseline: provenance, Stage 10 contracts, unit tests, performance, diff sanity, NeoForge build, GameTests, SavedData two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.

### Visual/manual acceptance still open

- [ ] Dense Ordinary growth/vein screenshots at realistic FOV/distance.
- [ ] Deadly withered-growth + Red Sludge screenshots.
- [ ] Large-surface seam/tile/checkerboard inspection.
- [ ] Reduced-effects readability.
- [ ] Sodium/Fusion coexistence visual check on the checkpoint versions above.
- [ ] Full 612-mod client visual smoke required at that checkpoint.
- [ ] **ART APPROVED** remains open until the above evidence exists.

## 10.07 — HUD / UI art — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Authority and presentation architecture

- [x] Stage 03 remains authoritative for exposure, reserve, Madness and Passage/Flame gating.
- [x] `ShroudHudOverlay` remains client-side presentation-only and consumes the synchronized `ClientExposureState` projection.
- [x] No new packet, SavedData, gameplay clock, server mutation, death prediction, global scan or authoritative recomputation was introduced.
- [x] `ExposureHudModel.madnessSegments()` maps only the already server-authored `MadnessStage`; it does not recreate Madness thresholds or reducers.
- [x] Passage warning remains a projection of synchronized `deadlyBarrierActive`; the HUD does not decide eligibility.

### HUD package and accessibility

- [x] `textures/gui/shroud_hud_icons.png` is a bounded 384×64 first-party atlas with Ordinary/Deadly frame cells and four authored symbol cells.
- [x] Ordinary and Deadly frames differ structurally rather than by palette alone.
- [x] The final CI contract normalizes alpha into occupied/transparent masks before comparing frame/symbol topology, preventing opacity-only false positives.
- [x] Four 16×16 Ordinary, Deadly, Passage and Madness symbols must remain shape-distinct.
- [x] `AccessibilityProfile.MINIMAL` has a dedicated render path that retains hazard identity, countdown, Madness stage and Passage warning while removing optional ornament.
- [x] PT-BR/EN HUD translation-key parity remains contract-checked.
- [x] Fusion is optional; GeckoLib and shaders are not required for the static 2D HUD path.

### TDD and validation evidence

- [x] Deliberate RED head `13252ccc1d58b4e1c5b4f1a0e31097c968f22bd8` failed exactly in the new Stage 10.07 contract: Release Readiness `34107462335 / 101695661987` and Enshrouded CI `34107462334 / 101695662104`.
- [x] Final P2 review hardened alpha comparisons to normalized occupied topology; the thread was resolved only after the corrected head was green.
- [x] Final PR #93 HEAD `cda923713f6228e3c34885fc7da3d8592081c78b` passed Release Readiness `34140834386 / 101802215801`.
- [x] The same head passed Enshrouded CI `34140834405 / 101802215875`, including provenance, Stage 10 contracts, unit tests, performance, diff sanity, NeoForge build, GameTests, SavedData two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server smoke.
- [x] PR #93 merged to `main` as `341aa508bcd997cf32b7d550c9a37fca19f36ff3`.
- [x] Post-merge Release Readiness `34141675351` passed on exact `main@341aa508bcd997cf32b7d550c9a37fca19f36ff3`.
- [x] Post-merge Enshrouded CI `34141675549 / 101804820155` passed the complete matrix on the same baseline.
- [x] At the Stage 10.07 checkpoint, pack reconciliation was 612 mods with GeckoLib `4.9.2`, Sodium `0.8.13+mc1.21.1` and Fusion `1.3.15+a`.

### Visual/manual acceptance still open

- [ ] Ordinary vs Deadly screenshots at representative GUI scales.
- [ ] MINIMAL / reduced-effects comparison.
- [ ] Readability/coexistence near other HUD mods and common overlays.
- [ ] Full 612-mod client visual smoke required at that checkpoint.
- [ ] **ART APPROVED** remains open until the above evidence exists.

## 10.08 — Advanced VFX pass — TECHNICALLY COMPLETE / MERGED / POST-MERGE VERIFIED

### Authority and presentation architecture

- [x] Snapshot-derived transitions consume only synchronized `ClientExposureState`; they do not recreate Exposure, Shroud severity, Sanctuary or Madness authority on the client.
- [x] Discrete Core destruction, Flame ritual success and Lich manifestation cues are clientbound-only and emitted only after their canonical server transition succeeds.
- [x] `AdvancedVfxController`, `ClientAdvancedVfxState` and `AdvancedVfxSequenceBudget` own ephemeral presentation state only; no VFX `SavedData`, serverbound control payload, chunk forcing or world scan exists.
- [x] First synchronized snapshot establishes a baseline without replaying stale transitions; logout/config reload clears pending/active sequences.
- [x] Core proximity modifies only the existing local Stage 07 particle planner request and never computes gameplay state.

### Budgets, accessibility and dependency decision

- [x] Cue particle/lifetime/radius/cooldown limits are hard-bounded by `AdvancedVfxCue` and sequence emission is distributed through `AdvancedVfxSequenceBudget`.
- [x] `REDUCED_SENSORY` caps a complete advanced sequence at 4 particles.
- [x] `MINIMAL` emits 0 advanced particles while gameplay state remains unchanged.
- [x] At the Stage 10.08 checkpoint, the physical pack had 612 mods; Lodestone `1.8.2`, AAA Particles `2.2.3` and AAA Particles: World `2.0.0` were installed neighbors.
- [x] Lodestone was **not adopted** by Enshrouded for 10.08: native NeoForge/GeckoLib seams satisfied the required effects and full automation without a Lodestone compile/runtime dependency or API import.
- [x] Lodestone remains future task-gated rather than globally prohibited.

### TDD and validation evidence

- [x] Planner/budget RED: `cc643fe4b60bfad91e2471822212e5b765da64dc`; bounded planner/budget GREEN: `d9d00a5449c4fa06f51855da6e833d585d526e43`.
- [x] Discrete-cue RED: `89e28ff0e64f783c88d1807c2279db5cba3f7376`, failing before `AdvancedVfxPayload` / `ClientAdvancedVfxState` existed.
- [x] Temporal renderer implementation HEAD `35bfaacee1a2f3fe5b78e0e618922dc3347c502e` passed Release Readiness `34150710640` and Enshroued CI `34150710595`.
- [x] Closeout-contract RED HEAD `8aea9abe6520ddbb8162029db23a3418c49e7cd0` failed Release Readiness `34153754176 / 101841172426` exactly because the canonical 10.08 contract was absent.
- [x] Final PR #95 HEAD `9a4ff6ef53ff192bb1d28cf1c0e2c5dc8662e5a7` passed Release Readiness `34153873230 / 101841530155` and full Enshrouded CI `34153873227 / 101841529843`.
- [x] PR #95 merged to `main` as `00439c1593cf8e0699f6abe4e19d4848d1e97149`.
- [x] Post-merge Release Readiness `34154332957 / 101842896487` passed on exact `main@00439c1593cf8e0699f6abe4e19d4848d1e97149`.
- [x] Post-merge Enshrouded CI `34154332819 / 101842895697` passed the complete matrix on the same baseline, including GameTests, SavedData two-boot reload, Ars Zero real-distribution profile and dedicated-server save/reload smoke.
- [x] PR #96 is the documentation-only closeout; it changes no runtime authority.

### Visual/manual acceptance still open

- [ ] Full 612-mod client smoke required at that checkpoint.
- [ ] Sodium coexistence and shader-off/shader-on visual comparison.
- [ ] Full vs `REDUCED_SENSORY` vs `MINIMAL` presentation review.
- [ ] Fog enabled/disabled and day/night review.
- [ ] Reconnect/dimension-switch and rapid-boundary-crossing spam/duplication review.
- [ ] Core destruction, Flame ritual and Lich manifestation near/far review.
- [ ] **ART APPROVED** remains open and is carried forward to the final Stage 10 acceptance gate.

## 10.09 — Multiblock / set-piece pass — LOGIC CHECKPOINT IMPLEMENTED / STAGE OPEN

### Flame complex authority and lifecycle

- [x] `FlameAltarBlockEntity` remains the single physical controller; braces and runes own no BlockEntity, ritual inventory, progression or Sanctuary authority.
- [x] The canonical 3×3 structure is center Altar + four inward-facing cardinal braces + four corner runes.
- [x] A complete shell remains `UNFORMED` until explicit controller interaction performs bounded validation.
- [x] A valid first interaction commits `FORMED` and activates the existing canonical Flame Ward/Sanctuary path; subsequent interaction opens the existing ritual menu.
- [x] Missing components, wrong orientation, duplicate controller, protected positions, indeterminate protection and unavailable required chunks all fail closed before formation mutation.
- [x] `FlameAltarStructureValidator` reads only the nine footprint positions and never force-loads chunks.
- [x] Breaking a required shell part deterministically unforms the nearby formed controller; breaking the controller clears surviving shell presentation and Sanctuary.
- [x] Replacing a part does not passively reform; explicit controller interaction is required again.
- [x] Structural unform/reform does not consume or duplicate the ritual offering.
- [x] Persisted `FORMED` is recovery intent only: live authority is restored only after bounded post-load revalidation.
- [x] Missing required chunks use targeted retry indexing; production recovery does not force-load chunks.
- [x] A separate Purification Shrine controller/provider was intentionally not introduced; purification remains integrated with the same Flame complex and canonical Flame Ward/purification runtime.
- [x] PR #100 supplies survival acquisition for the full required shell: recipes yield four braces and four runes, and both components self-drop through their block loot tables.
- [ ] Brace/rune blockstates, block models, item models, textures and a visibly distinct `FORMED=false|true` presentation remain the **user-owned art handoff**. Automated CI must not substitute placeholder art for this requirement.

### Set-piece composition contracts and open consumers

- [x] `ShroudCoreNestLayout` provides bounded Ordinary/Deadly composition with exactly one `CORE_ANCHOR`; decorative ribs, root veins, sludge basins, hanging growths and ruins never become cores.
- [x] `LichManifestationLandmarkLayout` provides bounded ritual-dais / broken-halo / spectral-anchor / portal-frame / bone-motif composition with exactly one `ENCOUNTER_ORIGIN`.
- [x] Both layout classes are immutable/data-only and add no `ServerLevel`, SavedData, chunk loading, automatic worldgen, boss spawning or reward authority.
- [x] Existing Shroud Core and Stage 06 manifestation/Story services remain canonical.
- [ ] An approved production consumer for `ShroudCoreNestLayout` is still required before the Nest can be claimed as an in-world set piece. Any future consumer must remain bounded, loaded-chunk safe and route terrain mutation/protection through the canonical authority.
- [ ] An approved production placement/encounter-location consumer for `LichManifestationLandmarkLayout` is still required before the landmark can be claimed in-world. It must feed the existing Stage 06 lifecycle and must not create a second boss/Story/reward authority.
- [ ] These two missing consumers are Stage 10.09 implementation/architecture handoffs. **Stage 10.10 does not own them and must not silently absorb them as manual QA.**

### TDD and validation evidence

- [x] Formation authority RED began at `ddfd78dfc986285efb95293c3c15431062e2cacf`.
- [x] Set-piece RED `a501f11d6bd03501c2f4683d937095fe40b026f6` failed at unit compilation before the two layout classes existed.
- [x] Two-boot Shroud test isolation fix `9dd54e27203207fb08c53b1cc5a85b30110857ce` corrected sentinel attribution without changing expansion runtime.
- [x] Final PR #97 HEAD `7d9317fd0d211aaf6dce36298b84767408b2be20` passed Release Readiness `34188303297 / 101940989577` and Enshrouded CI `34188303299 / 101941000504`.
- [x] PR #97 merged to `main` as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`; independent post-merge Release Readiness `34189278455` and Enshrouded CI `34189278499` passed on that exact baseline.
- [x] Restart-harness correction PR #99 is integrated in the current lineage at `650c5c99308415a94ed51c9abd9abea1b5b26c10`.
- [x] Acquisition RED `92eab5e772184b47261196973926f2d1be659d30` ran 365 tests and failed exactly the two new resource tests because the recipes/loot tables did not yet exist.
- [x] Acquisition GREEN PR #100 HEAD `2b647d9e4dde39f4205b5082eca1ba9481b1e7d6` passed Release Readiness `34225536910` and Enshroued CI `34225536909 / 102058621586`.
- [x] PR #100 merged as `ed122c42f0eee0e706219361e30c9e1f05416a6d`; independent post-merge Release Readiness `34226166892` and Enshroued CI `34226166913 / 102060710827` passed the complete matrix on the same exact `main`.
- [x] The PR #98 reconciliation checkpoint physical-pack baseline was **603 mods** on NeoForge `21.1.248`; that checkpoint superseded the earlier 607/612 counts and is itself historical relative to the current 602-mod physical authority. The pertinent Notion dossier was rechecked and remains editorial relative to runtime/modlist authority.
- [x] Reconciliation PR #98 final HEAD `43bf73df5d4c3c93a731ee5d88397b5633badd1c` passed Release Readiness `34231676426` and Enshrouded CI `34231676553 / 102079337614`; it merged as `fa9552daa0372bf5061708a93ce5c263257df5d2`.
- [x] Independent post-merge verification on exact `main@fa9552daa0372bf5061708a93ce5c263257df5d2` passed Release Readiness `34234079530` and Enshroued CI `34234079616 / 102087197147`, including GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution and dedicated-server save/reload.

### Implementation/art handoffs still open before 10.10

- [ ] User-authored Flame shell render package: brace/rune blockstates, block/item models, textures and formed/unformed distinction.
- [ ] Approved/implemented production consumer for Shroud Core Nest.
- [ ] Approved/implemented production consumer for the Lich manifestation landmark.

### Manual final-acceptance work after those handoffs

- [ ] Unformed vs formed Flame complex screenshots at representative FOV/distance and from multiple directions.
- [ ] The FORMED 3×3 must read as one authored ritual construction; a visible cube-grid/checkerboard remains a rejection criterion.
- [ ] Sanctuary/purification readability under full, reduced-sensory and minimal presentation settings.
- [ ] Sodium/shader/resource-reload/reconnect coexistence.
- [ ] Full **602-mod** client visual smoke.
- [ ] **ART APPROVED** remains open.

## Hard boundaries carried forward

- GeckoLib receives presentation state only; animation completion never mutates gameplay authority.
- AzureLib remains intentionally unused by Enshroued unless a future ADR replaces the current decision.
- Fusion may improve environmental materials only when a valid base/fallback resource path exists.
- Lodestone was not needed by 10.08 and remains task-gated for future concrete effects; OctoLib and Player Animator likewise remain task-gated rather than automatic dependencies.
- The Flame complex has one authoritative controller and one Sanctuary provider; shell components cannot become parallel authorities.
- Player-built hero multiblocks are rejected if their FORMED presentation still reads as a normal Minecraft block grid.
- Shroud Core Nest and Lich landmark are currently data-only composition contracts; do not claim automatic worldgen/placement until an approved consumer is implemented and validated.
- Stage 10.10 is a final visual/compatibility acceptance gate; it must not be started while 10.09 implementation/art handoffs above remain open unless the user explicitly re-scopes them.
- Manual full **602-mod** pack smoke is the current external release gate before distribution; older 603/607/612 references above are historical checkpoint evidence.
- Do not mark the Flame Altar/Sanctuary focus, Shroud Core, Lich Skull, 10.06 world-art family, 10.07 HUD/UI, 10.08 advanced VFX or 10.09 multiblock/set-piece presentation **ART APPROVED** until the required in-game evidence exists.