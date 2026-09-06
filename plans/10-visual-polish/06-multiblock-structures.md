# Stage 10 — Multiblock Structures

## Principle

Multiblocks are approved as a visual/gameplay presentation strategy when they make major systems feel monumental. They must use an **authoritative-anchor pattern**: one controller owns gameplay state; surrounding parts validate shape, relay interaction and render composition.

No satellite block may own duplicated Flame level, Shroud lifecycle, ritual receipt, Sanctuary state or Story progression.

## Candidate 1 — Flame Altar Complex

### Prototype A: 3×3
- center: authoritative altar/controller;
- cardinal braces/pedestals;
- corner stone/rune pieces;
- vertical brazier/halo geometry centered over controller.

Advantages:
- fits bases more easily;
- clear build footprint;
- still visually larger than a single functional block.

### Prototype B: 5×5
- central altar;
- four ritual pillars;
- partial arches/rings;
- outer rune channel;
- greater room for progression-driven visual additions.

Gate: only choose 5×5 if in-game prototype shows the added scale improves fantasy without becoming annoying to place/use.

### Formation behavior
- deterministic orientation;
- controller performs bounded validation on placement/neighbor change/interaction, not full scan every tick;
- optional visual formation guide/preview;
- missing pieces should produce understandable feedback;
- breaking a required part transitions visual/functional state according to explicit contract;
- dismantling never duplicates ritual rewards.

## Candidate 2 — Purification Shrine

Suggested footprint: 3×3 or cross-shaped.

Pieces:
- central purification focus/controller;
- ward stones;
- engraved floor or plinth;
- vertical clean-light frame;
- optional decorative braziers/anchors.

Purpose:
- give Sanctuary/purification a physical visual center;
- show active protection state without depending solely on fog/HUD;
- retain one canonical provider/controller.

## Candidate 3 — Shroud Core Nest

This is primarily a worldgen/set-piece formation, not a player-built machine.

Composition kit:
- authoritative Shroud Core;
- organic ribs/arches;
- thick root/vein channels;
- sludge basins;
- hanging growths;
- ruined terrain/architecture pieces;
- Ordinary/Deadly variants.

Rules:
- surrounding structure visually points to the real core;
- decorative blocks do not become extra cores;
- core destruction/regression uses existing authority;
- worldgen remains bounded and does not create chunk-spanning validation work.

## Candidate 4 — Lich Shrine / Manifestation Set Piece

Narrative landmark for Stage 06 presentation.

May include:
- ritual dais;
- broken halo/arch;
- skull/bone motifs;
- spectral anchors;
- manifestation portal framing.

It stages the encounter; it does not own boss lifecycle. Story/Lich provider remains canonical.

## Domum Ornamentum and pack assets

Domum Ornamentum can be evaluated as a structural/decor provider for compatible generated/player-built architecture, especially if its shapes materially improve arches, pillars, trims or masonry. However:
- do not make it an implicit dependency because it happens to be installed;
- exact version/API/content IDs must be rechecked before implementation;
- an Enshrouded-native fallback or explicit hard-dependency decision is required;
- do not rely on player-created arbitrary DO variants as hidden multiblock keys unless the validator contract explicitly supports them.

Create/Supplementaries/Design n' Decor/etc. are aesthetic benchmarks unless a specific integration is deliberately designed.

## Technical validation contract

For every functional multiblock test:
- formation success;
- invalid orientation;
- missing component;
- duplicate controller;
- break/reform idempotence;
- unload/reload;
- restart persistence;
- chunk-edge placement;
- claim/protection interaction where world mutation occurs;
- dedicated-server behavior;
- no reward duplication after reform/reconnect.

## Rendering rule

Large multiblock beauty should come primarily from static block models plus a small number of meaningful animated anchors. Do not create a BlockEntity renderer on every decorative component.
