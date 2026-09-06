# Enshrouded

Enshrouded is a standalone **Minecraft 1.21.1 / NeoForge 21.1.248 / Java 21** mod centered on an expanding magical **Shroud**: persistent corruption regions, reversible terrain infestation, timed exposure and Madness, corrupted ecology, Flame-based progression and a recurring Lich storyline.

The design takes inspiration from the survival/exploration structure of the game *Enshrouded*, but this repository does not import proprietary game code, assets, audio or maps. The Minecraft implementation is server-authoritative and remains functional without the optional providers used by the surrounding modpack.

## Level 1 — complete repository milestone

`1.0.0` is the completed Level 1 repository milestone. Foundation through Stage 08 are complete and **Stage 09 Hardening is 5/5**. The recorded implementation release checkpoint is `main@47189826fe03cb633d32fd8eb695f275f4aaa96f`, independently GREEN in both `Enshrouded CI` and `Level 1 Release Readiness`. The authoritative implementation/evidence ledger is [`plans/STATUS.md`](plans/STATUS.md).

The surrounding 607-mod pack is not vendored in this repository. Before distributing that full modpack, perform the external/manual complete-pack smoke required by [`docs/release/level1-checklist.md`](docs/release/level1-checklist.md); repository CI does not pretend to be a literal 607-JAR boot.

Level 1 includes:

- persistent bounded Shroud fields and deterministic core lifecycle;
- reversible terrain corruption behind `MutationAuthority`;
- server-authoritative Exposure, Madness, Deadly/Red Shroud and Red Sludge;
- persistent corrupted ecology and canonical magic-resistance reduction;
- owner-scoped Flame progression, Flame Altar, Sanctuary and ritual progression;
- persistent recurring Lich story/manifestation/reward contracts with exactly-once reward semantics;
- synchronized HUD, fog, audio/particles and accessibility presentation;
- optional Ars Zero, Ars Nouveau, Iron's Spells, Epic Fight, FTB Teams, FTB Chunks, MineColonies and JourneyMap boundaries;
- intentional Level 1 no-op/flavour boundaries for Goety, Malum and Eidolon: Repraised;
- migration/recovery, performance, provenance and release-readiness hardening.

## Gameplay and authority model

Shroud state, Exposure, Flame progression, Story/rewards and terrain mutation are server authoritative. Logical Shroud state persists independently of chunk load state and expands through bounded work without global world scans or chunk forcing. Loaded terrain materialization/purification is reversible and passes through fail-closed mutation/protection decisions.

Sanctuary is a protection overlay over the logical field rather than a second Shroud authority. Optional providers may supply bounded facts, classification or presentation bodies; they do not silently acquire Enshrouded progression/state authority.

## Optional integrations — current pack baseline

The current release profile is [`docs/compat/current-pack-2026-09-06.md`](docs/compat/current-pack-2026-09-06.md), reconciled against the user-supplied 607-entry modlist. Key installed targets include:

- Ars Nouveau `5.13.1` and Ars Zero `2.0.2`;
- Iron's Spells `3.16.3`;
- Epic Fight `21.17.3.1`;
- FTB Chunks `2101.1.22` and FTB Teams `2101.1.11`;
- JourneyMap `6.0.7`;
- MineColonies `1.1.1376`;
- Goety `3.1.4`, Malum `1.8.2`, Eidolon: Repraised `0.5.0.2`;
- GeckoLib `4.9.2` as an audited compatibility/reference target.

**Spore and Infnexus are not supported Enshrouded integrations.** Their presence in a development pack is not a source/provider grant.

The repository does not vendor all 607 third-party pack JARs. CI therefore does not claim a literal complete-pack boot; the required external/manual distribution smoke is documented in [`docs/release/level1-checklist.md`](docs/release/level1-checklist.md).

## Persistence and release safety

Stage 09.03 owns explicit world schema migration/recovery. Supported persisted v1 state migrates deterministically to v2; malformed, unsupported/pre-versioned and unknown-future schemas fail closed rather than silently resetting progression. Core/ritual/reward idempotence and real two-boot reload behavior are covered by CI.

Third-party provenance is machine-enforced through [`provenance/third-party-provenance.json`](provenance/third-party-provenance.json) and [`scripts/verify_third_party_provenance.py`](scripts/verify_third_party_provenance.py). Stage 09.04 provides [`scripts/verify_level1_release.py`](scripts/verify_level1_release.py) and the `Level 1 Release Readiness` workflow so public release acceptance fails closed on missing release evidence or unresolved blockers.

## Localization and configuration

Level 1 ships `en_us` and `pt_br` language resources with release-time key-parity validation. The current Level 1 code does not export a NeoForge `ModConfigSpec` surface; gameplay defaults remain code/data-owned and regression-tested.

## Build and verification

```bash
./gradlew test
./gradlew build
python3 scripts/verify_third_party_provenance.py
python3 scripts/verify_level1_release.py
```

`Enshrouded CI` covers wrapper provenance, unit tests, performance baselines, NeoForge build, JAR integrity, canonical GameTests, SavedData two-boot reload, an isolated real Ars Zero 2.0.2 profile and dedicated-server save/reload. `Level 1 Release Readiness` independently enforces provenance plus the final release contract.

## Release documents

- [`docs/release/level1-checklist.md`](docs/release/level1-checklist.md)
- [`docs/release/level1-release-notes.md`](docs/release/level1-release-notes.md)
- [`plans/STATUS.md`](plans/STATUS.md)
- [`plans/PENDING.md`](plans/PENDING.md)

## License and provenance

Enshrouded is licensed under the **BSD 2-Clause License**; see [`LICENSE`](LICENSE). Third-party dependencies/references are indexed in [`SOURCES.md`](SOURCES.md) and [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md). `LICENSE` and third-party notices are packaged in the production JAR.

A gameplay idea, compatibility target or installed/inspected project is not permission to copy its code or assets. Actual source/asset reuse must be tied to an exact approved provenance record before release.
