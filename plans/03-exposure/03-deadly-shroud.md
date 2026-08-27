# Enshrouded Plan — Deadly Shroud Passage

**Milestone:** Level 1 required.

**Goal:** make Red/Deadly Shroud a Flame-level gate rather than merely a stronger ordinary zone.

**Planned types:** `DeadlyExposurePolicy`, `PassageRequirement`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/exposure/deadly/*`.
- Extend server config with deadly collapse duration/required passage tier defaults.

## Dependencies

- 01 player exposure.
- Foundation `ProgressionOwnerResolver` and `FlamePassageQuery` contracts/defaults from `00-foundation/02-domain-contracts.md`.

Stage 03 must **not** create a local passage stub and must not depend on Stage 05 implementation classes. During standalone Level 1, the Foundation fallback reports passage level 1; when Stage 05 merges, the same injected query boundary is backed by persistent Flame state.

## Implementation contract

- Level 1 world may contain `DEADLY` cells with passage requirement 2 while player Flame starts at 1.
- Underleveled entry collapses remaining survival to a short configurable emergency window and continues rapid drain; repeatedly stepping in/out cannot refill the lost reserve exploitably.
- If passage level meets the requirement in future saves, Deadly policy can degrade to an allowed exposure profile without schema changes.
- Barrier logic depends on `ProgressionOwnerResolver` + `FlamePassageQuery` only, never scans for altar blocks or imports Stage 05 state classes.
- `DEADLY` state is communicated distinctly to client presentation.

## TDD / verification

- [ ] Unit-test required-level comparison and underleveled collapse math.
- [ ] Unit-test player owner resolution + injected Foundation passage fallback produces Level 1 underleveled behavior.
- [ ] GameTest Level 1 player entering Deadly Shroud reaches fatal exposure rapidly.
- [ ] GameTest fake passage level 2 proves the policy extension point can permit the zone without changing cell data.
- [ ] GameTest edge-dancing cannot reset the emergency window.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Red Shroud is present during Level 1 as an unmistakable, mechanically enforced boundary for later progression, with no Stage 03 -> Stage 05 implementation dependency.
