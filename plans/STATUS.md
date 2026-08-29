# Project Status

Last structural update: 2026-08-29.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [x] 01 Shroud Field — all 5 tasks verified and merged.
- [x] 02 Terrain Corruption — all 4 tasks verified and merged.
- [x] 03 Exposure — all 4 tasks verified and merged.
- [ ] 04 Corrupted Ecology — Tasks 01–03 verified and merged; Task 04 pending.
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
- Final verification: workflow `33169285643`, job `98842192376` — GREEN
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

Stage 02 owns the runtime `DESTROYED -> PURIFIED` trigger; Stage 01 only defines/enforces the legal lifecycle.

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

## 02 Terrain Corruption — complete

### ✅ 04 terrain safety and protection

- Branch: `feat/02-terrain-safety`
- Final implementation HEAD: `230a89c7d6ce9def0ead75a635200418a0e6e7b9`
- PR: #15 — `02 — Terrain Safety`
- Push verification: workflow `33220960726`, job `99014693027` — GREEN
- Final PR-head verification: workflow `33222433328`, job `99019124609` — GREEN
- Merge SHA: `f398c13bac776f4f0c7b130153d69124e6970431`
- Completed file: `✅-04-terrain-safety.md`

The fail-closed `DefaultMutationAuthority` now exists before any Stage 02 block mutation sink. SAFE/AGGRESSIVE terrain tags, tri-state protection decisions, Foundation ward integration, block-entity safeguards and expert overrides are centralized behind this one authority.

`ENSH-L1-FLAME-WARD-001` remains open for Stage 03/05 completion; the Stage 02 authority side is proven. `ENSH-L1-CLAIM-SAFETY-001` remains open only until Stage 08 real FTB Chunks/MineColonies adapters prove their side of the same `ProtectedAreaService` boundary.

### ✅ 01 materialization rules

- Branch: `feat/02-materialization-rules`
- RED workflow: `33226668706` — exactly 2 regression failures for missing canonical Shroud revalidation and inert safety-class behavior.
- Final implementation HEAD: `12402efaad39d01d2a1b9c729f45c4f630015287`
- PR: #16 — `02 — Materialization Rules`
- Final PR-head verification: workflow `33227119616`, job `99032887579` — GREEN
- Merge SHA: `3a0ea59cb548d373b4b181f2c4bd2e77bd9ff925`
- Completed file: `✅-01-materialization-rules.md`

Materialization is data-driven, reversible, bounded globally/per chunk and loaded-chunk-only. Every world mutation is authorized through the merged `MutationAuthority`; queued jobs re-sample canonical `ShroudQuery` immediately before mutation; stale/unloaded/unknown/protected work fails closed; aggressive rules require `MutationSafetyMode.AGGRESSIVE`.

`ENSH-L1-CORE-TO-TERRAIN-001` is satisfied by executable Stage 01 + Stage 02 evidence: terrain materialization consumes the canonical Shroud field and does not own independent spread state.

### ✅ 02 corruption growths

- Branch: `feat/02-corruption-growths`
- RED contract commit: `f778a876d0e59b6f32e95e481cda236aa317b501`
- RED workflow: `33229310986` — Unit tests failed before bounded growth ownership was implemented in `ShroudMaterializationService`.
- Final implementation HEAD: `f77a921edffda3066b55c692918ae862d5e5a3b0`
- PR: #17 — `02 — Corruption Growths`
- Final PR-head verification: workflow `33229543824`, job `99039757162` — GREEN
- Merge SHA: `211c557f69b03bdffdfecac5f6ebaaa345f64066`
- Completed file: `✅-02-corruption-growths.md`

Growth placement is deterministic, intensity-scaled, loaded-chunk-only and bounded globally/per chunk. `GrowthPlacementService` re-samples canonical `ShroudQuery`, validates exposed support/replaceable target geometry and routes production placement through `MutationAuthority`; no independent growth-spread state exists. Common Shroud and Deadly/Red visual families, semantic tags, explicit loot behavior and original assets are present.

Exact PR-head gates passed: unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR sanity, growth-specific GameTests, Shroud SavedData plus growth-block two-boot reload and dedicated-server two-boot save/reload smoke.

### ✅ 03 purification and regression

