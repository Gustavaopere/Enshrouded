# Stage 10 — Art Direction, 3D Assets, Animation, VFX and Visual Polish

**Milestone:** Post-Level-1 visual production pass.
**Repository baseline:** Level 1 / Enshrouded 1.0.0 is functionally closed on `main`; this stage must not redesign or bypass the gameplay authority already proven by Stages 00–09.
**Stage state:** PLANNED — runtime implementation has not started.
**Target platform:** Minecraft 1.21.1, NeoForge 21.1.248, Java 21.

## 1. Purpose

Stage 10 exists because the Level 1 milestone is technically complete but several iconic objects still use visually provisional/simple assets. The objective is to turn Enshrouded into a visually authored mod with strong silhouettes, coherent art direction, polished 3D models, animation, VFX, UI and environmental composition.

This is not a gameplay redesign. Existing server-authoritative contracts for Shroud, Exposure, Madness, Flame, Sanctuary, Lich/story, terrain mutation and integrations remain canonical. Stage 10 consumes those states for presentation.

The visual bar is deliberately high: central objects must not look like vanilla placeholders or random modpack decoration. They must be recognizable in screenshots without UI labels.

## 2. Sources and pack context

The current pack already contains useful visual/runtime infrastructure. The design should prefer what is already installed when it is technically appropriate and stable rather than adding redundant frameworks.

Confirmed relevant installed libraries/mods include:

- GeckoLib 4 `4.9.2` — 3D geo models and state-driven animation for entities, block entities, items and armor.
- AzureLib `3.1.11` — alternative animated rendering stack; evaluated but not selected as a second concurrent animation engine for Enshrouded.
- Fusion `1.3.15` — connected/continuous/random/scrolling textures, overlays and model modifiers; good fit for environmental Shroud surfaces.
- Lodestone `1.8.2` — reusable visual-effects/render utilities; candidate for advanced VFX only where it materially improves presentation.
- OctoLib `0.6.2` — tween/keyframe/UI-particle utilities; optional reference/candidate for UI-only effects, not a required core dependency.
- Player Animator `2.0.4+1.21.1` — available for player-body animation if a future Stage 10 interaction truly needs authored player motion.
- Fusion-compatible Sodium is present in the current pack; every enhanced visual path still needs a graceful compatibility path.
- Entity Model Features / Entity Texture Features / Easy Model Entities are present in the pack, but they are resource/model customization tools rather than Enshrouded gameplay/render authority.

Visual benchmark mods already present in the pack include Domum Ornamentum, Supplementaries, Create and visual Create addons, Ars Nouveau family, L_Ender's Cataclysm, AAA Particles, AmbientSounds and other high-polish content mods. These are quality references only unless a specific API/dependency is explicitly approved.

## 3. Dependency decision

### 3.1 Primary animation/model runtime — GeckoLib 4

**Decision:** use GeckoLib 4 as the primary runtime for animated 3D Enshrouded assets that need skeletal/state-driven animation.

Reasons:

1. It is already installed in the 607-JAR pack.
2. It supports the exact object families needed here: block entities, entities and items.
3. It supports triggerable/server-originated animation without making the client authoritative.
4. It has established Blockbench workflows for `.geo.json` + `.animation.json` + textures.
5. It supports render layers such as emissive/glow masks.
6. The pack already has multiple GeckoLib consumers and Epic Fight includes GeckoLib compatibility handling.

**Rule:** do not use GeckoLib merely because a model is 3D. Static geometry should remain vanilla/model JSON where that is simpler and cheaper. GeckoLib is for assets that benefit from bones, animation, dynamic render layers or complex authored transformations.

### 3.2 AzureLib

AzureLib is present and technically capable of animated block entities/items/entities, render layers, alpha, custom render types and LOD features. It is **not selected as a parallel Stage 10 animation runtime**.

