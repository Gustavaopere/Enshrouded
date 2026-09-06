# Third-Party Notices and Source Provenance

Enshrouded is a standalone NeoForge 1.21.1 mod. This notice distinguishes independent compatibility/API use, inspected/reference-only sources and actual redistributed material.

**No third-party copied/derived/vendored source, assets, audio or binary material is registered in the production tree at the Stage 09.05 audit.** External mods are provided by the user's modpack/runtime and are not bundled inside the Enshrouded JAR.

Machine-readable ledger: `provenance/third-party-provenance.json`.
Retroactive audit: `docs/compat/third-party-provenance-audit-2026-09-05.md`.

## Runtime/API/compatibility targets

- Ars Nouveau `5.13.1` — LGPL-3.0 code with separately restricted assets; independent API/compatibility code only.
- Ars Zero `2.0.2` — GPL-3.0; audited source snapshot `9478291a9f331ee2b4a391c4581a342d342ac7dc`; runtime/provider API only.
- Epic Fight `21.17.3.1` — GPL-3.0 compatibility target; no upstream implementation bundled.
- FTB Chunks `2101.1.22` — visible-source/All Rights Reserved; API/claim facts only.
- FTB Teams `2101.1.11` — All Rights Reserved; API/team facts only.
- Iron's Spells 'n Spellbooks `3.16.3` — custom/All Rights Reserved terms permit addons/dependency use; no assets/source redistributed.
- JourneyMap runtime `6.0.7`, API `2.0.0-1.21.1` — compile-only API use; no JourneyMap source/classes/assets redistributed.
- MineColonies `1.1.1376-1.21.1-snapshot` — GPL-3.0 code with separately reserved material; protected-area API/facts only.
- GeckoLib `4.9.2` — MIT; runtime-provided library, not bundled by this project.

## Inspected/reference-only sources

- Sculk Horde — Apache-2.0 source snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`; architectural/algorithmic reference only in the audited tree.
- Goety `3.1.4` — MIT; inspected compatibility/reference target only.
- Malum `1.8.2` — LGPL-3.0; inspected compatibility/reference target only.
- Eidolon: Repraised `0.5.0.2` — LGPL-3.0; inspected compatibility/reference target only.
- The Forest public alpha — Endnight Games proprietary reference only.
- Minecraft Dungeons — Mojang/Microsoft proprietary reference only.
- Enshrouded — Keen Games proprietary design/gameplay reference only.

## Explicit exclusions

Spore and Infnexus are explicitly excluded. They are not Enshrouded providers, architecture sources, compatibility targets or acceptance dependencies. Production references to those provider ids fail the automated provenance gate.

## Material derivation policy

Material means `copied`, `derived` or `vendored` source/assets/binaries. Every such entry must record author, source URL, immutable revision/artifact ref, applicable license/permission and every local file. Material must have `license.status = approved`.

`restricted`, `unknown`, `REVIEW_REQUIRED` and `PERMISSION_REQUIRED` material fails closed. Source-derived Java should carry `// UPSTREAM-DERIVED: provenance-id`, which the CI validator cross-checks against the ledger.

## Release policy

`scripts/ci/test_third_party_provenance.py` exercises the contract before Gradle build acceptance. The validator rejects incomplete provenance, restricted/unknown material, unregistered distributable binaries, unmapped derivation markers, new direct integration directories without a provenance decision, and Spore/Infnexus references in `src/main`.

The BSD-2-Clause license in this repository applies only to Enshrouded-owned material and does not override third-party obligations. The production JAR includes repository `LICENSE` and this notice file.
