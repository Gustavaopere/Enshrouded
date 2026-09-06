# Stage 10 — Validation and Performance

## Validation philosophy

Stage 10 adds presentation complexity without relaxing the repository's existing hardening standards. Visual features require both automated technical gates and manual in-game art review.

## Automated gates for tasks that add code

When applicable:
- `./gradlew test`;
- NeoForge build;
- production JAR verification;
- existing GameTests;
- new unit tests for animation/presentation state mapping;
- dedicated-server startup/smoke;
- persistence/restart gates whenever a multiblock controller adds persisted state;
- diff/provenance checks used by the repository CI.

A documentation-only 10.01 planning task does not fake runtime evidence.

## Client lifecycle tests

Any new render/animation controller must cover:
- login initial state;
- logout reset;
- reconnect;
- world/dimension transition;
- resource reload;
- stale/newer snapshot handling where relevant;
- no presentation state leaking into a new server/session.

## Renderer compatibility matrix

At minimum test the renderer stack actually used by the pack:
- base NeoForge/vanilla-like path;
- Sodium current-pack path;
- enhanced Enshrouded fog on/off;
- Fusion enhancement on;
- Fusion fallback/base assets;
- optional VFX library present/absent if one is adopted.

Do not claim shader compatibility without testing the shader stack actually present at implementation time.

## Dedicated-server gate

No client renderer/model/particle-only class may be loaded from common bootstrap. Each task touching renderer registration requires dedicated-server evidence.

## Performance budgets

### Models
- bone/cube count must be intentional;
- no invisible detail chains that cost render/animation work but are unreadable;
- no per-frame world scan from model/renderer classes;
- distance behavior documented;
- animation controllers should sleep/stop when state is static where practical;
- emissive/additional render passes limited to semantic need.

### VFX
- hard aggregate particle caps remain;
- per-source cap;
- max distance;
- max lifetime;
- rate limit/cooldown;
- reduced-effects preset;
- no network broadcast each render tick;
- no chunk forcing.

### Multiblocks
- validation event-driven/bounded;
- no continuous full-volume scan;
- one controller wherever possible;
- static components use baked models, not unnecessary dynamic renderers.

## Performance evidence

For each P0 hero asset capture:
- idle cost with one instance;
- multiple visible instances stress case if players can build multiples;
- particle/VFX peak event;
- reduced-effects comparison;
- render distance/LOD behavior;
- absence of memory/state accumulation after unload/reload cycles.

Exact numeric thresholds should be established from a baseline measurement on the implementation branch rather than invented in planning.

## Visual QA matrix

Required screenshots/video checks:
- daylight;
- night;
- Shroud fog enabled;
- enhanced fog disabled;
- near view;
- medium silhouette;
- active state;
- inactive/default state;
- Ordinary/Deadly comparison where relevant;
- reduced particle setting;
- GUI/hand/ground for items;
- placement next to common modpack builds to verify scale/material coherence.

## Accessibility gates

- no color-only severity distinction;
- no mandatory rapid flashing;
- reduced-motion option for strong UI/VFX motion;
- readable warning contrast;
- HUD scale/anchor retained;
- PT-BR and EN layout review.

## Full-pack smoke

The existing external gate `MANUAL_CURRENT_PACK_SMOKE_REQUIRED` remains applicable. Stage 10 final release validation must launch the real surrounding 607-JAR pack and inspect:
- resource/model load errors;
- missing textures;
- renderer crashes;
- Sodium conflicts;
- animation desync;
- structure/block ID conflicts;
- visual clipping/z-fighting;
- unacceptable FPS/frame-time regression.

This manual pack smoke is not replaced by GitHub Actions.

## Failure policy

A visual enhancement that fails compatibility must fail soft where technically possible. It may reduce fidelity; it may not corrupt gameplay state or block dedicated-server startup.
