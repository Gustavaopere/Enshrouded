# Stage 10 — Visual Polish Status

**State:** 10.01 COMPLETE / MERGED / POST-MERGE VERIFIED — NEXT: 10.02 FLAME ALTAR HERO ASSET

**Planning PR:** #80 — `Stage 10 — Art Direction, Hero Assets and Visual Polish` — MERGED
**10.01 implementation PR:** #81 — `Stage 10.01 — Visual Bible and GeckoLib Runtime Contract` — MERGED

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

## 10.02 — Flame Altar hero asset — NEXT

The next implementation task must preserve the existing Flame Altar BlockEntity as the single authoritative controller and follow the merged 3×3/5×5 formed-multiblock design gate. A visually formed altar that still reads as a cube grid is rejected.

## Hard boundaries carried into implementation

- GeckoLib receives presentation state only; animation completion never mutates gameplay authority.
- AzureLib remains intentionally unused by Enshrouded unless a future ADR replaces the current decision.
- Fusion may improve environmental materials only when a valid base/fallback resource path exists.
- Lodestone, OctoLib and Player Animator remain task-gated rather than automatic dependencies.
- Player-built hero multiblocks are rejected if their FORMED presentation still reads as a normal Minecraft block grid.
- Manual full 607-mod pack smoke remains an external release gate before distribution.
