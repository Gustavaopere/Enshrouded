# Enshrouded Plan — Madness

**Milestone:** Level 1 required.

**Goal:** create escalating pre-death thresholds and authoritative fatal outcome when exposure reaches zero.

**Planned types:** `MadnessStage`, `MadnessService`, `ModDamageTypes`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/exposure/madness/*`.
- Register `enshrouded:madness` damage type/data.
- Create client-neutral event/snapshot fields for presentation stage.

## Dependencies

- 01 player exposure.

## Implementation contract

- Madness stages are threshold bands over remaining reserve and do not maintain a second competing timer.
- Server gameplay penalties are restrained and configurable; visual/audio hallucination flags are sent as presentation state.
- At zero reserve, server applies fatal Madness semantics robustly even against ordinary armor/resistance unless an explicit Enshrouded protection rule exists.
- Death message identifies Madness/Shroud cause.
- No random client hallucination can affect server targeting, inventory or world state.

## TDD / verification

- [ ] Unit-test threshold transitions and zero boundary.
- [ ] GameTest armored player still reaches the configured fatal outcome at exhaustion.
- [ ] GameTest leaving Shroud before zero permits recovery and clears escalating server penalties according to policy.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Exhausting the Shroud timer causes a deterministic Madness death with escalating warnings before the endpoint.
