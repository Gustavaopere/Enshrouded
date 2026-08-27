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
- `ShroudSample` carries canonical logical intensity/severity, owning core/region ID when present and effective sanctuary suppression. Sanctuary must set `sanctuarySuppressed=true` without overwriting the underlying logical severity/intensity.
- `ShroudQuery.sample(ServerLevel, BlockPos, Entity?)` is read-only and cannot load chunks.
- `MutationAuthority.canMutate(ServerLevel, BlockPos, MutationKind)` is the only terrain safety gate.
- `FlameWardQuery.suppresses(ServerLevel, BlockPos)` is the read-only Foundation boundary for Sanctuary. `none()` returns `false` and is used before Stage 05; Stage 05 supplies the indexed ward implementation. Stage 01/02/03 must not scan altar blocks or import Stage 05 ward classes.
- `ProgressionOwner` has a stable string/UUID key independent of FTB Teams classes.
- `ProgressionOwnerResolver.resolve(UUID playerId)` maps a player UUID to an Enshrouded-owned `ProgressionOwner`; the standalone implementation returns `ProgressionOwner.player(playerId)` and Stage 08 may substitute a team-aware resolver.
- `FlamePassageQuery.passageLevel(ProgressionOwner)` is a read-only Enshrouded-owned boundary. Foundation supplies a standalone Level 1 fallback returning passage level `1`; Stage 05 supplies the canonical persistence-backed implementation.
- Stage 03 consumes only Foundation progression/ward boundaries; it must not depend on Stage 05 implementation classes or scan altar blocks.
- `MagicDamageClassifier.classify(DamageSource)` returns a core-owned classification/confidence contract.
- `LichManifestationProvider` spawns/matches encounter entities without owning story rewards.

## TDD / verification

- [x] Write codec/ID round-trip unit tests for severity and owner keys.
- [x] Write contract tests proving query/mutation/combat interfaces are side-effect free at the value/API layer.
- [x] Freeze public API shapes for the cross-stage interfaces.
- [x] Add RED test source for `ProgressionOwnerResolver` and `FlamePassageQuery`, including standalone UUID ownership and Level 1 fallback behavior, before production implementation — commit `c714af1db5256537ea5e8a9f89c680f2c3e32d6a`.
- [x] Observe that RED before production implementation: the exact committed test source was compiled/executed under Java 21 with a temporary JUnit-compatible harness outside the repository and both tests failed only with the expected `ClassNotFoundException` for the two absent interfaces.
- [x] Implement the minimal owner/passage interfaces/defaults and verify the same test GREEN in the isolated Java 21 harness.
- [x] Add and observe RED for Foundation `FlameWardQuery` before production implementation, then implement the minimal no-ward fallback and permanent API-shape/behavior tests — RED commit `93127ff748029cd39fa33502eb704b10817308f0`, production commit `787d9c36c25459e8815066b8bd03fc20ff4285aa`.

## Current implementation checkpoint — 2026-08-27

Enshrouded-owned contracts are present under `src/main/java/com/gustavaopere/enshrouded/api/` with no optional-mod imports:

- Shroud severity uses stable string IDs and `ShroudSample` validates finite normalized intensity, including rejection of NaN/infinite/out-of-range values;
- `ShroudQuery` and `MutationAuthority` expose read/safety boundaries without implementing world mutation;
- `FlameWardQuery` is a spatial `@FunctionalInterface`; Foundation `none()` is a total no-ward fallback returning `false` without consulting world/position, allowing Stage 01/02/03 to run before Stage 05;
- `ProgressionOwner` serializes stable player/team/world keys without importing FTB Teams, including namespaced ids containing `:` and invalid-key rejection;
- `ProgressionOwnerResolver` is a UUID-based `@FunctionalInterface`; `standalone()` maps directly to `ProgressionOwner.player(UUID)` without runtime/player/FTB types;
- `FlamePassageQuery` is a read-only `@FunctionalInterface`; `levelOneFallback()` validates its owner argument and returns stable Passage Level `1` for player/team/world owners;
- magic classification is represented by Enshrouded-owned kind/confidence values, with `UNKNOWN` explicitly failing safe as non-magical;
- `LichManifestationProvider` owns entity selection/spawn/matching only, while its contract explicitly leaves rewards/progression to the story runtime;
- JUnit contract tests cover stable IDs, player/team/world owner round trips, invalid owner keys and value invariants;
- `PublicApiShapeTest` freezes the exact public shapes of Shroud/mutation/ward/progression/magic/Lich provider boundaries;
- `ProgressionBoundaryTest` permanently verifies standalone UUID owner resolution, owner-agnostic Level 1 passage fallback and fail-fast null behavior;
- `FlameWardBoundaryTest` permanently verifies the Foundation no-ward fallback cannot invent Sanctuary suppression;
- `ArchitectureBoundaryTest` rejects optional/foreign implementation imports from `api/` while allowing only Java/Mojang/Minecraft/NeoForge/annotations/Enshrouded platform namespaces;
- the same architecture guard rejects `net.minecraft.client.*` references from common/server production code outside the future dedicated `client/` package.

