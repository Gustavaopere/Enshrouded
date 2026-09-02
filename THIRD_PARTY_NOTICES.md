# Third-Party Notices and Source Provenance

Enshrouded is implemented as a standalone NeoForge 1.21.1 mod. This file is the canonical public attribution/compliance ledger for third-party references, providers and any future source-derived implementation.

Listing a project here does not mean its source or assets are bundled in Enshrouded. Installed artifacts, observable gameplay and public repositories are not automatic permission to copy or redistribute implementation material.

## Status vocabulary

- `REFERENCE_ONLY` — behavior/architecture may be studied; no source/assets are intended to be copied.
- `DEPENDENCY_API` — Enshrouded writes its own integration against a supported external dependency/API.
- `COMPATIBILITY_TARGET` — an external mod may be tested/supported without source ownership.
- `DERIVED_CODE` — source is copied/adapted and requires exact file/revision provenance plus license compliance.
- `DERIVED_ASSET` — an asset is copied/adapted and requires separately verified asset rights.
- `REVIEW_REQUIRED` — evidence is insufficient to authorize derivation.
- `PERMISSION_REQUIRED` — additional permission is required before public copying/adaptation.

## Source-derived marker convention

A Java source line of the form:

```text
// UPSTREAM-DERIVED: source-id
```

means the marked implementation contains substantial source-level material from the named upstream. `source-id` must exist in this file and the derivation register must identify local files, upstream revision/files and obligations.

No production file is currently registered in this ledger as `DERIVED_CODE` or `DERIVED_ASSET`. That statement describes the ledger state, not a substitute for the Stage 09 retroactive audit.

## Reproducible evidence — 2026-08-30

### Sculk Horde

- Provenance id: `sculk-horde-github-491aaa7e`
- Repository: `TeamPeril/Sculk-Horde`
- Audited source snapshot: `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`
- Audited repository license at that snapshot: **Apache License 2.0**.
- Use: architectural/algorithmic reference for persistent infestation state, bounded frontier work and node orchestration.
- Platform note: the audited source targets Forge 1.20.1-era APIs and is not a drop-in NeoForge 1.21.1 implementation.

Policy: isolated source adaptation is allowed only when the Apache-2.0 terms are satisfied, the exact upstream/local files are registered, required attribution/notices are preserved and the local file carries `// UPSTREAM-DERIVED: sculk-horde-github-491aaa7e`. Independent reimplementation needs no derivation marker merely because the architecture is similar.

### Ars Zero

- Provenance id: `ars-zero-runtime-2.0.2`
- Current pack version: **2.0.2** for Minecraft 1.21.1.
- Audited source snapshot: `9478291a9f331ee2b4a391c4581a342d342ac7dc`.
- License audited for this integration: **GPLv3**.
- Relevant runtime entity: `ars_zero:lich`.
- Use: optional runtime/provider integration.

Policy: Enshrouded does **not** copy the GPL Lich implementation into its core. Ars Zero may supply an encounter body; Enshrouded owns story identity, rewards and progression. Any proposal to incorporate GPL source requires an explicit copyleft compatibility review and derivation record before merge/release.

### JourneyMap API

- Provenance id: `journeymap-api-2.0.0-1.21.1`.
- Repository: `TeamJM/journeymap-api`.
- Audited tag: `1.21.1_2.0.0`.
- Compile-time artifact: `info.journeymap:journeymap-api-neoforge:2.0.0-1.21.1`.
- Current pack runtime: `journeymap-neoforge-1.21.1-6.0.7.jar`.
- Upstream terms audited at the tag expressly allow writing independent code that uses the `journeymap.*` API as a dependency and prohibit redistribution of JourneyMap API source/classes except where separately permitted.
- Use: optional client-only waypoint projection of Enshrouded-owned, server-authorized discovery snapshots.

Policy: Enshrouded uses the JourneyMap API as a `compileOnly` dependency and does not bundle or redistribute JourneyMap API classes/source. The adapter owns no discovery authority and never reads JourneyMap as canonical gameplay state. Status: `DEPENDENCY_API / COMPATIBILITY_TARGET`; no `DERIVED_CODE` or `DERIVED_ASSET` is declared.

