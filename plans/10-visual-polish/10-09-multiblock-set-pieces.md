# Stage 10.09 — Multiblock and set-piece pass

Status: LOGIC/AUTHORITY CHECKPOINT IMPLEMENTED / FLAME SHELL SURVIVAL ACQUISITION MERGED + VERIFIED / RENDER ASSETS + IN-WORLD SET-PIECE CONSUMERS OPEN / STAGE 10.09 NOT CLOSED

## Scope and current truth

Stage 10.09 establishes the authoritative multiblock/set-piece contracts without creating a second gameplay authority. It is **not** a claim that every promised set piece is already visible in-world.

Implemented and verified:

- the canonical player-built 3×3 Flame Altar formation lifecycle;
- bounded validation, explicit formation, deterministic unform/reform and restart recovery;
- Sanctuary activation gated by successful physical formation;
- survival recipes for the complete four-brace + four-rune shell and self-drop loot tables for both shell blocks;
- a bounded data-only Shroud Core Nest layout/authority contract;
- a bounded data-only Level-1 Lich manifestation landmark layout/authority contract;
- regression coverage for authority, claims/protection, lifecycle, chunk availability, persistence and dedicated-server behavior.

Still open and therefore **not** silently counted as complete:

- `flame_altar_brace` / `flame_altar_rune` blockstates, block models, item models, textures and visibly distinct `FORMED=false|true` presentation;
- production placement/worldgen consumption for `ShroudCoreNestLayout`;
- production placement/worldgen or approved encounter-location consumption for `LichManifestationLandmarkLayout`;
- final in-game screenshots and full 603-mod client visual smoke.

The shell render assets are a user-owned art handoff. This repository checkpoint must not invent substitute placeholder art to make automated review green.

No automatic Core Nest or Lich landmark worldgen/placement path is introduced here because no such production consumer has been explicitly approved. Leaving that work open is preferable to silently creating a new terrain/worldgen contract during closeout.

A separate Purification Shrine/controller remains intentionally deferred. Purification/Sanctuary presentation stays integrated with the existing Flame complex and canonical Flame Ward/purification runtime.

## Current deliverable matrix

| Deliverable | Runtime state | Authority state | 10.09 state |
|---|---|---|---|
| Flame Altar 3×3 lifecycle | implemented | `FlameAltarBlockEntity` + existing Flame/Sanctuary runtime remain canonical | **IMPLEMENTED / VERIFIED** |
| Brace/rune survival acquisition | recipes + self-drop loot merged in PR #100 | no new gameplay authority | **IMPLEMENTED / VERIFIED** |
| Brace/rune formed/unformed rendering | Java `FORMED` property exists; required client resources are not yet supplied | presentation only | **OPEN — USER ART HANDOFF** |
| Shroud Core Nest layout | immutable bounded Ordinary/Deadly composition exists | exactly one existing canonical Core anchor | **LAYOUT CONTRACT IMPLEMENTED / IN-WORLD CONSUMER OPEN** |
| Lich manifestation landmark layout | immutable bounded Level-1 composition exists | Stage 06 Story/manifestation lifecycle remains canonical | **LAYOUT CONTRACT IMPLEMENTED / IN-WORLD CONSUMER OPEN** |
| Separate Purification Shrine | not implemented | existing Flame Ward/purification authority retained | **INTENTIONALLY DEFERRED** |

## Canonical Flame Altar formation contract

### Authoritative anchor

`FlameAltarBlockEntity` remains the single physical/gameplay controller for the Flame complex. The four `FlameAltarBraceBlock` and four `FlameAltarRuneBlock` shell pieces own no BlockEntity, ritual inventory, progression state, Sanctuary state or reward authority.

The shell pieces expose presentation/validation state only:

- cardinal braces must face inward toward the controller;
- corner runes occupy the four diagonal positions;
- the shell `FORMED` property mirrors the controller's committed formation state;
- a freshly placed complete shell remains `UNFORMED` until explicit controller interaction validates it.

The activation UX is:

