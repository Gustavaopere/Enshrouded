# Project Status

Last structural update: 2026-09-09.

The detailed merged-task record through Stage 08.02 is preserved in [`STATUS-HISTORY-THROUGH-08.02.md`](STATUS-HISTORY-THROUGH-08.02.md). Completed-task dossiers and `plans/PENDING.md` remain the authoritative provenance for individual Level-1 contracts. Post-Level-1 Stage 10 visual work is tracked canonically under [`10-visual-polish/STATUS.md`](10-visual-polish/STATUS.md).

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
- [x] 09 Hardening — 5/5 verified and merged; Level 1 remains a completed historical milestone.
- [~] 10 Visual Polish — explicit post-Level-1 work is active by user instruction. 10.01–10.08 technical implementation is merged. Stage 10.09 logic/authority, restart-harness correction, survival acquisition, reconciliation, the Flame shell art-handoff contract and the Shroud Core Nest production consumer are merged and verified. PR #104 recorded the Lich encounter-wiring blocker, PR #105 reconciled the merged Core Nest/Lich state, and PR #106 reconciled the detailed set-piece/Lich dossiers; all are merged and independently post-merge verified. Bounded Lich placement/worldgen-only consumption remains an allowed separate path, while encounter wiring is blocked until an explicit gameplay trigger feeding canonical `ManifestationEncounterService.start(...)` is defined/proven. Stage 10.09 remains operationally **OPEN** for the user-owned Flame shell render package, the Lich placement/material/distribution contract/consumer, and the blocked encounter-trigger/wiring contract. Stage 10.10 has not started.

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

## 09 Hardening — complete

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

### ✅ 03 World upgrade and recovery

- Implementation branch: `feat/09-world-upgrade`.
- Final implementation HEAD: `aec0f6ad227642c096478c77343cf79efed7f88a`.
- PR: #74 — `Stage 09.03 — World upgrade and recovery`.
- Exact final PR-head workflow/job: `34003017774` / `101405148483` — `completed/success` across wrapper provenance, unit tests, performance benchmark baselines, diff sanity, NeoForge build, canonical + external GameTest compilation, JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `0d2b07f4cc3bf60627acab60237f087a1e102b58`.
- Independent post-merge `main` workflow/job: `34003313686` / `101405934457` — `completed/success` across the same complete gate set.
- Post-merge verified implementation `main`: `0d2b07f4cc3bf60627acab60237f087a1e102b58`.
- Completed file: `✅-03-world-upgrade.md`.
- Automated review P2 for omitted `ShroudDiscoverySavedData` migration coverage: corrected in final HEAD and review thread resolved.
- New cross-stage pending contracts: none.

Stage 09.03 centralizes explicit persistence evolution for all six persisted Level-1 stores: Shroud, Shroud Discovery, Exposure, Entity Corruption, Flame Progression and Story. Supported schema v1 migrates deterministically to v2 while preserving identity/progression/reward state; schema 0/pre-versioned, missing-schema, malformed representative data and unknown future schemas fail closed rather than silently resetting world state. Migration + reload coverage protects core/ritual/reward idempotence, and operator recovery remains narrowly scoped without force-loading chunks or bypassing canonical gameplay authorities.

### ✅ 05 Third-party licenses and provenance

- Implementation branch: `feat/09-third-party-provenance`.
- Final implementation HEAD: `bd80ad8618ace2fe923d0cd7e3575fd8c6e2b1cc`.
- PR: #76 — `Stage 09.05 — Third-party licenses and provenance`.
- Exact final PR-head workflow/job: `34009516524` / `101422757081` — `completed/success` across the complete gate set, including the new third-party provenance contract.
- Implementation merge SHA: `94fabffc807b34b4066fcbf7cec5411ed58a35fa`.
- Independent post-merge `main` workflow/job: `34009936498` / `101423866082` — `completed/success` across provenance contract tests, wrapper verification, unit tests, performance baselines, diff sanity, NeoForge build, canonical + external GameTest compilation, production-JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Post-merge verified implementation `main`: `94fabffc807b34b4066fcbf7cec5411ed58a35fa`.
- Completed file: `✅-05-third-party-licenses-provenance.md`.
- Automated review: 2 P1 + 3 P2 findings corrected with RED→GREEN evidence; all five review threads resolved before merge.
- New cross-stage pending contracts: none.

Stage 09.05 establishes a machine-readable provenance ledger and makes release acceptance fail closed for unresolved copied/derived/vendored material. First-party binary resources are enumerated explicitly; source derivation and required notices are reconciled bidirectionally; material immutable refs must be genuinely pinned; unregistered distributable binaries and new direct integration/provider directories without a provenance decision fail validation. The retroactive audit found no copied/derived/vendored third-party production material in the current tree, and installed/runtime compatibility does not become implicit source-reuse permission.

### ✅ 04 Level 1 release checklist

