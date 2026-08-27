# Project Status

Last structural update: 2026-08-27.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined from repository base `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.
- [ ] 00 Foundation — implementation is present on `round-1-foundation`; draft PR #2 is open. Acceptance is blocked by two explicit conditions: GitHub Actions jobs still terminate before checkout with `steps=null`, and the newly reviewed Foundation-owned `ProgressionOwnerResolver` / `FlamePassageQuery` contracts still require test-first implementation once a RED can be observed. Do not merge or rename tasks with `✅-` until those contracts exist and all final gates are GREEN.
- [ ] 01 Shroud Field — not implemented.
- [ ] 02 Terrain Corruption — not implemented.
- [ ] 03 Exposure — not implemented.
- [ ] 04 Corrupted Ecology — not implemented.
- [ ] 05 Flame Progression — not implemented.
- [ ] 06 Lich & Story — not implemented.
- [ ] 07 Client Experience — not implemented.
- [ ] 08 Integrations — not implemented.
- [ ] 09 Hardening — not implemented.

## Foundation implementation checkpoint

Branch: `round-1-foundation`

Key audited implementation commits include:

- `38a5d92f6869215d7388851777335bee00b61700` — executable regression guard preventing Spore/Infnexus from entering build configuration or production Java;
- `76ecc4c72c0d97ac4caaa9563832374e98f7aad1` / `6ef8962a92ce665dd78f51f2a72dafd363a62879` — two-boot dedicated-server save/reload harness and bounded graceful-stop handling;
- `80ad0a76f17fc054d106666c5d2ab57a8a62b603` / `14798e9c5c339b1417c82ea4156ed60ab07094f1` — GNU `timeout` bounds the entire server boot and `exec` makes the monitored PID the timeout supervisor rather than an intermediate shell;
- `00a679c2dbeb5ee5d0d76add1edd5019beb35cbd` — CI switched from one-boot server smoke to the two-boot reload harness;
- `8915cdadbcd43e68c1dec0fe80ed047e2eb9dc51` — explicit `System.in` forwarding to NeoGradle `runServer` because the generated `JavaExec` task does not configure `standardInput`;
- `b212e539de7ebd26cdf54cb8b1c2a99e449ab4fa` — dedicated server run made explicitly headless with `--nogui`;
- `df124bb7b3a16d828e67a87e7105c39e1a17d21e` / `8337e730385799247a23d748b2573d1dcd27664c` — Gradle 8.14 distribution checksum pin and CI enforcement;
- `e4547483af1ca4b642cf90ea04a593514cc8d3ad` / `beba82827f6650351dd5a11683540051a84ee144` — repository BSD-2-Clause license plus executable consistency test;
- `b0574c6246771b116221d07239cc5acb287d7c27` / `0e9ca1999187cac365c2dcd408f663325d24d6e1` — production JAR packages license/notices and the JAR sanity gate validates metadata expansion and artifact boundaries;
- `8294d6847bc77898177bf96c65269ae0e3aa2454` / `ac5f957fa3b890038d60c4146443722d8a599292` — architecture guard keeps optional providers out of the core API and client-only Minecraft classes out of common/server code;
- `85803b249f900f5e21338f89d63265e34d0bdfeb` — expanded stable-id/value contract coverage for player/team/world progression owners, invalid UUIDs and Shroud intensity edge cases;
- `5377ecf6effb23a90517dfc44c897d0c5918bfa9` — public API shape regression tests freeze the existing Shroud/mutation/magic/Lich provider boundaries;
- `fc9eea10c6f20584ddab8d51adf9272b9b43daa2` — lightweight CI harness prerequisite gate without pipefail/SIGPIPE ambiguity.

Draft PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`.

Implemented scope includes:

- NeoForge 1.21.1 / Java 21 project scaffold;
- existing stable Enshrouded-owned domain contracts;
- upstream provenance and current-pack inventory;
- deterministic unit-test fixtures and contract tests;
- executable exclusion guard keeping Spore/Infnexus out of core/build configuration;
- architecture boundary guard preventing optional implementation imports in core APIs and client-only Minecraft references in common/server code;
- GameTest source set, valid 3×3×3 template and reusable world-level fixtures;
- `@GameTestHolder(enshrouded)` registration and namespace restriction for Foundation GameTests;
- NeoGradle `gameTestServer` configured with `setForceExit false`;
- two-boot dedicated-server save/reload verification using a persistent scoreboard sentinel in the same world;
- explicit stdin forwarding for `runServer`, required for FIFO-fed `save-all` and `stop` console commands;
- bounded server process supervision through GNU `timeout --kill-after`;
- headless `runServer --nogui` behavior;
- current NeoForge 1.21.1 dependency metadata schema using `type="required"`;
- official Gradle 8.14 wrapper JAR and official generated launchers;
- wrapper Git blob SHAs match Gradle `v8.14.0`: POSIX launcher `0f14772e0e50ffe504fa3be1a869e6281b09ccd1`, Windows launcher `8de1053a1f921b9ac5910187e2f99fdaa774f81a`, wrapper JAR `1b33c55baabb587c669f562ae36f953de2481846`;
- Gradle distribution is pinned to `gradle-8.14-bin.zip` with SHA-256 `61ad310d3c7d3e5da131b76bbf22b5a4c0786e9d892dae8c1658d4b484de3caa`;
- `gradlew` remains executable in Git (`100755`);
- repository `LICENSE` matches `mod_license=BSD-2-Clause`;
- production JAR contract embeds `LICENSE` and `THIRD_PARTY_NOTICES.md` and rejects unexpanded metadata placeholders or leaked GameTest classes;
- CI verifies shell harness prerequisites, wrapper provenance/distribution checksum, unit tests, diff sanity, NeoForge build, production JAR contents, GameTests and the two-boot reload scenario.

