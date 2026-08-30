# Enshrouded Plan — Ecology Visuals, Drops and Restoration

**Milestone:** Level 1 required.

**Goal:** make corrupted entities readable and safely reversible without duplicating every mob asset.

**Planned types:** `CorruptionVisualState`, `CorruptionLootModifier`, `EntityPurificationService`.

## Files

- Create client render/event code under `src/main/java/com/gustavaopere/enshrouded/client/ecology/*`.
- Create particles/sounds and optional global loot modifier/data under `src/main/resources`.

## Dependencies

- 01-03 ecology tasks.

## Implementation contract

- Visual state uses particles/tint/layers where compatible; missing renderer support degrades to particles/nameplate cues rather than crashing.
- Corrupted entities may drop a small native Shroud reagent used only if Level 1 recipes require it; loot injection is bounded and not duplicated on repeated attachment events.
- Purification removes Enshrouded attribute/AI/visual state but preserves unrelated potion effects/third-party data.
- No hostile corruption remains after the canonical corruption state returns to clean.

## TDD / verification

- [x] GameTest corruption/purification cycle restores baseline attributes and target behavior.
- [x] Client test/smoke with common vanilla entities verifies render layer path does not crash.
- [x] Loot test guarantees at-most-once Enshrouded corruption loot roll per death.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Players can instantly recognize corrupted fauna, and purification cleanly removes only Enshrouded-owned behavior.
