# Project Status

Last structural update: 2026-09-02.

The detailed merged-task record through Stage 08.02 is preserved verbatim in [`STATUS-HISTORY-THROUGH-08.02.md`](STATUS-HISTORY-THROUGH-08.02.md). This file is the compact operational checkpoint used to determine the next task; completed-task dossiers and `plans/PENDING.md` remain the authoritative provenance for individual contracts.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined.
- [x] 00 Foundation — verified and merged.
- [x] 01 Shroud Field — all 5 tasks verified and merged.
- [x] 02 Terrain Corruption — all 4 tasks verified and merged.
- [x] 03 Exposure — all 4 tasks verified and merged.
- [x] 04 Corrupted Ecology — all 4 tasks verified and merged.
- [x] 05 Flame Progression — all 4 tasks verified and merged.
- [x] 06 Lich & Story — all 4 tasks verified and merged.
- [x] 07 Client Experience — all 4 tasks verified and merged.
- [ ] 08 Integrations — 4/5 tasks verified and merged.
- [ ] 09 Hardening — not implemented.

## Completed stage summary

- **00 Foundation:** complete. Detailed merged record archived in `STATUS-HISTORY-THROUGH-08.02.md`.
- **01 Shroud Field:** complete, 5/5 tasks.
- **02 Terrain Corruption:** complete, 4/4 tasks.
- **03 Exposure:** complete, 4/4 tasks.
- **04 Corrupted Ecology:** complete, 4/4 tasks.
- **05 Flame Progression:** complete, 4/4 tasks.
- **06 Lich & Story:** complete, 4/4 tasks.
- **07 Client Experience:** complete, 4/4 tasks.

## 08 Integrations — in progress

### ✅ 01 Ars Zero Lich provider

- Branch: `feat/08-ars-zero`.
- Final implementation HEAD: `77f1b285e7ba1c2c0290c31f95873559fb599010`.
- PR: #59 — `Stage 08.01: integrate Ars Zero Lich provider`.
- Final exact PR-head verification: workflow/job `33508994529` / `99859813891` — `completed/success`.
- Implementation merge SHA: `95b0189ca4421b688294a6cee2b9f06983159790`.
- Independent post-merge `main` workflow/job: `33509695387` / `99862094519` — `completed/success`.
- Completed file: `✅-01-ars-zero.md`.
- Open verification follow-up: `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` remains intentionally open because the actual Ars Zero 2.0.2 distribution is not co-loaded by repository CI.

### ✅ 02 Ars Nouveau + Iron's magic classification

- Branch: `feat/08-magic-systems`.
- Structural RED contract HEAD: `deab1aa1e777a73b599b53c5391bce79c3d394ff`; rerun workflow/job `33537531739` / `99961988664` failed at `:compileTestJava` on the deliberately absent adapter surface.
- Final implementation HEAD: `85ba070753031d6b8e41351b2cdc5055a34d47d7`.
- PR: #61 — `Stage 08.02: integrate Ars Nouveau and Iron's magic classification`.
- Final exact PR-head verification: workflow/job `33539127624` / `99960767541` — `completed/success` across the full gate set.
- Implementation merge SHA: `916ccf16c10fc521c89475f7cbd67e6efbe81751`.
- Independent post-merge `main` workflow/job: `33539723280` / `99962689287` — `completed/success`.
- Completed file: `✅-02-magic-systems.md`.
- Closed cross-stage contract: `ENSH-L1-MAGIC-CLASSIFY-001`.

Stage 08.02 preserves the Stage 04 tag-only baseline and adds optional evidence composition without a second damage reducer or event authority. The detailed implementation narrative remains in `✅-02-magic-systems.md` and the archived status record.

### ✅ 03 Epic Fight / FTB Teams / FTB Chunks / MineColonies

- Branch: `feat/08-combat-claims-teams`.
- Initial RED: `438cbbb37549c9fe1ca2b4ef11311aae01fd66fa`, workflow/job `33570064559` / `100061866347`.
- Bootstrap RED: `e9d270a3ede4e01c23cb057f4ccad069212cb31c`, workflow/job `33571053152` / `100064875675`.
- Provider RED: corrected contract head `a6e93768078afc5d41badc932880da3046e1dfce`, provider contract commit `2fb773c65be59b5b9a796b5d43153dab05394417`, workflow/job `33572510900` / `100069311087`.
- Provider GREEN checkpoint: `cad48de528ea5965ae813dc55f20d3b68af32f68`; push workflow/job `33572991500` / `100070765342`; PR-head workflow/job `33572995664` / `100070788707`.
- Final review RED: `469d6d625c53334107cd5d48ecd8df33df4b43e3`, PR workflow/job `33576732210` / `100082231972` — production compiled and test compilation failed only for the deliberately absent runtime-protection composition helper.
- Final implementation HEAD: `054eed2ab50f0ac755d98748d948510d977d6ad9`.
- Final push verification: workflow `33577170535` — `completed/success`.
- PR: #64 — `Stage 08.03: integrate teams, claims, colonies and Epic Fight`.
- Final exact PR-head verification: workflow/job `33577174521` / `100083590564` — `completed/success` across unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Implementation merge SHA: `7e119ee893801edf3e3ed3856030da78c97edfd1`.
- Independent post-merge `main` workflow/job: `33580200835` / `100092676457` — `completed/success` across the same complete gate set.
- Completed file: `✅-03-combat-claims-teams.md`.
- Closed cross-stage contracts: `ENSH-L1-FLAME-PASSAGE-001`, `ENSH-L1-OWNER-SNAPSHOT-001`, `ENSH-L1-CLAIM-SAFETY-001`.

