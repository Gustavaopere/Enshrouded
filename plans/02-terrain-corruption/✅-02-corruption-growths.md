# ✅ Enshrouded Plan — Corruption Growths

Merged into `main` as PR #17 after exact-head pull-request CI completed GREEN.

**Milestone:** Level 1 required.

**Goal delivered:** original ordinary and Deadly/Red Shroud surface-growth families now materialize as bounded, deterministic visual projections of the canonical logical Shroud field without replacing every underlying base block or creating a second spread system.

**Implemented types/services:** `ShroudGrowthBlock`, `ShroudVeinBlock`, `WitheredGrowthBlock`, `GrowthCandidateSampler`, `GrowthPlacementService` and bounded growth work integrated into `ShroudMaterializationService`.

## Runtime contract delivered

- Growth placement is loaded-chunk-only and never forces chunk loading.
- Candidate density scales with logical Shroud intensity through deterministic sampling.
- Placement requires an exposed sturdy support face and a replaceable target.
- Canonical `ShroudQuery` is re-sampled at placement time; clear or sanctuary-suppressed cells cannot materialize growths.
- Every support/target world mutation decision routes through the merged `MutationAuthority`; `GROWTH_PLACEMENT` is a centralized mutation kind rather than a parallel safety path.
- Growth work is ephemeral, bounded by queue capacity and global/per-chunk attempt budgets, and does not persist independent spread state.
- Ordinary Shroud exposes common `shroud_growth` and `shroud_vein` visuals; `withered_growth` is gated behind `ShroudSeverity.DEADLY`.
- Growth blocks are explicit non-colliding, non-occluding, replaceable decorative blocks, so decorative veins do not accidentally suffocate normal entities.
- Semantic block tags cover the complete growth family and the Deadly-only subset.
- Loot tables are explicit and empty because these decorative blocks do not register collectible BlockItems in Level 1.
- Automated removal is intentionally not introduced here; Stage 02 purification owns gradual growth cleanup and must route it through the same `MutationAuthority`.

## TDD / verification

- [x] Deterministic candidate sampling and density bounds are unit-tested.
- [x] Mutation-safety tests prove `GROWTH_PLACEMENT` is threat-introducing, requires a replaceable target and is denied for protected/warded/unsafe targets.
- [x] RED contract commit `f778a876d0e59b6f32e95e481cda236aa317b501` required bounded growth ownership from `ShroudMaterializationService`.
- [x] RED workflow `33229310986` failed at Unit tests before the production integration existed.
- [x] GameTests prove authorized exposed placement, invalid-face rejection, authority veto, clear-cell rejection and Deadly severity gating.
- [x] GameTests prove global/per-chunk growth work remains bounded.
- [x] The two-boot reload harness proves an actual `shroud_growth` block survives save/restart in the reused GameTest world.
- [x] Static/diff review confirmed no independent growth-spread state or unrelated stage code entered the PR.

## Merge gate

- [x] All task-specific tests GREEN on final branch HEAD.
- [x] `./gradlew test` GREEN.
- [x] Frontier benchmark baseline GREEN.
- [x] Diff sanity GREEN.
- [x] NeoForge build GREEN.
- [x] Production JAR sanity GREEN.
- [x] GameTest server GREEN.
- [x] Shroud SavedData + growth-block two-boot reload GREEN.
- [x] Dedicated-server two-boot save/reload smoke GREEN.
- [x] Exact PR-head verification GREEN: workflow `33229543824`, job `99039757162`.
- [x] Final implementation HEAD: `f77a921edffda3066b55c692918ae862d5e5a3b0`.
- [x] PR #17 merged into `main` as `211c557f69b03bdffdfecac5f6ebaaa345f64066`.
- [x] No unresolved task-local blocker was introduced; `plans/PENDING.md` requires no growth-specific addition.

**Acceptance:** Ordinary and Deadly Shroud are visually legible through native surface growths, placement remains deterministic and budgeted, every production placement mutation is safety-authorized, persistence is verified across restart and growths cannot become an independent spread mechanism.