Using two animation frameworks for Enshrouded would increase maintenance, renderer-boundary risk, asset-pipeline fragmentation and dedicated-server/client-class complexity without a current feature that requires AzureLib specifically.

**Gate for future use:** AzureLib may only replace GeckoLib for a specific asset after a written ADR proves a concrete feature/performance advantage and migration cost is acceptable. It must not be mixed opportunistically asset-by-asset.

### 3.3 Fusion

**Decision:** allow Fusion as an optional/soft visual enhancement path for Shroud environmental materials where connected, continuous, overlay, random or scrolling texture behavior materially improves the scene.

Good candidates:
- Shroud veins crossing neighboring blocks;
- corruption overlays on host terrain;
- continuous membrane/root surfaces;
- controlled random variants to break tiling;
- subtle scrolling/pulsing texture effects where appropriate.

Fusion must not become required for server gameplay. If absent or disabled, blocks still need valid base models/textures and gameplay remains intact.

### 3.4 Lodestone

**Decision:** candidate, not automatic dependency.

Use only if the implementation task proves it gives a substantial advantage for one of:
- high-quality trails/ribbons;
- stylized energy arcs;
- layered screen/world particles;
- custom visual interpolation that would otherwise require a fragile home-grown renderer.

The existing Stage 07 particle/fog pipeline is already bounded and server-safe. Lodestone must extend presentation, not create a second Shroud state or duplicate existing particle ownership.

### 3.5 OctoLib / Player Animator / Veil / other transitive render libs

- **OctoLib:** optional for UI tweening/presentation if justified; not core.
- **Player Animator:** only for explicit player interaction animations such as altar ritual poses if those are added to the visual experience; never required just to animate world objects.
- **Veil:** currently appears transitively in the pack. Stage 10 must not declare a hard dependency on a transitive JAR. Any Veil-backed experiment must have a supported direct dependency decision and fallback before merge.
- **Flywheel/Create renderer internals:** reference/neighbor only. Do not couple Enshrouded hero assets to Create internals unless the feature is explicitly a Create integration.

## 4. Visual identity

### 4.1 Core fantasy

Enshrouded's visual identity is built from four tensions:

1. **ancient sacred flame** — ordered, protective, intentional;
2. **living Shroud corruption** — invasive, organic, asymmetrical;
3. **ruin and forgotten civilization** — monumental stone/metal framing and old ritual architecture;
4. **spectral Lich presence** — death, ritual geometry, bone, fractured arcana and unstable manifestation.

### 4.2 Shape language

**Flame / Sanctuary**
- strong vertical axis;
- symmetry and radial order;
- stepped stone/metal framing;
- protected central void or brazier;
- controlled arcs/circles/triangles;
- stable animation rhythm.

**Ordinary Shroud**
- curved growths;
- branching veins;
- sagging membranes;
- uneven clusters;
- slow breathing/pulsing motion.

**Deadly Shroud**
- sharper silhouette;
- split/thorned branches;
- exposed glowing cores;
- stronger contrast and faster internal pulse;
- obvious danger even in grayscale.

**Lich**
- bone + ancient ritual frame;
- broken halo/ring motifs;
- asymmetry caused by damage/corruption;
- floating fragments and spectral separation.

### 4.3 Palette

**Flame:** charcoal, aged stone, brass/dark metal, deep ember orange, amber/gold, warm white at hottest core only.

**Ordinary Shroud:** blue-gray, desaturated violet, corpse-green accents used sparingly, cold pale glow.

**Deadly Shroud:** dark crimson, oxidized red, deep magenta, near-black organic material, bright hostile internal red.

**Purification/Sanctuary:** warm gold + restrained pale cyan/white relief accent; must visually read as clean and breathable rather than merely “blue magic”.

### 4.4 Material rules

- Hero assets need authored material separation: stone, metal, organic material, bone and energy should not look like one flat texture.
- Emissive should be localized to semantically meaningful sources, not applied to every edge.
- Avoid excessive neon saturation.
- Avoid relying on vanilla blocks/textures for the face of a hero asset.
- Texture resolution may exceed vanilla 16×16 where detail warrants it, but texel density must remain coherent and performance-aware.

