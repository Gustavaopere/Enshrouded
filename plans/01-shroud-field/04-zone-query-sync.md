# Enshrouded Plan — Zone Query and Sync

**Milestone:** Level 1 required.

**Goal:** expose one canonical severity/intensity query and minimal server-to-client synchronization.

**Planned types:** `DefaultShroudQuery`, `ShroudSpatialIndex`, `ShroudSamplePayload`, `ClientShroudState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/query/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/network/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/client/state/ClientShroudState.java`.

## Dependencies

- 03 frontier expansion.
- Foundation `ShroudQuery` contract.

## Implementation contract

- Queries use a spatial index/coarse-cell lookup and do not iterate all cores.
- Effective sample combines logical intensity with sanctuary suppression through an injected/read-only ward query contract.
- `DEADLY` classification is based on stable configurable thresholds/tier metadata; no client heuristic decides severity.
- Client receives only the local player/nearby presentation state needed for HUD/fog; it does not receive the entire world field.
- Packets are rate-limited/change-driven and versioned.

## TDD / verification

- [ ] Unit-test intensity-to-severity boundaries and overlapping-core resolution.
- [ ] Prove query complexity does not grow linearly with total world cores for indexed local lookup cases.
- [ ] GameTest/network test player crossing `CLEAR -> SHROUD -> DEADLY -> CLEAR` receives ordered authoritative state.
- [ ] Prove malformed/stale client payload cannot mutate server state.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** All later systems read one canonical `ShroudQuery`; local clients receive enough authoritative state for presentation without world-state dumps.
