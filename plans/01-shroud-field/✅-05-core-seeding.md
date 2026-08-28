# ✅ Enshrouded Plan — Core Seeding and Discovery

Merged into `main` as PR #14 after exact-head pull-request CI completed GREEN.

**Milestone:** Level 1 required.

**Goal:** place sparse Level 1 core sites into newly generated Overworld terrain and provide deterministic/admin discovery tools.

**Implemented types/services:** `ShroudCoreCandidateField`, `ShroudCoreFeature`, `ShroudCoreWorldgenRegistry`, `ShroudCoreRegistrationQueue`, `ShroudExpansionRuntime`, `ShroudCoreCommand`.

## Runtime contract delivered

- Candidate placement is deterministic from world seed/coarse candidate cells and preserves minimum spacing.
- Automatic physical core placement is worldgen-only; ordinary loading/ticking of existing chunks does not retroactively seed a core.
- Worldgen/block-entity discovery queues canonical registration for the server runtime; it does not mutate `ShroudSavedData` from the worldgen thread.
- Registration is idempotent and preserves the original core/region identity when the same physical seed is observed again.
- Automatically seeded/admin-created cores activate through the existing lifecycle and seed their initial logical Shroud cell through the canonical bounded `ShroudExpansionScheduler` path.
- A per-dimension expansion runtime continues bounded logical growth for active cores without chunk forcing or world scans.
- Configured feature, placed feature and NeoForge biome modifier data are supplied under `data/enshrouded`.
- Admin commands expose explicit create/inspect/destroy tooling without becoming normal progression mechanics.

## TDD / verification

- [x] Initial deterministic-candidate RED captured before production candidate-field implementation: `7a3445cc3b86fa3326fa4d9e215e3e73cedfa47e`.
- [x] Candidate determinism/spacing and dimension-filter coverage promoted to permanent tests.
- [x] Runtime RED observed: an automatically activated seeded core initially had no logical Shroud cell.
- [x] Runtime fix routes automatic activation into the canonical frontier scheduler rather than creating a second propagation path.
- [x] Reload regression exposed an ACTIVE persistence sentinel being legitimately mutated by runtime expansion; reload fixture was corrected to DORMANT while active-core expansion remains covered separately.
- [x] GameTests prove automatic registration/initial logical cell occurs exactly once and ordinary loaded chunks do not seed a core.
- [x] Worldgen registry/data contracts are covered by unit/bootstrap/runtime verification.
- [x] Final implementation HEAD: `f5f5c252fb6b1df02fa2812572b58265dbae6a61`.

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
- [x] Pull-request-triggered verification GREEN on exact final HEAD: workflow `33213229397`, job `98991031965`.
- [x] PR #14 merged into `main` as `9a36a6bb8cc3e1d06f47ea5981dfd70b5e5093f7`.
- [x] No unresolved task-local blocker remains.

**Acceptance:** Newly explored Overworld terrain can contain sparse discoverable Level 1 cores that register idempotently and feed the canonical expansion system without retroactive carving, world scans or chunk forcing.
