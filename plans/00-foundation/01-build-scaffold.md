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
- [ ] Verify the current final scaffold with committed `./gradlew test` plus `./gradlew build` GREEN.
- [ ] Run the current dedicated-server save/reload profile and verify bootstrap/reload reaches a clean stop rather than classloading client code.

## Current implementation checkpoint — 2026-08-27

Implemented on `round-1-foundation`:

- Minecraft `1.21.1`, NeoForge `21.1.248`, Java 21 and mod id `enshrouded` are pinned in the build/metadata;
- `Enshrouded`, `ModRegistries` and the initial config namespace exist without client-side imports in the common bootstrap path;
- NeoForge dependency metadata uses the current 1.21.1 schema (`type="required"`) rather than the legacy `mandatory=true` form;
- official Gradle 8.14 wrapper is committed, with executable Unix launcher and byte-identical upstream wrapper JAR (`1b33c55baabb587c669f562ae36f953de2481846`);
- wrapper distribution is pinned to `gradle-8.14-bin.zip` with SHA-256 `61ad310d3c7d3e5da131b76bbf22b5a4c0786e9d892dae8c1658d4b484de3caa`;
- project license is declared as `BSD-2-Clause` and a matching repository `LICENSE` is present;
- production JAR packaging includes `LICENSE` and `THIRD_PARTY_NOTICES.md`;
- CI invokes verification through `./gradlew` and validates wrapper provenance, distribution checksum, metadata expansion, required dependencies, license/notices packaging, absence of GameTest classes, and the common entrypoint;
- dedicated-server verification is now the two-boot save/reload harness rather than startup-only smoke.

Historical runner evidence reached unit tests/build GREEN before the final wrapper/GameTest/reload changes, but this does not satisfy final-HEAD acceptance. Current Actions jobs terminate before checkout with `steps=null`; therefore the final wrapper/build/dedicated-server gate cannot currently be re-proved on the branch HEAD. This task stays open and unrenamed.

## Structural verification while final Actions execution is blocked

- the current `neoforge.mods.toml` and `pack.mcmeta` were expanded with the actual current `gradle.properties` values outside Gradle and parsed successfully as TOML/JSON;
- no `${...}` placeholders remained after expansion;
- the resulting dependency metadata is `neoforge [21.1.248,) type=required` and `minecraft [1.21.1,1.21.2) type=required`;
- the expanded resource pack metadata reports `pack_format=34` and `enshrouded resources`;
- `BootstrapContractTest` passed in the local Java 21 structural suite using only minimal NeoForge/SLF4J type stubs for entrypoint classloading and the real current metadata path;
- these structural checks do not replace the committed Gradle/NeoForge build/JAR/runtime gates.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A clean checkout builds a NeoForge 1.21.1 JAR under Java 21 and CI can prove server-safe bootstrap.
