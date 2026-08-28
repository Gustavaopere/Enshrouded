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

- [x] Create one intentionally failing unit fixture and one intentionally failing GameTest to prove both paths execute, then revert/replace them with real smoke assertions.
- [ ] Verify clean unit, GameTest and dedicated-server jobs on the final HEAD.

## Current verification checkpoint — 2026-08-27

Implemented on `round-1-foundation`:

- deterministic `TickClock` and scripted-random fixtures with executable JUnit coverage;
- `GameTestBootstrap` helpers for `ServerLevel`, block mutation, mock player state, entity spawning/state and forced save/flush;
- Foundation GameTests for template/server-level/block mutation and player/entity/save fixtures;
- a valid compressed NBT GameTest template at `data/enshrouded/structure/foundation_empty.nbt`, decoded during review as a 3×3×3 structure;
- GameTests registered through `@GameTestHolder(Enshrouded.MOD_ID)` with the `enshrouded` namespace enabled;
- `gameTestServer` uses `setForceExit false` so a successful test server is not reported as an unconditional Gradle daemon failure;
- production-JAR sanity verifies mod metadata/main class and rejects leaked GameTest-only classes;
- official Gradle 8.14 wrapper is committed and CI executes through `./gradlew`;
- dedicated `scripts/ci/dedicated-server-reload-smoke.sh` implements the save/reload scenario with two server process launches against the same test world;
- first boot creates scoreboard objective `ensh_reload`, sets `ensh_sentinel=1`, executes `save-all flush`, requires the save marker and performs a graceful `stop`;
- the harness requires `world/level.dat` after the first shutdown;
- second boot reopens the same world and succeeds only when `execute if score ensh_sentinel ensh_reload matches 1` emits `ENSHROUDED_RELOAD_SENTINEL_OK`;
- startup, bootstrap marker, save, reload marker and graceful shutdown all have explicit fail-closed timeouts;
- the entire `runServer` process tree is additionally supervised by GNU `timeout --kill-after`; `exec timeout` makes `SERVER_PID` the actual supervisor rather than an intermediate shell;
- `runServer` forwards `System.in` explicitly because NeoGradle creates the run as `JavaExec` without configuring `standardInput`; this is required for FIFO-fed `save-all`/`stop` commands to reach Minecraft;
- the dedicated server run also uses `--nogui` for a deterministic headless CI environment;
- CI has a pre-Gradle shell gate that executes `bash -n` on the harness and requires GNU `timeout` before downloading/starting Java/Gradle work.

## TDD execution evidence

- unit-path RED: workflow `33040886237` executed 10 tests and failed only on the deliberately failing `TickClock` assertion; the intentional failure was then replaced with the permanent deterministic fixture assertions;
- GameTest-path RED: workflow `33041461064` compiled the `gameTest` source set, discovered exactly one GameTest and failed only on the intentional `helper.fail("INTENTIONAL RED: Enshrouded GameTest discovery is active")`, proving real GameTest discovery/execution;
- the zero-discovery guard remains in CI and rejects `No test functions were given!`.

## Structural verification performed while Actions is unavailable

- the **current** reload harness passes `bash -n` after the GNU-timeout/process-supervision hardening;
- a current fake-server simulation exercised `exec timeout`, both FIFO boots, startup/bootstrap markers, `save-all flush`, `world/level.dat`, persisted sentinel, second-boot sentinel query and graceful `stop`, and reached both `Dedicated-server save/reload smoke test: PASS` and `HARNESS_CURRENT_SIMULATION=PASS`;
- the local pure-Java Foundation verification passed 25/25 test/guard methods outside Minecraft runtime, including bootstrap classloading with minimal NeoForge/SLF4J type stubs;
- Java 21 compile-shape verification of the **current** `GameTestBootstrap` + `FoundationGameTests` sources passed against stubs matching the 1.21.1 API signatures actually used (`GameTestHelper#getLevel`, `makeMockPlayer`, `setBlock`, `assertBlockPresent`, generic `spawn`, `assertTrue`, `MinecraftServer#saveEverything`, `@GameTestHolder`, `@PrefixGameTestTemplate`), producing `GAMETEST_COMPILE_SHAPE=PASS`;
- official NeoForge 1.21.1 documentation confirms `@GameTestHolder(enshrouded)` + `@PrefixGameTestTemplate(false)` resolves `template="foundation_empty"` to `data/enshrouded/structure/foundation_empty.nbt`;
- these checks prove shell orchestration/pure-Java/API compilation only; they are **not** accepted as Minecraft/NeoForge runtime evidence.

## External verification blocker

- current GitHub Actions jobs terminate before checkout and expose `steps=null`;
- a cross-repository control on private `Gustavaopere/Volcanoes` produces the same `verify` + `steps=null` behavior, so the current runner-start failure is not specific to Enshrouded;
- explicit `ubuntu-24.04` and concurrency-group experiments did not change the pre-step failure and were reverted;
- the local fallback environment has no Gradle/NeoForge cache and cannot resolve `services.gradle.org`, so it cannot execute the actual NeoForge build offline;
- therefore the current two-boot harness and current GameTests have not yet run GREEN against a real Minecraft 1.21.1/NeoForge server.

The former restart/reload **implementation gap is closed**, but its executable acceptance gate remains open. This task deliberately remains unrenamed until a normally initialized final-HEAD runner proves unit, build, JAR, GameTest and two-boot dedicated-server reload gates GREEN.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD under the committed Gradle/NeoForge stack.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN.
- [ ] GameTests discover and execute the Foundation tests GREEN.
- [ ] Save → graceful stop → second boot → persisted scoreboard sentinel is proven GREEN by `dedicated-server-reload-smoke.sh` on real NeoForge.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Every later branch has a proven test path appropriate to deterministic logic, world logic and server bootstrap/reload.
