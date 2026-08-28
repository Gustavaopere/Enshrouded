# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined from repository base `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.
- [ ] 00 Foundation — implementation is present on `round-1-foundation`; draft PR #2 is open. Foundation-owned progression/passage and Flame-ward boundaries are implemented after isolated Java 21 RED -> GREEN cycles, and `EncounterContext` now requires an explicit immutable origin after a separate RED -> GREEN correction. Current pure-Java/repository guards and GameTest compile-shape checks are clean, but acceptance remains blocked because GitHub Actions jobs terminate before runner allocation with `runner_id=0`, `runner_name=""` and `steps=[]`; the committed Gradle/JUnit, NeoForge build, real GameTest and real dedicated-server gates therefore have not executed on the final HEAD. Do not merge or rename tasks with `✅-` until all final gates are GREEN.
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
- `85803b249f900f5e21338f89d63265e34d0bdfeb` — expanded stable-id/value contract coverage for player/team/world progression owners, invalid UUIDs and Shroud intensity edge cases;
- `c714af1db5256537ea5e8a9f89c680f2c3e32d6a` — progression owner/passage RED checkpoint before production interfaces existed;
- `66d4e5c23771792df15b1548a1e45e835bd1b9d1` / `d9e5edc8a413a5034475914b671a4d7f13d97be5` — Foundation `ProgressionOwnerResolver` and `FlamePassageQuery` defaults;
- `93127ff748029cd39fa33502eb704b10817308f0` / `787d9c36c25459e8815066b8bd03fc20ff4285aa` — Foundation Flame ward RED checkpoint and minimal `FlameWardQuery` no-ward fallback;
- `9f43281e48375770f1fda45d1b0a7755eb17efae` and later updates — public API shape regression coverage includes progression and ward boundaries;
- `3578ff2d43f39c6b54fb1e75b5f8adf653dea6be` — architecture guard now forbids core `api/` from importing Enshrouded implementation packages, not only optional third-party mods;
- `3794568bbaebf105b070e08055e92715d30c1a3c` / `5fb833e1b9e912819b0c1438a923dfa8ce7f0981` — explicit encounter-origin RED checkpoint and production removal of the origin-omitting `EncounterContext(UUID,int,long)` overload;
- `cea155842dfdfb371c99c6ccb1ca7ce5d88936fa` — public API shape guard permanently requires the single explicit-origin `EncounterContext(UUID,BlockPos,int,long)` constructor.