1. player obtains and places the center Altar plus the required 3×3 shell;
2. first explicit controller interaction performs bounded validation;
3. validation success commits `FORMED` and activates the existing canonical Flame Ward/Sanctuary path;
4. a later controller interaction opens the existing ritual menu;
5. validation failure remains fail-closed and opens no ritual menu.

There is no per-tick structure scan.

### Survival acquisition

PR #100 closes the survival-blocking acquisition gap discovered during PR #98 review:

- `data/enshrouded/recipe/flame_altar_brace.json` crafts four braces, matching the exact cardinal requirement;
- `data/enshrouded/recipe/flame_altar_rune.json` crafts four runes, matching the exact corner requirement;
- `data/enshrouded/loot_table/blocks/flame_altar_brace.json` makes the brace self-drop under the normal explosion-survival condition;
- `data/enshrouded/loot_table/blocks/flame_altar_rune.json` does the same for the rune;
- `ModItems` already supplied both `BlockItem`s before PR #100; no duplicate item registration was added.

The acquisition recipes intentionally use vanilla materials and do not add an optional-mod dependency or a second progression gate.

### Bounded validator and protection boundary

`FlameAltarStructureValidator` reads only the nine positions of the canonical 3×3 footprint. It never mutates formation state and never force-loads chunks.

Formation fails closed for:

- missing required components;
- wrong brace orientation;
- a duplicate controller inside the footprint;
- a required position whose chunk is not already available;
- `ProtectedAreaService` returning `PROTECTED` for `MutationKind.RITUAL_STRUCTURE`;
- `ProtectedAreaService` returning `INDETERMINATE`.

Only a fully loaded, unprotected, structurally exact shell returns `VALID`.

### Formation, Sanctuary and purification authority

A bare or merely assembled `enshrouded:flame_altar` does not imply an active Sanctuary. The physical formation lifecycle reaches the existing Flame Ward path only after the controller commits a valid `FORMED` state.

Breaking/unforming the structure calls the existing Flame Ward removal path. Structural lifecycle never resets or replaces Flame progression, Passage, ritual inventory or the canonical ward/purification services.

A separate player-built Purification Shrine is intentionally **not** introduced. This preserves the single physical authority path rather than adding a competing shrine/controller.

### Unform, reform and restart recovery

Removing a required shell part performs a bounded local search for a formed controller and deterministically revokes formation. Removing the controller clears surviving shell presentation and deactivates Sanctuary.

Replacing a missing part does not passively reform the complex. A new explicit controller interaction must validate and commit formation again. Structural unform/reform does not consume or duplicate the ritual offering.

Persisted `FORMED` is recovery intent rather than immediate gameplay authority:

- deserialization leaves live authority inactive until bounded world validation re-confirms the shell;
- recovery does not force-load missing footprint chunks;
- an unavailable required chunk indexes a targeted retry for that chunk only;
- protection `INDETERMINATE` remains inactive and is not treated as a chunk-wait condition;
- successful recovery restores shell presentation and the existing canonical Sanctuary provider;
- invalid recovery revokes formation instead of trusting stale NBT.

`FlameAltarRestartGameTests` provides the two-boot formation/recovery sentinel.

## Shroud Core Nest contract

`ShroudCoreNestLayout` is an immutable, bounded composition contract. It does not access a world, place blocks, load chunks, register worldgen or own Shroud state.

Both Ordinary and Deadly variants contain exactly one `CORE_ANCHOR` at the layout origin. Other roles are presentation/environmental composition hints:

- `RIB`;
- `ROOT_VEIN`;
- `SLUDGE_BASIN`;
- `HANGING_GROWTH`;
- `RUIN`.

The footprint is bounded to a maximum horizontal offset of four blocks. The Deadly variant is denser/distinct while retaining the same single authoritative Core anchor.

This is an implemented **layout/authority contract**, not an implemented in-world set piece. A production consumer remains open. Any future consumer must use the existing Shroud Core authority, remain loaded-chunk/bounded, respect protection/wards through the canonical mutation boundary and must not create additional cores.

## Lich manifestation landmark contract