- Branch: `feat/02-purification-regression`
- Observed TDD RED: commit `a123cf900c15e9d87835a88786fb9dae31f98504`, workflow `33229924692` — Unit tests failed before production regression existed.
- Final implementation HEAD: `e7190fc2ad7aaaaa77abb0ef9cd0bf2e04b48d54`
- Push verification: workflow `33230760825`, job `99043064368` — GREEN
- PR: #18 — `02 — Purification and Regression`
- Final PR-head verification: workflow `33230950508`, job `99043572562` — GREEN
- Merge SHA: `a3b598f9f40e4ad7e0445fb61feea639cba2533b`
- Completed file: `✅-03-purification-regression.md`

Logical regression is deterministic, bounded and frontier-to-center. Stage 02 is the sole runtime owner of the one-way `DESTROYED -> PURIFIED` transition. Visual cleanup is separately budgeted, loaded-world-only and fail-closed: unknown states, ambiguous reverse mappings, protected blocks and later player edits are never overwritten. Every restoration/removal mutation routes through `MutationAuthority(PURIFICATION)`, and visual leftovers cannot resurrect logical Shroud or keep a core out of terminal `PURIFIED`.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

No new task-local pending contract was introduced. Existing Flame Ward and claim-adapter pendings remain explicitly cross-stage.

## Stage 02 acceptance

- [x] Terrain mutation safety is centralized and fail-closed.
- [x] Logical Shroud materializes through bounded, data-driven, reversible rules.
- [x] Native corruption growths are deterministic, bounded and authority-gated.
- [x] Destroyed-core regions regress deterministically to terminal `PURIFIED` independently of best-effort visual cleanup.
- [x] Visual restoration/removal cannot overwrite protected or later player-edited state.
- [x] All four Stage 02 task PRs received exact-head GREEN runtime CI before merge.

**02 Terrain Corruption is complete.**

## 03 Exposure — complete

### ✅ 01 player exposure state

- Branch: `feat/03-player-exposure-state`
- Observed RED evidence: workflows `33244211798`, `33244734634`, `33244853207`, `33244989474` and reconnect regression workflow `33246349587`.
- Final implementation HEAD: `99cb4174e047fe0c85e351a4758a869a1d812af0`
- Push verification: workflow `33246472651` — GREEN
- PR: #22 — `03 — Player Exposure State`
- Final PR-head verification: workflow `33246650603`, job `99085167187` — GREEN
- Merge SHA: `73396291680aebe9d45e7f6d6347579d04010dd1`
- Completed file: `✅-01-player-exposure-state.md`

Player exposure is a versioned NeoForge player attachment with configurable 300-second Level-1 reserve, deterministic server-authoritative CLEAR recovery / SHROUD drain, bounded lag delta and a stable injected `DeadlyExposurePolicy`. The Level-1 Deadly fallback fails closed through a bounded emergency window. Sanctuary suppression is effective-safe without erasing latent logical severity/intensity. Respawn receives a safe baseline; reconnect/save-reload preserves unsafe reserve; sync is changed-snapshot-only and clientbound-only. Logical-client logout resets the presentation sequence epoch so reconnect cannot reject fresh authoritative packets as stale.

`ENSH-L1-CORE-TO-EXPOSURE-001` is closed: Stage 03 consumes the same canonical `DefaultShroudQuery`/`ShroudSample` field established by Stage 01 and owns no independent severity state. The Stage 03 exposure side of `ENSH-L1-FLAME-WARD-001` is proven; that contract remains open only for the Stage 05 real Sanctuary provider.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server including zone-boundary/persistence coverage, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 02 madness

- Branch: `feat/03-madness`
- Structural RED: commit `86127f1eaa47b8568cd4dce4c7c54175749500c5`, workflow `33262797262`.
- Threshold/presentation RED: commit `daf4df4c59bc32e7c9b189bba93ab66d1974f2a7`, workflow `33263055822`.
- Runtime-surface RED: commit `f051b6b69b612c38d7e0223b2b56677529c1c2e5`, workflow `33263292918`.
- Valid behavioral RED: commit `e08b713c97d9f08c25a47681de22742e3525d6c6`, workflow `33263628901` — unit/build/JAR GREEN; exactly the fatal-outcome and critical-sprint GameTests failed against the no-op runtime. The preceding `makeMockPlayer` cast failure was a fixture defect and was not counted as behavioral evidence.
- Final implementation HEAD: `c1e4ef5e00d6d9c616cf69890556563ccb6296a8`
- Push verification: workflow `33264034907`, job `99130770574` — GREEN
- PR: #23 — `03 — Madness`
- Final PR-head verification: workflow `33264262862`, job `99131401210` — GREEN
- Merge SHA: `1ac4ae537786603df87ddb252753c4402713776c`
- Completed file: `✅-02-madness.md`

