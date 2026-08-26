# Enshrouded Plan — Level 1 Ritual

**Milestone:** Level 1 required.

**Goal:** define the first Lich Skull offering as the one Level 1 story milestone and prepare future progression without unlocking Level 2 content.

**Planned types:** `FlameRitual`, `FlameRitualRegistry`, `LevelOneLichSkullRitual`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/ritual/*`.
- Create data/translation for ritual `enshrouded:lich_manifestation_1`.

## Dependencies

- 02 Flame Altar.
- 06 Lich Skull task will supply the required item; integration closes `ENSH-L1-LICH-REWARD-001`.

## Implementation contract

- Ritual recognizes only a valid Enshrouded Level 1 Lich Skull item/state.
- Success records `enshrouded:lich_manifestation_1` completion exactly once.
- For the Level 1 milestone, completion may mark `nextLevelReady=true`/story checkpoint but does not grant Passage Level 2 until Level 2 content is deliberately implemented, avoiding an unlocked empty tier.
- UI/lore clearly communicates that the Flame has been strengthened/prepared while Deadly Shroud remains a future progression boundary in this release.
- Data model supports later ritual definitions without hard-coded switch statements.

## TDD / verification

- [ ] Unit-test ritual eligibility/idempotence.
- [ ] GameTest valid skull consumed once and checkpoint persisted.
- [ ] GameTest duplicate skull after completed ritual does not advance again or consume unexpectedly.
- [ ] Cross-stage test after Lich reward merges: boss-earned skull satisfies the altar ritual end-to-end.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Defeating the first manifestation and offering its skull completes the entire Level 1 progression loop without pretending Level 2 exists.
