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
| Fusion | 1.3.15+a | installed | optional environmental connected/continuous/overlay materials; no gameplay authority |
| Lodestone | 1.8.2 | installed | advanced VFX candidate after proof |
| OctoLib | 0.6.2 | installed | optional UI tween utility |
| Player Animator | 2.0.4+1.21.1 | installed | optional ritual/player interaction animation |
| Veil | 4.3.2 transitive | transitively present | no hard dependency without explicit ADR |
| Sodium | 0.8.13+mc1.21.1 | installed | mandatory compatibility test target |
| Entity Model Features | 3.3.5 | installed | resource/model neighbor, not Enshrouded authority |
| Entity Texture Features | 7.2.1 | installed | resource/model neighbor, not Enshrouded authority |
| Easy Model Entities | 2.3.0 filename | installed | design/reference tooling neighbor; not selected runtime core |

## Stage 10.04 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Lich Skull Manifestation I model | `assets/enshrouded/geo/lich_skull_manifestation_1.geo.json`; `art/blockbench/lich_skull_manifestation_1.bbmodel` | Enshrouded first-party; GeckoLib presentation | authored 3D model + editable source present | pending | **REVIEW_IN_GAME** |
| Lich Skull base/emissive material | `textures/item/lich_skull_manifestation_1.png`; `textures/item/lich_skull_manifestation_1_glowmask.png` | first-party binaries listed in provenance | base + selective glowmask present | pending | **REVIEW_IN_GAME** |
| Lich Skull item render path | `LichSkullItem`; `LichSkullRenderProvider`; `LichSkullRenderer`; `LichSkullGeoModel` | split-source client presentation; no gameplay authority | `builtin/entity`, GUI/ground/fixed/first-person/third-person transforms, `AutoGlowingGeoLayer` | pending | **REVIEW_IN_GAME** |
| Lich Skull animations | `animations/lich_skull_manifestation_1.animation.json` | GeckoLib presentation-only | `idle` + `ritual_resonance` authored; animation completion cannot mutate Story/reward state | pending | **REVIEW_IN_GAME** |
| Lich manifestation VFX | `LichManifestationPresentation`; `particles/lich_arcana.json`; `textures/particle/lich_arcana.png` | Enshrouded first-party presentation downstream of canonical Stage 06 lifecycle | bounded 28/36 particles, 48-block audience radius, no chunk forcing/provider fork | pending | **REVIEW_IN_GAME** |

Stage 10.04 technical verification is complete on implementation PR #87 and post-merge `main@a54e8f32e85c6b07dd3ace89a301743bfeb669ca`. This does **not** promote these rows to `KEEP`: the P0 approval rule still requires in-game screenshots at realistic FOV/distance, reduced-effects readability and the external full 607-mod-pack visual smoke.

## Stage 10.05 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Sanctuary ward focus on Flame Altar | `assets/enshrouded/geo/flame_altar.geo.json`; `art/blockbench/flame_altar.bbmodel` | Enshrouded first-party geometry; GeckoLib presentation; canonical ward remains `FlameWardRuntime` / `FlameWardService` | `ward_focus`, `ward_ring`, `purification_aperture` and `ward_fragments` authored; editable source is contract-checked against every runtime cuboid | pending | **REVIEW_IN_GAME** |
| Sanctuary / purification altar animation language | `assets/enshrouded/animations/flame_altar.animation.json`; `FlameAltarBlockEntity` | GeckoLib presentation-only; no Flame/ward state mutation | `animation.flame_altar.sanctuary_active` plus reserved one-shot `purification_release`; animation state cannot become gameplay authority | pending | **REVIEW_IN_GAME** |
| Sanctuary local VFX | `SanctuaryPresentationController`; `particles/sanctuary_mote.json`; `ModParticles.SANCTUARY_MOTE` | client presentation reads only synchronized `ClientShroudState`; particle descriptor reuses an existing first-party sprite | max 6 motes every 6 ticks, existing particle config respected, no scan, server packet or chunk force | pending | **REVIEW_IN_GAME** |
| Canonical terminal purification release | `ShroudPurificationPresentation`; `ShroudPurificationRuntime` | server presentation strictly downstream of persisted `DESTROYED -> PURIFIED` | max 24 motes once per canonical terminal transition, loaded-center check, no state mutation or chunk force | pending | **REVIEW_IN_GAME** |
| Sanctuary over latent Shroud | `ShroudSample.sanctuarySuppressed`; `SanctuaryPresentationController`; canonical query/sync path | underlying intensity remains authoritative Shroud data; Sanctuary is a synchronized suppression fact | protected contamination remains visually legible instead of pretending the Shroud was deleted | pending | **REVIEW_IN_GAME** |

