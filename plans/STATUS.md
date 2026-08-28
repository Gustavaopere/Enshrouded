# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [ ] 01 Shroud Field — 4/5 tasks merged; state/persistence, core lifecycle, bounded frontier expansion and canonical zone query/client sync complete.
- [ ] 02 Terrain Corruption — not implemented.
- [ ] 03 Exposure — not implemented.
- [ ] 04 Corrupted Ecology — not implemented.
- [ ] 05 Flame Progression — not implemented.
- [ ] 06 Lich & Story — not implemented.
- [ ] 07 Client Experience — not implemented.
- [ ] 08 Integrations — not implemented.
- [ ] 09 Hardening — not implemented.

## 00 Foundation — merged record

- Implementation branch: `round-1-foundation`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`
- Final verification workflow: `33165771852`
- Final verification job: `98830694040`
- Verification result: GREEN
- Merge SHA on `main`: `0b3c345673b81adbbc34a61505cb16200f689ba2`
- Completed task files: `✅-01-build-scaffold.md`, `✅-02-domain-contracts.md`, `✅-03-upstream-provenance.md`, `✅-04-test-infrastructure.md`

### Final executable gates

- [x] Shell harness prerequisites.
- [x] Official Gradle wrapper provenance/integrity.
- [x] Gradle 8.14 wrapper execution under Java 21.
- [x] `./gradlew test` — 33/33 tests GREEN.
- [x] Diff sanity.
- [x] NeoForge build.
- [x] Production JAR sanity, including metadata/license/notices and no GameTest leakage.
- [x] GameTest server with Foundation tests discovered and GREEN.
- [x] Dedicated-server two-boot save/reload sentinel GREEN.

### Final corrections found by executable CI

- `1c221423ebb9a62c487368d358788a1596048441` removed unsupported NeoGradle 7.1.26 `setForceExit false` DSL after the first real runner failed during project evaluation.
- `0b1940012628ff0d762961cccb480dc72989455d` added the audited Ars Zero source SHA required by `ProvenanceDocumentationTest`; the subsequent final run passed all 33 tests and every downstream runtime gate.

### Foundation-owned contracts now frozen

- Shroud query/severity/sample boundaries.
- `MutationAuthority` / mutation kinds.
- `ProgressionOwner`, `ProgressionOwnerResolver`, `FlamePassageQuery`.
- `FlameWardQuery` and latent-field Sanctuary semantics.
- Magic classification boundary.
- Lich manifestation provider boundary and explicit immutable encounter origin.
- Architecture/provenance guards and excluded fungus regression protection.

Cross-stage implementation/consumption obligations remain tracked in `PENDING.md`; they do not make Foundation incomplete.

## 01 Shroud Field — progress

### ✅ 01 state/persistence

- Implementation branch: `feat/01-shroud-state`
- Initial RED: `9e3f1a4a5295a9d749ca7edf8a0610ec98de72f0`
- Ownership-integrity RED: `e4b6b61e7bee713ba5deeb7596d620a47f403b67`
- Verified implementation HEAD: `6b8a4fbd550721b74bccc7f1b705d99c62ff054d`
- PR: #8 — `Stage 01: persist dimension-local Shroud state`
- Final PR head: `3b893d9c54a4252b551f130715dfef7cc6210aab`
- Final PR-head verification workflow: `33169285643`
- Final PR-head verification job: `98842192376`
- Verification result: GREEN
- Merge SHA on `main`: `46250e44e119c4a7e7adb4d7c23ecf5afe5a5434`
- Completed task file: `✅-01-shroud-state-persistence.md`

### Executable gates on exact merged PR head

- [x] Wrapper provenance/integrity.
- [x] Unit tests.
- [x] Diff sanity.
- [x] NeoForge build and production JAR sanity.
- [x] GameTest server.
- [x] Shroud SavedData two-boot reload GameTest.
- [x] Dedicated-server two-boot save/reload smoke.

### ✅ 02 core lifecycle

- Implementation branch: `feat/01-core-lifecycle`
- Initial RED: `c0e03e6c0c5dd00adacf981bdc76da21f79bb254`
- Final implementation HEAD: `950c75b39c0c5c9c3cb2f9fc9203dc3597208647`
- PR: #10 — `Stage 01: implement Shroud core lifecycle`
- Final verification workflow: `33188789656`
- Final verification job: `98908621757`
- Verification result: GREEN
- Merge SHA on `main`: `4eac58f685592e518a6d36c24591bc498003b361`
- Completed task file: `✅-02-core-lifecycle.md`

### Core lifecycle executable gates on exact merged PR head

- [x] Wrapper provenance/integrity.
- [x] Unit tests and lifecycle/resurrection contracts.
- [x] Diff sanity.
- [x] NeoForge build and production JAR sanity.
- [x] GameTest server, including physical core lifecycle edge cases.
- [x] Shroud SavedData two-boot reload GameTest.
- [x] Dedicated-server two-boot save/reload smoke.
- [x] Server-side core radius/growth safety clamps.

Stage 02 remains the owner of the runtime `DESTROYED -> PURIFIED` trigger; Stage 01 only defines and enforces the legal lifecycle shape.

### ✅ 03 bounded frontier expansion

- Implementation branch: `feat/01-frontier-expansion`
- Initial RED: `39cf5d602baf8b20be29c42c505f6ded37e7a510`
- Review-regression RED: `d457d46d4d7b774cb26aac906e94d35ac36bb15c`
- Final implementation HEAD: `6fc270e4d5faaf35b1d52365f34ab2b318b41811`
- PR: #11 — `Stage 01: implement bounded frontier expansion`
- Final PR-head verification workflow: `33195407903`
- Final PR-head verification job: `98931223350`
- Verification result: GREEN
- Merge SHA on `main`: `bd2ae8faee2b1c69d8e1f08e314f48e71ae418c8`
- Completed task file: `✅-03-frontier-expansion.md`

### Frontier expansion executable gates on exact merged PR head

- [x] Wrapper provenance/integrity.
- [x] Unit tests, including scheduler-owned hard-radius and capacity-1 overflow regressions.
- [x] Reject-only 10k/100k frontier benchmark.
- [x] Apply-heavy `20k existing + 5k applied` performance regression benchmark.
- [x] Diff sanity.
- [x] NeoForge build and production JAR sanity.
- [x] GameTest server.
- [x] Shroud SavedData two-boot reload GameTest.
- [x] Dedicated-server two-boot save/reload smoke.

### Frontier review closeout

- Scheduler now enforces `maxInfluenceRadius` independently of `ShroudPropagationPolicy`.
- Region updates use one mutable working map per touched region/tick and freeze once, avoiding per-cell full-region copies.
- Frontier capacity remains strictly bounded; retryable overflow is reconstructed deterministically from persisted logical region state after queue exhaustion without Minecraft world/chunk scans or an unbounded auxiliary queue.
- All three Codex review findings were answered with regression-test and CI evidence before merge.

### ✅ 04 canonical zone query and client sync

- Implementation branch: `feat/01-zone-query-sync`
- Initial query/sync RED: `4c1dc0d44cc1dc9493c239cb70c96950c1051136`
- Spatial-semantics RED: `b071ba032f4cbc80741cb8f3c54eabe3783a933d`
- Sync ordering/rate-limit RED: `192402664738f4e400d1ff5442873e7ad87df39a`
- Final implementation HEAD: `844760a4cbc411b3cce2409fa99f4b20e19bddaa`
- PR: #13 — `Stage 01: implement canonical zone query and client sync`
- Push verification workflow: `33198832868`
- Push verification job: `98942849019`
- Final PR-head verification workflow: `33199383533`
- Final PR-head verification job: `98944636449`
- Verification result: GREEN
- Merge SHA on `main`: `39ed2b1a689b1560a06b6a9e961fde50c68c18a1`
- Completed task file: `✅-04-zone-query-sync.md`

### Zone query/sync executable gates on exact merged PR head

- [x] Wrapper provenance/integrity.
- [x] Unit tests for severity thresholds, deterministic overlap, latent-field Flame Ward semantics, payload ordering and sync rate limits.
- [x] Indexed local-query complexity regression benchmark.
- [x] Clientbound-only network direction guard; no Shroud serverbound/bidirectional payload registration.
- [x] Connection-scoped client sequence reset and stale payload rejection.
- [x] Frontier performance baseline.
- [x] Diff sanity.
- [x] NeoForge build and production JAR sanity.
- [x] GameTest server.
- [x] Shroud SavedData two-boot reload GameTest.
- [x] Dedicated-server two-boot save/reload smoke.

### Zone query/sync closeout

- The canonical query consumes existing logical SavedData and does not introduce a second world-state authority.
- Spatial lookup is cached/indexed per immutable logical snapshot and does not scan all world cores per sample.
- Overlap resolution is deterministic; server config owns severity thresholds.
- Sanctuary remains an effective overlay through `sanctuarySuppressed`; latent logical severity/intensity/source are preserved.
- Client synchronization is compact, clientbound-only, change-driven and rate-limited; renderer-specific presentation stays local.

## Immediate next step

Create `feat/01-core-seeding` from the latest `main`. Read `plans/01-shroud-field/05-core-seeding.md`, inspect the merged core lifecycle, frontier, query and safety/config contracts, then begin with an observed RED before production seeding code.

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.
