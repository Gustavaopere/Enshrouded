# Project Status

Last structural update: 2026-08-30.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [x] 01 Shroud Field — all 5 tasks verified and merged.
- [x] 02 Terrain Corruption — all 4 tasks verified and merged.
- [x] 03 Exposure — all 4 tasks verified and merged.
- [x] 04 Corrupted Ecology — all 4 tasks verified and merged.
- [x] 05 Flame Progression — all 4 tasks verified and merged.
- [ ] 06 Lich & Story — 3/4 tasks verified and merged; 06.01 Story State, 06.02 Boss Provider and 06.03 First Manifestation complete; 06.04 Lich Skull + Flame binding is next.
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

Exact final PR-head gates passed: unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR sanity, growth-specific GameTests, Shroud SavedData plus growth-block two-boot reload and dedicated-server two-boot save/reload smoke.

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

## 04 Corrupted Ecology — complete

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

### ✅ 04 ecology visuals, loot and restoration

- Branch: `feat/04-ecology-visuals`
- Structural RED: commit `6779c7cf03507b13d6e189c0ea41238dd1553ce3`, workflow `33280996926` — `compileTestJava` failed only because the three planned completion surfaces did not exist.
- API RED: commit `0f4db80fa52498ea1603a1cd206618041ceadc1e`, workflow `33281264969` — compile failed only for the deliberately absent visual/loot/purification behavior APIs.
- Visual behavioral RED: commit `bea3615ca5606914942646d86b90e6c283d1ff15`, workflow `33281375399` — 169 unit tests ran with exactly one expected visual-state failure.
- Purification behavioral RED: commit `5fc22006459d3769c3e4180458689b903b4bd420`, workflow `33281489335` — unit/build/JAR were GREEN and 48 GameTests ran with exactly one expected purification failure.
- Visual-sync RED: commit `be8e5dfe2cb49e833ba37ee357396a1ad7b9217e`, workflow `33284672627` — `compileTestJava` failed with exactly 9 missing-symbol errors for the absent `CorruptionVisualRuntime` / attachment `STREAM_CODEC` contracts.
- Final implementation HEAD: `d6e78dec43a3bf3f7e61d84f1c4cb6d1801262fd`
- Push verification: workflow `33284776823`, job `99185955412` — GREEN
- PR: #29 — `feat: complete corrupted ecology visuals and restoration`
- Final PR-head verification: workflow `33285026758`, job `99186610687` — GREEN
- Merge SHA: `958744a2c6a41eb9d6cdc78adf593e2c79cc98d7`
- Completed file: `✅-04-ecology-visuals.md`

Corrupted fauna now exposes bounded client-only visual cues without renderer replacement or per-mob asset duplication. The existing versioned `EntityCorruptionAttachment` remains the single source of truth and is synchronized with NeoForge `AttachmentType.sync` through a tested stream codec. `CorruptionVisualRuntime` projects TAINTED/CORRUPTED intensity into bounded vanilla `WITCH` / `REVERSE_PORTAL` particles and naturally degrades on custom renderers because no renderer-specific state is required.

`EntityPurificationService` centralizes restoration: it removes only Enshrouded-owned corruption attachment, transient attributes and owned target behavior. GameTest evidence proves unrelated potion effects and third-party persistent data survive purification. The Level-1 corruption reagent policy is explicitly disabled with zero rolls because no current Level-1 recipe consumes such a reagent; this avoids orphan items and trivially prevents duplicate corruption drops.

No new task-local pending contract was introduced. Existing Flame passage/ward, Stage 08 magic-adapter and claim-safety pendings remain under their canonical owners.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 48 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.

## Stage 04 acceptance

- [x] Eligible non-player living entities preserve identity while carrying persistent canonical corruption state.
- [x] Corruption hostility and bounded attribute buffs are reversible and do not replace native/external combat ownership.
- [x] Magic resistance uses the single Foundation classification model and one reducer while physical counterplay remains unchanged.
- [x] Corrupted fauna has bounded client-readable cues derived from the canonical synced attachment without per-mob renderer duplication.
- [x] Level-1 corruption loot is explicitly bounded to zero while no recipe consumes a native reagent.
- [x] Purification removes only Enshrouded-owned ecology state and preserves unrelated effects/third-party data.
- [x] All four Stage 04 task PRs received exact-head GREEN runtime CI before merge.

**04 Corrupted Ecology is complete.**

## 05 Flame Progression — complete

### ✅ 01 persistent Flame state

