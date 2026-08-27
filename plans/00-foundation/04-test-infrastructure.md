# Enshrouded Plan — Test Infrastructure

**Milestone:** Level 1 required.

**Goal:** establish reusable unit, GameTest and dedicated-server verification paths before gameplay state is added.

**Planned types:** `TestFixtures`, `GameTestBootstrap`.

## Files

- Create `src/test/java/com/gustavaopere/enshrouded/test/*`.
- Create `src/gametest/java/com/gustavaopere/enshrouded/gametest/*` or the NeoForge 1.21.1 equivalent source set selected by the build.
- Modify `.github/workflows/ci.yml`.

## Dependencies

- 01 build scaffold.
- 02 domain contracts.

## Implementation contract

- Provide deterministic fake clock/random helpers for expansion/exposure math.
- Provide GameTest helpers for server level, block mutation, player/entity state and save/reload scenarios.
- CI distinguishes expected RED commits/checkpoints from final GREEN merge gates in commit messages/status reporting.
- Dedicated-server smoke runs with only mandatory dependencies.

## TDD / verification

- [ ] Create one intentionally failing unit fixture and one intentionally failing GameTest to prove both paths execute, then revert/replace them with real smoke assertions.
- [ ] Verify clean unit, GameTest and dedicated-server jobs on the final HEAD.

## Current verification checkpoint — 2026-08-27

Implemented on `round-1-foundation`:

- deterministic `TickClock` and scripted-random fixtures with executable JUnit coverage;
- `GameTestBootstrap` helpers for `ServerLevel`, block mutation, mock player state, entity spawning/state and forced save/flush;
- Foundation GameTests for template/server-level/block mutation and player/entity/save fixtures;
- a valid compressed NBT GameTest template at `data/enshrouded/structure/foundation_empty.nbt`, decoded during review as a 3×3×3 structure;
- GameTests are registered through `@GameTestHolder(Enshrouded.MOD_ID)`, a supported NeoForge 1.21.1 registration mechanism;
- `gameTestServer` sets `neoforge.enabledGameTestNamespaces` to `enshrouded`;
- `gameTestServer` sets `setForceExit false`, required by the NeoForge 1.21.1 NeoGradle GameTest guidance so a normal test-server exit is observable by Gradle rather than being reported as an unconditional daemon failure;
- dedicated-server smoke path requires normal server startup plus the Enshrouded bootstrap marker;
- production-JAR sanity gate verifies mod metadata/main class and rejects leaked GameTest-only classes;
- official Gradle 8.14 wrapper is committed to the repository. The wrapper JAR Git blob SHA is `1b33c55baabb587c669f562ae36f953de2481846`, exactly matching Gradle upstream `v8.14.0`;
- final CI commands execute through the committed `./gradlew` wrapper rather than an ambient Gradle installation.

Verification evidence/blocker:

- an earlier functioning runner executed the unit-test and build portions successfully while the wrapper bootstrap was being established; that run is historical evidence only and does not satisfy the final-HEAD gate;
- push run `33093634341` on code/CI HEAD `db183f21d80ecaa88fcba33c25998350c6361759` failed before checkout and exposes no steps (`steps=null`);
- draft PR #2 triggered pull-request run `33093751380`, which failed with the same pre-checkout `steps=null` condition;
- later push runs, including `33094149126`, continued to fail before checkout with `steps=null`;
- a separate experiment removing the workflow concurrency group produced the same failure mode and was reverted;
- therefore no current failure is attributable to unit tests, Gradle, GameTests or dedicated-server bootstrap because none of those steps executes.

This task deliberately remains open. Its checkboxes and `✅-` rename require a normally initialized final-HEAD runner and GREEN unit, build, JAR sanity, GameTest and dedicated-server gates.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Every later branch has a proven test path appropriate to deterministic logic, world logic and server bootstrap.
