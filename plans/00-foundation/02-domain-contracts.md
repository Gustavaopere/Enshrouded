# Enshrouded Plan — Domain Contracts

**Milestone:** Level 1 required.

**Goal:** define the interfaces and stable value types every subsystem will share before world behavior is implemented.

**Planned types:** `ShroudSeverity`, `ShroudSample`, `ShroudQuery`, `MutationAuthority`, `FlameWardQuery`, `ProgressionOwner`, `ProgressionOwnerResolver`, `FlamePassageQuery`, `MagicDamageClassifier`, `LichManifestationProvider`, `EncounterContext`.

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
- `ShroudSample` carries canonical logical intensity/severity, owning core/region ID when present and effective sanctuary suppression. Sanctuary sets `sanctuarySuppressed=true` without overwriting logical severity/intensity.
- `ShroudQuery.sample(ServerLevel, BlockPos, Entity?)` is read-only and cannot load chunks.
- `MutationAuthority.canMutate(ServerLevel, BlockPos, MutationKind)` is the only terrain safety gate.
- `FlameWardQuery.suppresses(ServerLevel, BlockPos)` is the read-only Foundation boundary for Sanctuary. `none()` returns `false`; Stage 05 supplies the indexed ward implementation.
- `ProgressionOwner` has a stable string/UUID key independent of FTB Teams classes.
- `ProgressionOwnerResolver.resolve(UUID playerId)` maps UUID to an Enshrouded-owned owner; standalone returns `ProgressionOwner.player(playerId)`.
- `FlamePassageQuery.passageLevel(ProgressionOwner)` is Foundation-owned; standalone fallback returns Level 1.
- `MagicDamageClassifier.classify(DamageSource)` returns a core-owned classification/confidence contract.
- `LichManifestationProvider` spawns/matches encounter entities without owning story rewards.
- `EncounterContext` requires explicit caller-supplied `BlockPos origin`, snapshots it with `immutable()`, and has no origin-omitting convenience constructor.

## TDD / verification

- [x] Stable severity/owner IDs and value invariants tested.
- [x] Public API shapes frozen for cross-stage interfaces.
- [x] `ProgressionOwnerResolver` / `FlamePassageQuery` RED observed before implementation, then GREEN after minimal defaults.
- [x] `FlameWardQuery` RED observed before implementation, then GREEN after minimal no-ward fallback.
- [x] `EncounterContext` RED observed for the unsafe origin-omitting constructor, then GREEN after removing it.
- [x] Committed Gradle/JUnit suite GREEN on implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` in workflow `33165771852`.

## Final implementation checkpoint — 2026-08-28

Foundation-owned contracts are present under `src/main/java/com/gustavaopere/enshrouded/api/` with no optional-mod imports. `PublicApiShapeTest`, `ArchitectureBoundaryTest`, progression/ward boundary tests and domain contract tests protect stable shapes, standalone defaults, explicit encounter origin and architecture boundaries.

Cross-stage contracts intentionally remain in `plans/PENDING.md` until later consumers prove both sides: Flame passage, Flame ward, owner snapshot semantics, core-to-terrain/exposure, Lich reward, magic classification and claim safety. Their existence does not make the Foundation side incomplete.

## Executable acceptance evidence

PR workflow `33165771852`, job `98830694040`, on HEAD `0b1940012628ff0d762961cccb480dc72989455d` completed wrapper verification, unit tests, NeoForge build/JAR checks, GameTests and dedicated-server save/reload GREEN.

The closing documentation-only checkpoint must itself pass the same pipeline before merge; otherwise this task is reopened.

## Merge gate

- [x] All task-specific tests are GREEN on the verified implementation HEAD under committed Gradle/JUnit.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests and dedicated-server smoke are GREEN.
- [x] Foundation progression/passage/ward/default-origin contracts are implemented and test-first evidence is preserved.
- [x] Genuine cross-stage contracts remain explicitly tracked in `plans/PENDING.md`.
- [x] Task is ready to be renamed with `✅-` in the Foundation closing checkpoint merged to `main`.

**Acceptance:** Later tasks compile against one stable set of Enshrouded-owned interfaces without importing optional-mod or later-stage implementation classes or silently inventing encounter locations.
