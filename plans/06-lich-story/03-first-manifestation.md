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

- Encounter trigger is explicit and server-side; random natural/external living entities are not mistaken for the story boss.
- Encounter creation resolves/receives one canonical `ProgressionOwner` and stores that immutable owner snapshot with the encounter ID. Defeat/reward paths reuse the stored owner and never re-resolve the initiating player mid-encounter.
- Encounter entity is marked with unique encounter ID and manifestation ID.
- Level 1 director supports provider-neutral phases keyed to health/time/events; external boss native behavior remains intact unless a narrowly defined modifier is applied.
- Stage 06 core tests remain standalone and do not require Ars Zero or any other optional boss provider to be installed.
- Team membership changes during an active encounter cannot transfer that encounter or its reward authority to another owner; membership changes affect only later encounters unless an explicit migration exists.
- Arena may intensify ordinary Shroud locally but cannot permanently create an unowned region or bypass core ownership rules.
- Defeat records manifestation 1 as defeated but explicitly describes the Lich as surviving through phylactery/next manifestation.

## TDD / verification

- [ ] GameTest start one encounter and reject concurrent duplicate for same progression owner.
- [ ] GameTest an unrelated unmarked native/test living entity death cannot complete story or issue a reward.
- [ ] GameTest valid marked boss defeat transitions state to `DEFEATED` exactly once.
- [ ] GameTest mutate the owner resolver/team membership after encounter start and prove defeat/reward still target the original stored owner exactly once.
- [ ] GameTest arena cleanup removes temporary encounter effects while preserving pre-existing Shroud field.
- [ ] Leave the explicit unrelated `ars_zero:lich` negative test to `08-integrations/01-ars-zero.md`, where the optional provider is actually present.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players can fight and defeat the first body of the recurring Lich through a provider-neutral, non-duplicable story encounter with immutable encounter ownership, while Stage 06 remains fully testable without optional boss mods.
