# Stage 10 — Scope and Principles

**Status:** PLANNED — design/documentation only.
**Platform:** Minecraft 1.21.1 / NeoForge 21.1.248 / Java 21.
**Depends on:** Level 1 repository milestone completed through Stage 09.

## Goal

Turn the technically complete Level 1 into a visually authored, production-quality mod. Stage 10 owns models, textures, material language, animation, VFX, UI art, environmental set dressing and optional multiblock presentation.

## Hard boundaries

- No redesign of Shroud, Exposure, Madness, Flame, Sanctuary, terrain mutation, ecology, Story/Lich or integrations.
- Existing server-authoritative APIs remain the only gameplay authority.
- Visual state consumes canonical snapshots/events and must converge after reconnect, dimension change or packet delay.
- Client animation completion never awards progress, consumes ritual offerings, destroys cores or changes SavedData.
- No proprietary Enshrouded assets and no ripped Minecraft-mod assets.
- Optional visual libraries must fail soft; missing enhancement may reduce fidelity, never gameplay correctness.
- Dedicated server must remain free from physical-client renderer classloading.

## Art quality bar

Central objects are not approved merely because they have a custom texture. Hero assets must have intentional silhouette, authored materials, readable hierarchy, appropriate animation and in-game screenshot review.

The player should identify a Flame structure, a Shroud core and a Lich trophy from shape/material language before reading a tooltip.

## Stage deliverables

1. dependency/render ADR;
2. visual bible;
3. Flame Altar redesign;
4. Shroud Core redesign;
5. Lich Skull and Lich presentation pass;
6. Sanctuary/purification presentation;
7. Shroud world-art family;
8. HUD/UI art pass;
9. advanced VFX pass;
10. multiblock/set-piece pass;
11. visual QA, renderer compatibility, performance and provenance gates.

## Completion definition

Stage 10 is complete only when the hero assets no longer read as placeholders, the world/UI/VFX share one coherent art direction, optional enhancements have fallbacks, performance stays bounded and the final visuals survive in-game review in the real modpack.

See `MASTER-PLAN.md` for the canonical complete contract.
