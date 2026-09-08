# Stage 10.09 — Multiblock and set-piece pass

Status: TECHNICALLY COMPLETE / IMPLEMENTATION MERGED / POST-MERGE VERIFIED / ART APPROVED OPEN

## Scope

Stage 10.09 closes the technical multiblock/set-piece checkpoint without creating a second gameplay authority.

Delivered:

- the canonical player-built 3×3 Flame Altar formation lifecycle;
- bounded validation, explicit formation, deterministic unform/reform and restart recovery;
- Sanctuary activation gated by successful physical formation;
- a bounded data-only Shroud Core Nest composition kit;
- a bounded data-only Level-1 Lich manifestation landmark composition kit;
- regression coverage for authority, claims/protection, lifecycle, chunk availability, persistence and dedicated-server behavior.

Not delivered or implied:

- no automatic worldgen/placement path for the Shroud Core Nest or Lich landmark;
- no second Shroud Core identity;
- no second Lich/Story encounter lifecycle;
- no separate Purification Shrine controller or Sanctuary provider;
- no visual `ART APPROVED` decision from automated CI.

## Canonical Flame Altar formation contract

### Authoritative anchor

`FlameAltarBlockEntity` remains the single physical/gameplay controller for the Flame complex. The four `FlameAltarBraceBlock` and four `FlameAltarRuneBlock` shell pieces own no BlockEntity, ritual inventory, progression state, Sanctuary state or reward authority.

The shell pieces expose presentation/validation state only:

- cardinal braces must face inward toward the controller;
- corner runes occupy the four diagonal positions;
- the shell `FORMED` property mirrors the controller's committed formation state;
- a freshly placed complete shell remains `UNFORMED` until explicit controller interaction validates it.

The activation UX is therefore:

1. player places the center Altar plus the required 3×3 shell;
2. first explicit controller interaction performs bounded validation;
3. validation success commits `FORMED` and activates the existing canonical Flame Ward/Sanctuary path;
4. a later controller interaction opens the existing ritual menu;
5. validation failure remains fail-closed and opens no ritual menu.

There is no per-tick structure scan.

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

A bare or merely assembled `enshrouded:flame_altar` no longer implies an active Sanctuary. `FlameWardRuntime.onAltarLoaded(...)` is reached by the physical formation lifecycle only after the controller commits a valid `FORMED` state.

Breaking/unforming the structure calls the existing Flame Ward removal path. Structural lifecycle never resets or replaces Flame progression, passage, ritual inventory or the canonical ward service.

A separate player-built Purification Shrine is intentionally **not** introduced in 10.09. Stage 10.05 already established the Flame Altar/Sanctuary focus as the purification presentation center, and Stage 10.09 preserves that single physical authority path instead of adding a competing shrine/controller. Purification presentation remains integrated with the Flame complex and the existing canonical Flame Ward runtime.

### Unform and reform

Removing a required shell part performs a bounded local search for a formed controller and deterministically revokes formation. Removing the controller itself clears the surviving shell presentation and deactivates Sanctuary.

Repairing a missing shell component does not passively reform the complex. A new explicit controller interaction must validate and commit formation again.

Structural unform/reform does not consume or duplicate the ritual offering.

### Persistence and restart recovery

Formation persistence is fail-closed:

- persisted `FORMED` is recovery intent, not immediate gameplay authority;
- deserialization leaves the live controller `UNFORMED` until bounded world validation re-confirms the physical shell;
- recovery does not force-load missing footprint chunks;
- an unavailable required chunk indexes a targeted retry for that chunk only;
- protection `INDETERMINATE` remains inactive and does not masquerade as a chunk-wait condition;
- successful recovery restores shell presentation and the existing canonical Sanctuary provider;
- invalid structure recovery revokes formation instead of silently trusting stale NBT.

`FlameAltarRestartGameTests` provides a real two-boot sentinel proving block state, BlockEntity formation intent, ritual inventory and Sanctuary restoration across a server restart.

## Shroud Core Nest set-piece contract

`ShroudCoreNestLayout` is an immutable, bounded composition contract. It does not access a world, place blocks, load chunks, register worldgen or own Shroud state.

Both Ordinary and Deadly variants contain exactly one `CORE_ANCHOR` at the layout origin. All other roles are decorative/environmental composition hints:

- `RIB`;
- `ROOT_VEIN`;
- `SLUDGE_BASIN`;
- `HANGING_GROWTH`;
- `RUIN`.

