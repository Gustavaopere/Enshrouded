# Project Status

Last structural update: 2026-08-28.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined from repository base `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.
- [x] 00 Foundation — implementation and closing documentation verified on `round-1-foundation`; PR #2 is ready for merge after the closing checkpoint CI remains GREEN.
- [ ] 01 Shroud Field — next stage; create `feat/01-shroud-state` only from the merged Foundation `main`.
- [ ] 02 Terrain Corruption — not implemented.
- [ ] 03 Exposure — not implemented.
- [ ] 04 Corrupted Ecology — not implemented.
- [ ] 05 Flame Progression — not implemented.
- [ ] 06 Lich & Story — not implemented.
- [ ] 07 Client Experience — not implemented.
- [ ] 08 Integrations — not implemented.
- [ ] 09 Hardening — not implemented.

## 00 Foundation acceptance

Branch: `round-1-foundation`

PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`.

Verified implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`.

Executable acceptance: PR workflow `33165771852`, job `98830694040`, completed GREEN on a real GitHub-hosted runner through:

- shell harness prerequisites;
- official Gradle 8.14 wrapper provenance/integrity;
- `./gradlew test`;
- diff sanity;
- NeoForge build;
- production-JAR metadata/license/notices/GameTest-leak checks;
- GameTest server with real discovery/execution;
- dedicated-server two-boot save/reload harness using the same world and persisted scoreboard sentinel.

Documentation verification HEAD `94843a022171108386148f48974d3167d6ae7801` also completed the same workflow GREEN in run `33168410234`, proving the resolved Actions blocker did not recur on the documentation state immediately preceding this closing checkpoint.

Foundation-owned contracts include stable Shroud severity/sample/query boundaries, `MutationAuthority`, `FlameWardQuery`, `ProgressionOwner`, `ProgressionOwnerResolver`, `FlamePassageQuery`, magic-damage classification contracts, provider-neutral Lich manifestation contracts and explicit-origin `EncounterContext`.

Important RED -> GREEN evidence retained in history includes:

- bootstrap RED `33000394152` before the main mod class/metadata existed;
- deterministic unit-fixture RED `33040886237`;
- real GameTest discovery RED `33041461064`;
- progression owner/passage RED `c714af1db5256537ea5e8a9f89c680f2c3e32d6a` then minimal contracts `66d4e5c23771792df15b1548a1e45e835bd1b9d1` / `d9e5edc8a413a5034475914b671a4d7f13d97be5`;
- Flame ward RED `93127ff748029cd39fa33502eb704b10817308f0` then `FlameWardQuery` `787d9c36c25459e8815066b8bd03fc20ff4285aa`;
- explicit encounter-origin RED `3794568bbaebf105b070e08055e92715d30c1a3c` then removal of the unsafe origin-omitting overload in `5fb833e1b9e912819b0c1438a923dfa8ce7f0981`.

Foundation hardening includes official Gradle launchers/JAR, distribution SHA-256 pin, NeoForge 1.21.1 metadata schema, BSD-2-Clause license/notices packaging, architecture/provenance guards, Spore/Infnexus executable exclusion, GameTest source-set isolation, `setForceExit false`, `runServer` stdin forwarding + `--nogui`, bounded GNU `timeout` process supervision and the two-boot reload proof.

## Cross-stage contracts intentionally still open

These are not Foundation failures; their Foundation side is implemented and later consumers must close the other side in their own merge:

- `ENSH-L1-FLAME-PASSAGE-001` — Stage 03 consumer, Stage 05 persistence-backed implementation, optional Stage 08 FTB Teams resolver.
- `ENSH-L1-FLAME-WARD-001` — Stage 01 logical sample preservation, Stage 02 mutation veto matrix, Stage 03 effective safety, Stage 05 indexed altar-backed service.
- `ENSH-L1-OWNER-SNAPSHOT-001` — transactional owner snapshot behavior in Stages 05/06/08.
- `ENSH-L1-CORE-TO-TERRAIN-001`.
- `ENSH-L1-CORE-TO-EXPOSURE-001`.
- `ENSH-L1-LICH-REWARD-001`.
- `ENSH-L1-MAGIC-CLASSIFY-001`.
- `ENSH-L1-CLAIM-SAFETY-001`.

`plans/PENDING.md` is the canonical detailed record for these boundaries.

## Reviewed causal order for upcoming stages

- Stage 02: `terrain safety -> materialization -> corruption growths -> purification/regression`; no world-mutation branch may precede `MutationAuthority` implementation.
- Stage 03: Task 01 owns the `DeadlyExposurePolicy` seam/fail-closed fallback; Task 03 supplies the Flame-gated implementation, avoiding a cycle.
- Stage 05: `flame state -> generic ritual engine -> flame altar -> sanctuary`.
- Stage 06 owns authentic Lich Skull + concrete Level 1 ritual binding; Stage 05 never depends back on Stage 06.
- Stage 06 story state persists Foundation `ProgressionOwner` directly and remains standalone; Ars Zero-specific negative/provider tests live in Stage 08.

## Immediate next step

After PR #2 merges, verify the resulting `main` merge SHA and CI state, then create `feat/01-shroud-state` from that exact latest `main`. Do not skip the task order in `plans/README.md`.

## Level 1 release gate

Level 1 is complete only when every **Level 1 required** task under `00` through `09` has executable GREEN acceptance and has been renamed with the `✅-` prefix after merge. Optional flavor integrations may remain open only when `plans/PENDING.md` explicitly states that they do not block standalone Level 1.

## Rules for updating this file

Every merged implementation task records branch, final HEAD, PR, merge SHA, CI evidence, completed `✅-` task filename, unresolved boundary if any, and the next branch created from the resulting `main`.
