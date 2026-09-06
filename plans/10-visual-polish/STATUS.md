# Stage 10 — Visual Polish Status

**State:** 10.02 TECHNICAL IMPLEMENTATION COMPLETE / PR #83 — FINAL CI REQUIRED BEFORE MERGE / P0 IN-GAME ART REVIEW STILL REQUIRED

**Planning PR:** #80 — `Stage 10 — Art Direction, Hero Assets and Visual Polish` — MERGED
**10.01 implementation PR:** #81 — `Stage 10.01 — Visual Bible and GeckoLib Runtime Contract` — MERGED
**10.01 closeout PR:** #82 — MERGED
**10.02 implementation PR:** #83 — `Stage 10.02 — Flame Altar Hero Asset` — OPEN AT THIS CHECKPOINT

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

## 10.02 — Flame Altar hero asset — TECHNICAL IMPLEMENTATION COMPLETE

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
- [x] Editable Blockbench source contains all 26 runtime cuboids and both base/emissive texture references.
- [x] First-party PNGs registered in provenance.
- [x] Old polished-blackstone-bricks + magma placeholder removed from the Flame Altar presentation.
- [x] Automated Stage 10 contract covers render path, model/animation assets, provenance, editable-source completeness and authority → presentation ordering.

### TDD / automated evidence

- [x] Initial Stage 10.02 RED intentionally failed for missing hero-asset pieces before implementation.
- [x] Authority/presentation RED commit `93d2a941b558c842af894f9e00ad1e3124d95dda` failed exactly at the Stage 10 visual-stack contract before authoritative trigger wiring existed.
- [x] Pre-polish implementation HEAD `ffa824256ebb45e06b1c49e4b6412539a23c8996` passed Level 1 Release Readiness and the complete Enshrouded CI matrix including dedicated-server save/reload smoke.
- [ ] Final PR #83 HEAD must pass both workflows after the material/source/authority hardening before merge.

### Visual/manual acceptance still open

- [ ] P0 approval requires in-game client screenshots/review at realistic FOV and distance, per `03-hero-assets.md`.
- [ ] Reduced-effects view must remain readable.
- [ ] Full 607-mod-pack visual smoke remains an external/manual gate because the complete pack is not vendored into CI.
- [ ] Technical merge does **not** waive the later 3×3/5×5 multiblock formation requirement.

## Multiblock boundary carried forward

Full player-built 3×3/5×5 formation/validation is deliberately not implemented inside 10.02. The future formation task must preserve the Flame Altar BlockEntity as the authoritative anchor and use the agreed UX: components placed by the player → explicit activation/validation → FORMED state. A FORMED altar that still reads as a Minecraft cube grid/checkerboard is rejected even if mechanically correct.

## Hard boundaries carried into subsequent tasks

- GeckoLib receives presentation state only; animation completion never mutates gameplay authority.
- AzureLib remains intentionally unused by Enshrouded unless a future ADR replaces the current decision.
- Fusion may improve environmental materials only when a valid base/fallback resource path exists.
- Lodestone, OctoLib and Player Animator remain task-gated rather than automatic dependencies.
- Player-built hero multiblocks are rejected if their FORMED presentation still reads as a normal Minecraft block grid.
- Manual full 607-mod pack smoke remains an external release gate before distribution.
- Do not mark Flame Altar P0 **ART APPROVED** until the required in-game evidence exists.
