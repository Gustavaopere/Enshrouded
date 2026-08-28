# Enshrouded Plan — Frontier Expansion

**Milestone:** Level 1 required.

**Goal:** advance corruption as a deterministic, bounded logical frontier without scanning the world or loading chunks.

**Implemented types:** `ShroudExpansionScheduler`, `ShroudFrontier`, `ShroudFrontierEntry`, `ShroudPropagationPolicy`, `ShroudWorkBudget`, `ShroudGridGeometry`.

## Files

- `src/main/java/com/gustavaopere/enshrouded/shroud/expansion/*`.
- Deterministic tests under `src/test/java/.../shroud/expansion/*`.

## Dependencies

- ✅ 01 state/persistence.
- ✅ 02 core lifecycle.

## Implemented contract

- Each active core owns a strictly bounded frontier queue over coarse cells; work per server tick is capped globally and per core.
- Propagation uses coordinates/known logical field state only and never calls APIs that load an unloaded chunk.
- Distance from the core, deterministic noise/seed and terrain-neutral costs determine candidate intensity; material block predicates remain deferred to Stage 02.
- The scheduler itself enforces the core maximum influence radius, independent of the pluggable propagation policy.
- Scheduler fairness prevents one large core from starving other active cores.
- Core destruction/lifecycle epoch invalidates stale pending work so it cannot re-grow the region.
- When a bounded frontier cannot admit every eligible sibling, retryable work is recovered deterministically after frontier exhaustion from the persisted logical region only; no unbounded overflow queue and no world/chunk scan are introduced.
- A touched region uses one mutable working-cell map per tick and freezes one immutable `ShroudRegionState` after the tick, avoiding full-region reconstruction for every applied cell.

## TDD / verification

- [x] Deterministic frontier tests prove the same seed/order produces the same logical cell set.
- [x] Strict scheduler-owned radius cap and global/per-core work-budget caps are proven, including a permissive custom-policy regression.
- [x] Shared global budget fairness across multiple cores is proven.
- [x] Destroyed/stale-epoch frontier work is a no-op and cannot re-grow logical state.
- [x] Capacity-1 overflow regression proves eligible neighbors remain retryable while the queue stays bounded.
- [x] Synthetic 10k/100k reject-only frontier benchmark is recorded.
- [x] Apply-heavy regression benchmark covers `20k existing + 5k applied` and guards against per-cell region copying.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] Frontier benchmark gate is GREEN.
- [x] NeoForge build and production JAR sanity are GREEN.
- [x] GameTest server is GREEN.
- [x] Shroud SavedData two-boot reload GameTest is GREEN.
- [x] Dedicated-server two-boot save/reload smoke is GREEN.
- [x] Review findings were reproduced before fixes and addressed with regression coverage.
- [x] Merged to `main` and renamed with the `✅-` prefix.

## Verified merge record

- Branch: `feat/01-frontier-expansion`
- Initial RED: `39cf5d602baf8b20be29c42c505f6ded37e7a510`
- Review-regression RED: `d457d46d4d7b774cb26aac906e94d35ac36bb15c`
- Final implementation HEAD: `6fc270e4d5faaf35b1d52365f34ab2b318b41811`
- PR: #11 — `Stage 01: implement bounded frontier expansion`
- Final PR-head workflow: `33195407903`
- Final job: `98931223350`
- Verification: GREEN
- Merge SHA: `bd2ae8faee2b1c69d8e1f08e314f48e71ae418c8`

## Review closeout

- Hard radius is enforced by the scheduler even when a custom policy returns positive intensity outside the allowed radius.
- Region mutation is accumulated in one `WorkingRegion` per touched region/tick and frozen once.
- Full-frontier backpressure remains retryable without introducing an unbounded auxiliary queue.

**Acceptance:** An active core grows its logical region deterministically with bounded work, a hard influence radius, fair scheduling and zero forced chunk loads.
