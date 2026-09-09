# Stage 10.09 — Multiblock and set-piece pass

Status: LOGIC/AUTHORITY CHECKPOINT IMPLEMENTED / FLAME SHELL SURVIVAL ACQUISITION + SHROUD CORE NEST CONSUMER MERGED + VERIFIED / LICH ENCOUNTER-WIRING BLOCKER RECORDED / SHELL ART + LICH PLACEMENT CONTRACT/CONSUMER OPEN / STAGE 10.09 NOT CLOSED

## Scope and current truth

Stage 10.09 establishes the authoritative multiblock/set-piece contracts without creating a second gameplay authority. It is **not** a claim that every promised set piece is already visible in-world.

Implemented and verified:

- the canonical player-built 3×3 Flame Altar formation lifecycle;
- bounded validation, explicit formation, deterministic unform/reform and restart recovery;
- Sanctuary activation gated by successful physical formation;
- survival recipes for the complete four-brace + four-rune shell and self-drop loot tables for both shell blocks;
- a bounded data-only Shroud Core Nest layout/authority contract;
- the bounded Ordinary Shroud Core Nest production worldgen consumer merged in PR #103 downstream of the existing canonical `ShroudCoreFeature` placement path;
- a bounded data-only Level-1 Lich manifestation landmark layout/authority contract;
- the Lich landmark runtime audit merged in PR #104, which separates presentation-only placement from still-blocked encounter wiring;
- regression coverage for authority, claims/protection, lifecycle, chunk availability, persistence and dedicated-server behavior.

Still open and therefore **not** silently counted as complete:

- `flame_altar_brace` / `flame_altar_rune` blockstates, block models, item models, textures and visibly distinct `FORMED=false|true` presentation;
- an explicit placement/material/distribution contract plus production placement/worldgen-only consumer for `LichManifestationLandmarkLayout`;
- a separate explicit gameplay-trigger contract before any encounter-location/wiring path may invoke canonical `ManifestationEncounterService.start(...)`;
- final in-game screenshots and full **595-mod** client visual smoke.

The shell render assets are a user-owned art handoff. This repository checkpoint must not invent substitute placeholder art to make automated review green.

Automatic Core Nest placement is now present only through the bounded Ordinary consumer merged in PR #103. No automatic Lich landmark placement or encounter-start path is introduced because its placement contract and gameplay-trigger contract remain separately undefined. Leaving those Lich handoffs open is preferable to silently creating new worldgen or Story/gameplay semantics during visual-polish closeout.

A separate Purification Shrine/controller remains intentionally deferred. Purification/Sanctuary presentation stays integrated with the existing Flame complex and canonical Flame Ward/purification runtime.

## Current deliverable matrix

| Deliverable | Runtime state | Authority state | 10.09 state |
|---|---|---|---|
| Flame Altar 3×3 lifecycle | implemented | `FlameAltarBlockEntity` + existing Flame/Sanctuary runtime remain canonical | **IMPLEMENTED / VERIFIED** |
| Brace/rune survival acquisition | recipes + self-drop loot merged in PR #100 | no new gameplay authority | **IMPLEMENTED / VERIFIED** |
| Brace/rune formed/unformed rendering | Java `FORMED` property exists; required client resources are not yet supplied | presentation only | **OPEN — USER ART HANDOFF** |
| Shroud Core Nest layout + Ordinary consumer | immutable bounded Ordinary/Deadly composition exists; bounded Ordinary production consumer merged in PR #103 | exactly one existing canonical Core anchor; consumer remains downstream of `ShroudCoreFeature` and canonical mutation authority | **IMPLEMENTED / VERIFIED FOR CURRENT CONSUMER SCOPE** |
| Lich manifestation landmark layout | immutable bounded Level-1 composition exists; no production placement consumer yet | Stage 06 Story/manifestation lifecycle remains canonical | **LAYOUT CONTRACT IMPLEMENTED / PLACEMENT CONTRACT + CONSUMER OPEN / ENCOUNTER WIRING BLOCKED** |
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

`ShroudCoreNestLayout` remains an immutable, bounded composition contract. The layout itself does not access a world, place blocks, load chunks, register worldgen or own Shroud state.

Both Ordinary and Deadly variants contain exactly one `CORE_ANCHOR` at the layout origin. Other roles are presentation/environmental composition hints:

- `RIB`;
- `ROOT_VEIN`;
- `SLUDGE_BASIN`;
- `HANGING_GROWTH`;
- `RUIN`.

The footprint is bounded to a maximum horizontal offset of four blocks. The Deadly variant is denser/distinct while retaining the same single authoritative Core anchor.

PR #103 adds the current production consumer without changing that layout authority. `ShroudCoreNestWorldgenConsumer` is constructed from the existing `MutationAuthority` and runs only after canonical `ShroudCoreFeature` successfully places/registers the real Core. It:

- consumes only the bounded Ordinary layout in the current production path;
- skips `CORE_ANCHOR`, because `ShroudCoreFeature` remains the sole Core placement/lifecycle owner;
- resolves decorative targets against local `WORLD_SURFACE_WG` and never performs a world scan;
- requires `WorldGenLevel.ensureCanWrite(target)` before mutation;
- requires canonical `MutationAuthority.canMutate(..., MutationKind.GROWTH_PLACEMENT)` for each decorative target;
- never force-loads chunks and fails closed on protected/warded/non-replaceable targets;
- maps the current roles only to existing first-party Enshrouded blocks rather than inventing external/provider materials;
- does not create a second Core, SavedData path, activation queue or Shroud lifecycle.

The current PR #103 scope does not silently promote the Deadly layout or fluid placement into a second unreviewed worldgen path. Any future expansion of that scope requires its own explicit contract and validation.

## Lich manifestation landmark contract

`LichManifestationLandmarkLayout` is immutable and data-only. It contains exactly one `ENCOUNTER_ORIGIN` and bounded narrative composition roles:

- `RITUAL_DAIS`;
- `BROKEN_HALO`;
- `SPECTRAL_ANCHOR`;
- `PORTAL_FRAME`;
- `BONE_MOTIF`.

The Level-1 layout remains within a five-block horizontal composition radius and owns no encounter ID, Story state, provider selection, boss spawn/defeat lifecycle or reward.

PR #104 establishes that two different downstream concerns must remain separate:

1. **Placement/worldgen-only consumption** is permitted but still requires an explicit contract for origin/distribution/frequency, role-to-material mapping, loaded-chunk/no-force-load behavior, canonical protection/mutation handling, deterministic/idempotent placement and restart/chunk lifecycle. Placement alone must not start an encounter.
2. **Encounter-location/wiring** remains blocked until an explicit server-side gameplay trigger is defined/proven. `ManifestationEncounterService.start(...)` is the canonical Stage 06 encounter-start boundary; the landmark must not create a second Story/boss/reward authority or invent proximity, Shroud/Core, interaction, item, ritual, login/tick, command or worldgen-time trigger semantics.

The layout itself remains presentation/spatial composition only.

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

The PR #98 reconciliation checkpoint consulted a **603-mod** physical snapshot on NeoForge `21.1.248`; that count remains historical checkpoint evidence. The current physical modlist was rechecked on 2026-09-09 and is the authority now: **595 mods** on NeoForge `21.1.248`.

### Shroud Core Nest production consumer

PR #103 final HEAD `7b7d01e94e6fe8037a579a812a2c8a723e834fc6` passed:

- exact-head Level 1 Release Readiness `34355655800` — `completed/success`;
- exact-head Enshrouded CI `34355655793` — `completed/success` across the full matrix.

PR #103 merged as `c4b555cb4f0d199bed2ebef80b7cd06b306913c0`. Independent post-merge Release Readiness `34368796815` and Enshrouded CI `34368796770` passed on exact `main@c4b555cb4f0d199bed2ebef80b7cd06b306913c0`.

### Lich blocker audit and merged-state reconciliation

PR #104 final HEAD `3b96578850aa23b5eae3df76aa95e5171398faf2` passed exact-head Release Readiness `34369500109` and Enshrouded CI `34369500120`, then merged as `9e1b4a77d1d24e2c00ebaec5160458ffbe1184c3`. Independent post-merge Release Readiness `34370682361` and Enshrouded CI `34370682102` passed on that exact main.

PR #105 reconciled the canonical Stage 10 status after #103/#104. Its exact PR HEAD `b471165c4d6155908e11998fea36b9c823901a5b` passed Release Readiness `34372629522` and Enshrouded CI `34372629554`, then squash-merged as `005b195fc058234be159e37018f57ebf77731f62`. Independent post-merge Release Readiness `34373476741` and Enshrouded CI `34373476952` both completed `success` on exact `main@005b195fc058234be159e37018f57ebf77731f62`.

## Open handoffs / next work

Stage 10.09 remains operationally open; none of these handoffs is hidden inside 10.10:

1. **User art handoff — Flame shell render resources.** Supply the approved brace/rune blockstates, block/item models and textures, including a visibly distinct formed/unformed presentation. Automated CI cannot substitute for this art decision.
2. **Lich placement architecture.** Define/approve the bounded landmark placement-only contract before implementation: origin/distribution/frequency, role-to-material mapping, protection/mutation boundary, loaded-chunk/no-force-load behavior and deterministic lifecycle/idempotence.
3. **Lich encounter trigger/wiring.** Remains blocked until the explicit gameplay trigger feeding canonical `ManifestationEncounterService.start(...)` is defined/proven. Placement must not be used as a generic substitute for this missing gameplay contract.

After those handoffs are satisfied or explicitly re-scoped by the user, Stage 10.10 can perform the final visual/compatibility acceptance matrix: screenshots, renderer/shader/Sodium coexistence, reconnect/resource reload, accessibility variants and full **595-mod** client smoke.

`ART APPROVED` remains open. No document may infer rendered formed-state assets, Lich landmark placement or encounter-start behavior from the Stage 10.09 layout/Java contracts alone.
