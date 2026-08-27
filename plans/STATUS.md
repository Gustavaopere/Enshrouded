# Project Status

Last structural update: 2026-08-27.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined from repository base `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.
- [ ] 00 Foundation — implementation is present on `round-1-foundation`; draft PR #2 is open. Acceptance remains blocked because current GitHub Actions jobs terminate before checkout with no recorded steps, so the final unit/build/JAR/GameTest/two-boot reload gates have not executed on the current HEAD. Do not merge or rename tasks with `✅-` until those gates are GREEN.
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

Latest audited implementation changes before this status commit include:

- `38a5d92f6869215d7388851777335bee00b61700` — executable regression guard preventing Spore/Infnexus from entering build configuration or production Java;
- `76ecc4c72c0d97ac4caaa9563832374e98f7aad1` / `6ef8962a92ce665dd78f51f2a72dafd363a62879` — two-boot dedicated-server save/reload harness and bounded graceful-stop handling;
- `00a679c2dbeb5ee5d0d76add1edd5019beb35cbd` — CI switched from one-boot server smoke to the two-boot reload harness;
- `8915cdadbcd43e68c1dec0fe80ed047e2eb9dc51` — explicit `System.in` forwarding to NeoGradle `runServer` because the generated `JavaExec` task does not configure `standardInput`;
- `b212e539de7ebd26cdf54cb8b1c2a99e449ab4fa` — dedicated server run made explicitly headless with `--nogui`.

Draft PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`.

Implemented scope includes:

- NeoForge 1.21.1 / Java 21 project scaffold;
- stable Enshrouded-owned domain contracts;
- upstream provenance and current-pack inventory;
- deterministic unit-test fixtures and contract tests;
- executable exclusion guard keeping Spore/Infnexus out of core/build configuration;
- GameTest source set, valid 3×3×3 template and reusable world-level fixtures;
- `@GameTestHolder(enshrouded)` registration and namespace restriction for Foundation GameTests;
- NeoGradle `gameTestServer` configured with `setForceExit false`;
- two-boot dedicated-server save/reload verification using a persistent scoreboard sentinel in the same world;
- explicit stdin forwarding for `runServer`, required for FIFO-fed `save-all` and `stop` console commands;
- headless `runServer --nogui` behavior;
- current NeoForge 1.21.1 dependency metadata schema using `type="required"`;
- official Gradle 8.14 wrapper JAR and official generated launchers;
- wrapper Git blob SHAs match Gradle `v8.14.0`: POSIX launcher `0f14772e0e50ffe504fa3be1a869e6281b09ccd1`, Windows launcher `8de1053a1f921b9ac5910187e2f99fdaa774f81a`, wrapper JAR `1b33c55baabb587c669f562ae36f953de2481846`;
- `gradlew` remains executable in Git (`100755`);
- CI verifies wrapper provenance/executable mode, unit tests, diff sanity, NeoForge build, production JAR contents, GameTests and the two-boot reload scenario.

## Structural evidence while Actions is unavailable

- `scripts/ci/dedicated-server-reload-smoke.sh` passes `bash -n`;
- a fake-server simulation exercised both FIFO boots, save marker, persistent scoreboard sentinel and graceful shutdown and reached its PASS marker;
- inspection of NeoGradle `NG_7.1` `RunsUtil.createTasks(...)` confirmed run tasks are `JavaExec` and no `standardInput` forwarding is configured there, motivating the explicit project-side `System.in` fix;
- these are structural/control-flow checks only and do not replace a real Minecraft/NeoForge runner.

## Current external verification blocker

- Enshrouded push and pull-request runs repeatedly terminate before checkout with `steps=null`;
- controlled Enshrouded reruns continue to produce the same no-step failure;
- cross-repository control: private `Gustavaopere/Volcanoes`, workflow `33099719939`, job `98615923587`, showed the same `steps=null` failure mode during the same period, demonstrating the blocker is not specific to the Enshrouded workflow/repository;
- removing the workflow concurrency group was separately tested and did not change the failure mode, so that experiment was reverted;
- the local fallback environment cannot resolve `services.gradle.org` and has no usable Gradle/NeoForge cache;
- because no available real executor can start the build, these failures are not evidence of a failing Gradle build, unit test, GameTest or server reload test.

Static review while Actions was unavailable has already caught and corrected multiple real defects: NeoGradle GameTest force-exit behavior, legacy NeoForge dependency metadata, noncanonical abbreviated Gradle launchers, missing reload proof, missing `runServer` stdin forwarding and lack of headless server argument. All corrections still require executable final-HEAD verification.

## Immediate next step

Re-run PR #2 after GitHub Actions can allocate/initialize the job normally. The final branch HEAD must execute wrapper integrity, unit tests, diff sanity, build/JAR sanity, GameTests and the two-boot scoreboard save/reload harness GREEN. If any code-level failure appears once the runner starts, fix that failure first. Only after those gates pass may the four Foundation tasks be renamed with `✅-`, this status become complete, PR #2 be marked ready and merged, and `feat/01-shroud-state` be created from the resulting latest `main`.

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
