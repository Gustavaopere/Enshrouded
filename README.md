# Enshrouded

Enshrouded is a standalone **Minecraft 1.21.1 / NeoForge 21.1.248 / Java 21** mod centered on an expanding magical **Shroud**: persistent corruption regions, reversible terrain infestation, timed exposure and Madness, corrupted ecology, Flame-based progression and a recurring Lich storyline.

The design takes inspiration from the survival/exploration structure of the game *Enshrouded*, but this repository does not import proprietary game code, assets, audio or maps. The Minecraft implementation is server-authoritative and designed to function without the optional magic/content mods used by the surrounding modpack.

## Current implementation on `main`

The canonical state is recorded in [`plans/STATUS.md`](plans/STATUS.md). At this repository checkpoint:

- **00 Foundation — complete.** Build/CI, persistence/networking contracts, test infrastructure and provenance rules.
- **01 Shroud Field — complete.** Persistent per-dimension Shroud state, core lifecycle, bounded frontier expansion, indexed querying/sync and deterministic new-terrain core seeding.
- **02 Terrain Corruption — complete.** Fail-closed mutation authority, bounded/reversible materialization, corruption growths and purification/regression.
- **03 Exposure — complete.** Server-owned exposure reserve/timer, Madness, Deadly Shroud progression barrier and Red Sludge hazard.
- **04 Corrupted Ecology — complete.** Persistent creature corruption, hostility/buffs, magic-resistance classification and reversible visual/drop behavior.
- **05 Flame Progression — 3/4 canonical on this base.** Persistent Flame state, generic ritual execution and Flame Altar are merged; Sanctuary is the remaining canonical task at this checkpoint and is tracked by active development.
- **06 Lich & Story — planned.** Recurring antagonist state, optional boss-provider body, first manifestation, unique Lich Skull and Level 1 Flame ritual binding.
- **07 Client Experience — planned.** HUD, fog, sound/particles and accessibility controls driven only by synchronized server state.
- **08 Optional Integrations — planned.** Ars Zero, Ars Nouveau, Iron's, Epic Fight, claims/teams, JourneyMap and necromancy-flavour providers.
- **09 Hardening — planned.** Full test matrix, performance, world upgrades and release/provenance closure.

Because other implementation branches may advance while this README is being read, [`plans/STATUS.md`](plans/STATUS.md) remains the authority for exact task completion.

## Level 1 gameplay model

### Shroud cores and persistent regions

Shroud cores own persistent logical regions. The logical field exists independently of chunk load state, is scoped per dimension and expands through bounded deterministic frontier work. Enshrouded does not scan or force-load the entire world merely to advance corruption.

Destroying a core stops expansion and starts regression/purification. Visual leftovers never become the source of truth for Shroud state.

### Reversible terrain corruption

Loaded terrain can materialize the logical Shroud through data-driven corruption rules and native growths under strict world-mutation budgets. Unknown/protected/player-modified blocks fail closed rather than being overwritten blindly.

Every destructive mutation routes through a shared `MutationAuthority`, allowing Sanctuary, claims and other protection providers to converge on one safety boundary. Purification is independently bounded and does not resurrect Shroud state from visual remnants.

### Exposure, Madness and Deadly Shroud

Players inside ordinary Shroud consume a server-authoritative exposure reserve. When that reserve is exhausted, Madness produces the lethal outcome. Client HUD/effects may communicate the state but never decide remaining time or death.

Deadly/Red Shroud is a progression barrier controlled through the Flame Passage contract rather than a simple fixed-damage zone. **Red Sludge** is a concentrated lethal hazard associated with the Deadly Shroud rules.

### Corrupted ecology

Living entities can become persistently corrupted while preserving their original identity where possible. Corruption can:

- turn normally passive creatures hostile;
- strengthen corrupted hostile creatures;
- apply configurable magic-resistance behavior while keeping physical counterplay viable;
- expose reversible visual/drop presentation instead of permanently rewriting third-party AI internals when safer hooks exist.

