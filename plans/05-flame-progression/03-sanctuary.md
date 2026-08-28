# Enshrouded Plan — Flame Sanctuary

**Milestone:** Level 1 required.

**Goal:** make active altars project a bounded ward that suppresses Shroud exposure and threat-introducing terrain mutation without trapping safe cleanup.

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
- `MutationAuthority` interprets the ward result by `MutationKind`: new `CORRUPTION` and `CORE_PLACEMENT` are denied inside the ward, while `PURIFICATION` may proceed when all other claim/container/player-edit safety checks pass. `RITUAL_STRUCTURE` is not rejected solely because the ward is active and remains subject to normal ritual/claim authorization.
- Existing growth cleanup/restoration may therefore be scheduled gradually after logical regression instead of becoming permanently stranded by an altar ward.
- Ward does not delete the underlying logical field, so destroying/deactivating the altar can expose a still-corrupted region again if that field has not independently regressed.
- Only loaded/known indexed altar state is used safely; no forced chunk loads.

## TDD / verification

- [ ] Unit-test indexed radius query and overlap semantics through the Foundation `FlameWardQuery` interface.
- [ ] Contract test swapping Foundation `none()` fallback for `FlameWardService` changes only suppression, not logical sample intensity/severity/source.
- [ ] GameTest player standing in logical Shroud inside ward recovers exposure.
- [ ] GameTest new corruption/core placement is vetoed inside ward and proceeds just outside when otherwise authorized.
- [ ] GameTest a logically cleared warded cell may run safe purification cleanup, while a player-modified/protected target remains untouched.
- [ ] GameTest remove altar and prove underlying Shroud becomes effective again without field regeneration when the logical field has not independently regressed.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Flame Altars implement the pre-existing Foundation ward boundary, protect players/world surfaces from new Shroud threat through shared query/authority paths, permit safe cleanup of independently regressed corruption and leave the latent logical threat intact underneath until that field itself retreats.