### Enshrouded game

Gameplay concepts such as timed Shroud exposure, Deadly/Red Shroud and Flame-gated passage are design inspiration only. The commercial game's code, textures, models, audio, writing and exact maps are not source material for this repository. Status: `REFERENCE_ONLY`; any direct proprietary asset/code reuse is `PERMISSION_REQUIRED`.

## Optional integration/compatibility ledger

The installed baseline comes from `docs/compat/current-pack-2026-08-26.md`; later audited pack deltas are stated explicitly in their rows.

| Target | Pack evidence | Intended use | Derivation status |
| --- | --- | --- | --- |
| Ars Nouveau | `5.13.0` | optional magic classification/content bridge | external integration only; code license has been observed as LGPLv3 with separately restricted assets, but exact source revision is not frozen here, so `DERIVED_CODE`/assets remain `REVIEW_REQUIRED` |
| Iron's Spells 'n Spellbooks | `3.16.3` | optional magic classification/content bridge | custom/All-Rights-Reserved terms have been observed for the project; own addon/API integration only; source/assets are `REVIEW_REQUIRED`/`PERMISSION_REQUIRED` until exact terms evidence permits reuse |
| Epic Fight | `21.17.3.1` | boss-combat compatibility | GPLv3 observed historically; API/compatibility use only, source derivation `REVIEW_REQUIRED` until exact revision/copyleft impact is recorded |
| Goety | `3.1.4` | future necromancy/lore flavour | project metadata has been observed as MIT, but exact corresponding source revision is not pinned here; source derivation `REVIEW_REQUIRED` |
| Malum | `1.8.2` | future spirit/thematic bridge | LGPLv3 observed for current project line; exact source revision/obligations not pinned here, so derivation `REVIEW_REQUIRED` |
| Eidolon: Repraised | `0.5.0.2` | future occult/thematic bridge | LGPLv3 observed for current project line; exact source revision not pinned here, so derivation `REVIEW_REQUIRED` |
| FTB Chunks | `2101.1.21` | claim/protection facts | `DEPENDENCY_API / COMPATIBILITY_TARGET`; no copied source declared; derivation requires separate audit |
| FTB Teams | `2101.1.11` | owner/team facts | `DEPENDENCY_API / COMPATIBILITY_TARGET`; no copied source declared; derivation requires separate audit |
| JourneyMap | `6.0.7`; API `2.0.0-1.21.1` | client-only map presentation of authorized Shroud discoveries | `DEPENDENCY_API / COMPATIBILITY_TARGET`; compile-only API use, renderer only, never state authority; no copied source/assets declared |
| MineColonies | `1.1.1374-1.21.1-snapshot` | protected-area facts | `DEPENDENCY_API / COMPATIBILITY_TARGET`; no copied source declared; derivation requires separate audit |
| GeckoLib | `4.9.2` | optional animation implementation if selected | external dependency only; no copied assets/source declared |

## Explicit exclusions

Spore and Infnexus are explicitly excluded from Enshrouded architecture. Their presence in a development pack does not authorize code/assets to enter this repository and does not make them runtime dependencies.

## Derivation register

Before substantial source or assets are copied/adapted, add a record here:

```text
Local file(s):
Provenance/source id:
Upstream URL:
Upstream commit/tag:
Upstream file(s):
Use type: DERIVED_CODE | DERIVED_ASSET
License/permission:
Copyright/notice obligations:
Modification note/date:
Permission evidence (if required):
```

## Release policy

A release must fail closed if material is actually derived while its provenance/rights are `REVIEW_REQUIRED`, `PERMISSION_REQUIRED` or unknown. Normal API/compatibility integration may continue without granting Enshrouded rights to external source/assets.

The BSD-2-Clause license in this repository applies to Enshrouded-owned material and does not override upstream obligations. The build packages both `LICENSE` and this notice file in the JAR.
