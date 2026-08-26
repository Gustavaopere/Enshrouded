# Enshrouded Plan — Flame Sanctuary

**Milestone:** Level 1 required.

**Goal:** make active altars project a bounded ward that suppresses Shroud exposure and terrain mutation.

**Planned types:** `FlameWardService`, `FlameWardIndex`, `FlameWardState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/ward/*`.
- Integrate with `ShroudQuery` effective sample and `MutationAuthority`.

## Dependencies

- 02 Flame Altar.
- 02 Terrain Safety.
- 03 Exposure.

## Implementation contract

- Ward radius is configurable and indexed; exposure queries do not scan every loaded altar.
- Inside an active ward, effective Shroud severity for player exposure is `CLEAR` or explicitly suppressed according to one canonical rule.
- Materialization jobs inside the ward are denied; existing growth cleanup may be scheduled gradually.
- Ward does not delete the underlying logical field, so destroying/deactivating the altar can expose a still-corrupted region again.
- Only loaded/known altar state is used safely; no forced chunk loads.

## TDD / verification

- [ ] Unit-test indexed radius query and overlap semantics.
- [ ] GameTest player standing in logical Shroud inside ward recovers exposure.
- [ ] GameTest terrain mutation is vetoed inside ward and proceeds just outside.
- [ ] GameTest remove altar and prove underlying Shroud becomes effective again without field regeneration.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Flame Altars create real sanctuaries that protect players/world surfaces while leaving the logical threat intact.
