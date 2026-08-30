# Source provenance

This file indexes third-party projects used by Enshrouded as architectural references, gameplay inspiration, optional providers or compatibility targets.

**A source link, installed mod or observed behavior is not a license grant.** Before copying/adapting source or assets, consult [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md) and [`plans/09-hardening/05-third-party-licenses-provenance.md`](plans/09-hardening/05-third-party-licenses-provenance.md). Actual derivation requires exact source revision/file provenance and applicable rights.

## Primary references

| Source | Enshrouded use | Compliance posture |
| --- | --- | --- |
| [Sculk Horde](https://github.com/TeamPeril/Sculk-Horde) | architectural/algorithmic reference for persistent infestation and bounded frontier work | pinned Apache-2.0 source snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`; any actual adaptation must be registered file-by-file |
| Ars Zero | optional runtime provider for a Lich manifestation body | pinned source snapshot `9478291a9f331ee2b4a391c4581a342d342ac7dc`, GPLv3; provider integration only, no GPL implementation copied into core |
| *Enshrouded* game | design inspiration for timed Shroud, Deadly Shroud and Flame-gated passage | proprietary `REFERENCE_ONLY`; no game code, assets, audio or maps may be imported |

## Current-pack optional integration candidates

The exact pack baseline is recorded in [`docs/compat/current-pack-2026-08-26.md`](docs/compat/current-pack-2026-08-26.md).

| Target | Pack baseline | Planned role |
| --- | --- | --- |
| Ars Nouveau | `5.13.0` | magic-damage/content bridge |
| Iron's Spells 'n Spellbooks | `3.16.3` | magic-damage/content bridge |
| Epic Fight | `21.17.3.1` | boss-combat compatibility |
| Goety | `3.1.4` | necromancy/lore flavour |
| Malum | `1.8.2` | thematic/spirit compatibility |
| Eidolon: Repraised | `0.5.0.2` | occult/thematic compatibility |
| FTB Chunks | `2101.1.21` | claim/protected-area facts |
| FTB Teams | `2101.1.11` | team/progression-owner facts |
| JourneyMap | `6.0.5` | optional map presentation |
| MineColonies | `1.1.1374-1.21.1-snapshot` | protected-area facts |
| GeckoLib | `4.9.2` | optional animation implementation if explicitly selected |

AmbientSounds and Particular Reforged are presentation neighbours only and are not Enshrouded authorities.

## Explicit exclusions

- Spore 2.2.0j
- Infnexus 2.0.4

They may exist in a development pack but no Enshrouded runtime feature, acceptance criterion or compatibility adapter may depend on them.