Madness is a pure threshold projection of the one authoritative exposure reserve: `STABLE -> UNEASY -> DISTORTED -> CRITICAL -> FATAL`. It adds no second timer or separately persisted progression. Presentation flags are authored by the server and remain clientbound-only. `CRITICAL` applies a configurable, reversible sprint restraint without potion/attribute residue; zero reserve applies `enshrouded:madness`, bypassing ordinary armor/resistance/enchantment/cooldown mitigation with a direct server death fallback for deterministic fatal semantics if a generic damage hook absorbs the lethal hit.

GameTests prove a netherite-armored Resistance V player still dies at exhaustion and that leaving the Shroud before zero recovers the same reserve and clears the escalating sprint penalty. No new task-local cross-stage pending contract was introduced; existing Flame Ward/progression/provider boundaries remain open under their canonical owners.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 30/30 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 03 deadly shroud passage

- Branch: `feat/03-deadly-shroud`
- Structural RED: commit `76b10f9d1a45f1bbe4502228aa4c1a2cf153c22b`, workflow/job `33264854956` / `99133012859` — exactly one missing-type failure for `PassageRequirement`.
- Structural GREEN surface: commit `42828a3dbf35bdf8a316c753ad7c0728dc70872d`, workflow/job `33264949921` / `99133260628` — GREEN.
- Behavioral RED: commit `9fca15d5f87f87eed3ef843efceb1bccbd2618ba`, workflow/job `33265752520` / `99135405525` — exactly one failure proving passage level 2 was still blocked.
- Runtime/config RED: commit `097f620e70377faa3a328901244f1e9b8ed299a2`, workflow/job `33265915887` / `99135868595` — exactly two expected failures for missing required-passage config and old runtime barrier wiring.
- Final implementation HEAD: `22bcbb859ee4fa97b12a55988b9b4c4378dd763f`
- Push verification: workflow `33266195145`, job `99136603220` — GREEN
- PR: #24 — `03 — Deadly Shroud`
- Final PR-head verification: workflow `33266387083`, job `99137076697` — GREEN
- Merge SHA: `036664ea35747ae3bb16f556f1cd1dc0b1d89669`
- Completed file: `✅-03-deadly-shroud.md`

Deadly Shroud now uses the existing `DeadlyExposurePolicy` seam through `FlameGatedDeadlyExposurePolicy`. The required passage tier defaults to 2; standalone Level 1 resolves ownership through `ProgressionOwnerResolver.standalone()` and passage through `FlamePassageQuery.levelOneFallback()`, so passage remains blocked at Flame 1. Underleveled or uncertain progression fails closed into the existing configurable emergency window/rapid drain. Injected passage level 2 removes only the barrier and retains the same canonical `DEADLY` sample while using the existing exposure reserve; no second timer/schema or Stage 05 implementation dependency exists.

GameTests prove Level-1 Deadly exposure reaches zero reserve rapidly, fake passage level 2 permits the same zone data, and brief edge-dancing cannot reset the emergency window. `ENSH-L1-FLAME-PASSAGE-001` remains open for Stage 05's persistence-backed provider and Stage 08 ownership/membership semantics; the Stage 03 consumer side is now executable and verified.

Exact final PR-head gates passed: 148 unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 04 red sludge

