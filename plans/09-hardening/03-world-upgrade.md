# Enshrouded Plan — World Upgrade and Recovery

**Milestone:** Level 1 required.

**Goal:** version every persistent format and prove safe reload/migration/recovery behavior.

**Planned types:** `EnshroudedDataFixer`, `SchemaMigration`, `RecoveryCommand`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/datafix/*`.
- Create fixture saves/NBT test resources under `src/test/resources/world-upgrades/*`.
- Create admin diagnostic/recovery commands under `command/*`.

## Dependencies

- Persistent state from Shroud, exposure, Flame and story implemented.

## Implementation contract

- Every SavedData/attachment format has explicit schema version or stable codec evolution policy.
- At least one synthetic old-schema fixture is migrated to current schema in tests before release.
- Unknown/future schema fails safely with clear log/admin diagnostic; it must not silently reset a world-scale Shroud state.
- Recovery commands can inspect orphaned cores/encounters and perform narrowly scoped repair with explicit admin action.
- Player/entity data migration preserves progression/reward idempotence.

## TDD / verification

- [ ] Load current and old-schema fixtures and verify exact migrated values.
- [ ] Corrupt/truncate representative data and verify controlled failure/diagnostic path.
- [ ] GameTest/server fixture reload confirms no duplicate core, ritual or skull reward after migration.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Level 1 saves have a tested upgrade contract and administrators can diagnose persistent-state problems without deleting the world.
