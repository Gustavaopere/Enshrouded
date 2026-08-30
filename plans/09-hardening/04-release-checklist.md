# Enshrouded Plan — Level 1 Release Checklist

**Milestone:** Level 1 required.

**Goal:** perform the final code/content/license/config validation and mark Level 1 complete only on exact verified HEAD.

**Planned types:** `ReleaseChecklist`.

## Files

- Create `docs/release/level1-checklist.md`.
- Finalize `README.md`, `SOURCES.md`, `THIRD_PARTY_NOTICES.md`, default configs and changelog/release notes.
- Update every completed plan task and `plans/STATUS.md`.

## Dependencies

- 01 test matrix.
- 02 performance.
- 03 world upgrade.
- 05 third-party licenses/provenance.

## Implementation contract

- Check all required task files are `✅-` and every pending Level 1 contract is closed.
- Check no source/runtime dependency on Spore/Infnexus exists.
- Check Sculk Horde provenance/notices are complete for any adapted source actually used.
- Check Ars Zero integration remains optional and GPL source was not copied into core without an explicitly compliant derivation decision.
- Check all other optional providers/references are represented in `SOURCES.md`/`THIRD_PARTY_NOTICES.md` and no actual derivation remains unresolved.
- Check dedicated-server/client separation, config defaults, translations, assets, datapack validation and registry IDs.
- Build exact release JAR and verify `LICENSE` and `THIRD_PARTY_NOTICES.md` are included.
- Record final commit SHA and CI run in `STATUS.md`.

## TDD / verification

- [ ] Run clean `./gradlew test`.
- [ ] Run clean GameTests.
- [ ] Run clean `./gradlew build` and JAR inspection.
- [ ] Run standalone dedicated-server smoke from the built artifact.
- [ ] Run final current-pack smoke/profile.
- [ ] Perform repository scan for `spore`, `infnexus`, `UPSTREAM-DERIVED`, accidental GPL/proprietary source copies and unresolved provenance markers.
- [ ] Verify every actual `DERIVED_CODE`/`DERIVED_ASSET` record has exact upstream revision/file/license/notice evidence.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; GameTests/dedicated-server smoke are GREEN where applicable.
- [ ] No unresolved cross-stage contract is hidden; `plans/PENDING.md` is current.
- [ ] No release contains actual derived material whose rights remain `REVIEW_REQUIRED`, `PERMISSION_REQUIRED` or unknown.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same checkpoint.

**Acceptance:** An exact tagged/recorded HEAD provides a standalone, performant, persistent, provenance-audited and pack-compatible Level 1 Enshrouded vertical slice.
