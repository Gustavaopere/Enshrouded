# Enshrouded Plan — Level 1 Test Matrix

**Milestone:** Level 1 required.

**Goal:** run the full standalone and selected current-pack compatibility matrix over the complete vertical slice.

**Planned types:** `LevelOneScenarioGameTests`.

## Files

- Create/expand `src/gameTest/java/com/gustavaopere/enshrouded/gametest/LevelOneScenarioGameTests.java` inside the existing Foundation `gameTest` source set; do not create a second `gametest` source root with different casing.
- Add any required structures/resources under the matching existing `src/gameTest/resources/` source root.
- Create `docs/testing/level1-matrix.md`.
- Extend CI profiles/jobs.

## Dependencies

- 00-08 required Level 1 tasks implemented.

## Implementation contract

- Matrix covers standalone mandatory dependencies plus current-pack profiles for Ars Zero, Ars/Iron, Epic Fight, FTB/MineColonies and JourneyMap where automation is feasible.
- End-to-end scenario: discover/seed core -> observe expansion -> enter ordinary Shroud -> corrupt mob -> escape/recover -> destroy core -> observe regression -> fight manifestation -> receive skull -> offer at altar -> persist Level 1 completion.
- Deadly Shroud remains gated at Flame 1 and Red Sludge is lethal.
- Multiplayer scenario verifies independent/default owners and optional team owner behavior.
- Server restart is exercised mid-expansion, mid-exposure, mid-purification and around encounter/reward boundaries.
- Stage 09 extends the already-registered Foundation `gameTest` source set and namespace; it must not add a parallel source-set spelling/casing that Gradle/NeoGradle would ignore.

## TDD / verification

- [ ] Run all unit tests.
- [ ] Run all GameTests including full Level 1 scenario.
- [ ] Verify `compileGameTestJava` and GameTest discovery include `LevelOneScenarioGameTests` from the canonical `src/gameTest` source set.
- [ ] Run dedicated-server smoke standalone.
- [ ] Run selected integration profile smoke tests and document any automation gaps with manual reproducible commands.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The complete Level 1 loop is reproducibly GREEN standalone and does not regress under the critical installed-mod integration profiles.
