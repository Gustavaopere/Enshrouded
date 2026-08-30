# 09.05 — Third-Party Licenses & Provenance

## Goal

Close source/asset provenance and license obligations for every reference, optional provider and any actual copied/adapted material before the Level 1 release.

Ambiguous rights fail closed. This engineering gate does not replace legal advice.

## Public records

Maintain:

- root `LICENSE` — Enshrouded BSD-2-Clause;
- root `SOURCES.md` — dependency/reference/compatibility index;
- root `THIRD_PARTY_NOTICES.md` — reproducible audit and derivation ledger;
- exact compatibility baselines under `docs/compat/`.

The built JAR must continue to package `LICENSE` and `THIRD_PARTY_NOTICES.md`.

## Minimum evidence for actual derivation

For each `DERIVED_CODE`/`DERIVED_ASSET` entry record:

- exact upstream project and URL;
- immutable commit/tag/source snapshot;
- exact upstream file/resource;
- local file/resource receiving the derivation;
- source-code license and asset license separately where relevant;
- required notice/source/copyleft obligations;
- permission evidence for restricted/custom/ARR material;
- modification date/note.

An installed artifact/version is compatibility evidence, not automatically source-reuse permission.

## Mandatory audit scope

Audit at least:

- Sculk Horde pinned snapshot and any code carrying/expected to carry `UPSTREAM-DERIVED` markers;
- Ars Zero provider boundary and absence of copied GPL Lich implementation;
- the commercial *Enshrouded* inspiration boundary, ensuring no proprietary code/assets/audio/maps are present;
- Ars Nouveau;
- Iron's Spells 'n Spellbooks;
- Epic Fight;
- Goety;
- Malum;
- Eidolon: Repraised;
- FTB Chunks / FTB Teams;
- JourneyMap;
- MineColonies;
- GeckoLib if directly selected for shipped animation code.

Also confirm excluded Spore/Infnexus code/assets did not enter the repository.

## Retroactive repository audit

Before release, search both text and binary resources for:

- source-derived markers and copyright headers;
- unusually close class/method structure or identifiers from reference implementations;
- copied translations/text;
- textures, models, sounds or other binary assets with third-party provenance;
- bundled libraries/binaries not represented in the ledger.

If an apparent derivation cannot be traced to an allowed exact source snapshot, classify it `REVIEW_REQUIRED` and do not release it until resolved or replaced independently.

## CI/validator direction

Release validation should fail when:

- a source-derived marker has no ledger entry;
- a derivation lacks exact revision/file/license;
- restricted asset/source material lacks permission evidence;
- required notices are absent from the JAR/release materials;
- a new compatibility/provider target is introduced without provenance coverage.

## Acceptance

- every external reference/provider is indexed;
- all actual source/asset derivation has reproducible evidence and compliant notices;
- optional providers remain integrations, not implied source grants;
- no unresolved derived material ships;
- the release JAR carries required license/notices.