- Branch: `feat/03-red-sludge`
- Contact RED: `bd2cca9492fc9a431888592ed66007500830e613`, workflow `33267826053`.
- DEADLY-only materialization RED: `4c16ca581c6ac51da49b5292dd71736fda1092ca`, workflow `33268123277`.
- Resource/data RED: `386382fd1b32a9004467c14e08be8d44c750c72e`, workflow `33268785863`.
- Valid damage-sink seam RED: `589bb5daeff991a4858a51fa04dbd43b6abd219c`, workflow/job `33270632993` / `99148356894` — `compileGameTestJava` failed only because the explicit bounded-damage seam did not exist yet.
- Final implementation HEAD: `fc0ef4ba3c838165c44ed60fe67a001073719503`
- Push verification: workflow/job `33270727369` / `99148609949` — GREEN
- PR: #25 — `03 — Red Sludge`
- Final PR-head verification: workflow/job `33270929411` / `99149151268` — GREEN
- Merge SHA: `6c4f54a91a8bb65657bc8a720065b8f14ce2c32c`
- Completed file: `✅-04-red-sludge.md`

Red Sludge is a physical projection of canonical `DEADLY` Shroud, never an independent logical authority. The Level-1 data rule is severity-gated and explicitly reversible through the unique `red_sand -> red_sludge -> red_sand` mapping. Moving or flowing physical sludge outside a logical region remains locally hazardous but does not write cores, regions, cells or synthetic intensity. Contact routes through the existing forced-DEADLY exposure path and emits bounded secondary vanilla-generic damage (`1.0F`, once per player/server tick). No bucket is registered in this Level-1 slice, preventing accidental remote logical semantics through transport.

GameTests prove immediate DEADLY exposure, relocated physical sludge remaining hazardous while canonical query stays CLEAR, ordinary SHROUD materialization never emitting sludge, and duplicate same-tick callbacks emitting secondary damage only once. Fixture diagnostics involving NeoForge mock-player health/network negotiation were isolated without weakening production networking or damage behavior.

No new task-local pending contract was introduced. `ENSH-L1-FLAME-PASSAGE-001` and `ENSH-L1-FLAME-WARD-001` remain open under Stage 05/08 ownership exactly as recorded in `plans/PENDING.md`.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 37 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

## Stage 03 acceptance

- [x] Player exposure is versioned, persistent and server-authoritative over the canonical Shroud query.
- [x] Madness is derived from the single exposure reserve and adds no second progression timer.
- [x] Deadly Shroud passage is fail-closed and consumes the Foundation progression boundary without Stage 05 implementation coupling.
- [x] Red Sludge is DEADLY-only physical terrain, locally hazardous but non-authoritative over logical Shroud state.
- [x] All four Stage 03 task PRs received exact-head GREEN runtime CI before merge.

**03 Exposure is complete.**

## 04 Corrupted Ecology — in progress

### ✅ 01 entity corruption

- Branch: `feat/04-entity-corruption`
- Final implementation HEAD: `a56898c32f646611a13fc296e94047438d801ac1`
- Push verification: workflow `33273816223`, job `99156933489` — GREEN
- PR: #26 — `04 — Entity Corruption State`
- Final PR-head verification: workflow `33274927748`, job `99159870992` — GREEN
- Merge SHA: `169915a6c0d16a7e4b2fe219fc109dbb8e3f6d18`
- Completed file: `✅-01-entity-corruption.md`

Entity corruption is a version-aware persistent NeoForge attachment on eligible non-player living entities. It samples the canonical `DefaultShroudQuery`, accumulates only in effective Shroud, regresses in CLEAR/Sanctuary and removes clean state without replacing the entity. Eligibility is fail-closed through `corruptible`, `immune` and `boss_excluded` tags; players remain on the Exposure system. Cow/wolf/zombie GameTests preserve original entity type, and the tamed-wolf fixture preserves owner UUID, tame and sitting state. A real two-boot server fixture proves corruption intensity/stage persist across save/restart.

No new task-local pending contract was introduced. `ENSH-L1-MAGIC-CLASSIFY-001` remains open for Stage 04 Task 03 under the existing Foundation classification boundary; Flame and claim pendings remain under their canonical later-stage owners.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData/entity-corruption two-boot reload and dedicated-server save/reload smoke.

### ✅ 02 hostility and attribute buffs

- Branch: `feat/04-hostility-buffs`
- Final implementation HEAD: `724ef6be4f2d79f1cb92646ed37e97b15fa9ee49`
- Push verification: workflow `33278518895`, job `99169481921` — GREEN
- PR: #27 — `feat: add reversible corruption hostility and buffs`
- Final PR-head verification: workflow `33278705200`, job `99170029199` — GREEN
- Merge SHA: `44f6ce548812dfef0c007d5da646f1eedeac6389`
- Completed file: `✅-02-hostility-buffs.md`

