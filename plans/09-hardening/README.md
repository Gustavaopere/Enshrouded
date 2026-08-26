# 09 — Hardening

**Goal:** prove Level 1 correctness, performance, persistence compatibility and release readiness in the real large-pack context.

## Task order

1. [`01 test matrix`](01-test-matrix.md)
2. [`02 performance`](02-performance.md)
3. [`03 world upgrade`](03-world-upgrade.md)
4. [`04 release checklist`](04-release-checklist.md)

## Runtime contracts

- No task is marked complete solely because unit tests pass; relevant GameTests and dedicated-server smoke are required.
- Performance budgets are measured under multiple simultaneous cores/entities.
- Save schema evolution is tested before the Level 1 release is declared stable.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
