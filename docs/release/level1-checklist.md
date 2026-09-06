# Enshrouded Level 1 release checklist

## Release target

- Enshrouded `1.0.0`.
- Minecraft `1.21.1`.
- NeoForge 1.21.1 (`21.1.248`).
- Java 21.
- License: BSD-2-Clause.
- Recorded implementation release checkpoint: `main@47189826fe03cb633d32fd8eb695f275f4aaa96f`.
- No unverified release tag is invented by this checklist.

## Prerequisite hardening

All prerequisite closeouts exist and are merged:

- [x] 09.01 — Level 1 test matrix and real Ars Zero co-load.
- [x] 09.02 — performance budgets/observability.
- [x] 09.03 — world upgrade and recovery.
- [x] 09.05 — third-party licenses and provenance.

## Automated release gates

The repository release gate passed all of the following on the final implementation HEAD and independently on merged `main`:

- [x] Gradle wrapper provenance and wrapper validation.
- [x] Third-party provenance contract tests and fail-closed repository provenance validation.
- [x] Level 1 release-readiness contract tests and fail-closed repository release validation.
- [x] Unit tests.
- [x] Performance benchmark baselines.
- [x] Diff sanity.
- [x] NeoForge build.
- [x] Canonical and external GameTest source-set compilation.
- [x] Production-JAR integrity, including `LICENSE` and `THIRD_PARTY_NOTICES.md`.
- [x] Standalone canonical GameTests.
- [x] SavedData two-boot restart/reload GameTests.
- [x] Isolated real Ars Zero 2.0.2 distribution profile.
- [x] Dedicated-server save/reload smoke.

Final implementation HEAD `4c983331d8d9b5310376254fd3e20fad27604fab` passed PR-head `Level 1 Release Readiness` workflow/job `34031623079 / 101482073832` and `Enshrouded CI` workflow/job `34031623105 / 101482073811`.

PR #78 merged as `47189826fe03cb633d32fd8eb695f275f4aaa96f`. Independent push validation on that exact `main` passed `Enshrouded CI` `34033865386 / 101488274386` and `Level 1 Release Readiness` `34033865468 / 101488268597`.

## Current-pack profile

The current compatibility baseline is `docs/compat/current-pack-2026-09-06.md`, reconciled against the user-supplied 607-entry modlist. It pins the versions relevant to Enshrouded release decisions and preserves the distinction between installed compatibility and permission to reuse source/assets.

**MANUAL_CURRENT_PACK_SMOKE_REQUIRED**

The repository does not contain the complete 607-JAR pack distribution, so GitHub Actions cannot honestly claim a literal boot of all 607 mods. Before distributing the surrounding modpack, perform an external/manual boot using the same 2026-09-06 modlist profile and verify startup, world load, Enshrouded registration and no immediate provider conflict. This external pack smoke is a distribution check; it does not replace the repository's automated standalone/provider/dedicated-server gates.

## Config, language and resources

- [x] No public NeoForge `ModConfigSpec`/`registerConfig` surface exists in the current Level 1 code, so there is no generated Enshrouded config file whose defaults can drift independently. Gameplay defaults remain code/data-owned and regression-tested.
- [x] `en_us.json` and `pt_br.json` parse and have identical key sets under the release validator.
- [x] Production resources remain subject to the first-party/third-party binary classification gate from 09.05.
- [x] The built JAR contains project license and third-party notices.

## Persistence and breaking-change review

- [x] Stage 09.03 remains the canonical world-upgrade/recovery contract.
- [x] Supported v1 persisted stores migrate deterministically to v2; malformed, pre-versioned/unsupported and unknown-future schema states fail closed instead of silently resetting progression.
- [x] Core/ritual/reward idempotence and SavedData reload behavior remain covered by CI.
- Any future schema or provider-authority change requires a new reviewed migration/release decision rather than silent compatibility assumptions.

## Optional integrations

- Optional providers are integrations, not standalone runtime requirements.
- Enshrouded remains authority for Shroud, Exposure, Flame, Story/rewards and mutation/protection decisions owned by its canonical boundaries.
- Ars Zero may provide the preferred Lich body but not narrative/reward authority.
- JourneyMap remains presentation-only.
- Goety/Malum/Eidolon remain intentional Level 1 no-op/flavour boundaries.
- Spore/Infnexus are not supported Enshrouded integrations.

## Blockers

- [x] No unresolved P0/P1 release blocker is recorded in `plans/PENDING.md`.
- [x] The only automated review P1 in Stage 09.04 — real Markdown compatibility-table parsing — was corrected and regression-tested before merge.
- Any future unresolved legal/provenance decision, failed release-readiness validation, failed required CI gate, unknown authority regression or unsupported persistence migration blocks a later release.

## Acceptance

- [x] Stage 09.04 implementation PR-head `Enshrouded CI` is GREEN.
- [x] Stage 09.04 implementation PR-head `Level 1 Release Readiness` is GREEN.
- [x] Implementation PR #78 is merged.
- [x] Independent post-merge `main` `Enshrouded CI` is GREEN.
- [x] Independent post-merge `main` `Level 1 Release Readiness` is GREEN.
- [x] Stage 09.04 documentation closeout is prepared with the exact evidence above.

With this closeout merged and independently GREEN, Stage 09 is 5/5 and the repository Level 1 milestone is complete. The surrounding full modpack still requires `MANUAL_CURRENT_PACK_SMOKE_REQUIRED` before pack distribution.
