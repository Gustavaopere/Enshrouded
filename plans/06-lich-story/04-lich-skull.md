# Enshrouded Plan — Lich Skull Reward and Concrete Flame Binding

**Milestone:** Level 1 required.

**Goal:** emit one authentic Level 1 skull trophy for a valid first-manifestation defeat and bind that item to the generic Flame ritual framework from Stage 05.

**Planned types:** `LichSkullItem`, `LichRewardService`, `RewardReceipt`, `LevelOneLichSkullRitual`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/reward/*`.
- Create/register the concrete ritual binding under the appropriate Flame/story integration package without moving reward authority into the altar.
- Register `enshrouded:lich_skull_manifestation_1` item, model, translations and tooltip/lore.
- Register concrete ritual ID `enshrouded:lich_manifestation_1` into the Stage 05 `FlameRitualRegistry`.

## Dependencies

- 03 first manifestation.
- 05 Level 1 ritual **framework** (generic registry/executor/checkpoint semantics only).

There is no reverse dependency from Stage 05 to this task. Stage 06 owns the authentic skull type, authenticity/component validation and the concrete binding that closes the Level 1 loop.

## Implementation contract

- Reward service checks encounter ID, manifestation ID, `DEFEATED` state and reward-issued flag atomically.
- Exactly one Enshrouded skull is created per valid encounter; external provider loot tables/drops are not suppressed or duplicated by default.
- Skull encodes/validates manifestation identity through item component/data appropriate to 1.21.1, not display name text.
- Dropping, storing or transferring the skull is allowed; ritual checks authenticity/component, not original owner unless a later design explicitly changes this.
- `LevelOneLichSkullRitual` recognizes only the authentic Enshrouded Level 1 Lich Skull item/state and delegates execution/idempotence/checkpoint mutation to the Stage 05 ritual engine.
- Successful offering records `enshrouded:lich_manifestation_1` exactly once and may mark `nextLevelReady=true`/equivalent checkpoint, but **must not** grant Passage Level 2 in the Level 1 release.
- After issuance, crash/reload/replayed death event cannot issue a second skull.

## TDD / verification

- [ ] Unit-test reward receipt idempotence.
- [ ] Unit-test authentic component validation and rejection of unrelated skull-like items.
- [ ] Unit-test concrete ritual binding delegates to Stage 05 engine and cannot advance twice.
- [ ] GameTest valid death emits one skull, replayed death/reload emits zero additional skulls.
- [ ] End-to-end GameTest defeat -> obtain authentic skull -> altar offering -> Level 1 checkpoint.
- [ ] End-to-end test proves Passage Level remains 1 after Level 1 completion.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The first Lich defeat produces one authentic head/trophy and Stage 06 binds it to the already-complete Flame ritual engine, closing the Level 1 story-to-Flame loop without a circular stage dependency.
