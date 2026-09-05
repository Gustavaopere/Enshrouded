# Project Status

Last structural update: 2026-09-05.

The detailed merged-task record through Stage 08.02 is preserved in [`STATUS-HISTORY-THROUGH-08.02.md`](STATUS-HISTORY-THROUGH-08.02.md). Completed-task dossiers and `plans/PENDING.md` remain the authoritative provenance for individual contracts.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [x] 01 Shroud Field — 5/5 verified and merged.
- [x] 02 Terrain Corruption — 4/4 verified and merged.
- [x] 03 Exposure — 4/4 verified and merged.
- [x] 04 Corrupted Ecology — 4/4 verified and merged.
- [x] 05 Flame Progression — 4/4 verified and merged.
- [x] 06 Lich & Story — 4/4 verified and merged.
- [x] 07 Client Experience — 4/4 verified and merged.
- [x] 08 Integrations — 5/5 reviewed, verified and merged.
- [ ] 09 Hardening — 2/5 verified and merged.

## 08 Integrations — complete

### ✅ 01 Ars Zero Lich provider

- Branch: `feat/08-ars-zero`.
- Final implementation HEAD: `77f1b285e7ba1c2c0290c31f95873559fb599010`.
- PR: #59 — `Stage 08.01: integrate Ars Zero Lich provider`.
- Exact PR-head workflow/job: `33508994529` / `99859813891` — `completed/success`.
- Merge SHA: `95b0189ca4421b688294a6cee2b9f06983159790`.
- Post-merge `main` workflow/job: `33509695387` / `99862094519` — `completed/success`.
- Completed file: `✅-01-ars-zero.md`.
- Former verification limitation `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` is closed by Stage 09.01; full provenance is recorded in `plans/PENDING.md` and `plans/09-hardening/✅-01-test-matrix.md`.

### ✅ 02 Ars Nouveau + Iron's magic classification

- Branch: `feat/08-magic-systems`.
- Final implementation HEAD: `85ba070753031d6b8e41351b2cdc5055a34d47d7`.
- PR: #61 — `Stage 08.02: integrate Ars Nouveau and Iron's magic classification`.
- Exact PR-head workflow/job: `33539127624` / `99960767541` — `completed/success`.
- Merge SHA: `916ccf16c10fc521c89475f7cbd67e6efbe81751`.
- Post-merge `main` workflow/job: `33539723280` / `99962689287` — `completed/success`.
- Completed file: `✅-02-magic-systems.md`.
- Closed cross-stage contract: `ENSH-L1-MAGIC-CLASSIFY-001`.

### ✅ 03 Epic Fight / FTB Teams / FTB Chunks / MineColonies

- Branch: `feat/08-combat-claims-teams`.
- Final implementation HEAD: `054eed2ab50f0ac755d98748d948510d977d6ad9`.
- PR: #64 — `Stage 08.03: integrate teams, claims, colonies and Epic Fight`.
- Exact PR-head workflow/job: `33577174521` / `100083590564` — `completed/success` across unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Merge SHA: `7e119ee893801edf3e3ed3856030da78c97edfd1`.
- Post-merge `main` workflow/job: `33580200835` / `100092676457` — `completed/success`.
- Completed file: `✅-03-combat-claims-teams.md`.
- Closed cross-stage contracts: `ENSH-L1-FLAME-PASSAGE-001`, `ENSH-L1-OWNER-SNAPSHOT-001`, `ENSH-L1-CLAIM-SAFETY-001`.

FTB Teams remains an opt-in substitution of the Foundation `ProgressionOwnerResolver`; no progression is silently migrated. FTB Chunks and MineColonies feed the canonical fail-closed protection boundary without global scans. Epic Fight owns no second damage hook or reducer.

### ✅ 04 JourneyMap discovered-core markers

- Branch: `feat/08-journeymap`.
- Final implementation HEAD: `b82f43af85f56c89cec51f2de972acd32f70a3e4`.
- PR: #66 — `Stage 08.04: JourneyMap discovered-core markers`.
- Exact PR-head workflow/job: `33650126425` / `100314955596` — `completed/success` across the complete gate set.
- Implementation merge SHA: `a9450a9e773d9e15c1f8e2cd96b6b783d4bb9ef6`.
- Post-merge `main` workflow/job: `33650826014` / `100317085835` — `completed/success`.
- Documentation closeout PR: #67.
- Final Stage 08.04 closeout/main SHA: `03db94044b903628e51808de18a93134be9ad300`.
- Closeout post-merge workflow/job: `33654513354` / `100329595997` — `completed/success`.
- Completed file: `✅-04-journeymap.md`.
- No new cross-stage pending contract was introduced.

JourneyMap 6.0.7 remains a soft client presentation target through API `2.0.0-1.21.1`; Enshrouded owns discovery authority and sends only authorized owner-scoped snapshots. Undiscovered core coordinates are never globally exposed.

### ✅ 05 Goety / Malum / Eidolon flavor — intentional no-op

