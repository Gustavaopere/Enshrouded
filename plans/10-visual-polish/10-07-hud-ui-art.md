# Stage 10.07 — HUD / UI art

Status: TECHNICALLY COMPLETE / IMPLEMENTATION MERGED / POST-MERGE VERIFIED / ART APPROVED OPEN

## Scope

Stage 10.07 replaces the Shroud HUD placeholder presentation with bounded first-party HUD art while preserving the existing Stage 03 synchronization and gameplay authority.

The implemented presentation contains:

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

Ordinary and Deadly Shroud differ by silhouette/pattern in addition to palette; **color is not the sole distinction**.

The existing `AccessibilityProfile.MINIMAL` contract is preserved: **MINIMAL remains readable**. Reduced sensory profiles may suppress optional environmental effects but cannot hide the synchronized hazard/timer information.

The automated visual contract decodes the authored atlas and normalizes alpha into an occupied/transparent topology mask (`alpha > 0`). Ordinary/Deadly frames and the four HUD symbols therefore cannot satisfy the non-color distinction gate through palette or opacity-only changes.

UI scale remains bounded by the existing `EnshroudedClientConfig` client-only setting. PT-BR and EN HUD translation-key parity is preserved.

## Asset and dependency budget

- `textures/gui/shroud_hud_icons.png`: bounded 384×64 combined atlas containing two 160×60 frame cells (Ordinary/Deadly) and four 16×16 shape-distinct symbol cells (Ordinary, Deadly, Passage and Madness).
- The atlas replaces the existing first-party-declared GUI resource instead of introducing a second persistent HUD binary, keeping provenance surface bounded.
- The HUD art is original first-party material; the existing resource path remains explicitly declared in the provenance ledger.
- **Fusion is optional** and is not an HUD authority or mandatory render dependency.
- **GeckoLib is not required** for static 2D HUD art.
- A **shader is not required**; the vanilla/NeoForge `GuiGraphics` path is complete, preserving Sodium compatibility expectations.

## TDD and validation

The Stage 10.07 contract is enforced by `scripts/ci/test_stage10_hud_ui_art.py` in both Enshrouded CI and Level 1 Release Readiness.

Deliberate TDD RED checkpoint:

- exact RED head: `13252ccc1d58b4e1c5b4f1a0e31097c968f22bd8`;
- Level 1 Release Readiness run `34107462335`, job `101695661987`;
- Enshrouded CI run `34107462334`, job `101695662104`;
- provenance contract passed first;
- failures occurred exactly at the new Stage 10 presentation/visual contract before the implementation existed.

Final PR-head evidence:

- implementation PR: #93 — `Stage 10.07 — HUD / UI art`;
- final implementation HEAD: `cda923713f6228e3c34885fc7da3d8592081c78b`;
- Level 1 Release Readiness `34140834386 / 101802215801` — `completed/success`;
- Enshrouded CI `34140834405 / 101802215875` — `completed/success`, including provenance, Stage 10 visual contracts, unit tests, performance baselines, diff sanity, NeoForge build, GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke;
- the P2 alpha-normalization review was resolved only after the corrected head passed both workflows.

Implementation merge and post-merge evidence:

- PR #93 merged to `main` as `341aa508bcd997cf32b7d550c9a37fca19f36ff3`;
- Level 1 Release Readiness `34141675351` — `completed/success` on the exact merge SHA;
- Enshrouded CI `34141675549 / 101804820155` — `completed/success` on the same merge SHA, including the complete server/reload/profile matrix.

The technical implementation checkpoint is therefore closed. The documentation closeout records this evidence without changing runtime behavior.

## Current pack reconciliation

The current physical modlist baseline remains **612 mods**. Relevant presentation/runtime versions at closeout are:

- GeckoLib `4.9.2`;
- Sodium `0.8.13+mc1.21.1`;
- Fusion `1.3.15+a` (`fusion-1.3.15a-neoforge-mc1.21.1.jar`).

## Manual art gates

**ART APPROVED remains open.** Technical completion does not certify subjective visual quality.

The following remain **pending** until an in-game review is explicitly performed:

- full **612-mod** client smoke;
- **reduced-effects** / MINIMAL comparison;
- Ordinary versus Deadly **screenshots** at representative GUI scales;
- readability near other HUD mods and common overlays;
- final art-direction approval.

Stage 10.08 is the next canonical task after this closeout, but it must not start automatically in this cycle.
