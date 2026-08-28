# Enshrouded Plan — Flame State

**Milestone:** Level 1 required.

**Goal:** persist player/team-resolvable Flame level, passage level and completed rituals with versioned semantics.

**Planned types:** `FlameProgressionSavedData`, `FlameProgressionSchema`, `FlameProgressionState`, `DefaultProgressionOwnerResolver`, `FlamePassageService`.

`DefaultProgressionOwnerResolver` implements the Foundation `ProgressionOwnerResolver` contract. `FlamePassageService` implements the Foundation `FlamePassageQuery` contract; Stage 05 does not redefine either interface.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/state/*`.
- Create tests under `src/test/java/.../flame/state/*`.

## Dependencies

- Foundation progression contracts, including `ProgressionOwnerResolver` and `FlamePassageQuery`.

## Implementation contract

- `FlameProgressionSavedData` carries an explicit schema version/stable codec evolution policy from its first persisted form; Stage 09 migration tests build on that existing version contract rather than adding it later.
- Unknown future schema fails closed with a clear diagnostic and must not silently reset Flame level, passage level or completed rituals.
- Default owner key is player UUID; owner resolution is injectable for FTB Teams later.
- Initial Flame Level and Passage Level are `1` for a new owner.
- `DefaultProgressionOwnerResolver` preserves the Foundation standalone semantics and can later be replaced by the Stage 08 FTB Teams adapter through the same interface.
- `FlamePassageService` reads persistent progression state and replaces the Foundation Level 1 fallback without changing Stage 03 exposure code.
- Completed ritual IDs are stable resource IDs and make progression idempotent.
- Level values are bounded and future-compatible; Level 2 content is not defined here.
- Progression writes are transactional/idempotent enough that reconnect/double-click cannot grant the same advancement twice.

## TDD / verification

- [ ] Round-trip schema version, owner progression and ritual set through persistence.
- [ ] Unit-test unknown-future-schema diagnostic/fail-closed behavior.
- [ ] Unit-test initial Level 1, duplicate ritual rejection and owner isolation.
- [ ] Contract test `FlamePassageService` through the Foundation `FlamePassageQuery` interface.
- [ ] Contract test standalone resolver behavior matches the Foundation player-UUID resolver.
- [ ] GameTest two players have independent state under standalone resolver.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Flame progression survives restart with a version-aware persistence contract, starts at Level 1 and backs the pre-existing Foundation query boundaries without finding physical altar blocks or forcing Stage 03 changes.
