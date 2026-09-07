# Stage 10.07 — HUD / UI art

Status: TDD RED CHECKPOINT / IMPLEMENTATION PENDING

## Scope

Stage 10.07 replaces the Shroud HUD placeholder presentation with bounded first-party HUD art while preserving the existing Stage 03 synchronization and gameplay authority.

The target presentation contains:

- an authored fantasy frame for Ordinary Shroud;
- a structurally distinct authored fantasy frame for Deadly Shroud;
- readable synchronized reserve/countdown text;
- a Madness stage bar derived only from the synchronized `MadnessStage` already present in `ExposureSnapshot`;
- a Passage warning/icon driven only by the synchronized `deadlyBarrierActive` projection already exposed by `ExposureHudModel`;
- a dedicated MINIMAL presentation path that removes optional ornament but preserves hazard identity, timer, Madness stage and Passage warning readability.

## Authority boundary

**Stage 03 remains authoritative** for exposure, reserve, Madness and Passage/Flame gating.

`ShroudHudOverlay` remains **presentation-only** and consumes `ClientExposureState.INSTANCE` through `ExposureHudModel.fromSnapshot(state.snapshot(), 0)`.

Stage 10.07 allows **no authoritative recomputation**, **no new network packets**, **no new SavedData**, no client-side death prediction, no global scan and no second gameplay clock. The HUD must not infer exposure from wall-clock time.

## Accessibility contract

Ordinary and Deadly Shroud must differ by silhouette/pattern in addition to palette; **color is not the sole distinction**.

The existing `AccessibilityProfile.MINIMAL` contract is preserved: **MINIMAL remains readable**. Reduced sensory profiles may suppress optional environmental effects but cannot hide the synchronized hazard/timer information.

UI scale remains bounded by the existing `EnshroudedClientConfig` client-only setting. PT-BR and EN HUD translation-key parity must be preserved.

## Asset and dependency budget

- `textures/gui/shroud_hud_frame.png`: bounded 320×64 atlas containing 160×60 Ordinary and Deadly frame cells.
- `textures/gui/shroud_hud_icons.png`: bounded 64×16 atlas containing four 16×16 shape-distinct cells for Ordinary, Deadly, Passage and Madness.
- Assets are original first-party resources and must be declared in the provenance ledger.
- **Fusion is optional** and is not an HUD authority or mandatory render dependency.
- **GeckoLib is not required** for static 2D HUD art.
- A **shader is not required**; the vanilla/NeoForge `GuiGraphics` path is complete, preserving Sodium compatibility expectations.

## TDD and validation

The Stage 10.07 contract is enforced by `scripts/ci/test_stage10_hud_ui_art.py` in both Enshrouded CI and Level 1 Release Readiness.

Required technical exit evidence:

1. deliberate TDD RED on the exact PR head before implementation;
2. authored resources and presentation-only HUD implementation;
3. exact PR-head Enshrouded CI green;
4. exact PR-head Level 1 Release Readiness green;
5. implementation merge to `main`;
6. exact post-merge `main` CI green;
7. documentation closeout with persisted Notion delta.

## Manual art gates

**ART APPROVED remains open.** Technical completion does not certify subjective visual quality.

The following remain **pending** until an in-game review is explicitly performed:

- full **612-mod** client smoke;
- **reduced-effects** / MINIMAL comparison;
- Ordinary versus Deadly **screenshots** at representative GUI scales;
- readability near other HUD mods and common overlays;
- final art-direction approval.

Stage 10.08 is not part of this cycle and must not start automatically.
