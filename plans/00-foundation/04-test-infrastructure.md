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

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Every later branch has a proven test path appropriate to deterministic logic, world logic and server bootstrap.
