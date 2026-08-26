# 06 — Lich and Story

**Goal:** implement persistent recurring-antagonist story state, provider abstraction, first manifestation encounter and unique skull reward.

## Task order

1. [`01 story state`](01-story-state.md)
2. [`02 boss provider`](02-boss-provider.md)
3. [`03 first manifestation`](03-first-manifestation.md)
4. [`04 Lich Skull`](04-lich-skull.md)

## Runtime contracts

- External boss entities are encounter actors, never progression authorities.
- Defeating a manifestation cannot kill the canonical Lich story entity permanently.
- Every reward is idempotent and encounter-ID scoped.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
