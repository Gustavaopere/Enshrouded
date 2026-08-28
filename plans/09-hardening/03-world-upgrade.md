# Enshrouded Plan — World Upgrade and Recovery

**Milestone:** Level 1 required.

**Goal:** prove safe migration/recovery behavior for persistent formats that were already version-aware from their first implementation branches.

**Planned types:** `EnshroudedDataFixer`, `SchemaMigration`, `RecoveryCommand`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/datafix/*`.
- Create fixture saves/NBT test resources under `src/test/resources/world-upgrades/*`.
- Create admin diagnostic/recovery commands under `command/*`.

## Dependencies

- Persistent state from Shroud, exposure, entity corruption, Flame and story implemented with their own initial schema/version contracts.

## Implementation contract

- Stage 09 does not retrofit schema versioning. Each `SavedData`/attachment format must already expose an explicit version or stable codec evolution policy from the branch that first persisted it.
- Add migration paths only where a real/synthetic previous schema exists; migration ownership is centralized here to avoid ad-hoc conversion logic scattered through gameplay services.
- At least one synthetic old-schema fixture per persistent subsystem class is migrated to current schema in tests before release.
- Unknown/future schema fails safely with clear log/admin diagnostic; it must not silently reset a world-scale Shroud state, exposure reserve, entity corruption, Flame progression or story/reward state.
- Recovery commands can inspect orphaned cores/encounters and perform narrowly scoped repair with explicit admin action.
- Player/entity data migration preserves progression/reward idempotence and entity identity.

## TDD / verification

- [ ] Verify each persistent subsystem exposes the schema/version contract established in its originating stage.
- [ ] Load current and old-schema fixtures and verify exact migrated values.
- [ ] Corrupt/truncate representative data and verify controlled failure/diagnostic path.
- [ ] GameTest/server fixture reload confirms no duplicate core, ritual or skull reward after migration.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Level 1 saves are version-aware from inception, migrations are tested centrally, and administrators can diagnose persistent-state problems without deleting the world.