## 5. Hero assets

### 5.1 Flame Altar — priority P0

Current visual is considered placeholder-quality for the final art target.

**Target:** a recognizable ritual device that communicates Flame progression before the player reads text.

**Recommended architecture:**
- central functional anchor remains one authoritative block/BE for gameplay and persistence;
- rendered presentation may occupy a larger authored footprint;
- optional multiblock shell can add architecture without splitting authority.

**3D composition:**
- 3×3 visual footprint minimum candidate;
- raised ritual dais;
- sculpted central brazier/cradle;
- four directional braces/pillars;
- broken/partial halo or flame crown;
- inset runes and ember channels;
- geometric negative space around the flame.

**Animation states:**
- idle ember breathing;
- ritual available;
- ritual charging;
- ritual success burst;
- progression-level transition;
- suppressed/inactive state where applicable.

**Flame level visual progression:**
Level must alter appearance through more than particle count. Candidate signals include unlocked ring segments, new rune channels, flame height/shape, additional floating fragments and brighter material seams.

**Runtime:** GeckoLib BlockEntity only if skeletal/animated geometry is needed. Static architectural shell should remain ordinary blocks/models when possible.

### 5.2 Shroud Core — priority P0

**Target:** the iconic “heart” of local corruption, readable from medium distance and visibly linked to surrounding veins/growth.

**Composition:**
- rooted or suspended asymmetric organic core;
- outer husk/ribs;
- glowing inner heart;
- branching attachment points aligned with world growths;
- hanging/tension strands or fragments;
- no cube silhouette.

**Animation:**
- layered pulse with non-identical outer/inner timing;
- subtle contraction/expansion;
- tendril motion;
- aggressive pulse during nearby threat/event;
- death/collapse animation before final cleanup where gameplay timing allows.

**Ordinary vs Deadly:** separate texture/render profile and silhouette accents, not simple hue swap.

### 5.3 Lich Skull — priority P0

Replace vanilla skull appearance with a dedicated original model.

**Design:**
- elongated/ritualized skull or mask;
- broken crown/halo fragments;
- carved rune channel tied to the manifestation identity;
- residual spectral energy;
- asymmetrical damage or missing material;
- visually valuable as a trophy/reward.

**Item presentation:** custom 3D hand/ground/GUI transform. Animation should be subtle; do not make an inventory trophy constantly thrash.

### 5.4 Sanctuary / Purification Focus — priority P1

Create an unmistakable safe-zone focal asset. It should read as an intentional counter-technology/ritual to the Shroud.

Candidate form:
- small obelisk/lantern/ward-stone core;
- radial engraved base;
- clean vertical light volume;
- gentle upward motes;
- connected visual language with Flame Altar but smaller authority hierarchy.

### 5.5 Lich manifestation presentation — priority P1

The Stage 06 provider/gameplay contract remains unchanged. Stage 10 may add:
- manifestation aura;
- spawn/phase transition VFX;
- custom reward/trophy presentation;
- boss render layers/glow where provider compatibility permits;
- pre-encounter visual telegraphing.

Do not replace or fork the real provider entity merely for aesthetics unless a future design explicitly calls for an Enshrouded-owned entity.

## 6. Multiblock strategy

Multiblocks are allowed because they can make critical structures feel monumental, but they must not create distributed gameplay authority.

### 6.1 Authoritative-anchor pattern

Every functional multiblock uses one canonical controller/anchor. Decorative/satellite blocks may:
- validate formation;
- contribute geometry;
- display state;
- relay interaction to the controller.

They must not independently store Flame level, Shroud state, ritual completion, reward state or duplicated progression data.

### 6.2 Proposed multiblocks

