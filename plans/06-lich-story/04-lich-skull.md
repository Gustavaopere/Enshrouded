# Enshrouded Plan — Lich Skull Reward

**Milestone:** Level 1 required.

**Goal:** emit one Level 1 skull trophy for a valid first-manifestation defeat and connect it to Flame ritual progression.

**Planned types:** `LichSkullItem`, `LichRewardService`, `RewardReceipt`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/reward/*`.
- Register `enshrouded:lich_skull_manifestation_1` item, model, translations and tooltip/lore.
- Integrate with `LevelOneLichSkullRitual`.

## Dependencies

- 03 first manifestation.
- 05 Level 1 ritual.

## Implementation contract

- Reward service checks encounter ID, manifestation ID, `DEFEATED` state and reward-issued flag atomically.
- Exactly one Enshrouded skull is created per valid encounter; external provider loot tables/drops are not suppressed or duplicated by default.
- Skull encodes/validates manifestation identity through item component/data appropriate to 1.21.1, not display name text.
- Dropping, storing or transferring the skull is allowed; ritual checks authenticity/component, not original owner unless a later design explicitly changes this.
- After issuance, crash/reload/replayed death event cannot issue a second skull.

## TDD / verification

- [ ] Unit-test reward receipt idempotence.
- [ ] GameTest valid death emits one skull, replayed death/reload emits zero additional skulls.
- [ ] GameTest unrelated skull-like item is rejected by Flame ritual.
- [ ] End-to-end GameTest defeat -> obtain skull -> altar offering -> Level 1 checkpoint.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The first Lich defeat produces one authentic head/trophy that closes the Level 1 story-to-Flame loop.
