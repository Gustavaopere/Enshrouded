# ✅ Enshrouded — Performance and Budgets

**Stage:** 09.02 — Hardening

**Disposition:** implemented, verified, merged and independently post-merge verified.

## Goal

Measure and enforce bounded Level-1 work for Shroud expansion/regression, terrain mutation/restoration, canonical queries, corrupted-entity updates, networking and client effects under representative stress without introducing a second gameplay authority.

## Implemented scope

- Added passive process-local `PerformanceCounters` instrumentation for expansion, regression, materialization, restoration, local Shroud queries, entity samples/state updates, client payloads and client source/effect work.
- Added `PerformanceBudgetMatrix` as a validation helper only; it does not own gameplay scheduling or persistence.
- Split logical expansion into an explicit **global per-dimension** cap and **per-core** cap. Production defaults remain 32/32 work units per tick, with the existing hard range 1–512.
- Added a global corrupted-entity sampling cap: default 256 updates per Minecraft server tick, configurable 1–4096.
- Staggered entity-corruption sampling deterministically by entity UUID across a 20-tick window and acquired the global budget before the authoritative `ShroudQuery`.
- Preserved bounded regression and visual restoration, bounded invoke-on-demand materialization, loaded-chunk-only behavior and no chunk forcing.
- Preserved Shroud networking suppression/rate limiting and instrumented only payloads actually emitted.
- Kept client particle work bounded to one pulse every 4 client ticks, at most 192 sampled positions per pulse, already-loaded chunks only, and the existing configurable particle emission cap.
- Added source-contract regression guards against loaded-world chunk/entity enumeration and forced chunk loads in the canonical hot paths.
- Added deterministic benchmark coverage for 1/10/50 active cores, overload fixtures, 10,000 entity reducer updates and representative persistence/heap observations.
- Added NeoForge GameTest-server performance evidence with the `ENSHROUDED_LEVEL1_SERVER_PERF_PASSED` marker.
- Added CI gates for the Stage 09.02 benchmark report and GameTest performance fixture while preserving the complete Stage 09.01 regression matrix.

## Authority and bounded-work invariants

Stage 09.02 adds observability and closes missing work ceilings; it does **not** create a new Shroud field, scheduler, corruption authority, mutation authority or client gameplay authority.

Canonical runtime owners remain unchanged:

- `ShroudExpansionRuntime` / `ShroudExpansionScheduler` own bounded logical expansion over persisted Shroud state.
- `ShroudRegressionScheduler` and `TerrainRestorationService` own bounded purification work.
- `ShroudMaterializationService` remains a bounded invoke-on-demand service and does not gain an invented periodic owner.
- `DefaultShroudQuery` remains the read-only spatial query authority.
- `EntityCorruptionRuntime` remains event-driven per living entity; it performs no global entity scan.
- client particles/fog/audio remain presentation-only and never become server gameplay authority.

Under pathological backlog, the accepted behavior is delayed spread/sampling/visual cleanup rather than unbounded work per tick.

## Measured Level-1 baseline

The canonical evidence map is [`docs/performance/level1-baseline.md`](../../docs/performance/level1-baseline.md). Timing values are runner observations, **not production TPS/MSPT guarantees**.

Key deterministic evidence from the measured matrix:

- benchmark budget: 256 global / 8 per core;
- 1 core: 8 processed, 8 applied, max/core 8;
- 10 cores: 80 processed, 80 applied, max/core 8;
- 50 cores: 256 processed, 256 applied, max/core 6 — exact global-cap saturation;
- representative persistence fixture: 50 cores × 64 cells = 3,200 logical cells;
- compressed NBT observation: 10,645 bytes, with a deterministic safety assertion below 5,000,000 bytes;
- pure reducer benchmark: 10,000 unsafe entity samples/updates executed, while production admission remains separately bounded/staggered.

The 50-core result proves that adding queued work increases backlog latency after the global ceiling is reached rather than increasing that scheduler tick without bound.

## Review correction

Automated review identified a P2 race in the initial `PerformanceCounters.snapshotAndReset()` design: paired counters could be split across observation windows if a drain raced between two atomic increments.

Final implementation HEAD `224fe45b644990af9e636151e13b7192f7f9c9d4` fixes this by coordinating paired recorders and snapshots on the same monitor. `PerformanceCounterWindowAtomicityRedTest` protects the window invariant. The review thread was resolved only after the corrected HEAD completed the full CI matrix GREEN.

## Verification record

- Base before implementation: `main@dbbf79a12255584d953909f56d143229bb49f9bc`.
- Implementation branch: `feat/09-performance`.
- Final implementation HEAD: `224fe45b644990af9e636151e13b7192f7f9c9d4`.
- PR: #72 — `Stage 09.02 — Performance hardening and Level 1 baseline`.
- Exact final PR-head workflow/job: `33998728399` / `101393675566` — `completed/success`.
- Final PR-head gates GREEN: wrapper provenance, unit tests, performance benchmark baselines, diff sanity, NeoForge build, canonical + external GameTest compilation, production-JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `aac30dadcf0c31c1cfddba5ad66e8df281e33923`.
- Independent post-merge `main` workflow/job: `33999268064` / `101395101323` — `completed/success` across the same complete gate set.
- Post-merge verified implementation `main`: `aac30dadcf0c31c1cfddba5ad66e8df281e33923`.
- New cross-stage pending contracts: none.

## Merge gate

- [x] Stage-specific unit/regression tests are GREEN on the final implementation HEAD.
- [x] Performance benchmark baselines are generated and validated in CI.
- [x] 1/10/50-core deterministic cap adherence is executable.
- [x] Representative entity and persistence stress fixtures are executable.
- [x] No canonical hot path enumerates all loaded chunks/entities or forces chunk loads according to the committed source-contract gates.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN.
- [x] Canonical GameTests are GREEN and include `ENSHROUDED_LEVEL1_SERVER_PERF_PASSED`.
- [x] SavedData two-boot reload matrix is GREEN.
- [x] Real Ars Zero 2.0.2 isolated profile is GREEN.
- [x] Dedicated-server save/reload smoke is GREEN.
- [x] Production JAR integrity gate is GREEN.
- [x] Automated P2 review finding is corrected and protected by regression coverage.
- [x] PR #72 merged into `main`.
- [x] Independent post-merge `main` CI is GREEN.
- [x] No unresolved cross-stage contract was introduced.

**Acceptance:** Stage 09.02 is complete. Measured Level-1 work is bounded by explicit scheduler/entity/client limits, representative stress reaches the intended ceilings without unbounded per-tick scans, and the implementation has both exact-head and independent post-merge GREEN evidence.