# Enshrouded Plan — Upstream Provenance

**Milestone:** Level 1 required.

**Goal:** record exactly what may be adapted, what may only be integrated and which current-pack mods are relevant.

**Planned types:** `UpstreamInventory`.

## Files

- Create `THIRD_PARTY_NOTICES.md`.
- Create `plans/00-foundation/UPSTREAM.md`.
- Create `docs/compat/current-pack-2026-08-26.md`.

## Dependencies

- 01 build scaffold.

## Implementation contract

- Record Sculk Horde GitHub snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc` and Apache-2.0 source licensing; preserve attribution for any adapted source.
- Record Ars Zero 2.0.2 as GPLv3 and use integration/runtime-provider strategy rather than copying its Lich implementation into core.
- Record current pack candidates from `modlist agora atual.txt`.
- Record Spore 2.2.0j and Infnexus 2.0.4 only in an explicit exclusion section; no integration work derives from them.
- Do not promote transitive/JarJar libraries such as Veil to mandatory dependencies.

## TDD / verification

- [x] Documentation verification test checks every source-derived marker has a provenance entry.
- [x] `THIRD_PARTY_NOTICES.md` contains upstream license references before any adapted source enters the project.
- [x] Spore/Infnexus exclusion is executable and fails if either re-enters build configuration or production Java.
- [x] Repository license declaration/text and production-JAR notices are checked.
- [x] Final committed Gradle/JUnit provenance suite GREEN on implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d`.

## Final implementation checkpoint — 2026-08-28

Implemented on `round-1-foundation`:

- `THIRD_PARTY_NOTICES.md`, `plans/00-foundation/UPSTREAM.md`, current-pack compatibility inventory and `UpstreamInventory` exist;
- Sculk Horde source snapshot/licensing and Ars Zero version/licensing/integration strategy are pinned;
- Spore and Infnexus are recorded only as explicit exclusions and are not core dependencies, providers or design authorities;
- optional integration candidates are inventoried without becoming mandatory dependencies;
- `ProvenanceDocumentationTest` verifies required documents, audited source/version markers, exclusion entries and future `// UPSTREAM-DERIVED:` markers;
- repository BSD-2-Clause `LICENSE` matches `mod_license=BSD-2-Clause` and production JAR embeds `LICENSE` plus `THIRD_PARTY_NOTICES.md`;
- no production Java source currently contains an `UPSTREAM-DERIVED` marker, so no source-derived third-party implementation entered Foundation.

## Executable acceptance evidence

Historical provenance RED/GREEN evidence is preserved, and final PR workflow `33165771852`, job `98830694040`, on implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` completed unit tests, build/JAR checks, GameTests and dedicated-server reload GREEN.

The closing documentation-only checkpoint must itself pass the same pipeline before merge; otherwise this task is reopened.

## Merge gate

- [x] All task-specific tests are GREEN on the verified implementation HEAD under committed Gradle/JUnit.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build/JAR verification is GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; later-stage integration boundaries remain in `plans/PENDING.md`.
- [x] Task is ready to be renamed with `✅-` in the Foundation closing checkpoint merged to `main`.

**Acceptance:** The repository contains an auditable source/integration inventory, excluded fungus mods cannot silently enter core, the declared project license is distributable with the JAR, and no copied code can enter without provenance.
