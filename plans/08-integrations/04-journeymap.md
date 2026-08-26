# Enshrouded Plan — JourneyMap Markers

**Milestone:** Level 1 required.

**Goal:** optionally expose discovered active/purified core locations without turning the map into an omniscient scanner.

**Planned types:** `JourneyMapAdapter`, `ShroudDiscoveryState`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/integration/journeymap/*`.
- Create discovery persistence/API if not already covered by story state.

## Dependencies

- 01 core lifecycle/query.

## Implementation contract

- Only cores legitimately discovered by a player/team are eligible for markers.
- No adapter iterates undiscovered SavedData and dumps all core coordinates to client.
- Marker state updates on discovery, core destruction/purification and progression owner change.
- JourneyMap absence leaves discovery/story gameplay unchanged.

## TDD / verification

- [ ] Unit-test discovery visibility rules.
- [ ] Integration smoke creates/removes marker for discovered core only.
- [ ] Prove undiscovered core remains absent from client marker payload.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** JourneyMap helps revisit known threats without trivializing exploration by revealing every core.
