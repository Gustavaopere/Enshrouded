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

- [ ] Unit-test provider priority/fallback.
- [ ] GameTest with no optional mods spawns native manifestation and can complete a controlled defeat.
- [ ] GameTest provider returning `Optional.empty()` is recoverable and falls back according to selection policy.
- [ ] GameTest a provider-supplied living entity that is already dead/removed or otherwise unusable for the requested encounter is rejected without corrupting story state.
- [ ] Dedicated-server smoke verifies native entity registration and renderer separation.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Level 1 always has a functional Lich manifestation even in a standalone Enshrouded installation, and negative provider tests exercise states the strongly typed provider contract can actually produce.