- Implementation branch: `feat/09-release-checklist`.
- Initial RED HEAD: `877532b3eb9f6de944f2178aa0ea82a539c59700`.
- Initial RED workflow/job: `34030838375` / `101479911663` — expected failure because the release validator did not yet exist.
- Final implementation HEAD: `4c983331d8d9b5310376254fd3e20fad27604fab`.
- PR: #78 — `Stage 09.04 — Level 1 release checklist and fail-closed release gate`.
- Exact final PR-head Level 1 Release Readiness workflow/job: `34031623079` / `101482073832` — `completed/success` across 21 provenance tests, executable provenance validation, 8 release-contract tests and repository release validation.
- Exact final PR-head Enshrouded CI workflow/job: `34031623105` / `101482073811` — `completed/success` across wrapper provenance, unit tests, performance baselines, diff sanity, NeoForge build, GameTest compilation, production-JAR verification, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Implementation merge/main SHA: `47189826fe03cb633d32fd8eb695f275f4aaa96f`.
- Independent post-merge Enshrouded CI workflow/job: `34033865386` / `101488274386` — `completed/success` across the complete runtime/build gate set.
- Independent post-merge Level 1 Release Readiness workflow/job: `34033865468` / `101488268597` — `completed/success` across provenance and release-readiness contracts.
- Completed file: `✅-04-release-checklist.md`.
- Automated review P1 #1: compatibility-table parsing was corrected by validating provider name/version on the same logical line and using a real Markdown-table fixture before implementation merge.
- Closeout review P1 #2: the release validator originally did not require its own `✅-04-release-checklist.md`; RED head `329ce91ce9d1b116762080fbc39e1091b371079e` failed workflow/job `34035071072` / `101491523337`, then corrected head `fff7471e16aa95384f37b941ddab16322f5bbcd0` passed release-readiness `34035319418` / `101492195887` and full CI `34035319414` / `101492222107`.
- Documentation closeout PR: #79 — `Stage 09.04 — Close Level 1 release checkpoint`.
- No public NeoForge `ModConfigSpec`/`registerConfig` surface exists in the current Level 1 implementation.
- New cross-stage pending contracts: none.

Stage 09.04 establishes Enshrouded `1.0.0` release metadata, an independent fail-closed release-readiness workflow, final release/checklist notes, language-key parity validation and the then-current 607-entry compatibility profile. The final release validator requires all five Hardening closeouts, including 09.04 itself. It does not claim a literal 607-JAR CI boot because the complete pack distribution is not stored in the repository. `MANUAL_CURRENT_PACK_SMOKE_REQUIRED` remains an explicit external distribution gate before shipping the surrounding modpack.

## Stage 10 — explicit post-Level-1 work — active

Stage 10 began only after explicit user instruction and therefore does not contradict the historical rule against automatically starting future work. It is post-Level-1 visual/presentation work and does not reopen completed Level-1 gameplay authorities.

Current verified repository baseline is `main@5b4c64b3cb8152db7fc04711e0306f4b6ce83f3e`, after PR #106 (`Stage 10.09 — Reconcile set-piece and Lich blocker dossiers`).