- Branch: `feat/05-flame-state`
- Structural RED: commit `5ea2f951` — planned Flame persistence APIs absent.
- Core implementation checkpoint: `fbba700f`; workflow `33286153977` — GREEN.
- Runtime binding RED: commit `a73acafc` — `ProgressionRuntimeBindings` absent.
- Clean behavioral RED: commit `2b35171d1c7131a6b057a7644da4a1c254ee3f38`, workflow/job `33287106789` / `99192100243` — original 48 GameTests GREEN and only the Flame provider test failed because runtime still read the Level-1 fallback.
- Provider GREEN checkpoint: `0548849ced0b73997f9c6a48ce7f72e9ae78601f`; workflow/job `33287281198` / `99192559833` — GREEN, including 50/50 GameTests, two-boot reload and dedicated-server smoke.
- Final implementation HEAD: `6da96eeace23392816ec52ee667671f94bd3951f`
- Push verification: workflow `33287565385`, job `99193313036` — GREEN.
- PR: #30 — `05.01 — Persistent Flame progression state`.
- Final PR-head verification: workflow `33287848454`, job `99194092949` — GREEN.
- Merge SHA: `7d6d2f4b21f8839404e3689c972f68a01de68462`
- Completed file: `✅-01-flame-state.md`

Flame progression is now server-global and owner-scoped through the existing Foundation `ProgressionOwner`/resolver/query contracts. New owners read an implicit Flame Level 1 / Passage Level 1 baseline without materializing state. `FlameProgressionSavedData` persists a deterministic, version-aware owner map plus stable completed-ritual resource IDs; future unknown schemas fail closed instead of resetting progress. Ritual checkpoints are atomic/idempotent and cannot regress levels or grant the same ritual twice.

`ProgressionRuntimeBindings` keeps Stage 03 dependent only on Foundation interfaces. Stage 05 installs the persistent resolver/passage provider at server start and resets it on server stop; no Stage 03 gameplay policy or DEADLY schema was redesigned. The two Flame GameTests run in the isolated `flameState` batch so the existing Red Sludge fixture cannot mask progression behavior.

The final CI hardens restart evidence: exactly one server-global `enshrouded_flame_progression.dat` must exist, first boot must emit `ENSHROUDED_FLAME_PROGRESSION_CREATED`, second boot must emit `ENSHROUDED_FLAME_PROGRESSION_RELOADED`, and the inverse sentinels are rejected. Both boots execute 50/50 required GameTests.

`ENSH-L1-FLAME-PASSAGE-001` remains open only for Stage 08 optional FTB Teams substitution/membership semantics; the Stage 05 persistence-backed provider side is now executable and verified. `ENSH-L1-OWNER-SNAPSHOT-001` remains open because transactional ritual execution, Stage 06 encounter/reward ownership and Stage 08 membership-change behavior still need evidence.

Exact final PR-head gates passed: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 50 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 04 generic Level 1 ritual framework

- Branch: `feat/05-level1-ritual`
- TDD RED: commit `db7df85374a8065f435b8650f97c7b024eb3ebe7`, workflow `33289604543` — test compilation failed because the planned ritual framework/readiness API did not yet exist.
- Final implementation HEAD: `b3f6c0cabb3f4aa91191cf4a1197cadc68214a35`
- First complete exact-head verification: workflow `33289912453` / run #894 — GREEN.
- PR: #32 — `05.04 — Generic Flame ritual framework`.
- Final PR-head verification: workflow `33290135711` / run #895 — GREEN.
- Merge SHA: `37d100e7ea6511a76d954b640f6347bbb598d1d9`
- Completed file: `✅-04-level1-ritual.md`

Stage 05 now has a provider/UI-agnostic ritual engine through `FlameRitual`, `FlameRitualRegistry`, `FlameRitualExecutor` and `RitualOutcome`. Stable ritual IDs reject duplicates; invocation resolves `ProgressionOwner` once and retains that immutable owner through eligibility, offering validation/consumption, outcome application and persistence. Duplicate invocation is idempotently rejected before a second offering can be consumed.

Level-1 completion persists `nextLevelReady=true` while deliberately preserving Flame Level 1 and Passage Level 1. The persistence schema advanced from v1 to v2 with explicit v1 migration/default behavior for the readiness bit; invalid/future schemas remain fail-closed. Offering consumption occurs only after the resulting progression snapshot is validated under the same store lock.