FTB Teams is an opt-in substitution of the Foundation `ProgressionOwnerResolver`; `integrations.ftbTeamsSharedProgression` defaults false, personal teams remain PLAYER owners, shared/party teams produce stable TEAM owners, and membership changes affect only future resolutions. Existing player progression is never silently migrated or merged, while Stage 05/06 operation snapshots remain immutable in flight.

FTB Chunks and MineColonies feed the canonical Stage 02 tri-state `ProtectedAreaService`. FTB Chunks uses indexed `ClaimedChunkManager.getChunk(ChunkDimPos)` and MineColonies uses `IColonyManager.getColonyByPosFromWorld(Level, BlockPos)`; neither performs a global claim/colony scan. Missing providers add no uncertainty, while loaded API mismatch/query failure becomes bounded-diagnostic `INDETERMINATE` and therefore fails closed by default.

Final review proved that config-created mutation authorities could otherwise bypass runtime claims when callers explicitly supplied `ProtectedAreaService.none()`. The merged fix centrally composes every explicit protection service with `ProtectionRuntimeBindings`, covering worldgen and purification without adding a second mutation authority. Epic Fight remains compatibility/presence-only and owns no second damage hook or reducer. Provider class/method handles are detected once and cached outside hot loops.

### ✅ 04 JourneyMap discovered-core markers

- Branch: `feat/08-journeymap`.
- Representative runtime RED: `ac9e7b967b42b803d8af5d1ce7e21b8e0d41bc29`; workflow/job `33634842638` / `100263003296` — production/test compilation succeeded and only the deliberately absent runtime contract failed.
- JourneyMap client-boundary RED: `4c9f459272e6c59fa90ddf631144c2e68c441c3f`; workflow/job `33635654507` / `100266056178` — 301 tests ran and only the new integration contract failed.
- Adapter smoke GREEN: `e18aeb571ba1e02d7ecd620689177c77ddc203db`; workflow/job `33636971843` / `100270513234` — `completed/success`.
- Final implementation HEAD: `b82f43af85f56c89cec51f2de972acd32f70a3e4`.
- PR: #66 — `Stage 08.04: JourneyMap discovered-core markers`.
- Final exact PR-head verification: workflow/job `33650126425` / `100314955596` — `completed/success` across unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.
- Implementation merge SHA: `a9450a9e773d9e15c1f8e2cd96b6b783d4bb9ef6`.
- Independent post-merge `main` workflow/job: `33650826014` / `100317085835` — `completed/success` across the same complete gate set.
- Completed file: `✅-04-journeymap.md`.
- No new cross-stage pending contract was introduced.

JourneyMap runtime `6.0.7` is a client-only presentation target through pinned API `2.0.0-1.21.1` as `compileOnly`. Enshrouded owns all discovery authority: the server reuses the exact canonical `ShroudSample.sourceId()` already sampled for presentation, performs direct core-id lookup, persists knowledge per `ProgressionOwner`, and sends complete authorized snapshots. There is no global undiscovered-core scan, no JourneyMap-backed progression state and no second Shroud query authority.

Known lifecycle is explicit: ACTIVE markers are visible, DESTROYED cores remain known but hidden, and PURIFIED cores become visible again. Owner changes replace the full snapshot rather than migrating knowledge. JourneyMap waypoints are transient (`persistent=false`) and reconciled only from the authorized client snapshot. GameTests and dedicated-server save/reload run without JourneyMap runtime and remained GREEN, proving optional-mod isolation.

## Immediate next step

Stage 08.05 (`05-necromancy-flavor.md`, Goety/Malum/Eidolon flavor) is the next task in the canonical Stage 08 order. **Do not start it automatically.** Begin it only when explicitly requested from the then-current verified `main`.

Stage 08 causal implementation order:

1. `✅ feat/08-ars-zero`
2. `✅ feat/08-magic-systems`
3. `✅ feat/08-combat-claims-teams`
4. `✅ feat/08-journeymap`
5. `pending feat/08-necromancy-flavor`

## Open cross-stage contracts

- `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` — open exactly as recorded in `plans/PENDING.md`.
- No Stage 08.03 ownership/protection contract remains open.
- No Stage 08.04 discovery/JourneyMap contract remains open.

## Level 1 release gate

Level 1 is not complete until every Level-1-required task under stages 01–09 has verified GREEN implementation and is renamed with the `✅-` prefix after merge.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next branch to create from the resulting `main`. Historical verbose records are retained in immutable status-history snapshots when this operational checkpoint is compacted.