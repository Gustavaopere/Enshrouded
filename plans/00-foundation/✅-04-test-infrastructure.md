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

- [x] Intentionally failing unit fixture proved JUnit execution, then was replaced by permanent deterministic assertions.
- [x] Intentionally failing GameTest proved real discovery/execution, then was replaced by permanent smoke assertions.
- [x] Final unit, GameTest and dedicated-server save/reload gates are GREEN on implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d`.

## Final implementation checkpoint — 2026-08-28

Implemented on `round-1-foundation`:

- deterministic `TickClock` and scripted-random fixtures with executable JUnit coverage;
- `GameTestBootstrap` helpers for `ServerLevel`, block mutation, mock player state, entity spawning/state and forced save/flush;
- Foundation GameTests for template/server-level/block mutation and player/entity/save fixtures;
- valid compressed 3×3×3 NBT GameTest template at `data/enshrouded/structure/foundation_empty.nbt`;
- `@GameTestHolder(Enshrouded.MOD_ID)` registration with `enshrouded` namespace;
- `gameTestServer` uses `setForceExit false`;
- production-JAR sanity rejects GameTest-only classes;
- official Gradle 8.14 wrapper and `./gradlew` execution path;
- `scripts/ci/dedicated-server-reload-smoke.sh` performs two real server launches against the same world;
- first boot persists scoreboard objective/sentinel, performs `save-all flush` and graceful `stop`;
- second boot requires the persisted sentinel and emits `ENSHROUDED_RELOAD_SENTINEL_OK`;
- startup, save, reload and shutdown paths are fail-closed with timeouts;
- GNU `timeout --kill-after` supervises the full Gradle/Java process tree;
- `runServer` forwards `System.in` for FIFO console commands and uses `--nogui`;
- CI validates shell prerequisites before Gradle work;
- zero-discovery guard rejects `No test functions were given!`.

## RED evidence

- Unit RED: workflow `33040886237` executed the suite and failed only on the deliberate fixture assertion.
- GameTest RED: workflow `33041461064` compiled the GameTest source set, discovered exactly one test and failed only on the deliberate `helper.fail(...)`.

## Executable GREEN acceptance evidence

PR workflow `33165771852`, job `98830694040`, on exact implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` completed:

- wrapper provenance and integrity — GREEN;
- unit tests — GREEN;
- diff sanity — GREEN;
- NeoForge build — GREEN;
- built-JAR verification — GREEN;
- GameTest server — GREEN;
- dedicated-server two-boot save/reload smoke — GREEN.

A real GitHub-hosted runner `1000002910` executed the job. The closing documentation-only checkpoint must itself pass the same pipeline before merge; otherwise this task is reopened.

## Merge gate

- [x] All task-specific tests are GREEN on the verified implementation HEAD under the committed Gradle/NeoForge stack.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN.
- [x] GameTests discover and execute Foundation tests GREEN.
- [x] Save → graceful stop → second boot → persisted scoreboard sentinel is GREEN on real NeoForge.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` contains only later-stage boundaries.
- [x] Task is ready to be renamed with `✅-` in the Foundation closing checkpoint merged to `main`.

**Acceptance:** Every later branch has a proven test path appropriate to deterministic logic, world logic and server bootstrap/reload.
