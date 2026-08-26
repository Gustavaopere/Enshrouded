# Enshrouded Plan — Ars Zero Lich Provider

**Milestone:** Level 1 required.

**Goal:** use the installed `ars_zero:lich` as the preferred first-manifestation actor while keeping Enshrouded story/reward authority.

**Planned types:** `ArsZeroLichProvider`, `ArsZeroCompatibilityProbe`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/integration/arszero/*`.
- Add optional-mod test fixture/profile if CI infrastructure supports it.

## Dependencies

- 06 boss provider abstraction.
- Ars Zero 2.0.2 compatibility inventory.

## Implementation contract

- Probe `ModList`/registry once at startup/adapter initialization; provider is available only when `ars_zero:lich` resolves to a living entity type.
- Spawn via registry/generic Minecraft APIs where practical, then attach Enshrouded encounter metadata generically.
- Do not import/copy Ars Zero Lich source into Enshrouded core.
- Preserve Ars Zero native behavior and loot; Enshrouded adds only provider-neutral encounter modifiers/reward hooks.
- If compatibility probe fails, log one concise diagnostic and native provider takes over.

## TDD / verification

- [ ] Unit-test provider unavailable -> native fallback.
- [ ] Integration test with Ars Zero fixture resolves/spawns `ars_zero:lich` and marks encounter.
- [ ] GameTest unrelated naturally spawned Ars Zero Lich does not trigger story reward.
- [ ] GameTest provider boss defeat still emits exactly one Enshrouded Lich Skull.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** With the current pack, the richer Ars Zero Lich can act as manifestation 1; removing Ars Zero still leaves a playable native encounter.