- [x] Stage 10.01–10.08 technical implementation is merged; detailed provenance remains in `plans/10-visual-polish/STATUS.md` and the individual dossiers.
- [x] Stage 10.09 original logic/authority implementation PR #97 merged as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`.
- [x] Stage 10.09 restart-harness correction PR #99 is integrated in the current lineage at `650c5c99308415a94ed51c9abd9abea1b5b26c10`.
- [x] Stage 10.09 Flame shell survival-acquisition correction PR #100 final HEAD `2b647d9e4dde39f4205b5082eca1ba9481b1e7d6` passed Release Readiness `34225536910` and Enshrouded CI `34225536909 / 102058621586`, then merged as `ed122c42f0eee0e706219361e30c9e1f05416a6d`.
- [x] Independent post-merge #100 Release Readiness `34226166892` and Enshrouded CI `34226166913 / 102060710827` passed the complete matrix on exact `main@ed122c42f0eee0e706219361e30c9e1f05416a6d`.
- [x] Stage 10.09 reconciliation PR #98 branch `docs/10-09-multiblock-set-pieces-closeout`, final HEAD `43bf73df5d4c3c93a731ee5d88397b5633badd1c`, passed Release Readiness `34231676426` and Enshrouded CI `34231676553 / 102079337614`, including GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution and dedicated-server save/reload.
- [x] PR #98 merged as `fa9552daa0372bf5061708a93ce5c263257df5d2`. Independent post-merge Release Readiness `34234079530` and Enshrouded CI `34234079616 / 102087197147` passed the complete matrix on exact `main@fa9552daa0372bf5061708a93ce5c263257df5d2`.
- [x] PR #102 merged as `0b0486bf0fe0266e7bf96b7cca3be0157afdc6e1`, defining the manual/user-authored Flame shell art handoff without fabricating placeholder art or `ART APPROVED`.
- [ ] User-owned Flame shell render handoff remains open: brace/rune blockstates, block/item models, textures and visibly distinct formed/unformed presentation.
- [x] Shroud Core Nest production consumer PR #103 final HEAD `7b7d01e94e6fe8037a579a812a2c8a723e834fc6` passed exact-head Release Readiness `34355655800` and Enshrouded CI `34355655793`, then merged as `c4b555cb4f0d199bed2ebef80b7cd06b306913c0`. Independent post-merge Release Readiness `34368796815` and Enshrouded CI `34368796770` both passed on exact `main@c4b555cb4f0d199bed2ebef80b7cd06b306913c0`.
- [x] Lich landmark encounter-wiring audit PR #104 final HEAD `3b96578850aa23b5eae3df76aa95e5171398faf2` passed exact-head Release Readiness `34369500109` and Enshrouded CI `34369500120`, then merged as `9e1b4a77d1d24e2c00ebaec5160458ffbe1184c3`. Independent post-merge Release Readiness `34370682361` and Enshrouded CI `34370682102` both passed on exact `main@9e1b4a77d1d24e2c00ebaec5160458ffbe1184c3`.
- [x] Merged-state reconciliation PR #105 final HEAD `b471165c4d6155908e11998fea36b9c823901a5b` passed exact-head Release Readiness `34372629522` and Enshrouded CI `34372629554`, then squash-merged as `005b195fc058234be159e37018f57ebf77731f62`. Independent post-merge Release Readiness `34373476741` and Enshrouded CI `34373476952` both passed on exact `main@005b195fc058234be159e37018f57ebf77731f62`.
- [x] Set-piece/Lich dossier reconciliation PR #106 final HEAD `a2625eb269f1b18d57f3c97a7fbf7d68f318c644` passed exact-head Release Readiness `34378100839` and Enshrouded CI `34378100843`, then squash-merged as `5b4c64b3cb8152db7fc04711e0306f4b6ce83f3e`. Independent post-merge Release Readiness `34379035099` and Enshrouded CI `34379035208` both passed on exact `main@5b4c64b3cb8152db7fc04711e0306f4b6ce83f3e`.
- [ ] Lich manifestation landmark placement/worldgen-only consumer remains open and permitted, but its origin/distribution, role-to-material mapping, protection/mutation boundary, idempotence and lifecycle contract must be explicit before implementation; placement alone must not start an encounter.
- [ ] Lich manifestation encounter-location/wiring remains blocked. `ManifestationEncounterService.start(...)` is the canonical Stage 06 start boundary, but the audited production runtime still does not define the gameplay trigger that decides when normal play invokes it. Do not invent proximity, Shroud/Core, interaction, item, ritual, login/tick, command or worldgen-time trigger semantics.
- [x] A separate Purification Shrine/controller remains intentionally deferred; Sanctuary/purification retains the existing canonical Flame complex/ward/purification authority.
- [ ] Stage 10.10 has **not** started. It is the final visual/compatibility QA gate and must not silently absorb missing 10.09 implementation/design or user-art work.

The currently attached physical modlist was rechecked on 2026-09-09 and is the current pack authority: **595 mods** on NeoForge `21.1.248`. Older 602/603/607/612 counts in historical Stage 09/Stage 10 evidence are preserved only as snapshots of those earlier checkpoints and do not override the current physical file.

## Open cross-stage contracts

- No Level-1 cross-stage contract currently remains open in `plans/PENDING.md`.
- Stage 10 operational handoffs are intentionally tracked in the Stage 10 status/dossiers instead of being misrepresented as old Level-1 pending contracts.

## Immediate next step

Keep Stage 10.09 open. The next safe implementation work is to define/approve the bounded Lich landmark placement-only contract (origin/distribution, role-to-material mapping, protection/mutation boundary, idempotence/lifecycle) before writing a production consumer. Encounter wiring remains separately blocked until the explicit gameplay trigger contract feeding canonical `ManifestationEncounterService.start(...)` exists. The user-authored Flame shell render package and `ART APPROVED` also remain open. Do **not** start Stage 10.10 automatically.

## Level 1 release gate — historical completed milestone

The repository Level 1 milestone was completed and independently verified before Stage 10 began. The earlier `main@47189826fe03cb633d32fd8eb695f275f4aaa96f` entry above is the Stage 09 implementation checkpoint, not the current repository HEAD. Subsequent documentation closeout and post-Level-1 Stage 10 work do not invalidate that completed milestone.

The surrounding pack's current Stage 10 reconciliation baseline is **595 mods** and still requires the explicitly documented external/manual full-pack smoke before modpack distribution. That distribution smoke is not represented as completed GitHub Actions evidence.

## Rules for updating this file

Every merged task records branch, final HEAD, PR, merge SHA, CI result, completed task filename, unresolved cross-stage contracts and the next canonical task. Historical verbose records are retained in status-history snapshots or completed-task dossiers.