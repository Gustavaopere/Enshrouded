# 09 — Hardening

**Goal:** prove Level 1 correctness, performance, persistence compatibility, provenance/license compliance and release readiness in the real large-pack context.

## Task order

1. [`✅ 01 test matrix`](✅-01-test-matrix.md)
2. [`02 performance`](02-performance.md)
3. [`03 world upgrade`](03-world-upgrade.md)
4. [`05 third-party licenses/provenance`](05-third-party-licenses-provenance.md)
5. [`04 release checklist`](04-release-checklist.md)

The numeric filenames are historical identifiers; causal execution order places provenance/license closure before the final release checklist because `04-release-checklist.md` depends on task 05.

## Runtime/release contracts

- No task is complete solely because unit tests pass; relevant GameTests and dedicated-server smoke are required.
- Performance budgets are measured under multiple simultaneous cores/entities.
- Save schema evolution is tested before Level 1 is declared stable.
- The public release fails closed for actual copied/adapted third-party material with unresolved provenance, license or permission.
- Optional dependencies/references do not become source-reuse grants merely because they are installed or supported.

**Stage 09 status:** 1/5 tasks verified and merged. Stage 09.01 provides the complete Level-1 standalone/restart matrix and closes the real Ars Zero distribution verification. Stage 09.02 Performance is the next canonical task and is not started by this closeout.

Tasks are implemented and merged in causal order unless `STATUS.md` explicitly records a reviewed dependency change.
