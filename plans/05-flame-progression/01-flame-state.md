# Enshrouded Plan — Flame State

**Milestone:** Level 1 required.

**Goal:** persist player/team-resolvable Flame level, passage level and completed rituals with versioned semantics.

**Planned types:** `FlameProgressionSavedData`, `FlameProgressionState`, `DefaultProgressionOwnerResolver`, `FlamePassageService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/state/*`.
- Create tests under `src/test/java/.../flame/state/*`.

## Dependencies

- Foundation progression contracts.

## Implementation contract

- Default owner key is player UUID; owner resolution is injectable for FTB Teams later.
- Initial Flame Level and Passage Level are `1` for a new owner.
- Completed ritual IDs are stable resource IDs and make progression idempotent.
- Level values are bounded and future-compatible; Level 2 content is not defined here.
- Progression writes are transactional/idempotent enough that reconnect/double-click cannot grant the same advancement twice.

## TDD / verification

- [ ] Round-trip owner progression and ritual set through persistence.
- [ ] Unit-test initial Level 1, duplicate ritual rejection and owner isolation.
- [ ] GameTest two players have independent state under standalone resolver.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Flame progression survives restart, starts at Level 1 and can be queried without finding physical altar blocks.
