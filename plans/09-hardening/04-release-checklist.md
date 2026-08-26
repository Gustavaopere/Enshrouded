# Enshrouded Plan — Level 1 Release Checklist

**Milestone:** Level 1 required.

**Goal:** perform the final code/content/license/config validation and mark Level 1 complete only on exact verified HEAD.

**Planned types:** `ReleaseChecklist`.

## Files

- Create `docs/release/level1-checklist.md`.
- Finalize `README.md`, `THIRD_PARTY_NOTICES.md`, default configs and changelog/release notes.
- Update every completed plan task and `plans/STATUS.md`.

## Dependencies

- 01 test matrix.
- 02 performance.
- 03 world upgrade.

## Implementation contract

- Check all required task files are `✅-` and every pending Level 1 contract is closed.
- Check no source/runtime dependency on Spore/Infnexus exists.
- Check source-derived Sculk Horde notices are complete for any adapted code actually used.
- Check Ars Zero integration remains optional and GPL code was not copied into core.
- Check dedicated-server/client separation, config defaults, translations, assets, data pack validation and no missing registry IDs.
- Build exact release JAR and verify its contents/dependencies.
- Record final commit SHA and CI run in `STATUS.md`.

## TDD / verification

- [ ] Run clean `./gradlew test`.
- [ ] Run clean GameTests.
- [ ] Run clean `./gradlew build` and JAR inspection.
- [ ] Run standalone dedicated-server smoke from the built artifact.
- [ ] Run final current-pack smoke/profile.
- [ ] Perform repository scan for `spore`, `infnexus`, accidental GPL source copies and unresolved placeholder markers.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** An exact tagged/recorded HEAD provides a standalone, performant, persistent and pack-compatible Level 1 Enshrouded vertical slice.
