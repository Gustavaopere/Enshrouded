# Enshrouded Plan — Boss Provider Abstraction

**Milestone:** Level 1 required.

**Goal:** create provider selection and a native fallback manifestation so the story remains standalone.

**Planned types:** `LichManifestationProviderRegistry`, `NativeLichManifestationProvider`, `NativeShroudLichEntity`, `ManifestationDirector`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/boss/*`.
- Register native `enshrouded:shroud_lich` entity and minimal assets/renderer.
- Create provider capability/selection tests.

## Dependencies

- 01 Story state.
- 04 Magic resistance contract.

## Implementation contract

- Provider selection is deterministic by configured priority and availability.
- Native fallback always exists and supplies a beatable Level 1 boss with ranged necromantic/Shroud attacks, mobility and at least one phase change; it must not require Ars classes.
- External provider receives an `EncounterContext` and the director tags/attaches the spawned entity with encounter ID.
- `LichManifestationProvider.spawn(...)` remains strongly typed as `Optional<LivingEntity>`; the core director validates encounter usability/state rather than weakening the provider boundary to accept arbitrary `Entity` values.
- All providers feed one Enshrouded `ServerBossEvent`/encounter director so health/reward/story hooks are consistent.
- If an external provider becomes unavailable between save and encounter start, selection falls back rather than corrupting story state.

## TDD / verification

- [x] Unit-test provider priority/fallback.
- [x] GameTest with no optional mods spawns native manifestation and can complete a controlled defeat.
- [x] GameTest provider returning `Optional.empty()` is recoverable and falls back according to selection policy.
- [x] GameTest a provider-supplied living entity that is already dead/removed or otherwise unusable for the requested encounter is rejected without corrupting story state.
- [x] Dedicated-server smoke verifies native entity registration and renderer separation.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; GameTests and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; existing Stage 06/08 owner-snapshot and Lich-reward contracts remain explicitly open in `plans/PENDING.md`.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same closeout checkpoint.

## Verified implementation record

- Implementation branch: `feat/06-boss-provider`.
- Provider-neutral bossbar structural RED: commit `516af955dfdce9f58f2219aaf8c450969b4ad427`, workflow `33320592151` / run #1070 — `compileGameTestJava` failed only because `bossEvent()` / `syncBossEvent()` did not yet exist.
- Bossbar GREEN checkpoint: HEAD `cef36cd69fe8dbe85b0df7b4150a523ed76fdcc6`, workflow `33320744336` / run #1071 — all gates GREEN.
- Final implementation HEAD: `8fe1d56083e76135d272e1e8dd3763db321fccf6`.
- Final push verification: workflow `33326923926` / run #1074 — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.
- PR: #43 — `06.02 — Boss Provider Abstraction`.
- Final exact PR-head verification: workflow `33327173318` / run #1075 — GREEN across the same complete gate on exact HEAD `8fe1d56083e76135d272e1e8dd3763db321fccf6`.
- Merge SHA: `b45f164c6f3c29cf380e89e64ba19df3e2386c35`.

The native standalone actor is registered as `enshrouded:shroud_lich`, is excluded from Stage 04 ecology corruption, is persistent and physically beatable, has a real inherited ranged-AI path with Enshrouded-owned Shroud damage plus Darkness, and transitions to a faster second phase below half health. Client rendering is isolated through the physical-client renderer event and reuses the vanilla skeleton renderer; EN and pt-BR entity names are provided.

`ManifestationDirector` remains provider-neutral: it validates usable `LivingEntity` actors, binds the stable encounter UUID and owns one `ServerBossEvent`, but it does not mutate Story State, create encounter ownership, complete narrative defeat or issue rewards. Those responsibilities remain explicitly assigned to 06.03. `ENSH-L1-OWNER-SNAPSHOT-001` therefore remains open for 06.03 + Stage 08, and `ENSH-L1-LICH-REWARD-001` remains open for the first-manifestation reward path / 06.04 skull binding.

**Acceptance:** Level 1 always has a functional Lich manifestation even in a standalone Enshrouded installation, and negative provider tests exercise states the strongly typed provider contract can actually produce.