Corrupted passive/neutral mobs now receive reversible server-side player-target aggression without replacing native/external targets, while hostile mobs retain their native combat behavior. Corruption intensity drives transient max-health, attack-damage, movement-speed and knockback-resistance modifiers through stable IDs and Level-1 hard caps. Repeated synchronization is operationally idempotent, and purification removes only Enshrouded-owned targets/modifiers. Runtime wiring reuses the canonical 20-tick `EntityCorruptionRuntime` sample path, introducing no second world scan or parallel tick loop.

GameTests prove a clean passive remains non-hostile, a corrupted passive acquires an eligible survival-player target, purification releases only the Enshrouded-owned target, and a hostile mob preserves its native target while gaining bounded stats. The final runtime GameTest uses an explicit candidate seam solely because NeoForge's registered mock server-player helper reports creative/spectator eligibility inconsistently; production still discovers candidates from `ServerLevel.players()` and rejects creative, spectator and allied players.

No new task-local pending contract was introduced. `ENSH-L1-MAGIC-CLASSIFY-001` remains open for Stage 04 Task 03 under the existing Foundation magic-classification boundary.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 44 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 03 magic resistance

- Branch: `feat/04-magic-resistance`
- Structural RED: commit `8b43d4920aea75a3bf4de5c29936086eca5bad21`, workflow `33279346016` — compile failed because the planned classifier/service types did not yet exist.
- Tag-bridge RED: commit `5fd3845d`, workflow `33279693655` — exactly one GameTest proved vanilla magic did not yet flow through the canonical Enshrouded tag.
- Runtime behavioral RED: commit `92704438ca6dac089b3921660e4d574466df319b`, workflow `33279902600` — exactly the corrupted-magic reduction GameTest failed while ordinary melee already remained unchanged.
- Final implementation HEAD: `e7c8d5bd784d3f74a2fc877fe68b33dcc1e62d87`
- Push verification: workflow `33280150389`, job `99173834259` — GREEN
- PR: #28 — `04 — Magic Resistance`
- Final PR-head verification: workflow `33280436245`, job `99174583880` — GREEN
- Merge SHA: `2a7b83ef16645ed918d9e8d642471513a89d25a5`
- Completed file: `✅-03-magic-resistance.md`

Corrupted eligible creatures now receive configurable magic resistance through the single Foundation-owned classification model. `enshrouded:magic` bridges `#neoforge:is_magic`; `enshrouded:magic_bypass` is an explicit empty extension point by default. `DefaultMagicDamageClassifier` fails `UNKNOWN` closed/non-magical, and `MagicResistanceService` is the only reducer. Runtime applies once in `LivingDamageEvent.Pre`, scales the default 35% resistance by corruption intensity and hard-caps configuration at 75%, preserving physical/melee counterplay and preventing immunity.

Unit tests prove physical/magical classification, bounded one-pass resistance math, adapter-evidence convergence and fail-closed contradictory classification. GameTests prove vanilla tagged magic crosses the canonical tag boundary, a fully corrupted cow takes 2.6 damage from the same 4.0 magic hit that deals 4.0 to a clean cow, and ordinary melee remains identical. No optional magic mod is imported by the core.

`ENSH-L1-MAGIC-CLASSIFY-001` remains intentionally open only for Stage 08: PR #28 proves the standalone/core side; Ars Nouveau and Iron's Spellbooks still must prove their adapter-evidence side through the same Foundation `MagicDamageClassification` boundary without installing a second reducer.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 47 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

## Immediate next step

Create `feat/04-ecology-visuals` from the latest `main` after this documentation checkpoint.

Read `plans/04-corrupted-ecology/04-ecology-visuals.md`, the merged entity-corruption, hostility/buffs and magic-resistance runtimes, existing client/bootstrap seams, loot infrastructure and `plans/PENDING.md`. Begin with observed RED coverage before production visuals/loot/restoration code.

Task 04 owns readable corrupted-entity cues, bounded at-most-once corruption loot and purification cleanup of only Enshrouded-owned ecology state. It must preserve unrelated potion effects, third-party data and native entity identity.

Remaining canonical Stage 04 order is:

1. `feat/04-ecology-visuals`

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.
