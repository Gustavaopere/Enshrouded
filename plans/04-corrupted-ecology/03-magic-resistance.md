# Enshrouded Plan — Magic Resistance

**Milestone:** Level 1 required.

**Goal:** give corrupted creatures configurable magic resistance through one damage-classification service.

**Planned types:** `DefaultMagicDamageClassifier`, `MagicResistanceService`, `MagicClassification`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/combat/magic/*`.
- Create damage-type tags `enshrouded:magic` and `enshrouded:magic_bypass`.
- Hook server living damage event at the supported NeoForge 1.21.1 phase.

## Dependencies

- Foundation `MagicDamageClassifier`.
- 01 entity corruption.

## Implementation contract

- Core classifier recognizes Enshrouded/vanilla damage tags without importing optional magic mods.
- Corrupted-mob resistance is configurable; Level 1 default target is meaningful but not immunity.
- Resistance applies once at an auditable damage stage and cannot double-apply when Iron's/Ars adapters also classify a source.
- Physical/melee/projectile damage is unchanged unless the damage source is explicitly magic-classified.
- Boss provider may define its own external resistances; Enshrouded does not blindly stack mob-corruption resistance onto the story Lich.

## TDD / verification

- [ ] Unit-test physical vs magical classification and one-time resistance math.
- [ ] GameTest clean and corrupted copies receiving same tagged magic damage produce expected health delta.
- [ ] GameTest ordinary melee remains unchanged.
- [ ] Adapter contract test proves enriching classification cannot apply resistance twice.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Corrupted creatures are demonstrably resistant to magic while nonmagical counterplay remains intact.
