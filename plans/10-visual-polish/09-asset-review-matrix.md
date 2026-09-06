# Stage 10 — Asset Review Matrix

Use this matrix as the canonical visual-audit status for assets discovered during Stage 10.

## Status vocabulary

- `KEEP` — production-ready concept and execution.
- `POLISH` — correct concept; needs quality/material/animation/detail work.
- `REPLACE` — placeholder/vanilla reuse or wrong final identity.
- `REMOVE` — obsolete/duplicate.
- `OPTIONAL_ENHANCEMENT` — additional quality path with a safe base fallback.
- `REVIEW_IN_GAME` — source exists but final judgment requires runtime screenshots.

## Initial Level 1 audit

| Asset/system | Current evidence | Initial Stage 10 state | Required action |
|---|---|---|---|
| Flame Altar block model | simple cube-style model using vanilla polished blackstone bricks + magma | **REPLACE** | Original hero model, materials, animation; evaluate 3×3/5×5 presentation |
| Flame Altar item presentation | simple item model associated with current block | **REPLACE** | Match new hero asset and correct transforms |
| Lich Skull Manifestation 1 | item model inherits vanilla Wither Skeleton Skull | **REPLACE** | Original 3D trophy model + transforms |
| Exposure HUD logic/state seam | Stage 07 implemented/verified | **KEEP** | Do not redesign authority; art skin only |
| HUD icon/frame art | basic functional client presentation | **POLISH** | Full UI art pass + accessibility |
| Shroud fog/render pipeline | Stage 07 implemented/verified | **KEEP** | Tune profiles only after art review; preserve compatibility path |
| Shroud ambient audio ownership | Stage 07 bounded/original | **KEEP** | Optional art polish, no ownership rewrite |
| Shroud particles ownership/controller | Stage 07 bounded/original | **KEEP** | Higher-quality art/VFX while retaining budgets |
| `red_sludge_flow.png` | project asset exists | **REVIEW_IN_GAME** | Review tiling, flow, shore transition, fog readability |
| `red_sludge_still.png` | project asset exists | **REVIEW_IN_GAME** | Review material/depth/emissive |
| `shroud_growth.png` | project asset exists | **REVIEW_IN_GAME** | Family-level material review |
| `shroud_vein.png` | project asset exists | **POLISH** candidate | Evaluate connected/continuous Fusion path + fallback |
| `withered_growth.png` | project asset exists | **REVIEW_IN_GAME** | Check purified/regression visual story |
| Ordinary vs Deadly fog color | distinct profiles exist | **KEEP** | Ensure new world art still supports non-color distinction |
| Shroud Core hero model | final high-detail hero asset not established by Level 1 art closure | **REPLACE/CREATE** | Create original animated core + Deadly profile |
| Sanctuary/Purification focal asset | gameplay exists; hero art not treated as final | **CREATE** | Original ward/purification focus; consider multiblock shrine |
| Lich manifestation VFX | provider/runtime exists | **POLISH** | Add provider-safe visual staging without boss fork |

## Dependency/runtime audit

| Library/tool | Current pack | Status | Stage 10 role |
|---|---:|---|---|
| GeckoLib 4 | 4.9.2 | installed | **PRIMARY** animated 3D runtime |
| AzureLib | 3.1.11 | installed | evaluated alternative; do not mix by default |
| Fusion | 1.3.15 | installed | optional environmental connected/continuous/overlay materials |
| Lodestone | 1.8.2 | installed | advanced VFX candidate after proof |
| OctoLib | 0.6.2 | installed | optional UI tween utility |
| Player Animator | 2.0.4+1.21.1 | installed | optional ritual/player interaction animation |
| Veil | 4.3.2 transitive | transitively present | no hard dependency without explicit ADR |
| Sodium | 0.8.13+mc1.21.1 | installed | mandatory compatibility test target |
| Entity Model Features | 3.3.5 | installed | resource/model neighbor, not Enshrouded authority |
| Entity Texture Features | 7.2.1 | installed | resource/model neighbor, not Enshrouded authority |
| Easy Model Entities | 2.3.0 filename | installed | design/reference tooling neighbor; not selected runtime core |

## Audit expansion rule

During implementation, every new or touched asset is added here with:
- exact repository path;
- runtime owner;
- source/provenance;
- model/texture/animation status;
- optional dependency use;
- screenshot evidence link/path;
- final approval state.

No asset should silently skip from `REVIEW_IN_GAME` to complete because it compiled.
