# Current-Pack Compatibility Baseline — 2026-08-26

This document records the modpack state used to design Enshrouded Level 1. Entries here are integration candidates, not mandatory dependencies. The standalone Enshrouded core must continue to load when every optional provider listed below is absent.

## Platform

- Minecraft 1.21.1
- NeoForge 21.1.248
- Java 21

## Verified integration candidates

The audited current-pack Lich provider candidate is **Ars Zero 2.0.2**. It remains optional and must never become a core classloading dependency.

| Mod | Pack version | Intended Enshrouded role | Core dependency? |
| --- | --- | --- | --- |
| Ars Nouveau | 5.13.0 | Optional magic-damage classification and later content bridge | No |
| Ars Zero | 2.0.2 | Preferred optional Lich manifestation provider for the current pack | No |
| Iron's Spells 'n Spellbooks | 3.16.3 | Optional magic-damage classification/provider bridge | No |
| Epic Fight | 21.17.3.1 | Optional boss-combat compatibility | No |
| Goety | 3.1.4 | Future necromancy/lore flavour bridge; not a Shroud authority | No |
| Malum | 1.8.2 | Future thematic/compatibility bridge | No |
| Eidolon:Repraised | 0.5.0.2 | Future thematic/compatibility bridge | No |
| FTB Chunks | 2101.1.21 | Optional claim/protected-area bridge | No |
| FTB Teams | 2101.1.11 | Optional progression-owner/team bridge | No |
| JourneyMap | 6.0.5 | Optional map/region presentation | No |
| MineColonies | 1.1.1374-1.21.1-snapshot | Optional protected-area/mutation-safety bridge | No |
| GeckoLib | 4.9.2 | Animation implementation option if directly selected later | No |

AmbientSounds 6.3.8 and Particular Reforged 1.5.7 are presentation neighbours only. They do not own Shroud state and are not required by the core.

## Explicit exclusions

- Spore 2.2.0j — excluded
- Infnexus 2.0.4 — excluded

The two excluded mods may still be physically installed during development, but no Enshrouded architecture, acceptance criterion, runtime feature or compatibility adapter may rely on them. The intended final pack can remove them once Enshrouded is functional.

## Authority boundaries

- Enshrouded owns Shroud state, expansion, exposure, corruption, Flame progression and Lich story state.
- Ars Zero may provide a Lich entity body; it never owns Enshrouded story rewards or progression.
- Ars Nouveau and Iron's may enrich magic classification; the core classifier contract remains Enshrouded-owned.
- FTB Teams/Chunks and MineColonies may provide ownership/protection information; `ProgressionOwner` and `MutationAuthority` remain provider-neutral.
- JourneyMap may visualize state but is never authoritative state storage.
- Goety, Malum and Eidolon may supply future flavour/content integrations but are not prerequisites for Level 1.
- Veil or any other transitive/JarJar library is not considered installed infrastructure for Enshrouded unless deliberately promoted in a reviewed dependency decision.