**Flame Altar complex**
- controller at center;
- 3×3 minimum; 5×5 premium candidate after prototype;
- four corner/directional ritual supports;
- optional vertical crown/arch elements;
- formation preview/outline before assembly.

**Purification shrine**
- smaller 3×3 or cross footprint;
- ward stones around central focus;
- animated visual link to active Sanctuary radius.

**Shroud nest / core formation**
- worldgen formation around the core rather than player-built multiblock;
- radial veins, organic ribs, sludge basins and growth clusters;
- central core remains authoritative.

**Lich shrine/set piece**
- environmental narrative landmark, not a duplicate boss controller;
- ritual architecture can stage manifestation while Story State remains authoritative.

### 6.3 Formation UX

If player-built:
- readable ghost/preview or clear shape documentation;
- no pixel-hunting validation;
- deterministic orientation;
- formation errors should identify missing/invalid regions without leaking server internals;
- breaking a decorative segment must have an explicit and tested degradation contract.

## 7. Shroud environmental art

### 7.1 Surface families

Existing Shroud growth/vein/sludge textures should be audited as a family, not as isolated files.

Create coherent sets for:
- vein/root;
- membrane;
- crust;
- active growth;
- withered growth;
- sludge bank/shore;
- ordinary core-adjacent material;
- deadly core-adjacent material;
- purified residue/scarring.

### 7.2 Fusion enhancement plan

Use optional Fusion assets for:
- connected veins that avoid obvious tile seams;
- continuous membrane bands;
- block overlays creeping onto host materials;
- randomized variants to prevent wallpaper repetition;
- low-speed animated/scrolling internal energy where it remains tasteful.

All Fusion-enhanced resources require a valid non-Fusion fallback asset set.

### 7.3 World composition rules

A Shroud area should contain visual hierarchy:
1. distant fog/color mass;
2. mid-distance landmarks/growth silhouettes;
3. core/set-piece anchor;
4. near-field microdetail such as veins, sludge edges and particles.

Do not solve atmosphere only with fog. The world geometry must tell the same story when enhanced fog is disabled.

## 8. VFX plan

Existing bounded Stage 07 effects remain the baseline. Stage 10 adds authored sequences and higher-quality visuals without removing budgets.

Required sequences:
- crossing CLEAR → SHROUD;
- crossing SHROUD → DEADLY;
- entering Sanctuary while latent Shroud exists;
- core proximity escalation;
- core destruction/collapse;
- purification completion;
- Flame ritual start/charge/success;
- Flame level-up;
- Lich manifestation entrance/defeat;
- Madness stage escalation cues.

Each effect gets:
- trigger authority source;
- local-only/server-synced distinction;
- max particle count;
- max active lifetime;
- max spawn radius;
- LOD/distance behavior;
- reduced-effects behavior;
- fallback when optional VFX libraries are absent.

No world effect may perform unbounded scans, force chunks or emit network packets every render tick.

## 9. HUD/UI art pass

The existing HUD logic remains authoritative presentation of synchronized state.

Art pass requirements:
- custom frame language derived from Flame/Shroud motifs;
- separate icons for reserve, severity, Madness and passage danger;
- Ordinary vs Deadly readable by icon/shape/text and not color alone;
- high-contrast passage-blocked warning;
- localization-safe nine-slice/stretch strategy where appropriate;
- scale/anchor settings preserved;
- reduced motion option;
- optional minimal mode for players who prefer low visual footprint.

Avoid ornate fantasy UI that obscures information. Legibility is a hard acceptance gate.

## 10. Animation-state contracts

Animation is presentation of canonical state, not a new state machine for gameplay.

### Flame Altar
Canonical inputs may include:
- flame/progression level snapshot;
- ritual interaction event;
- ritual success/failure presentation receipt;
- active/inactive visual state.

### Shroud Core
Canonical inputs may include:
- core lifecycle state;
- severity/profile;
- destruction event;
- local presentation proximity.

