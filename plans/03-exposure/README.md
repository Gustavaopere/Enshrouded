# 03 — Exposure and Deadly Shroud

**Goal:** implement timed survival, Madness, Flame-gated Deadly Shroud and Red Sludge from canonical Shroud severity.

## Task order

1. [`01 player exposure`](01-player-exposure-state.md)
2. [`02 Madness`](02-madness.md)
3. [`03 Deadly Shroud`](03-deadly-shroud.md)
4. [`04 Red Sludge`](04-red-sludge.md)

## Runtime contracts

- Exposure is server-authoritative and samples `ShroudQuery`.
- Flame progression modifies passage rules through an interface rather than hard-coded altar searches.
- Client effects never determine death or remaining time.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