A real NeoForge GameTest invokes a synthetic ritual through `FlameRitualExecutor.forServer(...)` with no Flame Altar or production Lich Skull classes, proving the server adapter seam, stable owner, single consumption, persisted checkpoint and no premature Level-2 passage. The physical altar remains Task 05.02; the authentic Lich Skull and concrete ritual binding remain Stage 06 responsibilities.

The Stage 05 ritual-execution side of `ENSH-L1-OWNER-SNAPSHOT-001` is now executable and verified. That cross-stage contract remains open only for Stage 06 encounter/reward ownership and Stage 08 FTB Teams membership-change behavior. No new hidden cross-stage dependency was introduced.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 02 physical Flame Altar

- Branch: `feat/05-flame-altar`
- Adapter RED sequence: unit tests first isolated delegation, wrong-offering rejection and duplicate-consumption semantics from Minecraft registry bootstrap; physical runtime was then added over the already-merged executor.
- Final implementation HEAD: `b9fbc27a41eddaadc3515ac47bd1fc8bd7a751d0`.
- PR #34: closed unmerged only because the connector's ready-for-review GraphQL mutation failed while the PR remained draft.
- Final replacement PR: #35 — `05.02 — Flame Altar`.
- Prior exact-head verification: workflow `33292226154` / run #962 — GREEN.
- Final PR-head verification: workflow `33292568059` / run #963 — GREEN.
- Merge SHA: `f47bcd70247acbbbb550e3af02baadc0f41eab63`.
- Completed file: `✅-02-flame-altar.md`.

`enshrouded:flame_altar` is now a physical block/block entity with one persistent server-owned offering slot, a menu/screen, Level-1 recipe, loot behavior and EN/pt-BR localization. The altar delegates all ritual eligibility, idempotence, consumption and progression mutation to the canonical `FlameRitualExecutor`; client interaction sends only an intent and server code re-reads authoritative inventory/progression state.

Runtime GameTests prove the recipe is parsed and discoverable by the real Minecraft 1.21.1 `RecipeManager`, placed altar inventory survives serialization/reload, invalid server offerings cannot be forged through a client-like activation, two physical altars cannot duplicate one ritual outcome or consume twice, and destroying the altar never rolls back earned owner progression. The real Stage 06 Lich Skull is still absent from Stage 05 production/tests by design.

The behavioral RED run exposed two fixture/data defects before merge: shaped-recipe key entries initially used the pre-1.21 string form instead of the 1.21.1 object schema, and generic GameTest mock players were not `ServerPlayer`; both were corrected before final verification. No new task-local cross-stage contract was introduced. `ENSH-L1-OWNER-SNAPSHOT-001` remains open only for Stage 06 encounter/reward ownership and Stage 08 membership-change evidence.

Exact final PR-head gates passed: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 55/55 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 03 Flame Sanctuary

- Branch: `feat/05-sanctuary`.
- Initial structural RED: commit `cae28726e875e7165aeec95d16ec0553f1d52675`, workflow #969.
- Registry/config regression: workflow #991 exposed eager SERVER-config access from worldgen registry construction; configuration-dependent mutation authority creation was made lazy.
- Runtime/fixture regression: workflow #1003 exposed missing same-tick altar activation and mock-player networking noise; production gained immediate server-side ward activation and the exposure proof was moved to the authoritative reducer boundary.
- Shared-state regression: workflow #1008 exposed cross-GameTest ward contamination from simultaneous default-batch tests.
- Fixture-isolation regression: workflow #1014 proved batch separation but exposed pre-existing Shroud state contaminating the expected source core; logical fixtures now install a single deterministic state and restore previous `ShroudSavedData` in `finally`.
- Final implementation HEAD: `89c6346b4ed74af131b434662940ad9e0e171858`.
- Draft PR #37 was closed only because the connector could not transition it to ready-for-review.
- Final replacement PR: #38 — `05.03 — Flame Sanctuary`.
- Final exact-head verification: workflow `33303582593` / run #1016 — GREEN.
- Merge SHA: `35fd0f47239646b2df84cda2989126ad432e7fd4`.
- Completed file: `✅-03-sanctuary.md`.

Sanctuary is now a dimension-local, indexed physical ward projected by loaded Flame Altars through the single Foundation `FlameWardQuery` runtime handle. Radius is SERVER-configurable (`16` default, bounded `1..128`), overlap semantics are deterministic, queries inspect only relevant chunk buckets and no chunk is force-loaded. Placement activates the ward in the same server tick; block-entity `onLoad()` rebuilds it after lifecycle/restart, and removal deactivates it.

