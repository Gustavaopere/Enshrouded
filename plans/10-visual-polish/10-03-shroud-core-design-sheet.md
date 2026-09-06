# Stage 10.03 — Shroud Core design sheet

**Status:** TECHNICAL ART IMPLEMENTATION / P0 IN-GAME ART REVIEW REQUIRED

## Fantasy and silhouette

The Shroud Core is a living corruption anchor rather than a decorated cube. The authored silhouette is built around an exposed floating heart held by a broken rib/husk cage, six rooted attachment channels, hanging tendrils and thin membranes. The model intentionally exceeds the visual mass of one vanilla cube while keeping one canonical Shroud Core block/entity as the mechanical anchor.

### Ordinary profile

- low, broad roots that visually feed nearby veins/growth;
- dark charcoal/violet husk with cold blue-violet energy seams;
- exposed heart centered inside negative space rather than buried in a solid cube;
- asymmetric ribs and tendrils to avoid a machine-like radial symmetry;
- slower independent heart/husk breathing cycle.

### Deadly profile

Deadly is not a recolor. It uses `geometry.shroud_core_deadly`, a more open/split husk, longer tendrils, a larger exposed heart and a dedicated `deadly_thorns` bone with eight outward spikes. Material shifts toward rust-black/crimson and the threat animation runs at a shorter, more aggressive pulse cadence.

## Material hierarchy

1. **Outer husk:** near-black charcoal / corrupted purple or rust-black in Deadly.
2. **Inner heart:** saturated arcane violet for Ordinary, crimson-red for Deadly.
3. **Vein channels:** secondary energy lines tying the core to the Shroud ecology family.
4. **Selective emissive:** heart, narrow vein channels and fracture accents only. The full shell never becomes emissive.

The four first-party 128×128 atlases are:

- `shroud_core.png`
- `shroud_core_glowmask.png`
- `shroud_core_deadly.png`
- `shroud_core_deadly_glowmask.png`

## Animation sheet

- `animation.shroud_core.idle`: slow inner-heart pulse, delayed husk breathing and small tendril drift.
- `animation.shroud_core.threat`: faster heart contraction, wider husk motion, stronger tendril response and Deadly thorn movement.
- `animation.shroud_core.collapse`: one-shot contraction/cave-in authored as presentation downstream of canonical destruction.

Animation completion never writes lifecycle, severity, region, exposure or terrain state.

## Authority map

`ShroudSavedData` / `ShroudCoreService` remain lifecycle authority. The renderer receives only `PresentationProfile.ORDINARY|DEADLY`, derived read-only from the canonical `ShroudQuery` result at the physical core. `ShroudSeverity.DEADLY` is the only Deadly selector; `tier` is not a visual authority. The presentation profile is synchronized to clients and deliberately excluded from persisted gameplay state.

The collapse trigger is emitted only after an authoritative ACTIVE → DESTROYED mutation has succeeded and `ShroudCoreDestroyedEvent` has been posted. The block is never kept alive to wait for animation.

## Performance budget

- one existing BlockEntity remains the anchor; no decorative BlockEntities are added;
- one GeckoLib controller per loaded core;
- severity refresh is server-only, staggered by block position and limited to once per 20 ticks per loaded core;
- the query uses the canonical indexed Shroud lookup and therefore remains O(1) after the dimension snapshot index is current;
- presentation packets are sent only when Ordinary/Deadly actually changes;
- no particles are spawned continuously by this task; Stage 10 VFX remains separately budgeted.

## P0 manual acceptance still required

Technical CI does not grant ART APPROVED. Before final P0 art approval capture in-game evidence at realistic FOV/distance for Ordinary and Deadly profiles, verify the model in surrounding Shroud ecology, verify reduced-effects readability, and run the external full 607-mod-pack visual smoke. If the collapse clip is not perceptible because physical removal wins the client timing race, retain gameplay semantics and solve the presentation with a transient client-only effect rather than delaying authoritative destruction.
