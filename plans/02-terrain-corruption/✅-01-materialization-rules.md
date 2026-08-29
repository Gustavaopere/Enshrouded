# ✅ Enshrouded Plan — Materialization Rules

Merged into `main` as PR #16 after exact-head pull-request CI completed GREEN.

**Milestone:** Level 1 required.

**Goal delivered:** data-driven reversible Shroud materialization for loaded terrain with bounded scheduling, canonical Shroud revalidation and mandatory `MutationAuthority` authorization before every world mutation.

**Implemented types/services:** `ShroudMaterializationService`, `ShroudMutationJob`, `CorruptionRule`, `CorruptionRuleRegistry`, `CorruptionSafetyClass` and supporting reload/queue/runtime components.

Stage 02 continues to reuse the Foundation-owned `MutationKind`; no duplicate mutation-kind model was introduced.

## Runtime contract delivered

- Materialization operates only on loaded positions/chunks and never forces chunk loading.
- Global and per-chunk mutation budgets bound work per tick.
- Every candidate world mutation passes through the already-merged `MutationAuthority` before block placement/replacement.
- Rules are data-driven and declare reversible conversion intent plus safety class.
- Unknown, unloaded, protected and unauthorized positions fail closed.
- Queued work re-samples canonical `ShroudQuery` immediately before mutation, so stale jobs cannot outlive the authoritative logical Shroud field.
- `CorruptionSafetyClass.AGGRESSIVE` rules remain inert unless `MutationSafetyMode.AGGRESSIVE` is active.
- Block entities, containers, portals, machines and otherwise unclassified structural blocks remain protected by the pre-existing safety authority.
- Visual materialization remains lazy and bounded rather than bulk-converting chunks.
- Terrain materialization consumes the canonical Shroud state; no independent terrain-spread state was introduced.

## TDD / verification

- [x] Codec/rule tests cover valid reversible rules and invalid unsafe definitions.
- [x] Queue tests prove global/per-chunk budgets remain bounded.
- [x] Mutation sink tests prove authority is consulted before world mutation.
- [x] Regression RED workflow `33226668706` failed exactly on missing canonical Shroud revalidation and inert safety-class behavior.
- [x] GameTests cover gradual materialization, denied/unknown blocks, unloaded chunks, stale queued work and aggressive-rule gating.
- [x] Canonical Shroud revalidation is performed immediately before mutation.
- [x] Unknown/unloaded/protected targets fail closed and do not force chunk loading.

## Merge gate

- [x] All task-specific tests GREEN on final branch HEAD.
- [x] `./gradlew test` GREEN.
- [x] Frontier benchmark baseline GREEN.
- [x] Diff sanity GREEN.
- [x] NeoForge build GREEN.
- [x] Production JAR sanity GREEN.
- [x] GameTest server GREEN.
- [x] Shroud SavedData two-boot reload GREEN.
- [x] Dedicated-server two-boot save/reload smoke GREEN.
- [x] Exact PR-head verification GREEN: workflow `33227119616`, job `99032887579`.
- [x] Final implementation HEAD: `12402efaad39d01d2a1b9c729f45c4f630015287`.
- [x] PR #16 merged into `main` as `3a0ea59cb548d373b4b181f2c4bd2e77bd9ff925`.
- [x] No unresolved task-local blocker remains.

**Acceptance:** Loaded terrain now reflects the canonical Shroud field progressively through reversible, data-driven and budgeted mutations, with every world change passing through the already-merged fail-closed authority and stale queued work unable to survive canonical Shroud withdrawal.
