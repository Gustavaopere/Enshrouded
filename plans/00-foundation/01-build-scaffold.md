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

- [x] Add a bootstrap smoke test that fails before the mod metadata/main class exists.
- [x] Verify the expected RED/bootstrap failure before implementation — workflow `33000394152` reached `BootstrapContractTest` and failed only because the main mod class did not yet exist.
- [x] Verify the final scaffold with committed `./gradlew test` plus `./gradlew build` GREEN — PR workflow `33165771852`, job `98830694040`, HEAD `0b1940012628ff0d762961cccb480dc72989455d`.
- [x] Run the dedicated-server save/reload profile and verify bootstrap/reload reaches a clean stop rather than classloading client code — the same job completed `Dedicated-server save/reload smoke test` GREEN.

## Final implementation checkpoint — 2026-08-28

Implemented on `round-1-foundation`:

- Minecraft `1.21.1`, NeoForge `21.1.248`, Java 21 and mod id `enshrouded` are pinned in the build/metadata;
- `Enshrouded`, `ModRegistries` and the initial config namespace exist without client-side imports in the common bootstrap path;
- NeoForge dependency metadata uses the current 1.21.1 schema (`type="required"`) rather than legacy `mandatory=true`;
- official Gradle 8.14 wrapper is committed, with executable Unix launcher and byte-identical upstream wrapper JAR (`1b33c55baabb587c669f562ae36f953de2481846`);
- wrapper distribution is pinned to `gradle-8.14-bin.zip` with SHA-256 `61ad310d3c7d3e5da131b76bbf22b5a4c0786e9d892dae8c1658d4b484de3caa`;
- project license is declared as `BSD-2-Clause` and a matching repository `LICENSE` is present;
- production JAR packaging includes `LICENSE` and `THIRD_PARTY_NOTICES.md`;
- CI invokes verification through `./gradlew` and validates wrapper provenance, distribution checksum, metadata expansion, required dependencies, license/notices packaging, absence of GameTest classes, and the common entrypoint;
- dedicated-server verification is the two-boot save/reload harness rather than startup-only smoke.

## Executable acceptance evidence

PR workflow `33165771852`, job `98830694040`, on exact implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` completed GREEN. The job executed checkout, shell harness prerequisites, Java 21 setup, wrapper provenance/integrity, unit tests, diff sanity, NeoForge build, built-JAR verification, GameTest server and dedicated-server save/reload smoke.

The closing documentation-only checkpoint that renames/completes Foundation must itself pass the same pipeline before merge; if it does not, this task is reopened.

## Merge gate

- [x] All task-specific tests are GREEN on the verified implementation HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN and GameTest/dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` contains only genuine later-stage boundaries.
- [x] Task is ready to be renamed with `✅-` in the Foundation closing checkpoint merged to `main`.

**Acceptance:** A clean checkout builds a NeoForge 1.21.1 JAR under Java 21 and CI proves server-safe bootstrap and reload.
