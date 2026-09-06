# Stage 10 — Execution Order

Work Stage 10 sequentially so art direction and renderer architecture are fixed before large asset production.

## 10.01 — Visual Bible + dependency ADR

**Purpose:** freeze art language and technical stack.

Deliver:
- `02-visual-bible.md` approved;
- `01-render-animation-stack.md` approved;
- asset inventory/matrix;
- exact build/dependency proposal for GeckoLib/Fusion/optional VFX libs;
- no runtime dependency added until implementation task begins.

Gate: no hero modeling should be considered final before this closes.

## 10.02 — Flame Altar hero asset

Deliver:
- 3×3 and/or 5×5 composition prototype;
- final footprint decision;
- Blockbench model source;
- GeckoLib central animation design;
- textures/emissive;
- progression visual variants;
- multiblock controller contract if chosen;
- screenshots/performance evidence.

## 10.03 — Shroud Core hero asset

Deliver:
- Ordinary core design;
- Deadly variant/profile;
- idle/threat/destroy animation set;
- core nest attachment language;
- particle/render budget;
- screenshots.

## 10.04 — Lich Skull + manifestation polish

Deliver:
- original Lich Skull 3D trophy;
- item transforms;
- subtle render/animation layer if justified;
- provider-safe manifestation/defeat VFX;
- no Stage 06 gameplay redesign.

## 10.05 — Sanctuary / Purification presentation

Deliver:
- purification focus/ward asset;
- Sanctuary visual language;
- purification sequence;
- optional shrine multiblock decision;
- latent-Shroud visual behavior documented.

## 10.06 — Shroud world-art family

Deliver:
- growth/vein/membrane/crust/sludge family;
- Ordinary/Deadly variants;
- withered/purified states;
- Fusion connected/overlay enhancement with fallback;
- repetition/tiling review.

## 10.07 — HUD/UI art pass

Deliver:
- production frame;
- custom icons;
- Ordinary/Deadly warning language;
- Madness/passage treatment;
- minimal mode;
- localization/accessibility review.

## 10.08 — Advanced VFX pass

Deliver:
- authored transition/event sequences;
- decision whether Lodestone is actually needed;
- reduced-effects path;
- strict budgets and compatibility evidence.

## 10.09 — Multiblock/set-piece pass

Deliver:
- Flame complex final architecture;
- purification shrine if approved;
- Shroud core nest kit;
- Lich ritual landmark;
- authoritative-anchor tests and chunk-edge/reload cases.

## 10.10 — Visual QA / performance / release art gate

Deliver:
- full screenshot matrix;
- renderer compatibility matrix;
- performance evidence;
- resource reload/reconnect tests;
- provenance audit;
- dedicated server green;
- affected CI gates green;
- manual full 607-mod smoke recorded separately.

## PR discipline

Each implementation task should use a focused branch/PR and update the Stage 10 status after merge. A task is not marked complete from model creation alone; code, assets, screenshots, tests and provenance all need evidence.

## Current planning checkpoint

This branch is **planning only**. It must stop after the documentation PR is created. Do not automatically begin 10.01 runtime/dependency implementation until the user explicitly instructs it.