The footprint is bounded to a maximum horizontal offset of four blocks. The Deadly variant is compositionally larger/distinct while retaining the same single authoritative Core anchor.

This closes the Stage 10.09 **layout/authority contract** only. Automatic structure/worldgen placement is not present and is not inferred from these classes.

## Lich manifestation landmark contract

`LichManifestationLandmarkLayout` is likewise immutable and data-only. It contains exactly one `ENCOUNTER_ORIGIN` and bounded narrative composition roles:

- `RITUAL_DAIS`;
- `BROKEN_HALO`;
- `SPECTRAL_ANCHOR`;
- `PORTAL_FRAME`;
- `BONE_MOTIF`.

The Level-1 layout remains within a five-block horizontal composition radius and does not own encounter IDs, Story state, provider selection, boss spawn/defeat lifecycle or rewards. The existing Stage 06 manifestation/Story service remains canonical.

No automatic worldgen/placement behavior was introduced for this landmark in 10.09.

## TDD and regression evidence

The checkpoint was developed and repaired through explicit RED→GREEN evidence rather than treating compilation as proof.

- Initial formation authority RED began at commit `ddfd78dfc986285efb95293c3c15431062e2cacf`, requiring a formed altar before Sanctuary semantics.
- Set-piece contract RED: `a501f11d6bd03501c2f4683d937095fe40b026f6`; unit compilation failed because the two layout classes did not yet exist.
- A two-boot Shroud expansion regression in the same PR was isolated at `9dd54e27203207fb08c53b1cc5a85b30110857ce`: the test had attributed scheduler-global `appliedCells()` work to its sentinel core even when persisted unrelated cores could consume the budget. The fix changed test isolation only, not expansion runtime.
- Final implementation PR #97 HEAD: `7d9317fd0d211aaf6dce36298b84767408b2be20`.

GameTest coverage includes:

- fresh shell components have no gameplay authority;
- exact 3×3 validation;
- missing component;
- wrong cardinal orientation;
- duplicate controller;
- protected and indeterminate protection decisions;
- required chunk unavailable without forcing it;
- unformed ritual/Sanctuary gating;
- explicit formation interaction and idempotent subsequent interaction;
- invalid activation fail-closed;
- required-part break → unform → repair → explicit reform;
- controller removal and orphan-state cleanup;
- persisted formation intent requiring live revalidation;
- real two-boot formed-altar recovery;
- existing Level-1 vertical scenario compatibility.

Pure set-piece layout tests additionally enforce one authoritative anchor/origin, unique offsets, bounded footprints, deterministic translation and immutable returned placements.

A synthetic GameTest placement/worldgen path was deliberately not added for the two data-only layouts: doing so would introduce a world-mutation path that Stage 10.09 does not otherwise implement. Their gameplay authorities remain covered by the existing Core and manifestation server tests.

## Final implementation and post-merge evidence

Final PR #97 HEAD `7d9317fd0d211aaf6dce36298b84767408b2be20` passed both required workflows before merge:

- Level 1 Release Readiness `34188303297 / 101940989577` — `completed/success`;
- Enshrouded CI `34188303299 / 101941000504` — `completed/success`, including unit tests, performance baselines, NeoForge build, GameTests, SavedData two-boot reload, isolated Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke.

PR #97 merged as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`.

Independent post-merge verification on that exact `main` also passed:

- Level 1 Release Readiness `34189278455 / 101943814346` — `completed/success`;
- Enshrouded CI `34189278499 / 101943814741` — `completed/success`, including GameTest server, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke.

The physical pack baseline consulted for this checkpoint remains **612 mods**, NeoForge `21.1.248`. The Notion master catalog was reconciled as design/catalog context; no optional visual provider was promoted into gameplay authority by 10.09.

## Manual visual/world acceptance still open

Technical correctness does not satisfy the Stage 10 art gate. The following remain for 10.10/manual review:

- unformed vs formed Flame Altar screenshots from representative angles/distances;
- proof that the formed 3×3 reads as one authored ritual construction rather than a visible cube-grid/checkerboard;
- Sanctuary/purification readability with full, reduced-sensory and minimal presentation settings;
- eventual in-world visual review for Shroud Core Nest and Lich landmark when an approved placement/worldgen consumer exists;
- renderer/shader/Sodium coexistence;
- reconnect/resource-reload visual behavior;
- full 612-mod client smoke.

`ART APPROVED` remains open. Stage 10.10 owns the final visual/compatibility acceptance matrix and must not infer worldgen presence from the Stage 10.09 layout contracts.