`DefaultShroudQuery` preserves canonical latent intensity, severity and source while setting only `sanctuarySuppressed`. Exposure therefore recovers inside Sanctuary without rewriting logical Shroud. The shared `DefaultMutationAuthority` denies new `CORRUPTION` and `CORE_PLACEMENT` inside the ward while allowing safe `PURIFICATION` when all other protection checks permit it. `ShroudCoreFeature` also uses that same authority with lazy configuration construction, so no parallel mutation path was introduced.

The final GameTest suite isolates spatial ward producers by batch and isolates logical Shroud fixtures with restore-on-finally semantics. This prevents unrelated tests from making Sanctuary results order-dependent while preserving the production global runtime behavior. `ENSH-L1-FLAME-WARD-001` is now closed: Foundation, Stage 02 mutation, Stage 03 exposure and Stage 05 provider sides all have executable evidence through the same boundary.

Exact final PR-head gates passed: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR verification, 58/58 GameTests, SavedData two-boot reload GameTest and dedicated-server save/reload smoke.

## Stage 05 acceptance

- [x] Owner-scoped Flame/passage progression is version-aware, persistent and server-global.
- [x] Generic ritual execution is transactional/idempotent and resolves one stable `ProgressionOwner` snapshot per invocation.
- [x] Flame Altar is a physical server-authoritative adapter over the generic ritual engine and does not own progression truth.
- [x] Sanctuary uses the single Foundation ward boundary for exposure and mutation while preserving latent logical Shroud.
- [x] No Stage 05 production dependency on the Stage 06 Lich Skull or story implementation was introduced.
- [x] All four Stage 05 task PRs received exact-head GREEN runtime CI before merge.

**05 Flame Progression is complete.**

## 06 Lich & Story — in progress

### ✅ 01 persistent Story State

- Branch: `feat/06-story-state`.
- Initial structural RED: commit `d1ccd2bf8cb0bfb8ead2105d2c9c5d49a963c842`, workflow `33304524909` / run #1022 — `compileTestJava` failed only because the planned Story State domain/codec types did not yet exist.
- Domain/codec GREEN: HEAD `cc05f5a034ba106bb731bb7f6adb4d12a60bd4b4`, workflow `33304707537` / run #1029 — all gates GREEN.
- SavedData compile RED: commit `98ecdbc2703db6a2f912ca274406f384e429605c`, workflow `33304953122` / run #1031 — existing unit/build/JAR were GREEN; `compileGameTestJava` failed only because `StorySavedData` had not yet been implemented.
- Behavioral reboot RED: HEAD `85bdace450bcfec9fe63db7583cc509c8df27db6`, workflow `33305045499` / run #1032 — first boot passed and persisted Story State; second boot failed because the orphaned encounter remained `ACTIVE`.
- Recovery GREEN: HEAD `4e110095810035f1440a681dab586cbd069cde6e`, workflow `33305215278` / run #1034 — startup recovery and two-boot reconciliation GREEN.
- Final implementation HEAD: `057b074fd1476778d748cfe53b943ed25fbf8a1b`.
- PR: #41 — `06.01 — Persistent Lich Story State`.
- Final push verification: workflow `33305648279` / run #1036 — GREEN.
- Final exact PR-head verification: workflow `33305649672` / run #1037 — GREEN.
- Merge SHA: `77552a3d7f089a47908c109f5f8c19aff8a0f97d`.
- Completed file: `✅-01-story-state.md`.

Story State is server-global and stored in Overworld `DataStorage` as one version-aware `enshrouded_story.dat`. Its deterministic codec persists the Foundation `ProgressionOwner` stable key directly for PLAYER/TEAM/WORLD, manifestation progress, stable encounter UUID, explicit outcome, reward-issued flag and the transient physical actor UUID only while an encounter is `ACTIVE`. Unknown or pre-versioned/future schemas fail closed rather than resetting story/reward state.

The state machine is one-way and idempotent: `AVAILABLE -> ACTIVE -> DEFEATED|ABORTED`; only `DEFEATED` can become reward-issued. Duplicate encounter IDs, duplicate defeat/reward and concurrent open encounters for one owner fail closed. Server startup reconciles any persisted `ACTIVE` encounter whose tracked living actor is missing/dead to `ABORTED`, preserving the immutable owner/encounter identity and never manufacturing a victory or reward.

