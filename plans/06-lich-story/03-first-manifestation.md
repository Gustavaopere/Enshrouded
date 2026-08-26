# Enshrouded Plan — First Manifestation Encounter

**Milestone:** Level 1 required.

**Goal:** build the Level 1 encounter loop, arena conditions and escalating first-body behavior around the selected provider.

**Planned types:** `FirstManifestationDefinition`, `ManifestationEncounterService`, `LichArenaRule`, `LichPhaseDirector`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/manifestation/*`.
- Create data definition `data/enshrouded/manifestations/first.json` if codec-driven implementation is selected by Foundation contracts.

## Dependencies

- 02 boss provider.
- 03 Exposure/Deadly behavior.
- 04 ecology combat services.

## Implementation contract

- Encounter trigger is explicit and server-side; random natural external Liches are not mistaken for the story boss.
- Encounter entity is marked with unique encounter ID and manifestation ID.
- Level 1 director supports provider-neutral phases keyed to health/time/events; external boss native behavior remains intact unless a narrowly defined modifier is applied.
- Arena may intensify ordinary Shroud locally but cannot permanently create an unowned region or bypass core ownership rules.
- Defeat records manifestation 1 as defeated but explicitly describes the Lich as surviving through phylactery/next manifestation.

## TDD / verification

- [ ] GameTest start one encounter and reject concurrent duplicate for same progression owner.
- [ ] GameTest unrelated `ars_zero:lich`/native Lich death cannot complete story without encounter marker.
- [ ] GameTest valid boss defeat transitions state to `DEFEATED` exactly once.
- [ ] GameTest arena cleanup removes temporary encounter effects while preserving pre-existing Shroud field.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players can fight and defeat the first body of the recurring Lich through a provider-neutral, non-duplicable story encounter.