### Lich-related assets
Canonical inputs come from Story/Lich provider events already defined by Stage 06.

**Rule:** animations must tolerate packet delay/reconnect and must converge to the latest authoritative state. Client animation completion cannot grant progression or mutate world authority.

## 11. Blockbench production pipeline

Canonical authoring tool: **Blockbench** for GeckoLib geometry/animations and vanilla JSON model prototyping.

For each animated hero asset, repository source should preserve:
- source `.bbmodel` when license/provenance permits;
- exported `.geo.json`;
- `.animation.json`;
- base texture;
- emissive/glow mask if used;
- optional normal/specular only if the chosen supported pipeline actually consumes them;
- thumbnail/reference render for review;
- provenance entry.

Naming rules:
- bones semantic, not `cube42`/`group17`;
- animation names namespaced by behavior (`idle`, `ritual_charge`, `destroy`, etc.);
- no hidden gameplay semantics only encoded in animation names.

Texture source files should be retained in an editable format where practical, but production JAR ships only necessary runtime assets.

## 12. Asset review matrix

Every existing and new visual asset receives one state:

- **KEEP** — production-ready;
- **POLISH** — concept is right, quality/details need work;
- **REPLACE** — placeholder/vanilla reuse insufficient for final identity;
- **REMOVE** — obsolete/duplicate;
- **OPTIONAL ENHANCEMENT** — used only when optional library/resource path exists.

Initial known decisions:
- Flame Altar current cube-style block model: **REPLACE**.
- Lich Skull inheriting vanilla Wither Skeleton Skull: **REPLACE**.
- Stage 07 fog pipeline: **KEEP**, then tune profiles only.
- Stage 07 authority-safe HUD logic: **KEEP**, art skin **POLISH**.
- original Stage 07 audio/particle ownership: **KEEP**, art/VFX quality **POLISH**.
- Shroud block texture family: **POLISH** after full in-game review; individual files are not pre-declared failures without screenshots.

## 13. Performance budgets

Visual quality is not permission for uncontrolled render cost.

### 13.1 Hero models
- keep bone counts intentional;
- avoid tiny invisible cubes and excessive nested bones;
- animation controllers update only when useful;
- no per-frame world scanning from renderers;
- distance culling/LOD plan for models visible at range;
- emissive layers limited to necessary passes.

### 13.2 Particles/VFX
- retain aggregate hard caps;
- distance culling;
- configurable density;
- no global source enumeration;
- no chunk forcing;
- degraded/reduced-effects preset.

### 13.3 Multiblocks
- formation validation occurs on bounded events, not continuous full-volume scans;
- decorative render composition must not instantiate a renderer per component if a cheaper static model works;
- no large dynamic mesh rebuild each tick.

### 13.4 Compatibility test matrix
Must include the real pack renderer stack relevant to the user, especially Sodium and the currently installed optional visual libraries.

## 14. Compatibility contracts

Stage 10 must explicitly test:
- vanilla-like graphics path;
- Sodium path used by the pack;
- enhanced fog enabled/disabled;
- Fusion present and enhancement active;
- Fusion fallback/base resources;
- shaders where the actual pack supports them, without making shaders mandatory;
- resource reload;
- F3+A chunk reload;
- disconnect/reconnect;
- dimension transition;
- dedicated server startup (all client-only classes remain isolated);
- full 607-mod manual pack smoke before distribution.

Renderer integration must fail gracefully. A missing optional client enhancement may reduce visual quality; it may not corrupt saves, progression or server startup.

## 15. Provenance and legal asset policy

- Do not copy models, textures, sounds or proprietary game files from Enshrouded.
- Do not rip assets from other Minecraft mods.
- Other mods are references for quality/techniques only unless their license explicitly permits reuse and provenance is recorded.
- Prefer original assets authored for this project.
- AI-assisted concept art may guide design, but production assets still require a provenance note and manual review for accidental copying/brand marks/text artifacts.
- Every imported external asset requires source URL, license, author, modification record and redistribution compatibility before merge.

