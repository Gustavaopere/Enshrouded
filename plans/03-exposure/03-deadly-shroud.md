# Enshrouded Plan — Deadly Shroud Passage

**Milestone:** Level 1 required.

**Goal:** make Red/Deadly Shroud a Flame-level gate by replacing the fail-closed Level 1 barrier fallback with a passage-aware implementation.

**Planned types:** `FlameGatedDeadlyExposurePolicy`, `PassageRequirement`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/exposure/deadly/*`.
- Extend server config with deadly collapse duration/required passage tier defaults.

## Dependencies

- 01 player exposure, including its `DeadlyExposurePolicy` interface and fail-closed Level 1 barrier fallback.
- Foundation `ProgressionOwnerResolver` and `FlamePassageQuery` contracts/defaults from `00-foundation/02-domain-contracts.md`.

Stage 03 must **not** create a local passage stub and must not depend on Stage 05 implementation classes. During standalone Level 1, the Foundation fallback reports passage level 1; when Stage 05 merges, the same injected query boundary is backed by persistent Flame state.

## Implementation contract

- `FlameGatedDeadlyExposurePolicy` implements the existing `DeadlyExposurePolicy`; `ExposureService` does not gain direct passage/progression logic in this task.
- Level 1 world may contain `DEADLY` cells with passage requirement 2 while player Flame starts at 1.
- Underleveled entry collapses remaining survival to a short configurable emergency window and continues rapid drain; repeatedly stepping in/out cannot refill the lost reserve exploitably.
- If passage level meets the requirement in future saves, the policy can return the allowed exposure profile without schema changes.
- Barrier logic depends on `ProgressionOwnerResolver` + `FlamePassageQuery` only, never scans for altar blocks or imports Stage 05 state classes.
- The behavior remains fail-closed if owner/passage resolution is unavailable or inconsistent: uncertain progression never grants Deadly passage.
- `DEADLY` state is communicated distinctly to client presentation.

## TDD / verification

- [ ] Unit-test required-level comparison and underleveled collapse math.
- [ ] Unit-test player owner resolution + injected Foundation passage fallback produces Level 1 underleveled behavior.
- [ ] Contract test proves `FlameGatedDeadlyExposurePolicy` is substitutable for the Task 01 `DeadlyExposurePolicy` without changing `ExposureService`.
- [ ] Unit-test failed/uncertain owner or passage lookup fails closed rather than granting access.
- [ ] GameTest Level 1 player entering Deadly Shroud reaches fatal exposure rapidly.
- [ ] GameTest fake passage level 2 proves the policy extension point can permit the zone without changing cell data.
- [ ] GameTest edge-dancing cannot reset the emergency window.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Red Shroud is present during Level 1 as an unmistakable, mechanically enforced boundary for later progression, with no Task 01 -> Task 03 compile cycle and no Stage 03 -> Stage 05 implementation dependency.