- Provider review: Goety 3.1.4, Malum 1.8.2 and Eidolon: Repraised 0.5.0.2.
- Approved result: no adapters, provider dependencies, conditional recipes, conditional loot or runtime hooks are justified for Level 1.
- Decision branch: `feat/08-necromancy-flavor`.
- Final decision HEAD: `0a7aa14709bc370f6b8b85ea779eee3e10cc18f9`.
- PR: #68 — `Stage 08.05: close necromancy flavor as intentional no-op`.
- Exact PR-head workflow/job: `33665328518` / `100365540768` — `completed/success` across the complete gate set.
- Decision merge SHA: `737834816e7fac5b10284e1484536a6f3e5f5a3e`.
- Independent post-merge `main` workflow/job: `33665970274` / `100367685054` — `completed/success` across the complete gate set.
- Completed file: `✅-05-necromancy-flavor.md`.
- Production Java changes: none.
- Provider-specific datapack changes: none.
- Build/runtime dependency changes: none.
- New cross-stage pending contracts: none.

Goety souls/rituals/summons, Malum spirit arcana and Eidolon occult/ritual resources retain their native authorities. They do not become Enshrouded progression gates, currencies, offering substitutes or Shroud/Flame state. The authentic Enshrouded Lich skull remains the canonical Level-1 ritual offering.

## Stage 08 causal order

1. `✅ feat/08-ars-zero`
2. `✅ feat/08-magic-systems`
3. `✅ feat/08-combat-claims-teams`
4. `✅ feat/08-journeymap`
5. `✅ feat/08-necromancy-flavor` — intentional no-op after value review

## 09 Hardening — in progress

### ✅ 01 Level 1 test matrix

- Implementation branch: `feat/09-test-matrix`.
- Final implementation HEAD: `2db41c3569409beec11a6509c2c39fbfc7810a83`.
- PR: #70 — `Stage 09.01 — Level 1 test matrix and real Ars Zero co-load`.
- Exact PR-head workflow/job: `33986822592` / `101361842946` — `completed/success` across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, canonical + external GameTest compilation, JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `6e957bd0592723cc4849f2a4606222ad564c2aa4`.
- Independent post-merge `main` workflow/job: `33989419851` / `101368854913` — `completed/success` across the same complete gate set.
- Post-merge verified `main`: `6e957bd0592723cc4849f2a4606222ad564c2aa4`.
- Completed file: `✅-01-test-matrix.md`.
- Closed cross-stage contract: `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001`.

Stage 09.01 provides the canonical standalone Level-1 vertical scenario, real two-boot mid-expansion/mid-exposure restart evidence and an isolated real-distribution Ars Zero 2.0.2 co-load profile. Optional provider JARs remain fixture-only and do not leak into the production dependency/runtime surface.

### ✅ 02 Performance and budgets

- Implementation branch: `feat/09-performance`.
- Final implementation HEAD: `224fe45b644990af9e636151e13b7192f7f9c9d4`.
- PR: #72 — `Stage 09.02 — Performance hardening and Level 1 baseline`.
- Exact final PR-head workflow/job: `33998728399` / `101393675566` — `completed/success` across wrapper provenance, unit tests, performance benchmark baselines, diff sanity, NeoForge build, canonical + external GameTest compilation, JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `aac30dadcf0c31c1cfddba5ad66e8df281e33923`.
- Independent post-merge `main` workflow/job: `33999268064` / `101395101323` — `completed/success` across the same complete gate set.
- Post-merge verified implementation `main`: `aac30dadcf0c31c1cfddba5ad66e8df281e33923`.
- Completed file: `✅-02-performance.md`.
- New cross-stage pending contracts: none.

Stage 09.02 adds passive observability plus explicit bounded-work evidence without creating a second gameplay authority. Logical Shroud expansion is capped globally and per core; entity-corruption sampling is UUID-staggered and globally admitted before canonical queries; materialization/restoration remain loaded-chunk-only and bounded; networking/client effects remain rate/sample capped. The committed Level-1 baseline stresses 1/10/50-core scheduler loads, entity reducer work and representative persistence, while timing observations remain explicitly non-portable and are not production TPS/MSPT guarantees. The only automated review P2 — paired performance counters splitting across drain windows — was corrected in final HEAD `224fe45b...` and protected by regression coverage before merge.

## Open cross-stage contracts

- No cross-stage contract currently remains open in `plans/PENDING.md`.

## Immediate next step

Stage 09.03 (`plans/09-hardening/03-world-upgrade.md`) is the next canonical task. **Do not start it automatically.** Begin it only when explicitly requested from the then-current verified `main`.

## Level 1 release gate

Level 1 is not complete. Stage 09.03 World Upgrade, Stage 09.05 Third-Party Licenses/Provenance and Stage 09.04 Release Checklist remain in causal order. Every Level-1-required Stage 09 task must reach verified GREEN implementation and receive its `✅-` closeout before release completion can be declared.

## Rules for updating this file

Every merged task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next canonical task. Historical verbose records are retained in status-history snapshots or completed-task dossiers.