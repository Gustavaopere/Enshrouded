# ✅ Enshrouded Plan — Upstream Provenance

**Milestone:** Level 1 required — completed.

**Goal:** make source provenance, optional integrations and explicit exclusions auditable and executable as repository policy.

## Completed implementation

- `THIRD_PARTY_NOTICES.md`, `plans/00-foundation/UPSTREAM.md`, current-pack compatibility inventory and `UpstreamInventory` exist.
- Sculk Horde source snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc` and Apache-2.0 provenance are pinned.
- Ars Zero 2.0.2 is integration-only; GPLv3 policy and audited source snapshot `9478291a9f331ee2b4a391c4581a342d342ac7dc` are pinned.
- Spore 2.2.0j and Infnexus 2.0.4 are explicit exclusions; tests reject their re-entry into build configuration or production Java.
- Optional/current-pack mods remain integration candidates, never core authorities.
- Any future `// UPSTREAM-DERIVED:` marker must have a matching notice entry.
- BSD-2-Clause repository license and binary notice packaging are enforced.

## Verification

- [x] Provenance/documentation guard exists.
- [x] License/source markers are checked before source-derived code can enter.
- [x] Final `ProvenanceDocumentationTest` GREEN under the committed Gradle/JUnit stack.
- [x] Final JAR sanity proves `LICENSE` and `THIRD_PARTY_NOTICES.md` are packaged.
- [x] No production source is currently marked source-derived.

The final CI exposed one legitimate documentation gap: the Ars Zero source SHA expected by the guard was absent from the notice file. Commit `0b1940012628ff0d762961cccb480dc72989455d` pinned it; the final 33/33 test suite then passed.

## Final acceptance evidence

- Branch: `round-1-foundation`
- PR: #2
- Workflow/job: `33165771852` / `98830694040`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- Result: all Foundation gates GREEN.
- Merge SHA: `0b3c345673b81adbbc34a61505cb16200f689ba2`

**Acceptance:** satisfied. Third-party provenance/exclusion policy is auditable and enforced.
