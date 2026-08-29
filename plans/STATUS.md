# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [x] 01 Shroud Field — all 5 tasks verified and merged.
- [ ] 02 Terrain Corruption — in progress; terrain safety and materialization verified and merged, corruption growths is next.
- [ ] 03 Exposure — not implemented.
- [ ] 04 Corrupted Ecology — not implemented.
- [ ] 05 Flame Progression — not implemented.
- [ ] 06 Lich & Story — not implemented.
- [ ] 07 Client Experience — not implemented.
- [ ] 08 Integrations — not implemented.
- [ ] 09 Hardening — not implemented.

## 00 Foundation — merged record

- Branch: `round-1-foundation`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- PR: #2
- Final verification workflow/job: `33165771852` / `98830694040`
- Result: GREEN, including 33/33 unit tests, NeoForge build, JAR sanity, GameTest server and dedicated-server two-boot save/reload.
- Merge SHA: `0b3c345673b81adbbc34a61505cb16200f689ba2`
- Completed files: `✅-01-build-scaffold.md`, `✅-02-domain-contracts.md`, `✅-03-upstream-provenance.md`, `✅-04-test-infrastructure.md`

Foundation-owned contracts now frozen include Shroud query/severity/sample boundaries, `MutationAuthority`, `ProgressionOwner`/resolver, `FlamePassageQuery`, `FlameWardQuery`, magic classification, Lich provider boundary, architecture/provenance guards and excluded-fungus regression protection.

## 01 Shroud Field — merged record

### ✅ 01 state/persistence

- Branch: `feat/01-shroud-state`
- RED checkpoints: `9e3f1a4a5295a9d749ca7edf8a0610ec98de72f0`, `e4b6b61e7bee713ba5deeb7596d620a47f403b67`
- Final implementation HEAD: `6b8a4fbd550721b74bccc7f1b705d99c62ff054d`
- PR: #8
- Final PR-head verification: workflow `33169285643`, job `98842192376` — GREEN
- Merge SHA: `46250e44e119c4a7e7adb4d7c23ecf5afe5a5434`
- Completed file: `✅-01-shroud-state-persistence.md`

### ✅ 02 core lifecycle

- Branch: `feat/01-core-lifecycle`
- Initial RED: `c0e03e6c0c5dd00adacf981bdc76da21f79bb254`
- Final implementation HEAD: `950c75b39c0c5c9c3cb2f9fc9203dc3597208647`
- PR: #10
- Final verification: workflow `33188789656`, job `98908621757` — GREEN
- Merge SHA: `4eac58f685592e518a6d36c24591bc498003b361`
- Completed file: `✅-02-core-lifecycle.md`

Stage 02 remains responsible for the runtime `DESTROYED -> PURIFIED` trigger; Stage 01 only defines/enforces the legal lifecycle.

### ✅ 03 bounded frontier expansion

- Branch: `feat/01-frontier-expansion`
- RED checkpoints: `39cf5d602baf8b20be29c42c505f6ded37e7a510`, `d457d46d4d7b774cb26aac906e94d35ac36bb15c`
- Final implementation HEAD: `6fc270e4d5faaf35b1d52365f34ab2b318b41811`
- PR: #11
- Final verification: workflow `33195407903`, job `98931223350` — GREEN
- Merge SHA: `bd2ae8faee2b1c69d8e1f08e314f48e71ae418c8`
- Completed file: `✅-03-frontier-expansion.md`

Scheduler hard-enforces `maxInfluenceRadius`, uses bounded frontier capacity, reconstructs retryable work deterministically from persisted logical state and introduces no world/chunk scan.

### ✅ 04 canonical zone query and client sync

- Branch: `feat/01-zone-query-sync`
- RED checkpoints: `4c1dc0d44cc1dc9493c239cb70c96950c1051136`, `b071ba032f4cbc80741cb8f3c54eabe3783a933d`, `192402664738f4e400d1ff5442873e7ad87df39a`
- Final implementation HEAD: `844760a4cbc411b3cce2409fa99f4b20e19bddaa`
- PR: #13
- Final PR-head verification: workflow `33199383533`, job `98944636449` — GREEN
- Merge SHA: `39ed2b1a689b1560a06b6a9e961fde50c68c18a1`
- Completed file: `✅-04-zone-query-sync.md`

Canonical query is indexed/deterministic; Sanctuary remains a latent-field overlay; synchronization is compact, clientbound-only, change-driven and rate-limited.

### ✅ 05 deterministic core seeding and discovery

