# Third-Party Notices and Source Provenance

Enshrouded is a standalone NeoForge 1.21.1 mod. This notice distinguishes independent compatibility/API use, inspected/reference-only sources and actual redistributed material.

**No third-party copied/derived/vendored source, assets, audio or binary material is registered in the production tree at the Stage 09.05 audit.** External mods are provided by the user's modpack/runtime and are not bundled inside the Enshrouded JAR.

Machine-readable ledger: `provenance/third-party-provenance.json`.
Retroactive audit: `docs/compat/third-party-provenance-audit-2026-09-05.md`.

Each auditable notice below carries an explicit ledger ID. CI reconciles these IDs bidirectionally with entries whose `notice_required` flag is `true`.

## Runtime/API/compatibility targets

- ID `ars-nouveau` — Ars Nouveau `5.13.1` — LGPL-3.0 code with separately restricted assets; independent API/compatibility code only.
- ID `ars-zero` — Ars Zero `2.0.2` — GPLv3 (`GPL-3.0`); audited source snapshot `9478291a9f331ee2b4a391c4581a342d342ac7dc`; runtime/provider API only.
- ID `epic-fight` — Epic Fight `21.17.3.1` — GPL-3.0 compatibility target; no upstream implementation bundled.
- ID `ftb-chunks` — FTB Chunks `2101.1.22` — visible-source/All Rights Reserved; API/claim facts only.
- ID `ftb-teams` — FTB Teams `2101.1.11` — All Rights Reserved; API/team facts only.
- ID `irons-spellbooks` — Iron's Spells 'n Spellbooks `3.16.3` — custom/All Rights Reserved terms permit addons/dependency use; no assets/source redistributed.
- ID `journeymap` — JourneyMap runtime `6.0.7`, API `2.0.0-1.21.1` — compile-only API use; no JourneyMap source/classes/assets redistributed.
- ID `minecolonies` — MineColonies `1.1.1376-1.21.1-snapshot` — GPL-3.0 code with separately reserved material; protected-area API/facts only.
- ID `geckolib` — GeckoLib `4.9.2` — MIT; runtime-provided library, not bundled by this project.

## Inspected/reference-only sources

- ID `sculk-horde` — Sculk Horde — Apache License 2.0 (`Apache-2.0`) source snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`; architectural/algorithmic reference only in the audited tree.
- ID `goety` — Goety `3.1.4` — MIT; inspected compatibility/reference target only.
- ID `malum` — Malum `1.8.2` — LGPL-3.0; inspected compatibility/reference target only.
- ID `eidolon-repraised` — Eidolon: Repraised `0.5.0.2` — LGPL-3.0; inspected compatibility/reference target only.
- ID `endnight-the-forest-alpha` — The Forest public alpha — Endnight Games proprietary reference only.
- ID `minecraft-dungeons` — Minecraft Dungeons — Mojang/Microsoft proprietary reference only.
- ID `enshrouded-game` — Enshrouded — Keen Games proprietary design/gameplay reference only.

## Explicit exclusions

Spore and Infnexus are explicitly excluded. They are not Enshrouded providers, architecture sources, compatibility targets or acceptance dependencies. Production references to those provider ids fail the automated provenance gate.

## Material derivation policy

Material means `copied`, `derived` or `vendored` source/assets/binaries. Every such entry must record author, source URL, a genuinely immutable revision/artifact ref, applicable license/permission and every local file. Material must have `license.status = approved`.

`restricted`, `unknown`, `REVIEW_REQUIRED` and `PERMISSION_REQUIRED` material fails closed. Java files registered as `copied` or `derived` must carry `// UPSTREAM-DERIVED: provenance-id`; CI checks both marker-to-ledger and ledger-to-marker directions.

## Release policy

`scripts/ci/test_third_party_provenance.py` exercises the contract before Gradle build acceptance. The validator rejects incomplete provenance, restricted/unknown material, mutable material refs, missing or orphaned required notices, unregistered distributable binaries, one-sided derivation mappings, new direct integration directories without a provenance decision, and Spore/Infnexus references in `src/main`.

The BSD-2-Clause license in this repository applies only to Enshrouded-owned material and does not override third-party obligations. The production JAR includes repository `LICENSE` and this notice file.
