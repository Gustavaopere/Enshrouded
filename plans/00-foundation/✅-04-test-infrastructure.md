# ✅ Enshrouded Plan — Test Infrastructure

**Milestone:** Level 1 required — completed.

**Goal:** provide reusable unit, GameTest and dedicated-server verification before gameplay state is implemented.

## Completed implementation

- Deterministic `TickClock` and scripted-random fixtures with JUnit coverage.
- Isolated `gameTest` source set, `GameTestBootstrap`, Foundation GameTests and 3×3×3 template.
- GameTests cover server-level access, block mutation, mock player/entity state and forced save/flush paths.
- CI explicitly rejects zero GameTest discovery.
- Dedicated `scripts/ci/dedicated-server-reload-smoke.sh` performs two real server boots on the same world.
- First boot writes a persistent scoreboard sentinel, runs `save-all flush`, requires save output/world data and stops gracefully.
- Second boot reopens the world and must observe the persisted sentinel.
- Startup/save/reload/stop operations are fail-closed with bounded timeouts; the whole server process tree is supervised with GNU `timeout --kill-after`.
- `runServer` forwards stdin explicitly and runs `--nogui`.
- The unsupported NeoGradle 7.1.26 `setForceExit false` DSL discovered by final executable CI was removed in `1c221423ebb9a62c487368d358788a1596048441`.

## TDD / verification

- [x] Unit-path intentional RED: workflow `33040886237` failed on the deliberately failing fixture.
- [x] GameTest-path intentional RED: workflow `33041461064` discovered one real GameTest and failed only on the intentional `helper.fail(...)`.
- [x] Final committed unit suite GREEN: 33/33.
- [x] Final GameTest server GREEN with non-zero discovery.
- [x] Final dedicated-server save → graceful stop → second boot → persisted sentinel GREEN.
- [x] NeoForge build/JAR gates GREEN.

## Final acceptance evidence

- Branch: `round-1-foundation`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- PR: #2
- Workflow: `33165771852`
- Job: `98830694040`
- Result: complete success across all CI steps.
- Merge SHA: `0b3c345673b81adbbc34a61505cb16200f689ba2`

**Acceptance:** satisfied. Later branches have proven deterministic, world/GameTest and real dedicated-server save/reload paths.
