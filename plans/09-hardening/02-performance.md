# Enshrouded Plan — Performance and Budgets

**Milestone:** Level 1 required.

**Goal:** measure and enforce corruption, mutation, query, entity and client-effect budgets under stress.

**Planned types:** `ShroudPerformanceCounters`, `ShroudDebugMetrics`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/debug/perf/*`.
- Create `docs/performance/level1-baseline.md`.
- Add synthetic benchmarks/tests where stable.

## Dependencies

- Full server systems implemented.

## Implementation contract

- Record work counters for frontier jobs, regression jobs, mutation jobs, local queries, corrupted-entity updates and network payloads.
- Stress at least 1, 10 and 50 active cores with large queued frontiers without forcing chunk loads.
- Enforce hard configured caps so pathological backlog increases latency of visual spread rather than server work per tick.
- Entity corruption updates use staggered/sampled cadence rather than every eligible entity doing expensive scans every tick.
- Client particle/audio/fog work respects distance/settings and does not allocate unbounded transient collections.

## TDD / verification

- [ ] Benchmark deterministic scheduler queues and record throughput/cap adherence.
- [ ] Run server stress scenario and record MSPT-impact evidence available from the test environment.
- [ ] Assert no code path enumerates all loaded chunks every tick.
- [ ] Run heap/persistence size checks for representative region counts and document results.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Measured Level 1 workload stays within explicit bounded work budgets; increased world corruption cannot create unbounded per-tick scans.
