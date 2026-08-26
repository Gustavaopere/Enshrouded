# Enshrouded Plan — Build Scaffold

**Milestone:** Level 1 required.

**Goal:** bootstrap the actual mod project and CI on the pack-compatible NeoForge/Java baseline.

**Planned types:** `Enshrouded`, `ModRegistries`, `EnshroudedConfig`.

## Files

- Create `settings.gradle`, `build.gradle`, `gradle.properties`, Gradle wrapper and `src/main` resource skeleton.
- Create `src/main/java/com/gustavaopere/enshrouded/Enshrouded.java`.
- Create `.github/workflows/ci.yml` and update root `README.md`.

## Dependencies

- Planning baseline merged to `main`.

## Implementation contract

- Use Minecraft 1.21.1, NeoForge 21.1.248 and Java 21.
- Register mod id `enshrouded`; dedicated server must bootstrap without client classes.
- CI must run unit tests, NeoForge build and a built-JAR sanity check.
- Config is split into common/server/client concerns only when a value truly belongs on that side.

## TDD / verification

- [ ] Add a bootstrap smoke test that fails before the mod metadata/main class exists.
- [ ] Run `./gradlew test` and verify the expected RED/bootstrap failure before implementation.
- [ ] Implement minimal scaffold and verify `./gradlew test` plus `./gradlew build` GREEN.
- [ ] Run a dedicated-server smoke command/profile and verify registry/bootstrap reaches a clean stop rather than classloading client code.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A clean checkout builds a NeoForge 1.21.1 JAR under Java 21 and CI can prove server-safe bootstrap.
