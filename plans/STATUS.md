# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [ ] 01 Shroud Field — Task 01 `Shroud state persistence` verified and merged; Tasks 02–05 remain open.
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
- PR: #2
- Final verification workflow/job: `33165771852` / `98830694040` — GREEN
- Merge SHA: `0b3c345673b81adbbc34a61505cb16200f689ba2`
- Completed files: `✅-01-build-scaffold.md`, `✅-02-domain-contracts.md`, `✅-03-upstream-provenance.md`, `✅-04-test-infrastructure.md`

## 01 Shroud Field — Task 01 merged record

- Task: `01-shroud-state-persistence`
- Implementation branch: `feat/01-shroud-state`
- Initial RED: `9e3f1a4a5295a9d749ca7edf8a0610ec98de72f0`
- Ownership-integrity RED: `e4b6b61e7bee713ba5deeb7596d620a47f403b67`
- Verified implementation HEAD: `6b8a4fbd550721b74bccc7f1b705d99c62ff054d`
- PR: #8 — `Stage 01: persist dimension-local Shroud state`
- Verification workflow/job: `33168930216` / `98841011566` — GREEN
- Verification: 43 unit tests, NeoForge build/JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server two-boot reload all GREEN.
- Merge SHA on `main`: `46250e44e119c4a7e7adb4d7c23ecf5afe5a5434`
- Completed task file: `✅-01-shroud-state-persistence.md`

Task 01 provides dimension-local sparse/versioned `ShroudSavedData`, deterministic NBT codec, persisted core/region/cell state, fail-closed future schema handling, duplicate rejection and bidirectional core↔region ownership integrity.

## Immediate next step

Create `feat/01-core-lifecycle` from the latest `main`. Read `plans/01-shroud-field/README.md` and `plans/01-shroud-field/02-core-lifecycle.md`, then begin with an observed RED before production lifecycle behavior.

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.
