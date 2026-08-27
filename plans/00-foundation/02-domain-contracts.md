# Enshrouded Plan — Domain Contracts

**Milestone:** Level 1 required.

**Goal:** define the interfaces and stable value types every subsystem will share before world behavior is implemented.

**Planned types:** `ShroudSeverity`, `ShroudSample`, `ShroudQuery`, `MutationAuthority`, `ProgressionOwner`, `ProgressionOwnerResolver`, `FlamePassageQuery`, `MagicDamageClassifier`, `LichManifestationProvider`, `EncounterContext`.

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
- `ProgressionOwnerResolver.resolve(ServerPlayer)` maps a player to an Enshrouded-owned `ProgressionOwner`; the standalone implementation resolves to the player's UUID owner and Stage 08 may substitute a team-aware resolver.
- `FlamePassageQuery.passageLevel(ProgressionOwner)` is a read-only Enshrouded-owned boundary. Foundation supplies a standalone Level 1 fallback returning passage level `1`; Stage 05 supplies the canonical persistence-backed implementation.
- Stage 03 consumes only `ProgressionOwnerResolver` + `FlamePassageQuery`; it must not depend on Stage 05 implementation classes or scan altar blocks.
- `MagicDamageClassifier.classify(DamageSource)` returns a core-owned classification/confidence contract.
- `LichManifestationProvider` spawns/matches encounter entities without owning story rewards.

## TDD / verification

- [ ] Write codec/ID round-trip unit tests for severity and owner keys.
- [ ] Write contract tests proving query/mutation/combat interfaces are side-effect free at the value layer.
- [ ] Freeze public API shapes for the cross-stage interfaces.
- [ ] Add RED tests for `ProgressionOwnerResolver` and `FlamePassageQuery`, including standalone player ownership and Level 1 fallback behavior, before production implementation.
- [ ] Verify RED before implementations exist, then GREEN with the minimal records/interfaces.

## Current implementation checkpoint — 2026-08-27

Existing Enshrouded-owned contracts are present under `src/main/java/com/gustavaopere/enshrouded/api/` with no optional-mod imports:

- Shroud severity uses stable string IDs and `ShroudSample` validates finite normalized intensity, including rejection of NaN/infinite/out-of-range values;
- `ShroudQuery` and `MutationAuthority` expose read/safety boundaries without implementing world mutation;
- `ProgressionOwner` serializes stable player/team/world keys without importing FTB Teams, including namespaced ids containing `:` and invalid-key rejection;
- magic classification is represented by Enshrouded-owned kind/confidence values, with `UNKNOWN` explicitly failing safe as non-magical;
- `LichManifestationProvider` owns entity selection/spawn/matching only, while its contract explicitly leaves rewards/progression to the story runtime;
- JUnit contract tests cover stable IDs, player/team/world owner round trips, invalid owner keys and value invariants;
- `PublicApiShapeTest` freezes the exact public shapes of `ShroudQuery`, `MutationAuthority`, `MagicDamageClassifier` and `LichManifestationProvider`;
- `ArchitectureBoundaryTest` rejects optional/foreign implementation imports from `api/` while allowing only Java/Mojang/Minecraft/NeoForge/annotations/Enshrouded platform namespaces;
- the same architecture guard rejects `net.minecraft.client.*` references from common/server production code outside the future dedicated `client/` package.

### Newly clarified Foundation contract still pending implementation

`DECISIONS.md` decision 31 moved `ProgressionOwnerResolver` and `FlamePassageQuery` ownership into Foundation to remove the former Stage 03 -> Stage 05 stub dependency. Their production types are **not implemented yet** because the project requires test-first development and the current GitHub Actions outage prevents observing the required RED. `plans/PENDING.md` tracks this as `ENSH-L1-FLAME-PASSAGE-001`.

Do not accept this task until those two contracts and their standalone Level 1 defaults are implemented test-first and the final-HEAD suite executes GREEN.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] `ProgressionOwnerResolver` and `FlamePassageQuery` Foundation contracts/defaults are implemented and GREEN.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Later tasks compile against one stable set of Enshrouded-owned interfaces, including progression owner/passage queries, without importing optional-mod or later-stage implementation classes.
