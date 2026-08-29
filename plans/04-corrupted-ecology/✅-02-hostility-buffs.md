# Enshrouded Plan — Hostility and Attribute Buffs

**Milestone:** Level 1 required.

**Goal:** make corrupted passive/neutral mobs hostile and corrupted hostiles stronger through reversible modifiers.

**Planned types:** `CorruptedTargetingService`, `CorruptedAttributeModifiers`, `CorruptionCombatPolicy`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/ecology/ai/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/ecology/combat/*`.

## Dependencies

- 01 entity corruption.

## Implementation contract

- Passive/neutral entities at configured corruption stage acquire server-side player-target aggression while preserving original goals as much as possible.
- Use event/target injection or narrowly scoped goals; remove Enshrouded-added behavior when entity purifies.
- Attribute modifiers use stable UUIDs/IDs so reload/reapplication cannot stack duplicates.
- Default buffs scale from corruption intensity and have hard caps for health, damage, movement and knockback resistance.
- Hostile mobs retain their native combat behavior and receive modifiers rather than generic replacement AI.

## TDD / verification

- [x] GameTest corrupted passive targets nearby survival player but clean counterpart does not.
- [x] GameTest purification removes Enshrouded target behavior.
- [x] Unit-test repeated modifier application is idempotent and capped.
- [x] GameTest a third-party/mock hostile retains native target behavior while stats change.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Corruption visibly changes ecology: peaceful creatures become threats and hostile creatures become stronger without permanent AI corruption.
