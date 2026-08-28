# Enshrouded Plan — Entity Corruption State

**Milestone:** Level 1 required.

**Goal:** attach persistent Shroud corruption metadata to eligible living entities based on exposure to corrupted regions.

**Planned types:** `EntityCorruptionAttachment`, `EntityCorruptionSchema`, `CorruptionStage`, `EntityCorruptionService`, `CorruptionEligibility`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/ecology/state/*`.
- Create entity-type tags `corruptible`, `immune`, `boss_excluded`.

## Dependencies

- 03 Exposure available; 01 Shroud Query authoritative.

## Implementation contract

- The corruption attachment carries an explicit schema version/stable evolution policy from its first persisted form; Stage 09 may test migrations later but must not retrofit versioning after entities have been saved.
- Unknown future schema fails closed with a diagnostic and must not silently clear corruption state or replace entity identity.
- Eligible entities accumulate corruption only while in effective Shroud; sanctuary/clear zones can regress according to policy.
- Corruption stage/intensity persists through save/load and dimension transfer where entity persistence allows.
- Players use Exposure, not this mob-corruption attachment.
- External bosses and entities explicitly tagged immune fail closed.
- Conversion never replaces the entity type, owner UUID, tame state, inventory or genetics data.

## TDD / verification

- [ ] Unit-test schema version round-trip plus unknown-future-schema diagnostic behavior.
- [ ] Unit-test accumulation/regression and tag eligibility.
- [ ] GameTest cow/wolf/zombie examples gain persistent corruption without changing entity type.
- [ ] GameTest tame/owned entity preserves owner and inventory/state fields.
- [ ] Save/reload preserves corruption stage.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Existing creatures can become and remain corrupted through a version-aware attachment without destructive entity replacement.
