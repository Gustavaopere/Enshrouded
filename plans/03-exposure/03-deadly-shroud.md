# Enshrouded Plan — Deadly Shroud Passage

**Milestone:** Level 1 required.

**Goal:** make Red/Deadly Shroud a Flame-level gate rather than merely a stronger ordinary zone.

**Planned types:** `FlamePassageQuery`, `DeadlyExposurePolicy`, `PassageRequirement`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/exposure/deadly/*`.
- Extend server config with deadly collapse duration/required passage tier defaults.

## Dependencies

- 01 player exposure.
- Foundation progression contract; Stage 05 may initially provide a stub Level 1 passage query until its canonical implementation merges.

## Implementation contract

- Level 1 world may contain `DEADLY` cells with passage requirement 2 while player Flame starts at 1.
- Underleveled entry collapses remaining survival to a short configurable emergency window and continues rapid drain; repeatedly stepping in/out cannot refill the lost reserve exploitably.
- If passage level meets the requirement in future saves, Deadly policy can degrade to an allowed exposure profile without schema changes.
- Barrier logic depends on `FlamePassageQuery` only, never scans for altar blocks.
- `DEADLY` state is communicated distinctly to client presentation.

## TDD / verification

- [ ] Unit-test required-level comparison and underleveled collapse math.
- [ ] GameTest Level 1 player entering Deadly Shroud reaches fatal exposure rapidly.
- [ ] GameTest fake passage level 2 proves the policy extension point can permit the zone without changing cell data.
- [ ] GameTest edge-dancing cannot reset the emergency window.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Red Shroud is present during Level 1 as an unmistakable, mechanically enforced boundary for later progression.
