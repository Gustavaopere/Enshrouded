# ✅ Enshrouded — Third-Party Licenses & Provenance

**Stage:** 09.05 — Hardening

**Disposition:** implemented, reviewed, merged and independently post-merge verified.

## Goal

Close source/asset provenance and license obligations for every external reference, compatibility/provider boundary and any actual copied/adapted material before the Level 1 release, with ambiguous rights failing closed.

This is an engineering compliance gate and does not replace legal advice.

## Implemented scope

- Added machine-readable `provenance/third-party-provenance.json` as the release provenance ledger.
- Reconciled `SOURCES.md`, `THIRD_PARTY_NOTICES.md` and the compatibility audit `docs/compat/third-party-provenance-audit-2026-09-05.md`.
- Preserved the repository `LICENSE` as BSD-2-Clause for Enshrouded-owned material only.
- Added an explicit `first_party_binaries` inventory for the 12 project-authored `.png`/`.ogg` resources present in the production resource tree. There is no directory or wildcard exemption: every distributable binary/resource must be classified explicitly as first-party or mapped to approved third-party material.
- Added fail-closed validation for `copied`, `derived` and `vendored` material. Material requires an approved license status, exact local-file mapping and a genuinely pinned immutable reference.
- Material `immutable_ref` accepts recognized pinned git commits or cryptographic artifact digests instead of arbitrary text such as `main`, `latest` or an unpinned URL.
- Added bidirectional source-derivation verification: an existing `UPSTREAM-DERIVED` marker must map to the owning ledger entry/file, and every Java file registered as copied/derived must carry its matching marker.
- Added bidirectional notice verification: every `notice_required: true` ledger entry must be represented by an explicit provenance ID in `THIRD_PARTY_NOTICES.md`; orphan or duplicate notice IDs fail closed.
- Added repository scans for unregistered distributable binary resources and for direct production integration/provider directories lacking a provenance decision.
- Explicitly excluded Spore and Infnexus from production provenance/provider acceptance; production references to those provider IDs fail the automated provenance gate.
- The release JAR gate verifies that both `LICENSE` and `THIRD_PARTY_NOTICES.md` are packaged.

## Audited external boundaries

The Stage 09.05 audit covers the mandatory external/reference set, including:

- Sculk Horde pinned source snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc` — Apache License 2.0 / SPDX `Apache-2.0`, inspected/reference-only in the audited tree;
- Ars Zero 2.0.2 and source snapshot `9478291a9f331ee2b4a391c4581a342d342ac7dc` — GPLv3 / SPDX `GPL-3.0`, runtime/provider API boundary only;
- Ars Nouveau 5.13.1;
- Iron's Spells 'n Spellbooks 3.16.3;
- Epic Fight 21.17.3.1;
- FTB Chunks 2101.1.22;
- FTB Teams 2101.1.11;
- JourneyMap runtime 6.0.7 / API 2.0.0-1.21.1;
- MineColonies 1.1.1376-1.21.1-snapshot;
- GeckoLib 4.9.2;
- Goety 3.1.4;
- Malum 1.8.2;
- Eidolon: Repraised 0.5.0.2;
- The Forest public alpha, Minecraft Dungeons and commercial Enshrouded as reference/design-only boundaries.

The current repository audit found no copied/derived/vendored third-party production source, asset, audio or binary material. Existing integration code remains independent compatibility/API code or reference-only inspection; installed/runtime artifacts are not treated as source-reuse permission.

## Review hardening

Automated review identified five concrete provenance weaknesses, all corrected before merge:

1. **P1 — first-party resources:** the initial resource scan treated the 12 Enshrouded-owned `.png`/`.ogg` resources as unregistered material. The fix introduced exact first-party enumeration without a blanket exemption.
2. **P1 — license-label regression:** pre-existing Java tests require the strings `Apache License 2.0` and `GPLv3`. Notices now preserve those labels while also recording SPDX aliases `Apache-2.0` and `GPL-3.0`.
3. **P2 — notice-required reconciliation:** `notice_required` is now enforced bidirectionally between the ledger and `THIRD_PARTY_NOTICES.md`.
4. **P2 — ledger-to-source derivation marker:** Java files registered as copied/derived now require their matching `UPSTREAM-DERIVED` marker.
5. **P2 — mutable immutable_ref placeholders:** material refs such as `main`, `latest` and unpinned `/blob/main/` URLs are rejected.

The review-hardening RED checkpoint was HEAD `47041e70ff438ff7652874bd21e74803840dc5a1`, workflow/job `34009468453` / `101422626778`: 21 provenance contract tests ran and 6 failed exactly for the new negative cases. Corrected implementation HEAD `bd80ad8618ace2fe923d0cd7e3575fd8c6e2b1cc` then passed the complete CI matrix in workflow/job `34009516524` / `101422757081`.

All five review threads were replied to with their RED→GREEN evidence and resolved before merge.

## Verification record

- Base before implementation: `main@cd6850abdabc016136368fd2b6a9decfa6e1df51`.
- Implementation branch: `feat/09-third-party-provenance`.
- Initial TDD RED HEAD: `a0468cda6bf8d424175ccd6d6621bd8cdc333802`.
- Initial RED workflow/job: `34006965724` / `101415867427`.
- Review-hardening RED HEAD: `47041e70ff438ff7652874bd21e74803840dc5a1`.
- Review-hardening RED workflow/job: `34009468453` / `101422626778` — expected provenance contract failures.
- Final implementation HEAD: `bd80ad8618ace2fe923d0cd7e3575fd8c6e2b1cc`.
- PR: #76 — `Stage 09.05 — Third-party licenses and provenance`.
- Exact final PR-head workflow/job: `34009516524` / `101422757081` — `completed/success` across provenance contract tests, wrapper verification, unit tests, performance baselines, diff sanity, NeoForge build, GameTest compilation, production-JAR verification, canonical GameTests, SavedData two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `94fabffc807b34b4066fcbf7cec5411ed58a35fa`.
- Independent post-merge `main` workflow/job: `34009936498` / `101423866082` — `completed/success` across the same complete gate set.
- Post-merge verified implementation `main`: `94fabffc807b34b4066fcbf7cec5411ed58a35fa`.
- New cross-stage pending contracts: none.

## Acceptance checklist

- [x] Every mandatory external reference/provider is represented by a provenance decision.
- [x] Actual copied/derived/vendored material fails closed without exact source revision, local-file mapping and approved permission/license state.
- [x] Material immutable refs reject mutable placeholders.
- [x] Source derivation is verified marker→ledger and ledger→marker for registered Java material.
- [x] Notice-required entries are reconciled bidirectionally with `THIRD_PARTY_NOTICES.md`.
- [x] First-party distributable binary resources are enumerated explicitly and cannot overlap third-party material classifications.
- [x] Unregistered distributable binary resources fail the release provenance gate.
- [x] New direct integration/provider directories require a provenance decision.
- [x] Spore/Infnexus remain explicitly excluded from production provider/source acceptance.
- [x] The current audited tree contains no registered/found copied, derived or vendored third-party production material.
- [x] The built production JAR contains repository `LICENSE` and `THIRD_PARTY_NOTICES.md`.
- [x] Final implementation PR-head CI is GREEN.
- [x] All automated review threads are resolved.
- [x] PR #76 is merged into `main`.
- [x] Independent post-merge implementation `main` CI is GREEN.
- [x] No new cross-stage pending contract was introduced.

**Acceptance:** Stage 09.05 implementation is verified and merged. The documentation checkpoint is complete once this closeout PR is merged and the resulting `main` receives an independent GREEN CI. The next causal task after that is 09.04 — Release Checklist; it is not started by this closeout.