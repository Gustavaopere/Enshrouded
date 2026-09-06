# Stage 10 — Render and Animation Stack

## Decision summary

| Component | Current pack | Stage 10 decision |
|---|---:|---|
| GeckoLib 4 | 4.9.2 | **PRIMARY animated 3D runtime** |
| AzureLib | 3.1.11 | Evaluated alternative; **do not mix as parallel runtime** |
| Fusion | 1.3.15 | **Optional/soft environmental material enhancement** |
| Lodestone | 1.8.2 | Candidate for advanced VFX after task-specific proof |
| OctoLib | 0.6.2 | Optional UI/tween utility only if justified |
| Player Animator | 2.0.4+1.21.1 | Optional player-body interaction animation |
| Veil | 4.3.2 transitive in current pack | **No hard dependency on a transitive JAR** |
| Flywheel | transitive Create runtime | No coupling unless a dedicated Create integration requires it |

## GeckoLib 4 — selected primary runtime

Use GeckoLib for animated hero objects that genuinely need bones, state-driven animation, complex transforms or render layers.

Target use cases:
- Flame Altar animated BlockEntity core;
- Shroud Core animated BlockEntity/world object;
- Lich Skull 3D item if subtle animation is approved;
- future Enshrouded-owned animated entities only if one is explicitly designed.

Required production asset set for each GeckoLib hero object:
- Blockbench source `.bbmodel` retained in repository asset-source area when practical;
- `.geo.json` geometry;
- `.animation.json` animation set;
- production textures;
- glow/emissive mask when used;
- renderer/model class isolated client-side;
- animation controller mapping documented against canonical server-authored state.

### GeckoLib authority rule

GeckoLib receives presentation state. It does not become gameplay state.

Examples:
- ritual completion on server triggers a ritual-success presentation; finishing the client animation does not complete the ritual;
- core `DESTROYED` state can trigger collapse animation; the renderer does not mark the core destroyed;
- reconnect may snap/converge an animation to the newest authoritative state rather than replaying progression.

### When not to use GeckoLib

Use vanilla block/item models for static geometry that does not need skeletal animation. Do not turn every decorative block into a BlockEntity/GeoAnimatable.

## AzureLib — explicit non-selection

AzureLib has capable block/item/entity render pipelines and its current API exposes configurable render layers, scale/alpha/render types and LOD behavior. However, Enshrouded should not casually split assets between GeckoLib and AzureLib.

Reasons:
- duplicated renderer conventions;
- two animation controller systems;
- fragmented Blockbench/export expectations;
- larger compatibility/test matrix;
- additional client lifecycle/classloading surface;
- little current benefit because GeckoLib covers the required hero-asset cases.

A future task may propose migration to AzureLib only through a dedicated ADR with measurable benefit. No asset-by-asset opportunistic mixing.

## Fusion — environmental surface layer

Fusion is a strong fit for visual material continuity rather than skeletal animation.

Approved candidate features:
- connected Shroud veins;
- continuous textures spanning corruption clusters;
- creeping overlays over host blocks;
- randomized tile variants;
- carefully bounded scrolling/animated energy textures;
- conditional/model modifier use when it improves world composition.

Fusion is a **soft visual dependency**. Every target needs a valid base asset path when Fusion is absent or disabled.

## Lodestone — advanced VFX candidate

Do not add Lodestone to Enshrouded merely because it exists in the pack. A task must first show that the existing Stage 07 particle/render path cannot achieve the desired quality cleanly.

Potential valid reasons:
- stylized energy trails/ribbons;
- complex ritual arcs;
- layered world/screen particles;
- reusable high-quality VFX primitives that reduce custom renderer risk.

If adopted, Lodestone owns only rendering helpers. Stage 07's Shroud/particle presentation state remains canonical.

## OctoLib

May be used for UI tween/keyframe presentation only after proof that custom lightweight interpolation would be worse. It must not become a second world-animation framework.

## Player Animator

Only consider for clearly visible authored player interactions, such as a Flame ritual pose or touching/attuning to a structure. Must degrade gracefully and never become a ritual-completion dependency.

## Veil / transitive dependencies

A transitive JAR is not a stable API contract. Stage 10 may not compile production code directly against the currently transitively present Veil runtime without converting that into an explicit dependency decision, version contract and fallback strategy.

## NeoForge-native animation

NeoForge 1.21.1 provides native entity-model animation facilities and JSON animation support. Static/simple entity animation should not be forced through GeckoLib when NeoForge-native animation is sufficient. The hero-asset decision is based on complexity and authoring value, not brand preference.

## Compatibility rules

- client-only renderer registration must remain physical-client isolated;
- no render code in common server bootstrap;
- resource reload must rebuild/reload safely;
- visuals must coexist with the pack's Sodium renderer path;
- optional enhancement failures must not affect save data or gameplay;
- render controllers may read synchronized state but do no unbounded world scanning.

## ADR conclusion

**Approved Stage 10 stack:** GeckoLib primary + vanilla/NeoForge static rendering + optional Fusion surface enhancements. Lodestone/OctoLib/Player Animator are task-gated optional extensions. AzureLib remains evaluated but intentionally unused by Enshrouded unless a future migration ADR changes this decision.