### Flame progression

Flame progression is owner-scoped and persistent independently of any specific altar block. A provider-neutral ritual engine validates and executes progression rituals; the **Flame Altar** is the physical interaction/UI adapter over that engine.

The remaining Level 1 Flame contract is **Sanctuary**, a ward overlay that suppresses effective Shroud danger and feeds the same exposure/mutation safety interfaces without erasing the underlying logical Shroud field.

### Recurring Lich story — roadmap

Stage 06 will add persistent recurring-antagonist state. An external mod may provide the physical Lich entity body, but Enshrouded will always own encounter identity, story state, progression and rewards.

The Level 1 path is designed around:

1. defeating the first Lich Manifestation without permanently ending the canonical antagonist;
2. receiving the valid Level 1 Lich Skull exactly once for that encounter;
3. offering the Skull through the Flame ritual system;
4. completing the Level 1 milestone and preparing the save for later progression levels.

### Client presentation — roadmap

The planned client layer includes a clear exposure HUD, severity-aware fog/rendering, audio and particles, plus accessibility/performance presets. All presentation consumes synchronized server facts. Client configuration cannot weaken server-authoritative survival rules.

## Optional integrations

The standalone core cannot depend on these providers. Adapters must be isolated and fail safely if an installed version changes or disappears.

- **Ars Zero 2.0.2:** preferred optional Lich manifestation body for the current pack; never story/progression authority.
- **Ars Nouveau 5.13.0 / Iron's Spells 'n Spellbooks 3.16.3:** optional magic classification/content bridges.
- **Epic Fight 21.17.3.1:** optional boss-combat compatibility.
- **FTB Chunks / FTB Teams / MineColonies:** optional protection/ownership facts behind provider-neutral Enshrouded contracts.
- **JourneyMap 6.0.5:** optional presentation only; never authoritative Shroud storage.
- **Goety / Malum / Eidolon: Repraised:** optional future necromancy/thematic bridges, not Shroud authorities.
- **GeckoLib:** possible animation implementation option only if explicitly selected later.

**Spore** and **Infnexus** are explicitly excluded from the architecture. Enshrouded is intended to replace their infestation role in this pack rather than depend on them.

## Engineering invariants

- Server authority for Shroud, exposure, Flame, rewards and terrain mutation.
- No global loaded-chunk scan every tick.
- No chunk force-loading just to spread/purify the Shroud.
- Versioned persistent data and migration-aware world state.
- Bounded mutation/scheduler work.
- Optional integrations behind Enshrouded-owned interfaces.
- Fail-closed behavior for unknown protection/provider/API states.
- Dedicated-server/GameTest verification for world, persistence and networking behavior before task completion.

## Build

```bash
./gradlew test
./gradlew build
```

The project uses Java 21. CI also exercises the applicable NeoForge GameTests, production-JAR checks and dedicated-server save/reload smoke gates.

## Plans and project memory

The full Level 1 design and implementation sequence live in [`plans/`](plans/README.md). New implementation work should read:

1. [`plans/README.md`](plans/README.md)
2. [`plans/STATUS.md`](plans/STATUS.md)
3. [`plans/DECISIONS.md`](plans/DECISIONS.md)
4. [`plans/PENDING.md`](plans/PENDING.md)
5. the active stage/task files.

Deliberately deferred progression beyond Level 1 is recorded in [`plans/FUTURE-LEVELS.md`](plans/FUTURE-LEVELS.md).

## License and provenance

Enshrouded is licensed under the **BSD 2-Clause License**; see [`LICENSE`](LICENSE).

Third-party dependencies and references are indexed in [`SOURCES.md`](SOURCES.md) and governed by [`THIRD_PARTY_NOTICES.md`](THIRD_PARTY_NOTICES.md). The build packages the project license and third-party notices into the distributed JAR.

A gameplay idea, compatibility target or inspected external project is not permission to copy its code/assets. Any substantial source/asset derivation must be tied to an exact upstream revision, local files and applicable license/permission before release.