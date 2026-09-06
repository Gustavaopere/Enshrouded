# 09 — Hardening

**Goal:** prove Level 1 correctness, performance, persistence compatibility, provenance/license compliance and release readiness in the real large-pack context.

## Task order

1. [`✅ 01 test matrix`](✅-01-test-matrix.md)
2. [`✅ 02 performance`](✅-02-performance.md)
3. [`✅ 03 world upgrade`](✅-03-world-upgrade.md)
4. [`✅ 05 third-party licenses/provenance`](✅-05-third-party-licenses-provenance.md)
5. [`✅ 04 release checklist`](✅-04-release-checklist.md)

The numeric filenames are historical identifiers; causal execution order placed provenance/license closure before the final release checklist because task 04 depends on task 05.

## Runtime/release contracts

- No task is complete solely because unit tests pass; relevant GameTests and dedicated-server smoke are required.
- Performance budgets are measured under multiple simultaneous cores/entities.
- Save schema evolution is tested before Level 1 is declared stable.
- The public release fails closed for actual copied/adapted third-party material with unresolved provenance, license or permission.
- Optional dependencies/references do not become source-reuse grants merely because they are installed or supported.
- The complete 607-JAR modpack distribution is not vendored by this repository, so a literal full-pack boot remains an explicit external/manual distribution smoke rather than fabricated GitHub Actions evidence.

**Stage 09 status: COMPLETE — 5/5 verified and merged at the implementation level.**

- Stage 09.01 provides the complete Level-1 standalone/restart matrix and closes the real Ars Zero distribution verification.
- Stage 09.02 adds bounded performance observability, explicit global/per-core/entity/client work ceilings, reproducible 1/10/50-core stress and persistence evidence without introducing a second gameplay authority.
- Stage 09.03 centralizes explicit v1 → v2 migration and fail-closed recovery for all six persisted Level-1 stores, with exact legacy-value preservation, idempotent reload coverage and narrow operator recovery.
- Stage 09.05 establishes machine-readable third-party provenance, explicit first-party binary classification, fail-closed derivation/license/notice validation and a retroactive repository audit.
- Stage 09.04 adds the fail-closed Level 1 release validator/workflow, release docs, `1.0.0` metadata, current-pack compatibility baseline and final repository release gate.

Stage 09.04 final implementation HEAD `4c983331d8d9b5310376254fd3e20fad27604fab` passed both PR-head gates. PR #78 merged as `main@47189826fe03cb633d32fd8eb695f275f4aaa96f`; independent post-merge `Enshrouded CI` `34033865386 / 101488274386` and `Level 1 Release Readiness` `34033865468 / 101488268597` both completed GREEN.

**Level 1 repository milestone:** complete once this documentation checkpoint is merged and independently GREEN. The external/manual complete-pack smoke remains required before distributing the surrounding modpack.

Tasks were implemented and merged in causal order unless `STATUS.md` explicitly records a reviewed dependency change.