`LichManifestationLandmarkLayout` is immutable and data-only. It contains exactly one `ENCOUNTER_ORIGIN` and bounded narrative composition roles:

- `RITUAL_DAIS`;
- `BROKEN_HALO`;
- `SPECTRAL_ANCHOR`;
- `PORTAL_FRAME`;
- `BONE_MOTIF`.

The Level-1 layout remains within a five-block horizontal composition radius and owns no encounter ID, Story state, provider selection, boss spawn/defeat lifecycle or reward.

This is likewise an implemented **layout/authority contract**, not an in-world landmark. Any future placement/encounter-location consumer must feed the existing Stage 06 manifestation/Story service and preserve its exactly-once reward semantics; the layout itself must never spawn a second boss or create separate Story state.

## TDD and regression evidence

Stage 10.09 has explicit RED→GREEN evidence rather than compilation-only claims.

- Formation authority RED began at `ddfd78dfc986285efb95293c3c15431062e2cacf`.
- Set-piece contract RED `a501f11d6bd03501c2f4683d937095fe40b026f6` failed at unit compilation before the two layout classes existed.
- Expansion restart-test isolation was corrected at `9dd54e27203207fb08c53b1cc5a85b30110857ce` without changing production expansion semantics.
- PR #99 later hardened that restart sentinel against saturation and merged to `main` as `650c5c99308415a94ed51c9abd9abea1b5b26c10`.
- Flame shell acquisition RED `92eab5e772184b47261196973926f2d1be659d30` ran 365 tests and failed exactly two new tests with `NoSuchFileException` for the intentionally missing recipe/loot resources.
- GREEN acquisition HEAD `2b647d9e4dde39f4205b5082eca1ba9481b1e7d6` adds those four resources and no art/worldgen authority.

## Verification evidence

### Original 10.09 implementation

Final PR #97 HEAD `7d9317fd0d211aaf6dce36298b84767408b2be20` passed:

- Level 1 Release Readiness `34188303297 / 101940989577` — `completed/success`;
- Enshrouded CI `34188303299 / 101941000504` — `completed/success` across unit tests, performance baselines, NeoForge build, GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke.

PR #97 merged as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`; independent post-merge Release Readiness `34189278455` and Enshrouded CI `34189278499` passed on that baseline.

### Acquisition correction

PR #100 final HEAD `2b647d9e4dde39f4205b5082eca1ba9481b1e7d6` passed:

- Level 1 Release Readiness `34225536910` — `completed/success`;
- Enshrouded CI `34225536909 / job 102058621586` — `completed/success`, including GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke.

PR #100 merged as `ed122c42f0eee0e706219361e30c9e1f05416a6d`.

Independent post-merge verification on that exact `main` also passed:

- Level 1 Release Readiness `34226166892` — `completed/success`;
- Enshrouded CI `34226166913 / job 102060710827` — `completed/success` across the complete matrix.

The latest physical modlist consulted for this checkpoint contains **603 mods** on NeoForge `21.1.248`. Earlier 607/612 pack counts in prior Stage 10 dossiers are historical checkpoint evidence, not the current physical baseline.

## Open handoffs / next work

Stage 10.09 remains operationally open for two distinct reasons; neither is hidden inside 10.10:

1. **User art handoff — Flame shell render resources.** Supply the approved brace/rune blockstates, block/item models and textures, including a visibly distinct formed/unformed presentation. Automated CI cannot substitute for this art decision.
2. **Placement architecture — Core Nest and Lich landmark.** Select/approve a production consumer before implementation. The current layout classes deliberately do not mutate the world. Any later implementation must be bounded, fail-closed and authority-preserving.

After those handoffs are satisfied or explicitly re-scoped by the user, Stage 10.10 can perform the final visual/compatibility acceptance matrix: screenshots, renderer/shader/Sodium coexistence, reconnect/resource reload, accessibility variants and full 603-mod client smoke.

`ART APPROVED` remains open. No document may infer automatic worldgen or rendered formed-state assets from the Stage 10.09 layout/Java contracts alone.
