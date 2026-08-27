# 03 — Exposure and Deadly Shroud

**Goal:** implement timed survival, Madness, Flame-gated Deadly Shroud and Red Sludge from canonical Shroud severity.

## Task order

1. [`01 player exposure`](01-player-exposure-state.md)
2. [`02 Madness`](02-madness.md)
3. [`03 Deadly Shroud`](03-deadly-shroud.md)
4. [`04 Red Sludge`](04-red-sludge.md)

## Runtime contracts

- Exposure is server-authoritative and samples `ShroudQuery`.
- Sanctuary suppression is interpreted from the canonical `ShroudSample` overlay; Stage 03 does not scan Flame Altars or rewrite logical Shroud state.
- Task 01 owns the internal `DeadlyExposurePolicy` seam plus a fail-closed Level 1 barrier fallback, so `ExposureService` never has a compile dependency on Task 03.
- Task 03 replaces that fallback with `FlameGatedDeadlyExposurePolicy` through the same interface and reads progression only through Foundation `ProgressionOwnerResolver` + `FlamePassageQuery`.
- Flame progression modifies passage rules through interfaces rather than hard-coded altar searches or Stage 05 implementation imports.
- Client effects never determine death or remaining time.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