The real two-boot harness now proves exactly one server-global `enshrouded_story.dat`, first-boot creation and second-boot reconciliation/reload. PR-head parallel execution also exposed a pre-existing Red Sludge relocation fixture that assumed shared `ShroudSavedData` was CLEAR; that test was isolated with schema-preserving temporary state and restore-on-finally, with no Story or Red Sludge production behavior changed.

`ENSH-L1-OWNER-SNAPSHOT-001` remains open because 06.03 still must prove active encounter start/defeat/reward uses one immutable owner snapshot and Stage 08 must prove membership-change behavior. `ENSH-L1-LICH-REWARD-001` remains owned by 06.04. No new cross-stage blocker was introduced.

Exact final PR-head gates passed: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR verification, GameTest server, Story/Flame/Shroud two-boot reload and dedicated-server save/reload smoke.

### ✅ 02 Boss Provider Abstraction

- Branch: `feat/06-boss-provider`.
- Native entity API compatibility was proven against NeoForge/Minecraft 1.21.1; workflow #1065 / `33319620016` closed the registry-builder correction GREEN.
- Combat RED sequence required the real inherited ranged-AI path plus a health-driven second phase; fixture diagnostics were separated from product behavior before GREEN.
- Combat GREEN: HEAD `b275080df62af2a0452ef5f45563dd42cb9d316d`, workflow `33320345243` / run #1069 — all gates GREEN, including the native ranged Shroud attack, Darkness rider, phase transition, two-boot reload and dedicated-server smoke.
- Provider-neutral bossbar structural RED: commit `516af955dfdce9f58f2219aaf8c450969b4ad427`, workflow `33320592151` / run #1070 — `compileGameTestJava` failed only because `bossEvent()` / `syncBossEvent()` did not yet exist.
- Bossbar GREEN: HEAD `cef36cd69fe8dbe85b0df7b4150a523ed76fdcc6`, workflow `33320744336` / run #1071 — all gates GREEN.
- Final implementation HEAD: `8fe1d56083e76135d272e1e8dd3763db321fccf6`.
- PR: #43 — `06.02 — Boss Provider Abstraction`.
- Final push verification: workflow `33326923926` / run #1074 — GREEN.
- Final exact PR-head verification: workflow `33327173318` / run #1075 — GREEN.
- Merge SHA: `b45f164c6f3c29cf380e89e64ba19df3e2386c35`.
- Completed file: `✅-02-boss-provider.md`.

Provider selection is deterministic by priority/availability with stable ID ordering and a mandatory standalone native fallback. The selected actor remains strongly typed as `LivingEntity`, is validated as live, same-level and actually added before acceptance, and receives the stable encounter UUID without Story State mutation. Invalid, removed or unavailable preferred providers fall through to the native provider rather than corrupting story state.

The native `enshrouded:shroud_lich` is persistent, physically beatable, excluded from Stage 04 ecology corruption, explicitly armed for ranged Skeleton AI, and supplies Enshrouded-owned Shroud ranged damage plus Darkness. At or below half health it transitions to a faster second combat phase. All providers feed the same director-owned `ServerBossEvent`; the bossbar mirrors the accepted living actor but owns no narrative transition. Client rendering is isolated through `EntityRenderersEvent.RegisterRenderers` and uses the vanilla Skeleton renderer, with EN/pt-BR entity localization; exact dedicated-server smoke proves that client registration remains server-safe.

06.02 deliberately does not own active encounter creation, duplicate encounter prevention, immutable owner capture at encounter start, narrative defeat or reward issuance. Those remain 06.03 responsibilities. `ENSH-L1-OWNER-SNAPSHOT-001` therefore remains open for 06.03 + Stage 08, and `ENSH-L1-LICH-REWARD-001` remains open for the first-manifestation reward path / 06.04 skull binding. No new hidden cross-stage blocker was introduced.

Exact final PR-head gates passed: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

### ✅ 03 First Manifestation Encounter

