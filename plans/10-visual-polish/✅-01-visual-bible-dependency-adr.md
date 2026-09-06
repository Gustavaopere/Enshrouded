# ✅ Enshrouded — Stage 10.01 Visual Bible + Dependency ADR

**Stage:** 10.01 — Visual Polish

**Disposition:** implemented, verified, merged and independently post-merge verified.

## Goal

Freeze the Stage 10 rendering/animation dependency contract before hero-asset implementation while preserving all existing gameplay authority boundaries.

## Implemented scope

- Promoted GeckoLib `4.9.2` to the Enshrouded production animated-3D runtime.
- Centralized the GeckoLib version and accepted range `[4.9.2,5.0.0)` in `gradle.properties`.
- Added the GeckoLib NeoForge artifact as a normal external production dependency; it is not shaded or copied into the Enshrouded JAR.
- Declared GeckoLib as a required `BOTH`-side NeoForge dependency in `neoforge.mods.toml`.
- Reused the exact same GeckoLib version property in the isolated Ars Zero compatibility fixture so the test profile cannot silently drift from the production runtime.
- Kept Fusion deliberately soft/optional with no hard compile/runtime dependency and no gameplay authority.
- Kept AzureLib out of the Enshrouded runtime to avoid parallel animation-engine ownership.
- Kept Lodestone, OctoLib and Player Animator task-gated rather than automatic dependencies.
- Added `scripts/ci/test_stage10_visual_stack.py` to enforce version, metadata, provenance and Fusion-softness invariants.
- Extended production-JAR CI verification to assert that the GeckoLib requirement is expanded correctly and no resource placeholder leaks into packaged metadata.

## Authority invariants

Stage 10.01 creates presentation infrastructure only. GeckoLib animations, render completion and visual callbacks may never:

- complete a Flame ritual;
- destroy or purify a Shroud core;
- mutate Flame, Shroud, Exposure, Madness, Story or Lich authority;
- grant progression or rewards;
- create a second gameplay state machine.

Server-authored gameplay state remains canonical. Rendering consumes state; it does not author it.

## Multiblock boundary carried forward

The merged Stage 10 multiblock contract remains mandatory for 10.02 and 10.09:

- players place components;
- an explicit activation validates the structure;
- the authoritative controller owns `FORMED`/`UNFORMED` state;
- the formed presentation must read as one authored premium structure;
- visible normal-block checkerboard seams after formation are a rejection condition;
- component blocks may remain underneath for validation/collision, but may not duplicate Flame state, ritual state or rewards;
- formation animation is presentation-only.

## Verification record

- Pre-implementation base: `main@d3d31cc8afbf61d44a755170f31541753aaa0d8f`.
- Implementation branch: `feat/10-01-visual-stack`.
- Final implementation HEAD: `33a5da3b734055004323c9789622c52741d5a26d`.
- Implementation PR: #81 — `Stage 10.01 — Visual Bible and GeckoLib Runtime Contract`.
- PR-head Level 1 Release Readiness: `34049582677` — `completed/success`.
- PR-head Enshrouded CI: `34049582642` — `completed/success`.
- Implementation merge SHA: `abd938361697d20b431334cff47053a3a2787342`.
- Post-merge Level 1 Release Readiness: `34049961298` — `completed/success`.
- Post-merge Enshrouded CI: `34049961432 / 101531662766` — `completed/success`.
- Post-merge gates GREEN: Stage 10 visual-stack contract, wrapper/provenance, unit tests, performance baseline, diff sanity, NeoForge build, GameTest compilation, production-JAR integrity, canonical GameTests, SavedData two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Post-merge verified main: `abd938361697d20b431334cff47053a3a2787342`.
- New cross-stage gameplay contracts: none.

## Acceptance

- [x] GeckoLib 4.9.2 is the single primary Enshrouded animation runtime.
- [x] Production metadata requires GeckoLib in the approved range.
- [x] GeckoLib remains external/unbundled with approved provenance.
- [x] Fusion remains soft/optional.
- [x] No second animation engine was introduced.
- [x] No gameplay authority moved to rendering/animation.
- [x] PR #81 is merged.
- [x] PR-head CI is GREEN.
- [x] Independent post-merge `main` CI is GREEN.
- [x] The next canonical task is 10.02 — Flame Altar hero asset.

**Acceptance:** Stage 10.01 is complete. The art/runtime stack is frozen and independently verified; hero-asset work may proceed without reopening dependency architecture unless a future explicit ADR supersedes this decision.
