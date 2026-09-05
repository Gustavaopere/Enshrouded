# Level 1 — Test Matrix

This document is the Stage 09.01 evidence map for the complete Enshrouded Level-1 vertical slice on NeoForge 1.21.1 / Java 21.

It deliberately distinguishes **real co-load evidence** from **provider-contract evidence**. A proxy, registry stub or synthetic seam is never reported as proof that an installed third-party JAR actually co-loaded.

## Evidence classes

- **Standalone runtime** — Enshrouded + mandatory NeoForge dependency set, executing production runtime and canonical GameTests.
- **Real co-load** — exact current-pack third-party JARs are loaded by NeoForge in a dedicated GameTest invocation.
- **Contract/API** — executable tests cover the adapter/boundary without claiming that the third-party JAR booted.
- **Manual pack smoke** — reproducible local command is required because the complete user modpack is not legally or operationally reproduced by this repository CI.

## Canonical Level-1 loop

`src/gameTest/java/com/gustavaopere/enshrouded/gametest/LevelOneScenarioGameTests.java`

The `completeStandaloneLevelOneVerticalSlice` scenario composes existing production boundaries rather than introducing a second gameplay pipeline:

1. place a physical Shroud Core;
2. request canonical automatic activation;
3. observe ACTIVE state and initial logical expansion;
4. select an actually expanded ordinary `SHROUD` band and sample it through `DefaultShroudQuery`;
5. drain the canonical Exposure reserve;
6. corrupt a real mob through the production corruption tick seam without replacing identity;
7. escape to canonical `CLEAR` and recover Exposure;
8. destroy the physical core and observe the logical `DESTROYED` transition;
9. run the canonical bounded regression algorithm and prove intensity decreases;
10. start the first manifestation through `ManifestationRuntime`;
11. route a NeoForge `LivingDeathEvent` and obtain exactly one authentic Level-1 Lich Skull;
12. offer the skull through the real Flame ritual registry/executor;
13. assert the ritual ID is persisted and `nextLevelReady=true`;
14. assert Flame Level and Passage Level remain `1` — Level-1 completion never silently grants Level 2.

The scenario is additive: focused tests remain authoritative for individual safety invariants and failure modes.

## Standalone matrix

| Contract | Executable evidence |
|---|---|
| Core discovery/seed/activation | `ShroudCoreSeedingGameTests`, `ShroudCorePhysicalLifecycleRedGameTests`, `LevelOneScenarioGameTests` |
| Bounded expansion/canonical query | existing Stage 01 scheduler/query unit + GameTests; `LevelOneScenarioGameTests` vertical composition |
| Ordinary Exposure drain/recovery | `ExposureGameTests`, `LevelOneScenarioGameTests` |
| DEADLY/Red Shroud remains Flame-gated | `DeadlyShroudGameTests` |
| Red Sludge remains lethal/hazard-owned | `RedSludgeGameTests` |
| Corrupted mob identity/state | `EntityCorruptionGameTests`, `EntityCorruptionCombatRuntimeGameTests`, `LevelOneScenarioGameTests` |
| Core destruction | `ShroudCorePhysicalLifecycleRedGameTests`, `LevelOneScenarioGameTests` |
| Logical regression/purification | `PurificationGameTests`, `PurificationReloadGameTests`, `LevelOneScenarioGameTests` |
| Manifestation identity/defeat | Stage 06 manifestation GameTests + `LevelOneScenarioGameTests` |
| Exactly-once Lich Skull | `LichSkullRewardGameTests`; Ars Zero real profile repeats the same invariant on the external actor |
| Skull -> altar -> Level-1 completion | `LichSkullRewardGameTests`, `FlameAltarGameTests`, `LevelOneScenarioGameTests` |
| Production JAR excludes GameTests | CI `Verify built JAR` gate |
| Dedicated server boots/saves/reloads | `scripts/ci/dedicated-server-reload-smoke.sh` |

## Real restart matrix

The two-boot harness reuses one actual GameTest server world; it does not emulate reload by rebuilding an object graph in memory.

Command:

```bash
bash scripts/ci/shroud-saveddata-reload-gametest.sh
```

| Restart boundary | Evidence |
|---|---|
| Mid-expansion | `LevelOneScenarioGameTests.expansionFrontierRebuildsFromPersistedStateAfterRealRestart`; a fresh scheduler must reconstruct its frontier from persisted cells and resume growth |
| Mid-exposure | `LevelOneScenarioGameTests.exposureAttachmentSurvivesRealPlayerDataRestart`; the first boot writes vanilla-format playerdata containing the NeoForge attachment, the second boot loads it through `PlayerList.load`, and the next SHROUD interval must continue from the exact persisted reserve |
| Mid-purification | `PurificationReloadGameTests.purificationStateSurvivesRealServerRestart` |
| Flame progression | existing Flame progression two-boot sentinel |
| Story state | existing Story State two-boot sentinel |
| Encounter/reward exactly-once | existing `ENSHROUDED_LICH_REWARD_RELOADED_NO_REPLAY` sentinel |
| Shroud SavedData/growth | existing Shroud SavedData + physical growth two-boot sentinels |
| Entity corruption attachment | existing entity-corruption two-boot sentinel |

The CI harness requires both the `*_CREATED` marker on boot 1 and the corresponding `*_RELOADED` marker on boot 2 for the new expansion/exposure boundaries, and rejects recreation markers on boot 2. It also verifies that the exposure fixture resolves to exactly one stable playerdata file across both boots. A test that merely serializes/deserializes an object in one process is useful unit/GameTest coverage but does **not** satisfy this table by itself.

