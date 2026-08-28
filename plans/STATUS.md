# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [ ] 01 Shroud Field — 2/5 tasks merged; state/persistence and core lifecycle complete.
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

## Immediate next step

Create `feat/01-frontier-expansion` from the latest `main`. Read `plans/01-shroud-field/03-frontier-expansion.md`, inspect the merged state/core lifecycle contracts, then begin with an observed RED before production frontier scheduling/expansion code.

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.
