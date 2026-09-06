# ✅ Enshrouded — Level 1 Release Checklist

**Stage:** 09.04 — Hardening

**Disposition:** implemented, reviewed, merged and independently post-merge verified at the implementation checkpoint.

## Goal

Perform the final code/content/license/config/release validation and record an exact Level 1 implementation checkpoint without inventing unsupported full-pack CI evidence.

## Implemented scope

- Added `docs/release/level1-checklist.md` and `docs/release/level1-release-notes.md`.
- Recorded the Level 1 release metadata as Enshrouded `1.0.0` for Minecraft `1.21.1`, NeoForge `21.1.248` and Java 21.
- Added `scripts/verify_level1_release.py` with fail-closed repository validation and a dedicated contract-test suite.
- Added the independent `Level 1 Release Readiness` GitHub Actions workflow.
- Made the release workflow self-contained by running the existing Stage 09.05 provenance contract tests and executable provenance validator before the Level 1 release validator.
- Added `docs/compat/current-pack-2026-09-06.md` as the current compatibility profile reconciled against the user-supplied 607-entry modlist.
- Reconciled public README/provider/version statements with the current merged implementation and pack baseline.
- Validated both `en_us.json` and `pt_br.json` parse and carry identical key sets.
- Confirmed the current Level 1 implementation exports no public NeoForge `ModConfigSpec`/`registerConfig` surface; there is therefore no generated Enshrouded config file whose defaults can drift independently.
- Kept production resource/provenance acceptance behind the Stage 09.05 fail-closed ledger and binary-resource classification gate.
- Kept production-JAR inspection authoritative for required `LICENSE` and `THIRD_PARTY_NOTICES.md` packaging.
- Preserved the no-Spore/Infnexus integration boundary.
- Hardened the final release gate so the Stage 09.04 closeout file itself is mandatory; deleting `plans/09-hardening/✅-04-release-checklist.md` now fails release-readiness.

## Current-pack boundary

The repository does not vendor the complete 607 third-party JAR distribution. GitHub Actions therefore does **not** claim a literal full-pack boot.

`MANUAL_CURRENT_PACK_SMOKE_REQUIRED` remains an explicit external distribution gate: before distributing the surrounding modpack, boot the same recorded 2026-09-06 pack profile and verify startup, world load, Enshrouded registration and no immediate provider conflict. This does not weaken the repository release gate and is not represented as an automated result.

Repository acceptance remains executable through standalone/unit coverage, canonical GameTests, real two-boot persistence reload, isolated real Ars Zero 2.0.2 distribution co-load and dedicated-server save/reload smoke.

## TDD and review hardening

- Initial contract commit: `21b404811422d86fb142dcaa2d2f0dc87645f220`.
- Initial RED HEAD: `877532b3eb9f6de944f2178aa0ea82a539c59700`.
- Initial RED workflow/job: `34030838375` / `101479911663` — expected failure because the Level 1 release validator did not yet exist.
- The new workflow then exposed two wiring/path mistakes while being made self-contained; both were diagnosed as workflow path errors rather than provenance-contract failures and corrected without weakening the provenance gate.
- Automated review P1 #1: the compatibility validator searched for contiguous `Name Version` strings while the real profile stores name/version in Markdown table cells. The fixture had hidden the mismatch.
- The P1 #1 fix changed validation to require provider name and version on the same logical line regardless of Markdown separators and changed the fixture to the real table format.
- Corrected final implementation HEAD: `4c983331d8d9b5310376254fd3e20fad27604fab`.
- Exact final PR-head release-readiness workflow/job: `34031623079` / `101482073832` — `completed/success` across 21 provenance tests, executable provenance validation, 8 Level 1 release-contract tests and repository release-readiness validation.
- Exact final PR-head Enshrouded CI workflow/job: `34031623105` / `101482073811` — `completed/success` across wrapper provenance, unit tests, performance baselines, diff sanity, NeoForge build, GameTest compilation, production-JAR verification, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Closeout review P1 #2: the release validator required 09.01, 09.02, 09.03 and 09.05 closeouts but did not require the newly completed `✅-04-release-checklist.md`, so Stage 09 could be claimed 5/5 while the final closeout was absent.
- P1 #2 RED HEAD: `329ce91ce9d1b116762080fbc39e1091b371079e`.
- P1 #2 RED workflow/job: `34035071072` / `101491523337` — expected failure in the new `test_missing_stage_09_04_closeout_fails_closed` regression test.
- P1 #2 corrected head: `fff7471e16aa95384f37b941ddab16322f5bbcd0`.
- P1 #2 GREEN release-readiness workflow/job: `34035319418` / `101492195887` — `completed/success`, including the new Stage 09.04-closeout negative case.
- P1 #2 GREEN Enshrouded CI workflow/job: `34035319414` / `101492222107` — `completed/success` across the complete runtime/build gate set.

## Merge and independent verification

- Base before implementation: `main@24b7df890777eccca7148b27b4e5dc0a028abc34`.
- Implementation branch: `feat/09-release-checklist`.
- PR: #78 — `Stage 09.04 — Level 1 release checklist and fail-closed release gate`.
- Final implementation HEAD: `4c983331d8d9b5310376254fd3e20fad27604fab`.
- Implementation merge/main SHA: `47189826fe03cb633d32fd8eb695f275f4aaa96f`.
- Independent post-merge Enshrouded CI workflow/job: `34033865386` / `101488274386` — `completed/success` across the complete runtime/build gate set.
- Independent post-merge Level 1 Release Readiness workflow/job: `34033865468` / `101488268597` — `completed/success` across provenance and release-readiness contracts.
- Documentation closeout PR: #79 — `Stage 09.04 — Close Level 1 release checkpoint`.
- New cross-stage pending contracts: none.

## Acceptance checklist

- [x] Hardening closeouts 09.01, 09.02, 09.03, 09.04 and 09.05 are required by the final release validator.
- [x] `plans/PENDING.md` contains no open cross-stage contract.
- [x] Release metadata is `1.0.0` / Minecraft 1.21.1 / NeoForge 21.1.248 / Java 21.
- [x] Release-readiness validation fails closed on missing Hardening closeouts, release docs, current-pack profile, language parity, required metadata and explicit blocker markers.
- [x] Stage 09.05 provenance validation remains part of the release gate.
- [x] No supported production integration with Spore/Infnexus exists.
- [x] No unresolved copied/derived/vendored material is accepted by the provenance gate.
- [x] `en_us` and `pt_br` key parity is enforced.
- [x] No unreviewed public NeoForge config surface exists in the current Level 1 implementation.
- [x] Production JAR integrity is GREEN and required license/notices are packaged.
- [x] Unit tests, performance baselines, NeoForge build, GameTests, two-boot persistence, real Ars Zero profile and dedicated-server smoke are GREEN on the final implementation HEAD.
- [x] Independent post-merge `main` runtime/build CI is GREEN.
- [x] Independent post-merge `main` release-readiness is GREEN.
- [x] Both automated-review P1 findings are corrected by regression coverage; P1 #2 has explicit RED→GREEN closeout evidence.
- [x] The complete 607-JAR pack smoke is documented honestly as an external/manual distribution gate rather than fabricated CI evidence.

**Acceptance:** Stage 09.04 implementation is verified and merged at `main@47189826fe03cb633d32fd8eb695f275f4aaa96f`. With this documentation closeout merged and independently GREEN, Stage 09 is 5/5 and the repository Level 1 milestone is complete. The surrounding modpack still requires the explicitly documented external/manual full-pack smoke before pack distribution.