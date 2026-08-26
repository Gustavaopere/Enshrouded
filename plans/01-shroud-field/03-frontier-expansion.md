# Enshrouded Plan — Frontier Expansion

**Milestone:** Level 1 required.

**Goal:** advance corruption as a deterministic, bounded logical frontier without scanning the world or loading chunks.

**Planned types:** `ShroudExpansionScheduler`, `ShroudFrontier`, `ShroudFrontierEntry`, `ShroudPropagationPolicy`, `ShroudWorkBudget`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/expansion/*`.
- Create deterministic tests under `src/test/java/.../shroud/expansion/*`.

## Dependencies

- 01 state/persistence.
- 02 core lifecycle.

## Implementation contract

- Each active core owns a bounded frontier queue over coarse cells; work per server tick is capped globally and per core.
- Propagation uses coordinates/known field state only and never calls APIs that load an unloaded chunk.
- Distance from the core, deterministic noise/seed and configurable terrain-neutral costs determine candidate intensity; material block predicates are deferred to Stage 02.
- Level 1 propagation cannot exceed the core maximum influence radius.
- Scheduler fairness prevents one large core from starving other active cores.
- Core destruction clears/invalidates pending expansion work by generation/epoch so stale queue entries cannot re-grow the region.

## TDD / verification

- [ ] Write deterministic frontier tests for same seed/order producing the same cell set.
- [ ] Prove strict radius cap and work-budget cap under thousands of queued candidates.
- [ ] Prove two cores receive fair work under a shared global budget.
- [ ] Prove destroying a core makes every stale queued entry a no-op.
- [ ] Benchmark scheduler cost with synthetic 10k/100k-cell queues and record baseline in test output/docs.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** An active core visibly grows its logical region over time with deterministic bounded work and zero forced chunk loads.
