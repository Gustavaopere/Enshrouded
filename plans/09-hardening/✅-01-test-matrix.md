# ✅ Enshrouded — Level 1 Test Matrix

**Stage:** 09.01 — Hardening

**Disposition:** implemented, verified, merged and post-merge verified.

## Goal

Run an auditable Level-1 vertical matrix across the standalone runtime plus selected current-pack compatibility boundaries, while distinguishing real third-party co-load evidence from contract/API evidence and manual full-pack smoke.

## Implemented scope

- Added `LevelOneScenarioGameTests` in the canonical Foundation `src/gameTest` source set.
- Composed the complete standalone Level-1 loop through production boundaries: physical core → automatic activation → expansion → canonical Shroud query → Exposure drain → corrupted mob → CLEAR recovery → core destruction → bounded regression → manifestation → exactly-once authentic Lich Skull → Flame altar ritual → persisted Level-1 completion readiness.
- Added real two-boot restart coverage for mid-expansion and mid-exposure, while preserving existing two-boot coverage for purification, Flame/Story state, Shroud SavedData/growth, entity corruption and encounter/reward replay protection.
- Extended CI to compile both canonical and external-fixture GameTest source sets explicitly and reject GameTest leakage into the production JAR.
- Added `docs/testing/level1-matrix.md` as the evidence map for standalone, real co-load, contract/API and manual pack profiles.

## Ars Zero real-distribution closure

Stage 09.01 closes `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001`.

The actual current-pack provider chain is resolved only for the dedicated compatibility profile and is never bundled or promoted to ordinary Enshrouded runtime dependencies:

- Ars Zero `2.0.2`;
- Ars Nouveau `5.13.1`;
- Ars Elemental `0.7.10.1`;
- Curios API `9.5.1+1.21.1`;
- GeckoLib `4.9.2`;
- TerraBlender `4.1.0.8`.

The first real co-load attempt correctly exposed Ars Nouveau's missing mandatory Curios/GeckoLib/TerraBlender dependency chain. After adding those exact fixture dependencies, the real provider test itself passed but unrelated standalone tests failed because the complete canonical GameTest suite was running with optional-mod side effects loaded.

The final architecture isolates only the real external fixture in `src/arsZeroGameTest` and registers a dedicated NeoGradle `arsZeroGameTestServer` run with `runType 'gameTestServer'`. The canonical standalone suite remains in the original `gameTest` source set and run.

`ArsZeroRealDistributionGameTests.realDistributionUsesArsZeroLichAndKeepsRewardExactlyOnce` proves that the co-loaded distribution:

- loads `ars_zero` and resolves exact `ars_zero:lich`;
- constructs/spawns the real actor server-side;
- does not let a natural/unmarked Lich satisfy Enshrouded encounter identity or emit the Enshrouded story reward;
- is preferred by the Enshrouded manifestation runtime;
- receives the stored Enshrouded encounter identity;
- emits exactly one authentic Level-1 Lich Skull when the marked manifestation dies;
- rejects a replayed death callback from duplicating that reward.

The standalone `ArsZeroProviderGameTests` remains contract-only and continues to verify proxy/registry precedence and encounter tagging without claiming third-party co-load.

## Restart evidence

`scripts/ci/shroud-saveddata-reload-gametest.sh` now requires the real two-boot markers for:

- mid-expansion frontier reconstruction and resumed growth;
- mid-exposure vanilla playerdata/NeoForge attachment reload and continued drain from the exact persisted reserve;
- purification;
- Flame/Story persistence;
- Shroud SavedData/growth;
- entity corruption;
- exactly-once encounter/reward replay protection.

The exposure fixture also verifies that exactly one stable playerdata file is used across both boots.

## Verification record

- Base before implementation: `main@67f4ab9095e69a922f265ffc477381f84c30ec69`.
- Implementation branch: `feat/09-test-matrix`.
- Final implementation HEAD: `2db41c3569409beec11a6509c2c39fbfc7810a83`.
- PR: #70 — `Stage 09.01 — Level 1 test matrix and real Ars Zero co-load`.
- Exact PR-head workflow/job: `33986822592` / `101361842946` — `completed/success`.
- PR-head gates GREEN: wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, canonical + Ars Zero GameTest compilation, production-JAR integrity, standalone GameTests, two-boot reload matrix, isolated real Ars Zero profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `6e957bd0592723cc4849f2a4606222ad564c2aa4`.
- Independent post-merge `main` workflow/job: `33989419851` / `101368854913` — `completed/success` across the same complete gate set.
- Post-merge verified `main`: `6e957bd0592723cc4849f2a4606222ad564c2aa4`.

## Merge gate

- [x] All task-specific tests are GREEN on the final implementation HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN.
- [x] Canonical standalone GameTests are GREEN and include `ENSHROUDED_LEVEL_ONE_SCENARIO_PASSED`.
- [x] Real two-boot restart matrix is GREEN.
- [x] Real Ars Zero 2.0.2 co-load profile is GREEN and includes `ENSHROUDED_ARS_ZERO_REAL_FIXTURE_PASSED`.
- [x] Dedicated-server save/reload smoke is GREEN.
- [x] Production JAR excludes GameTest-only classes from both source sets.
- [x] `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` has executable evidence on both PR head and post-merge `main`.
- [x] PR #70 merged into `main`.
- [x] Independent post-merge `main` CI is GREEN.
- [x] Closeout checkpoint renames this plan with `✅-` and updates `plans/STATUS.md`, `plans/PENDING.md` and the Stage 09 index.

**Acceptance:** Stage 09.01 is complete. The Level-1 loop has deterministic standalone end-to-end evidence, true restart evidence at the required state boundaries and a real isolated Ars Zero distribution fixture without contaminating the standalone suite or production runtime.
