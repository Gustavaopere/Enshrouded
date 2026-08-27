# Enshrouded Plan — Flame Sanctuary

**Milestone:** Level 1 required.

**Goal:** make active altars project a bounded ward that suppresses Shroud exposure and terrain mutation.

**Planned types:** `FlameWardService`, `FlameWardIndex`, `FlameWardState`.

`FlameWardService` implements the Foundation `FlameWardQuery` contract; Stage 05 does not redefine the interface.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/ward/*`.
- Bind `FlameWardService` into the existing `ShroudQuery`/`MutationAuthority` injection points.

## Dependencies

- 02 Flame Altar.
- Foundation `FlameWardQuery` contract.
- 02 Terrain Safety.
- 03 Exposure.

## Implementation contract

- Ward radius is configurable and indexed; exposure queries do not scan every loaded altar.
- `FlameWardService.suppresses(ServerLevel, BlockPos)` becomes the injected Foundation ward query used by existing query/mutation paths.
- Inside an active ward, `ShroudSample` preserves canonical logical severity/intensity/source and sets `sanctuarySuppressed=true`; Stage 05 must not rewrite the latent logical sample to `CLEAR`.
- Exposure interprets the suppression flag as effective safety/recovery.
- Materialization jobs inside the ward are denied through the existing `MutationAuthority`; existing growth cleanup may be scheduled gradually.
- Ward does not delete the underlying logical field, so destroying/deactivating the altar can expose a still-corrupted region again.
- Only loaded/known indexed altar state is used safely; no forced chunk loads.

## TDD / verification

- [ ] Unit-test indexed radius query and overlap semantics through the Foundation `FlameWardQuery` interface.
- [ ] Contract test swapping Foundation `none()` fallback for `FlameWardService` changes only suppression, not logical sample intensity/severity/source.
- [ ] GameTest player standing in logical Shroud inside ward recovers exposure.
- [ ] GameTest terrain mutation is vetoed inside ward and proceeds just outside.
- [ ] GameTest remove altar and prove underlying Shroud becomes effective again without field regeneration.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Flame Altars implement the pre-existing Foundation ward boundary, protect players/world surfaces through shared query/authority paths and leave the logical threat intact underneath.
