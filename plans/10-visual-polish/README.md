# Stage 10 — Art Direction, 3D Assets, Animation, VFX and Visual Polish

Stage 10 is the post-Level-1 visual production milestone. It raises Enshrouded from technically complete to visually authored without redesigning the gameplay authority proven by Stages 00–09.

## Canonical documents

1. [`MASTER-PLAN.md`](MASTER-PLAN.md) — complete Stage 10 contract and acceptance gates.
2. [`00-scope-and-principles.md`](00-scope-and-principles.md) — scope, non-goals and authority boundaries.
3. [`01-render-animation-stack.md`](01-render-animation-stack.md) — GeckoLib/Fusion/Lodestone/AzureLib/etc. dependency ADR.
4. [`02-visual-bible.md`](02-visual-bible.md) — shape language, palette, materials, motion and UI identity.
5. [`03-hero-assets.md`](03-hero-assets.md) — Flame Altar, Shroud Core, Lich Skull, Sanctuary and Lich presentation.
6. [`04-shroud-world-art.md`](04-shroud-world-art.md) — environmental corruption families and Fusion enhancement strategy.
7. [`05-vfx-ui-audio-polish.md`](05-vfx-ui-audio-polish.md) — event VFX, HUD skin, accessibility and audio art pass.
8. [`06-multiblock-structures.md`](06-multiblock-structures.md) — Flame complex, purification shrine, Shroud nest and Lich set pieces.
9. [`07-validation-and-performance.md`](07-validation-and-performance.md) — CI, dedicated-server, renderer compatibility, budgets and manual pack smoke.
10. [`08-execution-order.md`](08-execution-order.md) — canonical 10.01 → 10.10 task sequence.
11. [`09-asset-review-matrix.md`](09-asset-review-matrix.md) — initial KEEP/POLISH/REPLACE/CREATE audit and dependency inventory.
12. [`STATUS.md`](STATUS.md) — Stage 10 planning checkpoint.

## Key technical decisions

- **GeckoLib 4.9.2** is the primary runtime for animated Enshrouded 3D hero assets.
- **AzureLib 3.1.11** is evaluated but intentionally not mixed as a parallel animation engine.
- **Fusion 1.3.15** is an optional/soft environmental material enhancement with base fallbacks.
- **Lodestone 1.8.2** is task-gated for advanced VFX only if the existing Stage 07 pipeline cannot reach the desired quality cleanly.
- Multiblocks use one authoritative controller/anchor; decorative pieces never duplicate progression or Shroud state.
- Stage 10 uses original assets and does not copy proprietary Enshrouded-game or other-mod assets.

## Current checkpoint

**PLANNING COMPLETE / IMPLEMENTATION NOT STARTED.**

This planning branch must stop after the documentation PR. Implementation starts only after explicit user instruction.