### Progression boundary TDD evidence

`DECISIONS.md` decision 31 moved `ProgressionOwnerResolver` and `FlamePassageQuery` ownership into Foundation to remove the former Stage 03 -> Stage 05 stub dependency. `plans/PENDING.md` tracks the remaining cross-stage consumption/persistence closure as `ENSH-L1-FLAME-PASSAGE-001`.

The resolver boundary is intentionally UUID-based rather than `ServerPlayer`-based so it remains unit-testable and runtime-agnostic while still allowing a Stage 08 adapter to map the UUID into FTB Teams state.

RED source commit: `c714af1db5256537ea5e8a9f89c680f2c3e32d6a`.

Observed isolated RED under Java 21:

- `standaloneOwnerResolverMapsUuidToPlayerOwner` -> `ClassNotFoundException: ...ProgressionOwnerResolver`;
- `levelOnePassageFallbackIsOwnerAgnosticAndStable` -> `ClassNotFoundException: ...FlamePassageQuery`.

Minimal production commits:

- `66d4e5c23771792df15b1548a1e45e835bd1b9d1` — `ProgressionOwnerResolver`;
- `d9e5edc8a413a5034475914b671a4d7f13d97be5` — `FlamePassageQuery`.

The same test then passed both methods in the isolated Java 21 harness. The temporary RED-named test was promoted to permanent `ProgressionBoundaryTest` and the RED checkpoint file was removed.

### Flame ward boundary TDD evidence

`DECISIONS.md` decision 33 makes `FlameWardQuery` Foundation-owned because Stage 01 query and Stage 02 mutation occur before Stage 05 Sanctuary. It also freezes the semantic rule that `ShroudSample` keeps canonical logical severity/intensity under a ward and represents effective suppression only through `sanctuarySuppressed`.

RED source commit: `93127ff748029cd39fa33502eb704b10817308f0`.

The exact RED test was compiled/executed under Java 21 with only same-name `ServerLevel`/`BlockPos` signature stubs and failed only with `ClassNotFoundException: ...FlameWardQuery` before production implementation.

Minimal production commit: `787d9c36c25459e8815066b8bd03fc20ff4285aa`.

The same test then passed; it was promoted to permanent `FlameWardBoundaryTest`, the RED-named file was removed, and `PublicApiShapeTest` now freezes `suppresses(ServerLevel, BlockPos)` plus static `none()`.

`plans/PENDING.md` keeps `ENSH-L1-FLAME-WARD-001` open only for cross-stage closure: Stage 01 must preserve logical samples while setting the suppression flag, Stage 02 must route ward veto through `MutationAuthority`, Stage 03 must interpret suppression as effective safety, and Stage 05 must supply the indexed altar-backed implementation.

### Broader pure-Java verification while Actions is blocked

A Java 21 micro-runner executed the current logical bodies of the Foundation tests that do not require Minecraft runtime behavior. Minecraft classes referenced only for API signature reflection were represented by empty same-name stubs; no Minecraft behavior was simulated.

Current equivalent result is clean across `DomainContractsTest`, `ProgressionBoundaryTest`, `FlameWardBoundaryTest`, `PublicApiShapeTest` and `TestFixturesExecutionTest`; repository architecture/provenance guards and bootstrap structural checks have also passed in the local verification path.

This isolated evidence proves pure-Java value/API behavior only. It does **not** replace `./gradlew test`, NeoForge build, GameTests or dedicated-server acceptance on the final branch HEAD.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD under the committed Gradle/JUnit stack.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [x] `ProgressionOwnerResolver` and `FlamePassageQuery` Foundation contracts/defaults are implemented and isolated RED -> GREEN is proven.
- [x] `FlameWardQuery` Foundation contract/default is implemented test-first and isolated RED -> GREEN is proven.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Later tasks compile against one stable set of Enshrouded-owned interfaces, including progression, passage and ward queries, without importing optional-mod or later-stage implementation classes.
