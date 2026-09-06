# Enshrouded Level 1 release checklist

## Release target

- Enshrouded `1.0.0`.
- Minecraft `1.21.1`.
- NeoForge 1.21.1 (`21.1.248`).
- Java 21.
- License: BSD-2-Clause.
- Release identity is the exact recorded Git HEAD that passes the implementation PR CI and the independent post-merge `main` CI; Stage 09.04 does not invent an unverified tag.

## Prerequisite hardening

The following prerequisite closeouts must already exist and remain green before this checklist can pass:

- 09.01 — Level 1 test matrix and real Ars Zero co-load.
- 09.02 — performance budgets/observability.
- 09.03 — world upgrade and recovery.
- 09.05 — third-party licenses and provenance.

## Automated release gates

The repository release gate must pass all of the following on the exact candidate HEAD:

- Gradle wrapper provenance and wrapper validation.
- Third-party provenance contract tests and fail-closed repository provenance validation.
- Level 1 release-readiness contract tests and fail-closed repository release validation.
- Unit tests.
- Performance benchmark baselines.
- Diff sanity.
- NeoForge build.
- Canonical and external GameTest source-set compilation.
- Production-JAR integrity, including `LICENSE` and `THIRD_PARTY_NOTICES.md`.
- Standalone canonical GameTests.
- SavedData two-boot restart/reload GameTests.
- Isolated real Ars Zero 2.0.2 distribution profile.
- Dedicated-server save/reload smoke.

## Current-pack profile

The current compatibility baseline is `docs/compat/current-pack-2026-09-06.md`, reconciled against the user-supplied 607-entry modlist. It pins the versions relevant to Enshrouded release decisions and preserves the distinction between installed compatibility and permission to reuse source/assets.

**MANUAL_CURRENT_PACK_SMOKE_REQUIRED**

The repository does not contain the complete 607-JAR pack distribution, so GitHub Actions cannot honestly claim a literal boot of all 607 mods. Before distributing the surrounding modpack, perform an external/manual boot using the same 2026-09-06 modlist profile and verify startup, world load, Enshrouded registration and no immediate provider conflict. This external pack smoke is a distribution check; it does not replace the repository's automated standalone/provider/dedicated-server gates.

## Config, language and resources

- No public NeoForge `ModConfigSpec`/`registerConfig` surface exists in the current Level 1 code, so there is no generated Enshrouded config file whose defaults can drift independently. Gameplay defaults remain code/data-owned and regression-tested.
- `en_us.json` and `pt_br.json` must both parse and have identical key sets.
- Production resources remain subject to the first-party/third-party binary classification gate from 09.05.
- The built JAR must contain project license and third-party notices.

## Persistence and breaking-change review

- Stage 09.03 is the canonical world-upgrade/recovery contract.
- Supported v1 persisted stores migrate deterministically to v2; malformed, pre-versioned/unsupported and unknown-future schema states fail closed instead of silently resetting progression.
- Core/ritual/reward idempotence and SavedData reload behavior remain covered by CI.
- Any future schema or provider-authority change requires a new reviewed migration/release decision rather than silent compatibility assumptions.

## Optional integrations

- Optional providers are integrations, not standalone runtime requirements.
- Enshrouded remains authority for Shroud, Exposure, Flame, Story/rewards and mutation/protection decisions owned by its canonical boundaries.
- Ars Zero may provide the preferred Lich body but not narrative/reward authority.
- JourneyMap remains presentation-only.
- Goety/Malum/Eidolon remain intentional Level 1 no-op/flavour boundaries.
- Spore/Infnexus are not supported Enshrouded integrations.

## Blockers

- No unresolved P0/P1 release blockers are recorded in `plans/PENDING.md` at this checkpoint.
- Any unresolved legal/provenance decision, failed release-readiness validation, failed required CI gate, unknown authority regression or unsupported persistence migration blocks public release.

## Acceptance

Stage 09.04 implementation is mergeable only when both `Enshrouded CI` and `Level 1 Release Readiness` are green on the exact PR HEAD. Final Level 1 closure additionally requires independent green push runs on the exact merged `main`, a documentation closeout, green post-closeout `main`, and canonical Notion synchronization.