- Branch: `feat/01-core-seeding`
- Initial deterministic-candidate RED: `7a3445cc3b86fa3326fa4d9e215e3e73cedfa47e`
- Runtime RED: automatic seeded core activated without an initial logical Shroud cell; fixed by routing activation through the existing frontier scheduler.
- Final implementation HEAD: `f5f5c252fb6b1df02fa2812572b58265dbae6a61`
- PR: #14 — `Stage 01: implement deterministic Shroud core seeding`
- Push verification: workflow `33212796865`, job `98989682893` — GREEN
- Final PR-head verification: workflow `33213229397`, job `98991031965` — GREEN
- Merge SHA: `9a36a6bb8cc3e1d06f47ea5981dfd70b5e5093f7`
- Completed file: `✅-05-core-seeding.md`

Exact PR-head gates passed: wrapper provenance, unit tests, frontier baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server two-boot save/reload.

Worldgen threads do not mutate SavedData directly; ordinary loaded chunks do not retroactively seed cores; registration is idempotent; active seeded cores use the canonical bounded expansion runtime without chunk forcing or world scans.

## Stage 01 acceptance

- [x] Dimension-local authoritative Shroud state persists and reloads.
- [x] Core lifecycle is explicit and idempotent.
- [x] Logical expansion is bounded and scheduler-owned.
- [x] Canonical server query and minimal read-only client sync are implemented.
- [x] Sparse deterministic new-terrain core seeding/discovery is implemented.
- [x] All five task PRs received exact-head GREEN runtime CI before merge.

**01 Shroud Field is complete.**

## 02 Terrain Corruption — in progress

### ✅ 04 terrain safety and protection

- Branch: `feat/02-terrain-safety`
- Final implementation HEAD: `230a89c7d6ce9def0ead75a635200418a0e6e7b9`
- PR: #15 — `02 — Terrain Safety`
- Push verification: workflow `33220960726`, job `99014693027` — GREEN
- Final PR-head verification: workflow `33222433328`, job `99019124609` — GREEN
- Merge SHA: `f398c13bac776f4f0c7b130153d69124e6970431`
- Completed file: `✅-04-terrain-safety.md`

The fail-closed `DefaultMutationAuthority` now exists before any Stage 02 block mutation sink. SAFE/AGGRESSIVE terrain tags, tri-state protection decisions, Foundation ward integration, block-entity safeguards and expert overrides are centralized behind this one authority.

`ENSH-L1-FLAME-WARD-001` remains open for Stage 03/05 completion; the Stage 02 authority side is proven. `ENSH-L1-CLAIM-SAFETY-001` remains open only for Stage 08 real claim adapters; the Stage 02 tri-state authority side is proven.

### ✅ 01 materialization rules

- Branch: `feat/02-materialization-rules`
- RED workflow: `33226668706` — exactly 2 regression failures for missing canonical Shroud revalidation and inert safety-class behavior.
- Final implementation HEAD: `12402efaad39d01d2a1b9c729f45c4f630015287`
- PR: #16 — `02 — Materialization Rules`
- Final PR-head verification: workflow `33227119616`, job `99032887579` — GREEN
- Merge SHA: `3a0ea59cb548d373b4b181f2c4bd2e77bd9ff925`
- Completed file: `✅-01-materialization-rules.md`

Materialization is data-driven, reversible, bounded globally/per chunk and loaded-chunk-only. Every world mutation is authorized through the merged `MutationAuthority`; queued jobs re-sample canonical `ShroudQuery` immediately before mutation; stale/unloaded/unknown/protected work fails closed; aggressive rules require `MutationSafetyMode.AGGRESSIVE`.

`ENSH-L1-CORE-TO-TERRAIN-001` is now satisfied by executable Stage 01 + Stage 02 evidence: terrain materialization consumes the canonical Shroud field and does not own independent spread state.

## Immediate next step

Create `feat/02-corruption-growths` from the latest `main`.

Read `plans/02-terrain-corruption/02-corruption-growths.md`, `plans/02-terrain-corruption/README.md`, merged materialization runtime, `DefaultMutationAuthority`, Foundation `MutationAuthority`/`MutationKind`, canonical Shroud query/ward semantics and core state. Begin with an observed RED before production growth placement code. Every growth placement/removal world mutation must route through the already-merged authority and must not create independent spread state.

Canonical Stage 02 order is:

1. `feat/02-terrain-safety`
2. `feat/02-materialization-rules`
3. `feat/02-corruption-growths`
4. `feat/02-purification-regression`

No branch may introduce a world block mutation sink before `DefaultMutationAuthority`/`ProtectedAreaService` is merged.

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.
