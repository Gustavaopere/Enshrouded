# Project Status

Last structural update: 2026-08-27.

## Current checkpoint

- [x] Master planning baseline — Level 1 architecture, task decomposition, integration inventory and completion rules defined from repository base `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.
- [ ] 00 Foundation — implementation is present on `round-1-foundation`; draft PR #2 is open. Acceptance remains blocked because current GitHub Actions jobs terminate before checkout with no recorded steps, so the final unit/build/JAR/GameTest/dedicated-server gates have not executed on the current HEAD. Do not merge or rename tasks with `✅-` until those gates are GREEN.
- [ ] 01 Shroud Field — not implemented.
- [ ] 02 Terrain Corruption — not implemented.
- [ ] 03 Exposure — not implemented.
- [ ] 04 Corrupted Ecology — not implemented.
- [ ] 05 Flame Progression — not implemented.
- [ ] 06 Lich & Story — not implemented.
- [ ] 07 Client Experience — not implemented.
- [ ] 08 Integrations — not implemented.
- [ ] 09 Hardening — not implemented.

## Foundation implementation checkpoint

Branch: `round-1-foundation`

Latest audited implementation/docs checkpoint before this status commit: `0b77cb3858dd61888732b732cf9ad4fef1c20e52`.

Draft PR: #2 — `Foundation: scaffold, contracts, provenance and test infrastructure`.

Implemented scope includes:

- NeoForge 1.21.1 / Java 21 project scaffold;
- stable Enshrouded-owned domain contracts;
- upstream provenance and current-pack inventory;
- deterministic unit-test fixtures and contract tests;
- GameTest source set, valid 3×3×3 template and reusable world-level fixtures;
- `@GameTestHolder(enshrouded)` registration and namespace restriction for Foundation GameTests;
- NeoGradle `gameTestServer` configured with `setForceExit false` so successful test-server exit is observable correctly by Gradle;
- dedicated-server smoke verification path;
- current NeoForge 1.21.1 dependency metadata schema using `type="required"`;
- official Gradle 8.14 wrapper launchers and properties;
- official `gradle-wrapper.jar`, whose Git blob SHA `1b33c55baabb587c669f562ae36f953de2481846` matches the Gradle `v8.14.0` upstream blob byte-for-byte;
- CI verification commands routed through the committed `./gradlew` wrapper.

Current external verification blocker:

- push run `33093634341` on `db183f21d80ecaa88fcba33c25998350c6361759` terminated `failure` before checkout; its job has `steps=null`;
- pull-request run `33093751380` for PR #2 terminated with the same `steps=null` pattern;
- later push run `33094149126` also terminated before checkout with `steps=null`;
- removing the workflow concurrency group was separately tested and did not change the failure mode, so that experiment was reverted;
- because the runner never starts, these failures are not evidence of a failing Gradle build, unit test, GameTest or server smoke test.

Static review performed while Actions was unavailable found and corrected two independent issues before merge: the NeoGradle GameTest force-exit behavior and the legacy NeoForge dependency metadata field. These corrections still require executable final-HEAD verification.

## Immediate next step

Re-run PR #2 after GitHub Actions can allocate/initialize the job normally. If the final branch HEAD executes all verification steps GREEN, update the four Foundation task files with their actual evidence, rename them with `✅-`, update this status, mark PR #2 ready and merge it. If any code-level failure appears once the runner starts, fix that failure first. Only after the Foundation merge may `feat/01-shroud-state` be created from the resulting latest `main`.

## Level 1 release gate

Level 1 is not considered complete until every task under `00` through `09` that is marked **Level 1 required** has a verified GREEN implementation and has been renamed with the `✅-` prefix after merge. Optional flavor integrations may remain open only if `PENDING.md` explicitly records that they do not block standalone Level 1 acceptance.

## Rules for updating this file

Every merged implementation task records:

- implementation branch;
- final branch HEAD;
- PR number;
- merge SHA on `main`;
- CI workflow/run result;
- task file renamed to `✅-...` when acceptance is satisfied;
- exact blocker when acceptance is partial;
- next branch to create from the resulting `main`.
