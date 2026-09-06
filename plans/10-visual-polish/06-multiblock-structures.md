# Stage 10 — Multiblock Structures

## Principle

Multiblocks are approved as a visual/gameplay presentation strategy when they make major systems feel monumental. They must use an **authoritative-anchor pattern**: one controller owns gameplay state; surrounding parts validate shape, relay interaction and render composition.

No satellite block may own duplicated Flame level, Shroud lifecycle, ritual receipt, Sanctuary state or Story progression.

## Non-negotiable formation UX — built from blocks, formed as one construction

The player-built Stage 10 multiblocks must follow a **formation workflow**, not remain a visible pile of unrelated decorative blocks.

Required interaction model:

1. The player places the required component blocks according to the structure blueprint/ghost guide.
2. The structure remains visibly and functionally **UNFORMED** while incomplete.
3. The player uses a dedicated Enshrouded activator/tool or an explicit controller interaction to request formation.
4. The authoritative controller performs bounded deterministic validation of footprint, orientation, required components and protected-area rules.
5. On successful validation the assembly enters **FORMED** state and presents itself as a **single coherent construction**.
6. Interaction, animation state and gameplay authority are routed through the controller/anchor; decorative satellites are no longer independent gameplay machines.
7. Breaking a required structural component or explicitly dismantling the assembly causes a deterministic **UNFORM** transition without duplicating drops, ritual receipts or rewards.

### Visual formation requirement

**FORMED must look materially different from merely placing the blocks.** This is a hard art acceptance gate.

The implementation may combine techniques such as:
- formed-state block models that hide internal faces and visible seams;
- controller-driven composite/GeckoLib geometry spanning the structure;
- large arches, halos, flames, runic rings, membranes or mechanical/arcane elements that only appear after formation;
- formed casing variants and connected surfaces;
- controlled Fusion overlays/continuous textures where appropriate;
- animation that locks the components into one silhouette during the formation event.

The final result must read at normal gameplay distance as **one authored altar/shrine/machine**, not as a 3×3 or 5×5 checkerboard of Minecraft blocks.

It is acceptable for the underlying world to retain component block positions for collision, break handling and validation, but their final presentation must visually merge into the assembled structure. Visible grid seams, repeated cube faces and obvious placeholder masonry on the central hero multiblocks are rejection criteria unless deliberately hidden by the art direction.

### Formation event

Successful formation should itself be a premium visual moment, subject to final performance budget:
- short lock-in/assembly animation;
- rune activation traveling from outer components to the controller;
- emissive ramp-up;
- controlled particles/audio cue;
- final persistent idle animation or flame/core motion.

Formation animation is presentation only. Server-authoritative formation state is committed independently of animation completion; disconnect, lag or disabled effects cannot change gameplay outcome.

### Tool/activator contract

Stage 10 implementation must provide or explicitly designate a clear activation interaction. Preferred design is an **Enshrouded-native formation tool/ritual implement** with its own polished 3D item model rather than silently overloading a vanilla wrench.

The tool does not own multiblock state. It only asks the authoritative controller to validate/form/unform the structure.

Required feedback:
- valid structure → formation begins;
- incomplete structure → identify missing/invalid region without exposing hidden server data;
- wrong orientation → clear corrective feedback;
- protected/blocked formation → fail closed with understandable reason;
- already formed → idempotent result, no duplicate activation effects/rewards.

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

### Flame Altar formed appearance

The selected Flame Altar prototype must not expose the prototype footprint as its final artistic identity. When formed:
- a dominant central brazier/flame assembly must unify the silhouette;
- outer pedestals/runes must visually connect to the center through carved channels, arches, emissive paths or equivalent authored geometry;
- the final profile should have vertical hierarchy and look intentional from all four cardinal views;
- Flame progression may add or replace formed-state ornaments/animation layers without changing the canonical progression authority;
- the unformed blocks are construction ingredients; the formed altar is the actual hero asset.

### Formation behavior
- deterministic orientation;
- controller performs bounded validation on activation, relevant neighbor change and recovery, not a full scan every tick;
- optional visual formation guide/preview;
- missing pieces should produce understandable feedback;
- breaking a required part deterministically unforms or degrades the assembly according to explicit contract;
- dismantling never duplicates ritual rewards;
- formed visual state must reconstruct correctly after chunk unload/reload and full server restart from canonical persisted state.

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

If player-built, it follows the same **UNFORMED → activated validation → FORMED single-construction** contract as the Flame Altar.

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

Because this formation is generally world-authored rather than player-formed, it does not require the activation-tool workflow, but it must meet the same silhouette/cohesion bar.

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

## Technical state machine

At minimum every player-built multiblock must expose the following controller states:

`UNFORMED → VALIDATING → FORMED → UNFORMED`

An implementation may add a transient `FORMING` presentation state, but server gameplay must not depend on the animation reaching its last frame.

Rules:
- `VALIDATING` is bounded and deterministic;
- duplicate activation requests are idempotent;
- a second controller in the same candidate structure invalidates formation;
- unloaded required chunks fail closed rather than force-loading;
- restart recovery reconstructs `FORMED` only from valid canonical data and revalidation policy;
- block break/dismantle cannot duplicate inventory, offering, ritual or reward state;
- satellite blocks store only minimal membership/controller reference data if needed, never duplicate gameplay authority.

## Technical validation contract

For every functional multiblock test:
- formation success via designated tool/controller interaction;
- visibly distinct UNFORMED vs FORMED presentation;
- invalid orientation;
- missing component;
- duplicate controller;
- activation spam/idempotence;
- break → unform → reform idempotence;
- unload/reload;
- restart persistence;
- chunk-edge placement;
- missing/unloaded required chunk fails closed without chunk forcing;
- claim/protection interaction where world mutation occurs;
- dedicated-server behavior;
- client with reduced/disabled VFX still sees correct formed gameplay state;
- no reward duplication after reform/reconnect;
- screenshot review confirms the formed asset reads as one coherent construction rather than visible component cubes.

## Rendering rule

Large multiblock beauty should come from a deliberate formed-state composition: static custom geometry for structure, plus a small number of meaningful animated anchors/composite renderers. Do not create a heavy BlockEntity renderer on every decorative component.

The renderer strategy must explicitly solve all of the following before implementation is accepted:
- hiding/merging internal block seams;
- culling hidden/internal faces where practical;
- coherent lighting/emissive behavior across the assembled silhouette;
- correct render after chunk reload and resource reload;
- distance/LOD or animation throttling for expensive hero geometry where needed;
- graceful fallback if optional enhancement libraries are absent;
- no client-only class loading on dedicated server.

## Hard acceptance gates for player-built multiblocks

A player-built hero multiblock is **rejected** if any of these remain true:

1. It looks like several normal blocks placed next to each other after formation.
2. The primary silhouette is still a cube-grid/checkerboard rather than one authored construction.
3. Formation can be triggered accidentally by passive placement with no clear player activation step.
4. Each component behaves like an independent machine/controller.
5. Breaking/reforming duplicates rewards, ritual state or inventory.
6. The pretty animation is required for gameplay state to become valid.
7. The structure only looks acceptable from one camera angle.
8. The result is visually below the quality bar established by the polished 3D/multiblock content already present in the pack.