## Foundation contract refinement still open

Reviewed cross-stage dependencies exposed a contract ownership bug before Stage 03/05 implementation:

- `ProgressionOwnerResolver` and `FlamePassageQuery` are now Foundation-owned per `DECISIONS.md` decision 31;
- Foundation standalone behavior is player UUID ownership + Passage Level 1;
- Stage 03 consumes these interfaces directly and must not create a Stage 05 stub;
- Stage 05 supplies the persistence-backed `FlamePassageService` through the same interface;
- Stage 08 may substitute `FtbTeamsOwnerResolver` without changing exposure/progression consumers;
- `plans/PENDING.md` tracks implementation as `ENSH-L1-FLAME-PASSAGE-001`;
- production implementation is deliberately deferred until a test RED can be observed, preserving the repository's TDD rule.

A separate planning cycle was also removed: Stage 05 now owns only the generic Flame ritual registry/executor/checkpoint engine, while Stage 06 owns the authentic first Lich Skull and concrete `enshrouded:lich_manifestation_1` binding. The branch order remains causal and no 05 <-> 06 circular dependency remains.

## Structural evidence while Actions is unavailable

- `scripts/ci/dedicated-server-reload-smoke.sh` has been structurally reviewed with bounded startup/save/reload/shutdown paths;
- a fake-server simulation exercised both FIFO boots, save marker, persistent scoreboard sentinel and graceful shutdown and reached its PASS marker before the later process-supervision hardening;
- inspection of NeoGradle `NG_7.1` `RunsUtil.createTasks(...)` confirmed run tasks are `JavaExec` and no `standardInput` forwarding is configured there, motivating the explicit project-side `System.in` fix;
- NeoForge 1.21.1 documentation confirms `@GameTestHolder(enshrouded)` + `@PrefixGameTestTemplate(false)` resolves `foundation_empty` to the committed `data/enshrouded/structure/foundation_empty.nbt` template;
- NeoForge 1.21.1 API references confirm the GameTest helper methods used by Foundation and `MinecraftServer.saveEverything(boolean, boolean, boolean)` signatures;
- official Gradle checksum reference confirms the committed Gradle 8.14 binary distribution SHA-256;
- these are structural/control-flow/API checks only and do not replace a real Minecraft/NeoForge runner.

## Current external verification blocker

- Enshrouded push and pull-request runs repeatedly terminate before checkout with `steps=null`;
- controlled Enshrouded reruns continue to produce the same no-step failure, with no downloadable job log blob;
- an explicit `ubuntu-24.04` runner-label control also failed before steps and was reverted to `ubuntu-latest`, ruling out the `ubuntu-latest` alias as the specific cause;
- removing the workflow concurrency group was separately tested and did not change the failure mode, so that experiment was reverted;
- cross-repository control: private `Gustavaopere/Volcanoes`, workflow `33099719939`, job `98615923587`, showed the same `steps=null` failure mode during the same period, demonstrating the blocker is not specific to the Enshrouded workflow/repository;
- the local fallback environment cannot resolve `services.gradle.org` and has no usable Gradle/NeoForge cache;
- because no available real executor can start the build, these failures are not evidence of a failing Gradle build, unit test, GameTest or server reload test.

Static review while Actions was unavailable has already caught and corrected multiple real defects: NeoGradle GameTest force-exit behavior, legacy NeoForge dependency metadata, noncanonical abbreviated Gradle launchers, missing reload proof, missing `runServer` stdin forwarding, lack of headless server argument, weak process-tree timeout handling, missing Gradle distribution checksum, missing repository license/artifact notices, weak JAR metadata validation, unenforced architecture boundaries and cross-stage progression/ritual ownership cycles. All corrections still require executable final-HEAD verification.

## Immediate next step

When a real runner becomes available, first implement `ProgressionOwnerResolver` and `FlamePassageQuery` test-first and observe the RED/GREEN cycle. Then execute the final branch HEAD through wrapper integrity, unit tests, diff sanity, build/JAR sanity, GameTests and the two-boot scoreboard save/reload harness. If any code-level failure appears, fix that failure first. Only after all Foundation contracts and gates pass may the four Foundation tasks be renamed with `✅-`, this status become complete, PR #2 be marked ready and merged, and `feat/01-shroud-state` be created from the resulting latest `main`.

## Level 1 release gate

Level 1 is not considered complete until every task under `00` through `09` that is marked **Level 1 required** has a verified GREEN implementation and has been renamed with the `✅-` prefix after merge. Optional flavor integrations may remain open only if `PENDING.md` explicitly records that they do not block standalone Level 1 acceptance.

## Rules for updating this file

Every merged implementation task records:

- implementation branch;
- final branch HEAD;
- PR number;
- merge SHA on `main`;
- CI workflow/run result;
- task file renamed to `✅-...` when acceptance is satisfied;
- exact blocker when acceptance is partial;
- next branch to create from the resulting `main`.
