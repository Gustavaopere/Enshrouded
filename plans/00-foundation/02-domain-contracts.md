# Enshrouded Plan — Domain Contracts

**Milestone:** Level 1 required.

**Goal:** define the interfaces and stable value types every subsystem will share before world behavior is implemented.

**Planned types:** `ShroudSeverity`, `ShroudSample`, `ShroudQuery`, `MutationAuthority`, `ProgressionOwner`, `MagicDamageClassifier`, `LichManifestationProvider`, `EncounterContext`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/api/shroud/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/api/progression/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/api/combat/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/api/story/*`.
- Test under `src/test/java/com/gustavaopere/enshrouded/api/*`.

## Dependencies

- 01 build scaffold.

## Implementation contract

- `ShroudSeverity` exposes stable IDs `clear`, `shroud`, `deadly`; persistence must not depend on enum ordinal.
- `ShroudSample` carries intensity, severity, owning core/region ID when present and effective sanctuary suppression.
- `ShroudQuery.sample(ServerLevel, BlockPos, Entity?)` is read-only and cannot load chunks.
- `MutationAuthority.canMutate(ServerLevel, BlockPos, MutationKind)` is the only terrain safety gate.
- `ProgressionOwner` has a stable string/UUID key independent of FTB Teams classes.
- `MagicDamageClassifier.classify(DamageSource)` returns a core-owned classification/confidence contract.
- `LichManifestationProvider` spawns/matches encounter entities without owning story rewards.

## TDD / verification

- [ ] Write codec/ID round-trip unit tests for severity and owner keys.
- [ ] Write contract tests proving query/mutation/combat interfaces are side-effect free at the value layer.
- [ ] Verify RED before implementations exist, then GREEN with the minimal records/interfaces.

## Current implementation checkpoint — 2026-08-27

The planned Enshrouded-owned contracts are present under `src/main/java/com/gustavaopere/enshrouded/api/` with no optional-mod imports:

- Shroud severity uses stable string IDs and `ShroudSample` validates finite normalized intensity;
- `ShroudQuery` and `MutationAuthority` expose read/safety boundaries without implementing world mutation;
- `ProgressionOwner` serializes stable player/team/world keys without importing FTB Teams;
- magic classification is represented by Enshrouded-owned kind/confidence values;
- `LichManifestationProvider` owns entity selection/spawn/matching only, while its contract explicitly leaves rewards/progression to the story runtime;
- JUnit contract tests cover stable IDs, owner round trips and value/interface invariants.

No optional provider class is part of these core contracts. Current final-HEAD GREEN verification remains blocked by GitHub Actions terminating before checkout; therefore this task stays open and unrenamed despite the implementation being present.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Later tasks can compile against one stable set of Enshrouded-owned interfaces without importing optional-mod classes.