Stage 10.05 technical verification is complete on implementation PR #89, merged as `771341394045bdef09eb9d9fbb4743aaad1c39f6`. Post-merge Level 1 Release Readiness `34074967881 / 101599175586` and Enshrouded CI `34074967864 / 101599175335` both passed on that exact `main` baseline. No new third-party binary or second Sanctuary/purification authority was introduced. These rows remain `REVIEW_IN_GAME` until active ward, latent-Shroud, terminal-release, reduced-effects and full 607-mod-pack visual evidence exists.

## Stage 10.06 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Ordinary Shroud growth family | `blockstates/shroud_growth.json`; `models/block/shroud_growth_ordinary_[a-c].json`; `textures/block/shroud_growth_ordinary_[a-c].png` | Enshrouded first-party static baked resources; Stage 02 terrain mutation remains authority | exactly three bounded weighted authored variants; CI requires exact, unique, existing `_a/_b/_c` references; no ticker/storage/network/randomizer | pending | **REVIEW_IN_GAME** |
| Ordinary Shroud vein family | `blockstates/shroud_vein.json`; `models/block/shroud_vein_ordinary_[a-c].json`; `textures/block/shroud_vein_ordinary_[a-c].png` | Enshrouded first-party static baked resources; no parallel spread state | exactly three weighted surface/root variants replacing single-cross repetition | pending | **REVIEW_IN_GAME** |
| Ordinary membrane/crust materials | `textures/block/shroud_membrane_ordinary.png`; `textures/block/shroud_crust_ordinary.png` | Enshrouded first-party binaries registered in provenance | 32×32 bounded support materials used by authored models | pending | **REVIEW_IN_GAME** |
| Deadly/Red withered-growth family | `blockstates/withered_growth.json`; `models/block/withered_growth_deadly_[a-c].json`; `textures/block/withered_growth_deadly_[a-c].png` | Enshrouded first-party presentation of existing Deadly ecology; not purification | exactly three weighted variants with distinct silhouette/material composition from Ordinary | pending | **REVIEW_IN_GAME** |
| Deadly membrane/crust materials | `textures/block/shroud_membrane_deadly.png`; `textures/block/shroud_crust_deadly.png` | Enshrouded first-party binaries registered in provenance | decoded RGBA contract requires distinct alpha topology with at least 1/8 of pixels structurally different; re-encoding or RGB-only recolor does not satisfy the gate | pending | **REVIEW_IN_GAME** |
| Red Sludge world material | `textures/block/red_sludge_still.png`; `textures/block/red_sludge_flow.png`; existing `RedSludgeFluidType` | existing Red Sludge gameplay/fluid authority unchanged; texture binaries first-party/provenance tracked | upgraded to 32×32 Deadly-family material; no fluid/runtime rule change | pending | **REVIEW_IN_GAME** |
| Fusion optional continuity path | `10-06-shroud-world-art-family.md`; vanilla blockstates/models above | Fusion `1.3.15+a` is optional presentation infrastructure only | complete vanilla fallback exists; no Fusion-only gameplay/resource requirement | pending coexistence check | **OPTIONAL_ENHANCEMENT / REVIEW_IN_GAME** |

Stage 10.06 technical verification is complete on final PR #91 HEAD `1bf6dca57f8ff3548ef41955b0db06b8eb46b1c8`: Release Readiness `34079531152` and Enshrouded CI `34079531285 / 101612091620` both completed successfully after the two P2 contract hardenings. PR #91 merged as `19b9cee08cf5b5f369497ad4c8c0329eff65253d`; post-merge Release Readiness `34079910949` and Enshrouded CI `34079910911 / 101613106937` passed the complete matrix again on that exact `main`. The current pack reconciliation is **612 mods**, GeckoLib `4.9.2`, Sodium `0.8.13+mc1.21.1` and Fusion `1.3.15+a`. Manual Ordinary/Deadly surface captures, seam inspection, reduced-effects readability, Sodium/Fusion coexistence and the full 612-mod client smoke remain open; no row is promoted to `KEEP` from CI alone and `ART APPROVED` remains open.

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
