# Enshrouded Plan — Ars Nouveau and Iron's Magic Classification

**Milestone:** Level 1 required.

**Goal:** enrich magic-damage classification for the two installed magic ecosystems without duplicating their spell systems.

**Planned types:** `ArsNouveauMagicAdapter`, `IronsSpellbooksMagicAdapter`, `CompositeMagicDamageClassifier`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/integration/arsnouveau/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/integration/irons/*`.

## Dependencies

- 04 magic resistance core.

## Implementation contract

- Use official/stable damage tags/APIs where available; otherwise classify narrowly by registry IDs/types with version-gated probes.
- Adapters and `CompositeMagicDamageClassifier` return classification evidence only; they never apply damage reduction themselves.
- `MagicResistanceService` remains the single reducer and consumes the final Enshrouded-owned `MagicDamageClassification` exactly once per damage event.
- Unknown spell sources fail to core default rather than being automatically labeled magical by namespace alone unless evidence justifies it.
- Compatibility targets current pack Ars Nouveau 5.13.0 and Iron's Spells 3.16.3, with absence-safe startup.

## TDD / verification

- [ ] Integration tests for representative Ars and Iron spell damage classify as magic.
- [ ] Physical melee/projectile from those mods remains correctly classified when not magical.
- [ ] Unit/integration test composite classification merges adapter evidence without invoking resistance/damage mutation.
- [ ] Combat integration test proves `MagicResistanceService` consumes the final classification and applies resistance exactly once even when multiple adapters contribute evidence.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Corrupted-mob magic resistance behaves correctly with the pack’s major magic mods, classification never becomes a second reducer, and standalone behavior remains valid when either integration is removed.