Draft PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`.

Implemented scope includes:

- NeoForge 1.21.1 / Java 21 project scaffold;
- stable Enshrouded-owned domain contracts, including owner/passage read boundaries required by Deadly Shroud and a Foundation Flame-ward read boundary required before Stage 05;
- canonical ward semantics: `ShroudSample` keeps logical intensity/severity/source and uses `sanctuarySuppressed` as an effective overlay rather than rewriting latent Shroud to `CLEAR`;
- explicit story encounter origin: `EncounterContext` never invents `BlockPos.ZERO`; the caller must supply the encounter location and the record snapshots it immutably;
- upstream provenance and current-pack inventory;
- deterministic unit-test fixtures and contract tests;
- executable exclusion guard keeping Spore/Infnexus out of core/build configuration;
- architecture boundary guard preventing optional or Enshrouded implementation imports in core APIs and client-only Minecraft references in common/server code;
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
- CI verifies shell harness prerequisites, wrapper provenance/distribution checksum, unit tests, diff sanity, NeoForge build, production JAR contents, GameTests and the two-boot reload scenario;
- persistence policy is schema-at-first-write: Shroud, Exposure, Entity Corruption, Flame and Story formats must be version-aware in the originating implementation branch; Stage 09 owns migration/recovery fixtures rather than retrofitting schema versions later.

## Foundation progression-boundary TDD evidence

`DECISIONS.md` decision 31 makes `ProgressionOwnerResolver.resolve(UUID)` and `FlamePassageQuery.passageLevel(ProgressionOwner)` Foundation-owned. The exact committed RED source at `c714af1d...` failed only with the expected missing-interface `ClassNotFoundException`s under Java 21 before production implementation. After the minimal contracts were committed, the same tests passed and permanent `ProgressionBoundaryTest` now covers standalone behavior plus fail-fast null handling.

`ENSH-L1-FLAME-PASSAGE-001` remains open only for cross-stage closure: Stage 03 consumption, Stage 05 persistence-backed implementation and optional Stage 08 FTB Teams substitution.

## Foundation Flame-ward TDD evidence

`DECISIONS.md` decision 33 makes `FlameWardQuery.suppresses(ServerLevel, BlockPos)` Foundation-owned. RED commit `93127ff748029cd39fa33502eb704b10817308f0` was executed under Java 21 with same-name Minecraft signature stubs and failed only with `ClassNotFoundException: ...FlameWardQuery`. Minimal production commit `787d9c36c25459e8815066b8bd03fc20ff4285aa` then made the exact test GREEN. Permanent `FlameWardBoundaryTest` plus `PublicApiShapeTest` freeze the no-ward fallback and interface shape.

`ENSH-L1-FLAME-WARD-001` remains open only for cross-stage closure: Stage 01 must preserve logical samples while applying suppression, Stage 02 must route ward veto through `MutationAuthority`, Stage 03 must treat suppression as effective safety, and Stage 05 must provide indexed altar-backed `FlameWardService` through the same interface.

## Foundation encounter-origin TDD evidence

`DECISIONS.md` decision 42 requires every `EncounterContext` to receive an explicit caller-supplied `BlockPos origin`; no default world-origin sentinel is permitted.

RED commit `3794568bbaebf105b070e08055e92715d30c1a3c` added the exact reflection contract and was executed under Java 21 against the then-current `EncounterContext`. It failed only because the convenience `(UUID,int,long)` constructor still existed and silently substituted `BlockPos.ZERO`.

Production commit `5fb833e1b9e912819b0c1438a923dfa8ce7f0981` removed that overload. The canonical record constructor still validates non-null ID/origin, snapshots `origin.immutable()` and rejects manifestation levels below `1`; the same reflection contract then passed GREEN. Commit `cea155842dfdfb371c99c6ccb1ca7ce5d88936fa` adds a permanent public-shape guard against reintroducing an origin-omitting constructor.

A separate planning cycle was removed earlier: Stage 05 owns only the generic Flame ritual registry/executor/checkpoint engine, while Stage 06 owns the authentic first Lich Skull and concrete `enshrouded:lich_manifestation_1` binding. No 05 <-> 06 circular dependency remains.

`DECISIONS.md` decision 39 establishes schema-at-first-write persistence. Shroud already declared `ShroudSchema`; Exposure, Entity Corruption, Flame and Story plans now explicitly declare their own schema/evolution contracts and unknown-future-schema fail-closed behavior. Stage 09 validates migrations/recovery over those pre-versioned formats rather than introducing versioning after save data already exists.

## Current local/structural verification while Actions is unavailable

These checks use current GitHub sources and Java 21 but deliberately do not claim Minecraft runtime acceptance:

- the previously verified pure-Java/value/API and repository/bootstrap guard set is clean; the newest explicit-origin RED -> GREEN was additionally executed in isolation under Java 21, while the newly committed constructor-shape JUnit guard still awaits the real committed Gradle/JUnit runner;
- the current dedicated-server harness passes `bash -n` and a current fake-server simulation exercises `exec timeout`, both FIFO boots, save marker, `world/level.dat`, persisted scoreboard sentinel, second-boot query and graceful stop to `HARNESS_CURRENT_SIMULATION=PASS`;
- current `GameTestBootstrap` + `FoundationGameTests` sources compile under Java 21 against stubs matching the official 1.21.1 signatures they use: `GAMETEST_COMPILE_SHAPE=PASS`;
- NeoGradle NG_7.1 source confirms `modSource(SourceSet)` adds a source set rather than replacing previous mod sources, so `configureEach { modSource main }` plus `gameTestServer { modSource gameTest }` retains main + gameTest inputs;
- current `neoforge.mods.toml` / `pack.mcmeta` expansion with actual `gradle.properties` parses successfully as TOML/JSON with no residual placeholders and produces the intended NeoForge/Minecraft ranges and pack format.

This evidence substantially reduces static/configuration uncertainty but does **not** replace the committed Gradle/JUnit stack, NeoForge runtime, GameTest server or dedicated-server save/reload acceptance.

## Current external verification blocker

- Enshrouded push and pull-request runs repeatedly terminate before checkout with `steps=[]`, `runner_id=0`, `runner_name=""` and no runner group allocation;
- prior exact candidate `3ce06be9e86adb0d90e4738ad6296854bf3d6b93`, workflow `33138923078`, failed before checkout across three controlled attempts; attempt 3 used job `98749505013`;
- candidate `ed0bc5114c050188a87c072fff2e32dfee036e80`, workflow `33140987098`, job `98751592555`, again terminated with `steps=null` before any committed command ran;
- current constructor-shape candidate `cea155842dfdfb371c99c6ccb1ca7ce5d88936fa`, workflow `33141090969`, also concluded failure before executable CI evidence was produced;
- no downloadable job log exists because no step starts;
- an explicit `ubuntu-24.04` runner-label control also failed before steps and was reverted to `ubuntu-latest`;
- removing the workflow concurrency group was separately tested and did not change the failure mode, so that experiment was reverted;
- private `Gustavaopere/Volcanoes`, workflow `33099719939`, job `98615923587`, showed the same pre-runner failure mode during the same period;
- the conversation-local environment still cannot resolve `services.gradle.org` and has no usable Gradle/NeoForge cache;
- because no available real executor can start the build, these failures are not evidence of a failing Gradle build, unit test, GameTest or server reload test.

The persistent `runner_id=0` behavior has no confirmed cause from the APIs available here. Account Actions quota/billing/policy state is not exposed through the connected GitHub actions, so no account-specific cause is asserted without evidence.

Static review while Actions was unavailable caught and corrected multiple real defects: NeoGradle GameTest force-exit behavior, legacy NeoForge dependency metadata, noncanonical abbreviated Gradle launchers, missing reload proof, missing `runServer` stdin forwarding, lack of headless server argument, weak process-tree timeout handling, missing Gradle distribution checksum, missing repository license/artifact notices, weak JAR metadata validation, unenforced architecture boundaries, progression/ritual ownership cycles, pre-Stage-05 Flame-ward dependency/latent-field ambiguity, late schema-versioning risk and the unsafe implicit `BlockPos.ZERO` encounter origin. All corrections still require executable final-HEAD verification.

## Immediate next step

Run the final Foundation HEAD through the actual committed pipeline as soon as GitHub can initialize a runner: shell harness prerequisites, wrapper integrity, `./gradlew test`, diff sanity, NeoForge build/JAR sanity, GameTests and the two-boot scoreboard save/reload harness. If any code-level failure appears, fix that failure first. Only after all Foundation gates pass may the four Foundation tasks be renamed with `✅-`, this status become complete, PR #2 be marked ready and merged, and `feat/01-shroud-state` be created from the resulting latest `main`.

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