- Branch: `feat/06-first-manifestation`.
- Structural encounter RED: commit `f392f7849f34fea23bb9b275538a9e8402713b1d`, workflow `33331097136` — GameTest compilation failed before `ManifestationEncounterService` existed.
- Actor-manifestation identity RED: commit `294766ecaaf10a04e19ffc46a0f2d93a9c722887` — unit/build/JAR were GREEN and the GameTest failed because the accepted physical actor did not yet carry a manifestation marker.
- Actor identity GREEN checkpoint: HEAD `0dbc8658246e5b1c44b7fa7755b2322df9c29c3a` — full CI GREEN.
- Runtime-arena RED: commit `27aca162e2afb67d28a0c5aef3c5946b08fc6279` — unit/build/JAR were GREEN and the GameTest proved the real encounter was not yet composed into the authoritative Exposure query.
- Shared-fixture diagnosis separated pre-existing stronger persistent Shroud from the temporary encounter overlay; the runtime fixture now installs/restores deterministic `ShroudSavedData` and proves the arena does not persist Shroud.
- Arena/runtime GREEN checkpoint: HEAD `bfc96b82c6ad25de3404ee869626a59a4c34a7ab`, workflow `33336703751` — full gate GREEN.
- Canceled-death RED: commit `ad35290d2949b3f311b0aeaaa60bfd7ae4b84278` — unit/build/JAR were GREEN and the GameTest gate failed before cancellation-safe death routing existed.
- Final implementation HEAD: `908aeb3cc6a623596bc79b35133100d75a22e3ec`.
- Final push verification: workflow `33337899786` / run #1122 — GREEN.
- Draft PR #45 was closed unmerged only because the connector's ready-for-review GraphQL mutation failed on GitHub schema field `Repository.fullDatabaseId`.
- Final replacement PR: #46 — `06.03 — First Manifestation Encounter`.
- Final exact PR-head verification: workflow `33338185074` / run #1124 — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.
- Merge SHA: `5811d8f29189ed35e6608157ee5e39975bf8cbe9`.
- Completed file: `✅-03-first-manifestation.md`.

The first recurring-Lich body now starts only through explicit server-side encounter orchestration. `ManifestationEncounterService` resolves one canonical `ProgressionOwner`, persists that immutable owner with one stable encounter UUID and rejects another concurrent open encounter for the same owner. The accepted provider actor is independently marked with both encounter UUID and manifestation ID, while external/native entity identity never becomes progression authority.

Narrative defeat accepts only the exact marked physical actor stored by the ACTIVE encounter. The defeat path reads the persisted `EncounterRecord.owner()` and never re-runs the owner resolver; a GameTest mutates resolver/team-like ownership after start and proves manifestation progress remains on the original stored owner. Unrelated actors and duplicate callbacks cannot complete the story. `LivingDeathEvent` routing runs at `EventPriority.LOWEST`, excludes canceled delivery and explicitly ignores canceled events, allowing an external provider to cancel death for a native phase transition without manufacturing a false Enshrouded victory.

The Level-1 arena is a bounded ephemeral `ShroudQuery` overlay composed into the existing Exposure sampling boundary. It may intensify ordinary local Shroud but never creates or mutates persistent cores, regions or cells; cleanup removes only the encounter overlay and exposes the unchanged underlying Shroud field. `LichPhaseDirector` supplies provider-neutral escalation derived from health/time/event pressure without replacing provider-native AI.

06.03 deliberately completes only the encounter/defeat portion of the reward chain: manifestation 1 reaches `DEFEATED`, the recurring Lich survives through the continuing phylactery/manifestation story, and `rewardIssued` remains false. The authentic skull, exactly-once reward issuance and concrete Stage 05 Flame ritual binding remain 06.04. The Stage 06 encounter/defeat side of `ENSH-L1-OWNER-SNAPSHOT-001` is now executable, but the contract remains open for 06.04 reward ownership and Stage 08 FTB Teams membership-change behavior. `ENSH-L1-LICH-REWARD-001` remains open for 06.04.

Exact final PR-head gates passed: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

## Immediate next step

Create `feat/06-lich-skull` from the latest verified `main` after this documentation checkpoint.

Read `plans/06-lich-story/README.md`, `plans/06-lich-story/04-lich-skull.md`, `plans/06-lich-story/✅-01-story-state.md`, `plans/06-lich-story/✅-02-boss-provider.md`, `plans/06-lich-story/✅-03-first-manifestation.md`, the Stage 05 generic ritual/altar contracts and `plans/PENDING.md`. Begin 06.04 with RED coverage for authentic skull identity, exactly-once encounter-scoped reward issuance through the stored owner, external-provider defeat equivalence, invalid/duplicate skull rejection at the altar and the concrete Level-1 Flame ritual checkpoint.

Stage 06 causal implementation order:

1. `✅ feat/06-story-state`
2. `✅ feat/06-boss-provider`
3. `✅ feat/06-first-manifestation`
4. `feat/06-lich-skull`

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`.