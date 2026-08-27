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
- Foundation `ShroudQuery` and `FlameWardQuery` contracts/defaults.

## Implementation contract

- Queries use a spatial index/coarse-cell lookup and do not iterate all cores.
- `DefaultShroudQuery` receives an injected `FlameWardQuery`; before Stage 05 it uses the Foundation no-ward fallback and never scans altar blocks.
- The returned sample always preserves **canonical logical** intensity/severity and owning source. Sanctuary only sets `ShroudSample.sanctuarySuppressed=true`; the query must not rewrite an underlying `SHROUD`/`DEADLY` sample to logical `CLEAR`.
- Effective consumers interpret `sanctuarySuppressed` according to their domain: exposure treats it as safe, terrain mutation is separately vetoed through `MutationAuthority`, and presentation may show both ward safety and the latent Shroud field.
- `DEADLY` classification is based on stable configurable thresholds/tier metadata; no client heuristic decides severity.
- Client receives only the local player/nearby presentation state needed for HUD/fog; it does not receive the entire world field.
- Packets are rate-limited/change-driven and versioned.

## TDD / verification

- [ ] Unit-test intensity-to-severity boundaries and overlapping-core resolution.
- [ ] Unit-test no-ward fallback leaves `sanctuarySuppressed=false`.
- [ ] Unit-test injected ward marks `sanctuarySuppressed=true` while preserving original logical severity/intensity/source.
- [ ] Prove query complexity does not grow linearly with total world cores for indexed local lookup cases.
- [ ] GameTest/network test player crossing `CLEAR -> SHROUD -> DEADLY -> CLEAR` receives ordered authoritative state.
- [ ] Prove malformed/stale client payload cannot mutate server state.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** All later systems read one canonical `ShroudQuery`; Sanctuary is an effective overlay rather than a rewrite of logical field state, and local clients receive enough authoritative state for presentation without world-state dumps.