## Installed-mod compatibility profiles

### Ars Zero — REAL CO-LOAD, exact current pack versions

The dedicated CI profile deliberately reuses NeoGradle's registered canonical `gameTestServer` run type instead of inventing a parallel run type. Immediately before the profile it deletes the standalone run directory, resolves the exact external JARs with `prepareArsZeroCompatMods`, places them only in `runs/gameTestServer/mods/`, and then invokes `runGameTestServer`.

Exact NeoForge 1.21.1 files:

- Ars Zero `2.0.2` — CurseForge project/file `1377482:8703997`;
- Ars Nouveau `5.13.1` — `401955:8721482`;
- Ars Elemental `0.7.10.1` — `561470:8399862` (its published embedded library remains provider-owned).

They are not `implementation`, `runtimeClasspath` or packaged dependencies of Enshrouded.

`ArsZeroProviderGameTests.realDistributionUsesArsZeroLichAndKeepsRewardExactlyOnce` fails unless the real distribution:

- loads as `ars_zero`;
- registers exact monster entity `ars_zero:lich`;
- can construct/spawn that entity server-side;
- leaves a naturally spawned Lich outside Enshrouded encounter/reward authority;
- is preferred for an Enshrouded manifestation and receives the stored encounter identity;
- emits exactly one authentic Enshrouded Lich Skull on the marked defeat;
- rejects a replayed death callback from duplicating the reward.

The standalone run is allowed to print `ENSHROUDED_ARS_ZERO_REAL_FIXTURE_SKIPPED_MOD_ABSENT`. The dedicated real-distribution profile treats that same marker as a hard failure and requires `ENSHROUDED_ARS_ZERO_REAL_FIXTURE_PASSED`, so a green real-co-load gate cannot be obtained by accidentally executing only the proxy path.

### Ars Nouveau / Iron's Spells — CONTRACT/API AUTOMATION

`src/test/java/com/gustavaopere/enshrouded/integration/magic/MagicSystemAdaptersTest.java` and the canonical magic-classification tests prove the narrow adapter contract: provider evidence/classification feeds the single Enshrouded `MagicResistanceService` reducer and does not create a second damage pipeline.

Stage 09.01 does not claim those tests boot every installed Ars/Iron addon. Full-pack co-load remains a manual pack smoke because the installed magic ecosystem contains a large optional addon graph unrelated to Enshrouded's narrow classification boundary.

### FTB Teams / FTB Chunks / MineColonies — CONTRACT/API AUTOMATION

`CombatClaimsTeamsContractTest`, `CombatClaimsTeamsProviderRedTest` and `CombatClaimsTeamsBootstrapRedTest` cover:

- independent player ownership by default;
- stable optional TEAM ownership for future resolutions only;
- no in-flight owner migration;
- indexed FTB Chunks / MineColonies protection queries;
- `INDETERMINATE` fail-closed behavior on provider/API failure;
- composition through the single canonical mutation-authority boundary.

The full installed FTB/MineColonies distributions are not copied into repository CI. The evidence class is contract/API, not real co-load.

### JourneyMap — API/SMOKE AUTOMATION

JourneyMap API `2.0.0-1.21.1` is an explicit compile/test-only dependency. `JourneyMapIntegrationContractTest` and `JourneyMapAdapterSmokeTest` cover the adapter boundary while preserving client-only authority. Enshrouded never makes JourneyMap a required runtime dependency.

### Epic Fight — CANONICAL COMBAT BOUNDARY + MANUAL PACK CO-LOAD

Enshrouded does not own an Epic-Fight-specific gameplay pipeline. Damage classification/reduction enters through the canonical NeoForge combat hook and remains provider-neutral. Stage 09.01 therefore does not invent a direct Epic Fight adapter merely to manufacture a profile.

A representative full-pack smoke is still required before public release to catch event-order/mod-interaction regressions with the installed Epic Fight stack. That smoke is classified as manual pack evidence until a minimal dependency-complete Epic Fight CI fixture is justified by a real Enshrouded-specific boundary.

## Manual current-pack smoke

With the user's current NeoForge 1.21.1 pack available locally, copy the built Enshrouded JAR into the pack's `mods/` directory and boot a dedicated server using the same Java 21 runtime/config used by the pack. Exercise at minimum:

1. create/activate one Level-1 core;
2. enter/leave ordinary Shroud;
3. confirm corrupted mob behavior under the installed combat stack;
4. destroy/purify the core;
5. run the first manifestation and altar completion;
6. restart once during an active Level-1 state;
7. verify no duplicate skull, ritual, Story or Flame progression after restart.

This manual profile is additional evidence. It never substitutes for the deterministic standalone and real Ars Zero CI gates above.

## CI closure requirements

Stage 09.01 is not closed unless one exact PR head has all of the following green:

- `./gradlew --no-daemon test`;
- frontier benchmark baseline;
- `./gradlew --no-daemon build`;
- explicit `compileGameTestJava` plus presence of `LevelOneScenarioGameTests.class` in the canonical source set;
- standalone GameTest server with non-zero discovery and `ENSHROUDED_LEVEL_ONE_SCENARIO_PASSED`;
- two-boot restart harness including expansion, exposure, purification and reward boundaries;
- real Ars Zero 2.0.2 co-load GameTest profile;
- dedicated-server save/reload smoke;
- production JAR integrity/no GameTest leakage;
- `git diff --check`.

Final workflow/run/job IDs and exact head SHA are recorded in the task plan/`STATUS.md` only after those gates actually complete.