## 16. Deliverable tasks

### 10.01 — Visual Bible and dependency ADR
Close art direction, palettes, shape language, material rules, dependency decisions and fallback policy.

### 10.02 — Flame Altar hero asset
Model, texture, emissive, animation set, optional multiblock shell, level-state variants and review screenshots.

### 10.03 — Shroud Core hero asset
Ordinary/Deadly presentation, animations, core-death sequence, world attachment language and performance budget.

### 10.04 — Lich Skull and Lich presentation
Custom trophy model plus compatible manifestation/boss VFX pass without replacing Stage 06 authority.

### 10.05 — Sanctuary / Purification visual system
Focus model, safe-zone visual language, purification sequence and world-state convergence.

### 10.06 — Shroud world-art family
Growths, veins, membranes, sludge edges, withered/purified variants and optional Fusion enhancement.

### 10.07 — HUD/UI polish
Production UI skin, icons, animation limits, accessibility and localization-safe layout.

### 10.08 — Advanced VFX pass
Event sequences, Lodestone decision if needed, configurable budgets and reduced-effects profile.

### 10.09 — Multiblock/set-piece pass
Flame complex, purification shrine, Shroud core nests and Lich ritual landmarks; authoritative-anchor contract enforced.

### 10.10 — Visual QA, performance and release art gate
Screenshot matrix, resource reloads, renderer compatibility, dedicated server, CI/build/GameTests affected by new code, and manual real-pack smoke.

## 17. Review screenshots required per hero asset

At minimum:
- front 3/4 daylight;
- front 3/4 night;
- close-up texture/material read;
- medium-distance silhouette;
- Ordinary/default state;
- dangerous/active state where applicable;
- reduced particles/effects;
- GUI/hand/ground view for items;
- comparison against surrounding common pack blocks to validate scale.

A hero asset is not approved from Blockbench viewport alone. It needs an in-game render review.

## 18. Acceptance gates

Stage 10 is complete only when all of the following are true:

1. Flame Altar, Shroud Core and Lich Skull have original production-quality models/textures and no longer read as placeholders.
2. The mod has a documented coherent visual language shared by world art, hero objects, UI, fog and VFX.
3. Ordinary Shroud, Deadly Shroud, Sanctuary and Flame progression are distinguishable without relying solely on color.
4. Hero assets have strong silhouettes at practical gameplay distance.
5. Multiblocks, if implemented, use a single gameplay authority/controller and bounded formation validation.
6. GeckoLib animations consume authoritative state and cannot award/progress gameplay through client animation completion.
7. Optional visual libraries have explicit fallback paths and never become accidental server/gameplay requirements.
8. Visuals remain usable with enhanced fog/particles reduced or disabled.
9. Dedicated-server verification proves client renderer classes do not leak into common bootstrap.
10. Performance budgets and real renderer/modpack compatibility tests pass.
11. Third-party provenance audit passes.
12. Final visual QA includes in-game screenshots and the manual full-pack smoke gate.

## 19. Non-goals for this stage

- no Level 2+ Flame gameplay progression;
- no new Shroud propagation mechanics;
- no new Lich combat design;
- no new gameplay resource just to justify an animation;
- no migration from GeckoLib to AzureLib without a separate approved ADR;
- no dependency on proprietary Enshrouded assets;
- no mandatory shader requirement.

## 20. Implementation handoff rule

This branch/PR is **planning only**. It may update planning/status documentation but must not introduce Stage 10 runtime Java, models, textures or production dependencies yet.

After the plan is approved, implementation should proceed task-by-task from 10.01 through 10.10, with each task recording:
- exact runtime/library contract;
- asset source/provenance;
- tests/visual QA;
- performance evidence;
- final screenshots;
- PR head SHA and CI result.

Do not automatically begin implementation after this planning PR is reviewed/merged; wait for an explicit user instruction to proceed.
