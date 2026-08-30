# Enshrouded Plan — Flame Sanctuary

**Milestone:** Level 1 required.

**Status:** ✅ Verified and merged.

**Goal:** make active altars project a bounded ward that suppresses Shroud exposure and threat-introducing terrain mutation without trapping safe cleanup.

**Implemented types:** `FlameWardService`, `FlameWardIndex`, `FlameWardState`, plus the Foundation-owned `FlameWardRuntimeBindings` handle and shared mutation/query wiring.

## Files

- [x] Created `src/main/java/com/gustavaopere/enshrouded/flame/ward/*`.
- [x] Bound `FlameWardService` into the existing Foundation `FlameWardQuery` boundary consumed by Shroud query and mutation authority.
- [x] Wired Flame Altar placement/load/removal lifecycle into the ward provider without creating a second logical Shroud state.

## Dependencies

- [x] 02 Flame Altar merged first.
- [x] Foundation `FlameWardQuery` contract reused unchanged.
- [x] 02 Terrain Safety shared `MutationAuthority` reused.
- [x] 03 Exposure consumes the existing `ShroudSample.sanctuarySuppressed` overlay.

## Implementation contract

- [x] Ward radius is SERVER-configurable (`16` default, bounded `1..128`) and indexed; exposure queries do not scan every loaded altar.
- [x] `FlameWardService.suppresses(ServerLevel, BlockPos)` is installed through the Foundation runtime handle rather than a parallel authority.
- [x] Inside an active ward, `ShroudSample` preserves canonical logical severity/intensity/source and changes only `sanctuarySuppressed`.
- [x] Exposure interprets Sanctuary suppression as effective safety/recovery while retaining latent logical Shroud.
- [x] Shared `MutationAuthority` denies `CORRUPTION` and `CORE_PLACEMENT` inside the ward.
- [x] `PURIFICATION` may proceed inside the ward when all other claim/container/player-edit protection checks allow it.
- [x] `RITUAL_STRUCTURE` is not denied solely because Sanctuary is active and remains subject to normal authorization.
- [x] Destroying/deactivating the altar reveals unchanged latent logical Shroud when the field has not independently regressed.
- [x] Only loaded/known indexed altar state is queried; the implementation performs no world scan and forces no chunk load.
- [x] Flame Altar placement activates Sanctuary in the same server tick; block-entity `onLoad()` reconstructs the provider after chunk/restart lifecycle and removal deactivates it.

## TDD / verification

- [x] Unit tests cover indexed radius query and overlap semantics through the Foundation `FlameWardQuery` interface.
- [x] Contract coverage proves swapping the Foundation fallback for `FlameWardService` changes only suppression and preserves logical intensity/severity/source.
- [x] GameTest proves logical Shroud inside Sanctuary recovers exposure reserve.
- [x] GameTest proves new corruption/core placement is vetoed inside the ward and proceeds just outside when otherwise authorized.
- [x] GameTest proves safe purification may proceed inside Sanctuary while protected/player-owned targets remain vetoed.
- [x] GameTest proves altar removal reveals the same latent Shroud without field regeneration.
- [x] Sanctuary-producing GameTests use isolated batches so spatially global ward state from one fixture cannot contaminate another.
- [x] Logical-Shroud fixtures temporarily install one deterministic state and restore prior `ShroudSavedData` in `finally`, preventing source-selection contamination from unrelated GameTests.

## Merge evidence

- Implementation branch: `feat/05-sanctuary`.
- Initial structural RED: commit `cae28726e875e7165aeec95d16ec0553f1d52675`; workflow #969 failed before the Sanctuary provider/binding existed.
- Workflow #991 exposed the registry-time SERVER-config access defect; runtime construction was made lazy.
- Workflow #1003 exposed missing same-tick altar activation plus mock-player networking noise; production gained immediate server-side activation and the exposure GameTest was isolated at the reducer boundary.
- Workflow #1008 exposed cross-GameTest ward contamination in the shared default batch.
- Workflow #1014 proved batch isolation worked but exposed a second shared `ShroudSavedData` fixture collision; logical-state fixtures were then made deterministic and restored in `finally`.
- Final implementation HEAD: `89c6346b4ed74af131b434662940ad9e0e171858`.
- Final PR: #38 — `05.03 — Flame Sanctuary` (replacement for draft #37 only because the connector could not transition that draft to ready-for-review).
- Final exact-head verification: workflow `33303582593` / run #1016 — GREEN.
- Final gates: wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 58/58 GameTests, SavedData two-boot reload GameTest and dedicated-server save/reload smoke — GREEN.
- Merge SHA: `35fd0f47239646b2df84cda2989126ad432e7fd4`.

## Cross-stage contracts

`ENSH-L1-FLAME-WARD-001` is closed by executable evidence across Foundation, Stage 02, Stage 03 and this Stage 05 provider. Sanctuary now flows through the single Foundation ward boundary for both exposure and mutation. No new task-local cross-stage blocker was introduced.

**Acceptance:** ✅ Flame Altars implement the pre-existing Foundation ward boundary, protect players/world surfaces from new Shroud threat through shared query/authority paths, permit safe cleanup and preserve the latent logical threat underneath until that field independently retreats.