# Enshrouded Plan — Magic Resistance

**Milestone:** Level 1 required.

**Goal:** give corrupted creatures configurable magic resistance through one damage-classification service.

**Planned types:** `DefaultMagicDamageClassifier`, `MagicResistanceService`.

The classification value type is the Foundation-owned `MagicDamageClassification`; Stage 04 must not introduce a second `MagicClassification` model.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/combat/magic/*`.
- Create damage-type tags `enshrouded:magic` and `enshrouded:magic_bypass`.
- Hook server living damage event at the supported NeoForge 1.21.1 phase.

## Dependencies

- Foundation `MagicDamageClassifier`, `MagicDamageClassification`, `MagicDamageKind` and `MagicDamageConfidence`.
- 01 entity corruption.

## Implementation contract

- Core classifier recognizes Enshrouded/vanilla damage tags without importing optional magic mods and returns the Foundation classification value.
- `DefaultMagicDamageClassifier` is the standalone baseline; optional adapters enrich/compose classification through the same Foundation boundary rather than replacing the value model.
- Corrupted-mob resistance is configurable; Level 1 default target is meaningful but not immunity.
- Resistance applies once at an auditable damage stage and cannot double-apply when Iron's/Ars adapters also classify a source.
- Physical/melee/projectile damage is unchanged unless the Foundation classification marks the source magical.
- `UNKNOWN` remains fail-safe/non-magical unless an adapter can positively classify the source.
- Boss provider may define its own external resistances; Enshrouded does not blindly stack mob-corruption resistance onto the story Lich.

## TDD / verification

- [ ] Unit-test physical vs magical Foundation classification and one-time resistance math.
- [ ] Unit-test adapter enrichment changes classification/confidence only and cannot trigger a second resistance application.
- [ ] GameTest clean and corrupted copies receiving same tagged magic damage produce expected health delta.
- [ ] GameTest ordinary melee remains unchanged.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Corrupted creatures are demonstrably resistant to positively classified magic through the single Foundation classification model while nonmagical counterplay remains intact.
