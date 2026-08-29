# Enshrouded Plan — Deadly Shroud Passage

**Milestone:** Level 1 required.

**Status:** ✅ implemented, verified and merged.

**Goal:** make Red/Deadly Shroud a Flame-level gate by replacing the fail-closed Level 1 barrier fallback with a passage-aware implementation.

**Implemented types:** `FlameGatedDeadlyExposurePolicy`, `PassageRequirement`.

## Files

- Added `src/main/java/com/gustavaopere/enshrouded/exposure/deadly/*`.
- Extended server exposure config with a bounded required passage tier; the Task-01 emergency-window config remains the single collapse-duration source.
- Wired `ExposureRuntime` through Foundation progression boundaries without Stage 05 implementation imports.

## Dependencies

- 01 player exposure, including its `DeadlyExposurePolicy` interface and fail-closed Level 1 barrier fallback.
- Foundation `ProgressionOwnerResolver` and `FlamePassageQuery` contracts/defaults from `00-foundation/02-domain-contracts.md`.

Stage 03 creates no local passage stub and has no Stage 05 implementation dependency. Standalone Level 1 resolves the player through `ProgressionOwnerResolver.standalone()` and reads passage through `FlamePassageQuery.levelOneFallback()`; the same boundary remains available for Stage 05's persistence-backed provider.

## Implementation contract

- [x] `FlameGatedDeadlyExposurePolicy` implements the existing `DeadlyExposurePolicy`; `ExposureService` gained no direct passage/progression logic.
- [x] Default required passage level is 2 while standalone Foundation passage remains level 1.
- [x] Underleveled entry clamps to the existing configurable emergency window and continues rapid drain.
- [x] Brief exit/re-entry cannot reset the emergency window because no second timer or barrier state was introduced; the policy operates only on the authoritative exposure reserve.
- [x] Passage level meeting the requirement removes the progression barrier while retaining the same `DEADLY` sample/schema and applying ordinary reserve drain.
- [x] Barrier logic depends only on `ProgressionOwnerResolver` + `FlamePassageQuery`; no altar scan or Stage 05 state class is used.
- [x] Null/failed owner resolution and failed passage queries fail closed.
- [x] `DEADLY` remains distinct in the authoritative exposure snapshot/client presentation.

## TDD / verification

- [x] Unit-test required-level comparison and underleveled collapse math.
- [x] Unit-test player owner resolution + injected Foundation passage fallback produces Level 1 underleveled behavior.
- [x] Contract test proves `FlameGatedDeadlyExposurePolicy` is substitutable for the Task 01 `DeadlyExposurePolicy` without changing `ExposureService`.
- [x] Unit-test failed/uncertain owner or passage lookup fails closed rather than granting access.
- [x] GameTest Level 1 player entering Deadly Shroud reaches fatal exposure rapidly.
- [x] GameTest fake passage level 2 proves the policy extension point can permit the zone without changing cell data.
- [x] GameTest edge-dancing cannot reset the emergency window.

## Evidence

- Branch: `feat/03-deadly-shroud`.
- Structural RED: commit `76b10f9d1a45f1bbe4502228aa4c1a2cf153c22b`, workflow/job `33264854956` / `99133012859` — 141 tests, exactly one failure because `PassageRequirement` did not exist.
- Structural GREEN surface: commit `42828a3dbf35bdf8a316c753ad7c0728dc70872d`, workflow/job `33264949921` / `99133260628` — full GREEN.
- Behavioral RED: commit `9fca15d5f87f87eed3ef843efceb1bccbd2618ba`, workflow/job `33265752520` / `99135405525` — 146 tests, exactly one failure because passage level 2 still remained blocked.
- Runtime/config RED: commit `097f620e70377faa3a328901244f1e9b8ed299a2`, workflow/job `33265915887` / `99135868595` — 148 tests, exactly two expected failures: missing required-passage config getter and runtime still using the Task-01 hard barrier.
- Final implementation/GameTest HEAD: `22bcbb859ee4fa97b12a55988b9b4c4378dd763f`.
- Push verification: workflow/job `33266195145` / `99136603220` — full GREEN.
- PR: #24 — `03 — Deadly Shroud`.
- Final PR-head verification: workflow/job `33266387083` / `99137076697` — full GREEN.
- Exact final gates: 148 unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.
- Squash merge SHA: `036664ea35747ae3bb16f556f1cd1dc0b1d89669`.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN through exact-head CI.
- [x] NeoForge build, GameTests, two-boot persistence harness and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract is hidden. `ENSH-L1-FLAME-PASSAGE-001` remains open for Stage 05 persistence-backed passage and Stage 08 ownership/membership semantics; the Stage 03 consumer side is now proven.
- [x] Plan renamed with `✅-` and project status/pending-contract record checkpointed after merge.

**Acceptance:** satisfied. Red/Deadly Shroud is an unmistakable Level-1 progression barrier, passage level 2 can traverse the same canonical DEADLY data through the stable Foundation boundary, uncertain progression fails closed, and no Task 01 -> Task 03 compile cycle or Stage 03 -> Stage 05 implementation dependency exists.
