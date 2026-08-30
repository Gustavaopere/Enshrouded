# Enshrouded — Master Plan

This directory is the canonical memory for the Enshrouded Minecraft mod. Every new implementation session must read this file, `STATUS.md`, `DECISIONS.md`, `PENDING.md`, then the README and task file of the active subsystem before changing code.

## Target

- Minecraft **1.21.1**.
- NeoForge **21.1.248** as the initial build/runtime target matching the current pack.
- Java **21**.
- Repository: `Gustavaopere/Enshrouded`.
- Mod id: `enshrouded`.
- Java package root: `com.gustavaopere.enshrouded`.
- Current planning base: `753021c46ddc5b8ee25a6ab586cfc9b8c4a8de88`.

## Level 1 product definition

Level 1 is a complete playable vertical slice, not a prototype. It is complete only when all of the following work together on a dedicated server:

1. Shroud cores can exist in the world and own persistent Shroud regions.
2. Living cores expand a bounded logical corruption field without forcing chunk loads.
3. Loaded terrain visibly materializes corruption from that logical field under a strict mutation budget.
4. Destroying a core immediately stops its expansion and begins gradual regression/purification.
5. Players entering ordinary Shroud receive a server-authoritative exposure timer and die from Madness when it is exhausted.
6. Deadly/Red Shroud exists as a Level 2 barrier during Level 1 and is effectively unsurvivable until Flame Passage is high enough.
7. Red Sludge is a concentrated lethal hazard tied to Deadly Shroud.
8. Creatures can become persistently corrupted; normally passive creatures become hostile and corrupted hostiles are strengthened.
9. Corrupted creatures receive configurable resistance to damage classified as magical while physical counterplay remains viable.
10. A Flame Altar exists, produces a sanctuary, and owns the ritual UI/interaction used to progress Flame power.
11. The first Lich Manifestation can be defeated, is narratively non-final, and yields the Level 1 Lich Skull exactly once per valid encounter.
12. Offering that skull at a valid Flame Altar completes the Level 1 progression milestone and prepares the save for a later Level 2 implementation without implementing Level 2 content now.
13. HUD, fog, particles, sound cues and accessibility/configuration communicate Shroud severity and timer state clearly.
14. The mod remains functional without Spore/Infnexus and without Ars Zero; optional integrations enhance rather than own the core.

## Completion convention

Task files begin as `NN-name.md`. A task is renamed to `✅-NN-name.md` only after its acceptance criteria are verified, the exact implementation branch is green in CI, and the branch has been merged into `main`. `STATUS.md` must be updated in that same merge with branch, merge SHA, verification result and next branch.

Planning documents themselves do not receive the implementation `✅-` prefix. The planning baseline is tracked as complete in `STATUS.md`.

## Git policy

Implementation branches are dependency-ordered and are **not** created in advance. Every task branch starts from the latest `main` after its predecessor has merged. One reviewable task may use more than one RED/GREEN commit, but it has one final merge gate.

Canonical implementation sequence:

1. `round-1-foundation`
2. `feat/01-shroud-state`
3. `feat/01-core-lifecycle`
4. `feat/01-frontier-expansion`
5. `feat/01-zone-query-sync`
6. `feat/01-core-seeding`
7. `feat/02-terrain-safety`
8. `feat/02-materialization-rules`
9. `feat/02-corruption-growths`
10. `feat/02-purification-regression`
11. `feat/03-exposure-state`
12. `feat/03-madness`
13. `feat/03-deadly-shroud`
14. `feat/03-red-sludge`
15. `feat/04-entity-corruption`
16. `feat/04-hostility-buffs`
17. `feat/04-magic-resistance`
18. `feat/04-ecology-visuals`
19. `feat/05-flame-state`
20. `feat/05-level1-ritual`
21. `feat/05-flame-altar`
22. `feat/05-sanctuary`
23. `feat/06-story-state`
24. `feat/06-boss-provider`
25. `feat/06-first-manifestation`
26. `feat/06-lich-skull`
27. `feat/07-hud`
28. `feat/07-fog-rendering`
29. `feat/07-audio-particles`
30. `feat/07-accessibility`
31. `feat/08-ars-zero`
32. `feat/08-magic-systems`
33. `feat/08-combat-claims-teams`
34. `feat/08-journeymap`
35. `feat/08-necromancy-flavor`
36. `feat/09-test-matrix`
37. `feat/09-performance`
38. `feat/09-world-upgrade`
39. `feat/09-third-party-provenance`
40. `feat/09-release-checklist`

A branch may be split only when the split preserves this causal order and each resulting branch independently satisfies a meaningful review gate.

## Required engineering method

- TDD for new server behavior: write a failing test, verify RED, implement the smallest correct behavior, verify GREEN, then refactor.
- Pure deterministic state/math belongs in JUnit tests.
- World interactions, persistence, networking and registry behavior belong in NeoForge GameTests/integration tests.
- Dedicated-server smoke is mandatory before any task is marked complete when the task touches bootstrap, registries, networking, SavedData, worldgen or events.
- No global loaded-chunk scan every tick.
- No forced chunk loading merely to advance corruption.
- No client authority over exposure, Flame progression, boss rewards, core state or terrain mutation.
- Optional integrations must fail closed when the target mod is absent or changes incompatibly.
- Core gameplay cannot depend on `spore` or `infnexus`; they are explicitly excluded from the architecture.

## Architecture order

`00-foundation` establishes build, tests, contracts and upstream provenance. `01-shroud-field` creates the authoritative logical infestation. `02-terrain-corruption` establishes mutation safety first, then materializes that state through the pre-existing authority. `03-exposure` turns Shroud severity into player survival gameplay. `04-corrupted-ecology` applies persistent corruption to creatures. `05-flame-progression` persists Flame state, builds the generic ritual engine before its physical altar adapter, then adds Sanctuary. `06-lich-story` supplies the recurring antagonist and Level 1 reward. `07-client-experience` communicates all server state. `08-integrations` binds verified mods without making them core dependencies. `09-hardening` proves the whole Level 1 vertical slice.

## Source strategy

- **Sculk Horde:** the GitHub source snapshot at commit `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc` is an architectural/algorithmic upstream candidate. Its repository contains Apache-2.0 licensing. Isolated algorithms may be adapted where useful, with required notices preserved. Forge 1.20.1 API code is not copied blindly into NeoForge 1.21.1.
- **Ars Zero:** current pack version `2.0.2` contains `ars_zero:lich`. Its project is GPLv3. Enshrouded integrates that entity through an optional runtime provider instead of copying GPL implementation into the core.
- **Enshrouded game:** gameplay concepts such as timed Shroud exposure, Deadly Shroud and Flame-gated passage are design inspiration. No proprietary assets, code, audio or exact maps are imported.
- **Spore/Infnexus:** excluded. Enshrouded is intended to replace the infestation role in this pack once functional.

## Subplans

- [`00-foundation`](00-foundation/README.md)
- [`01-shroud-field`](01-shroud-field/README.md)
- [`02-terrain-corruption`](02-terrain-corruption/README.md)
- [`03-exposure`](03-exposure/README.md)
- [`04-corrupted-ecology`](04-corrupted-ecology/README.md)
- [`05-flame-progression`](05-flame-progression/README.md)
- [`06-lich-story`](06-lich-story/README.md)
- [`07-client-experience`](07-client-experience/README.md)
- [`08-integrations`](08-integrations/README.md)
- [`09-hardening`](09-hardening/README.md)

See also [`FUTURE-LEVELS.md`](FUTURE-LEVELS.md) for deliberately deferred extension points.
