# Enshrouded Plan — Epic Fight, Claims, Teams and MineColonies

**Milestone:** Level 1 required.

**Status:** verified, merged and independently reverified on `main`.

**Goal:** integrate combat presentation/protection and optional shared progression with current multiplayer infrastructure.

**Implemented types:** `EpicFightAdapter`, `FtbTeamsOwnerResolver`, `FtbTeamsCompatibilityProbe`, `FtbChunksProtectionAdapter`, `FtbChunksCompatibilityProbe`, `MineColoniesProtectionAdapter`, `MineColoniesCompatibilityProbe`, `CompositeProtectedAreaService`, `ProtectionRuntimeBindings`, `CombatClaimsTeamsIntegrationRuntime`.

`FtbTeamsOwnerResolver` implements the Foundation `ProgressionOwnerResolver` contract; it does not introduce a second owner abstraction.

## Files

- Integration packages live under `src/main/java/com/gustavaopere/enshrouded/integration/{epicfight,ftbteams,ftbchunks,minecolonies}/*`.
- Canonical protection composition remains under `src/main/java/com/gustavaopere/enshrouded/protection/*`.

## Dependencies

- Foundation `ProgressionOwner` / `ProgressionOwnerResolver` contracts.
- 02 terrain safety and its `ProtectionDecision` / `ProtectedAreaService` semantics.
- 05 persistent progression state/service.
- 06 boss encounter.

## Implemented provider contracts

### FTB Teams

- Uses the current 1.21.1 public API through cached reflection handles: `FTBTeamsAPI.api()`, `isManagerLoaded()`, `getManager()`, `TeamManager.getTeamForPlayerID(UUID)`, `Team.isPlayerTeam()` and `Team.getId()`.
- `integrations.ftbTeamsSharedProgression` defaults to `false`.
- Personal teams preserve PLAYER ownership; non-personal shared/party teams resolve to stable TEAM ownership.
- Resolver output is a snapshot for the operation that requested it. Membership changes affect only future resolutions and never redirect an in-flight ritual, encounter or reward.
- Enabling team ownership does not rewrite, merge or migrate existing player-owned progression.
- Loaded-but-incompatible FTB Teams fails closed when team sharing is enabled; an absent provider preserves standalone player ownership.

### FTB Chunks

- Uses the indexed current API path `FTBChunksAPI.api()` -> manager -> `ClaimedChunkManager.getChunk(ChunkDimPos)` with `ChunkDimPos(Level, BlockPos)`.
- Definite claim -> `PROTECTED`; definite no claim -> `UNPROTECTED`.
- Loaded API mismatch/query failure -> `INDETERMINATE`; absent mod registers no adapter.
- No `getAllClaimedChunks` or other global claim scan is used.

### MineColonies

- Uses `IColonyManager.getInstance().getColonyByPosFromWorld(Level, BlockPos)`.
- Colony present -> `PROTECTED`; no colony -> `UNPROTECTED`.
- Loaded API mismatch/query failure -> `INDETERMINATE`; absent mod registers no adapter.
- No colony-list/global world scan is used.

### Epic Fight

- Detects the current mod ID `epicfight` as an optional compatibility provider.
- The adapter owns no damage pipeline. No second `LivingDamageEvent` mutation path and no `setNewDamage` hook were added.
- Native/Stage-04 combat authority remains unchanged.

## Authority and fail-closed guarantees

- FTB Chunks and MineColonies plug into the canonical Stage 02 `ProtectedAreaService` through `ProtectionRuntimeBindings` and `CompositeProtectedAreaService`.
- `PROTECTED` dominates composition; otherwise any uncertainty yields `INDETERMINATE`; only all-definite-unprotected results yield `UNPROTECTED`.
- Missing providers contribute no uncertainty.
- Diagnostics for provider mismatch/query failure are bounded.
- Final review found two existing config-created authority call sites (`ShroudPurificationRuntime` and `ShroudCoreFeature`) that explicitly supplied `ProtectedAreaService.none()`. A regression test proved this could bypass installed runtime providers. `DefaultMutationAuthority` now centrally composes every explicit service with `ProtectionRuntimeBindings.protectedAreas()`, so worldgen, purification and future config-created authorities cannot bypass claims/colonies.
- Optional provider classes/methods are detected once and cached; hot paths do not repeatedly probe classes/registries.

## TDD / verification

- [x] Claimed position returns `PROTECTED` and definitely unclaimed position returns `UNPROTECTED` through the FTB Chunks adapter contract.
- [x] MineColonies protected/unprotected position semantics are covered.
- [x] Loaded adapter API/query failure returns `INDETERMINATE`; canonical mutation policy remains fail closed by default.
- [x] Target mod absent registers no protection adapter and preserves ordinary standalone-safe terrain.
- [x] Owner resolver tests player vs shared-team key stability and future-operation-only membership changes.
- [x] FTB Teams resolver remains substitutable for the Foundation resolver boundary without changing passage/exposure consumers.
- [x] Existing Stage 05/06 transactional evidence plus Stage 08 resolver tests prove in-flight ritual/encounter/reward ownership remains bound to the original immutable owner.
- [x] Existing player-owned progression is never silently transferred/merged when team mode becomes available.
- [x] Epic Fight compatibility introduces no second damage hook/authority.
- [x] Regression coverage proves explicit config protection services cannot bypass installed runtime claim/colony providers.

## TDD provenance

- RED 1: `438cbbb37549c9fe1ca2b4ef11311aae01fd66fa`, workflow/job `33570064559` / `100061866347`.
- RED 2: `e9d270a3ede4e01c23cb057f4ccad069212cb31c`, workflow/job `33571053152` / `100064875675`.
- Provider RED: corrected contract head `a6e93768078afc5d41badc932880da3046e1dfce`, provider contract commit `2fb773c65be59b5b9a796b5d43153dab05394417`, workflow/job `33572510900` / `100069311087`.
- Provider GREEN checkpoint: `cad48de528ea5965ae813dc55f20d3b68af32f68`; push workflow/job `33572991500` / `100070765342`; PR-head workflow/job `33572995664` / `100070788707`.
- Final authority-bypass RED: `469d6d625c53334107cd5d48ecd8df33df4b43e3`, PR workflow/job `33576732210` / `100082231972` — production compiled and test compilation failed only for the deliberately absent composition helper.
- Final implementation HEAD: `054eed2ab50f0ac755d98748d948510d977d6ad9`.
- Final push verification: workflow `33577170535` — `completed/success`.
- Final exact PR-head verification: workflow/job `33577174521` / `100083590564` — `completed/success`.
- PR #64 merged as `7e119ee893801edf3e3ed3856030da78c97edfd1`.
- Independent post-merge `main` verification: workflow/job `33580200835` / `100092676457` — `completed/success` across unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, SavedData two-boot and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden.
- [x] `ENSH-L1-FLAME-PASSAGE-001`, `ENSH-L1-OWNER-SNAPSHOT-001` and `ENSH-L1-CLAIM-SAFETY-001` have executable evidence on both sides and are closed after the implementation merge.
- [x] `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` remains intentionally open and unchanged.
- [x] After merge, this task is renamed with `✅-` and recorded in `plans/STATUS.md`.

**Acceptance:** satisfied. Multiplayer claims/colonies are protected through the canonical fail-closed authority, optional team progression uses the Foundation owner boundary without silent migration or in-flight re-resolution, and Epic Fight adds no competing combat/damage authority.