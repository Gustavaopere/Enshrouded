# Stage 10 — Asset Review Matrix

Use this matrix as the canonical visual-audit status for assets discovered during Stage 10.

## Status vocabulary

- `KEEP` — production-ready concept and execution.
- `POLISH` — correct concept; needs quality/material/animation/detail work.
- `REPLACE` — placeholder/vanilla reuse or wrong final identity.
- `REMOVE` — obsolete/duplicate.
- `OPTIONAL_ENHANCEMENT` — additional quality path with a safe base fallback.
- `REVIEW_IN_GAME` — source exists but final judgment requires runtime screenshots.
- `USER_ART_HANDOFF` — a required first-party visual resource package is intentionally waiting for the user's authored models/textures; technical code does not substitute for that asset work.
- `RUNTIME_CONSUMER_OPEN` — a bounded data/layout contract exists, but no approved production world/encounter consumer currently makes it appear in gameplay.

Current physical-pack authority for this reconciliation: **603 mods**, NeoForge `21.1.248`. Counts of 607/612 below are retained only when they describe the pack snapshot at an earlier Stage 10 checkpoint.

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

| Library/tool | Audited Stage 10 snapshot | Status | Stage 10 role |
|---|---:|---|---|
| GeckoLib 4 | 4.9.2 | installed | **PRIMARY** animated 3D runtime |
| AzureLib | 3.1.11 | installed | evaluated alternative; do not mix by default |
| Fusion | 1.3.15+a | installed at the audited checkpoint | optional environmental connected/continuous/overlay materials; no gameplay authority |
| Lodestone | 1.8.2 | installed | **NOT ADOPTED IN 10.08** after native proof; remains future task-gated |
| OctoLib | 0.6.2 | installed at the audited checkpoint | optional UI tween utility |
| Player Animator | 2.0.4+1.21.1 | installed at the audited checkpoint | optional ritual/player interaction animation |
| Veil | 4.3.2 transitive | transitively present at the audited checkpoint | no hard dependency without explicit ADR |
| Sodium | 0.8.13+mc1.21.1 | installed | mandatory compatibility test target |
| Entity Model Features | 3.3.5 | installed at the audited checkpoint | resource/model neighbor, not Enshrouded authority |
| Entity Texture Features | 7.2.1 | installed at the audited checkpoint | resource/model neighbor, not Enshrouded authority |
| Easy Model Entities | 2.3.0 filename | installed at the audited checkpoint | design/reference tooling neighbor; not selected runtime core |

## Stage 10.04 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Lich Skull Manifestation I model | `assets/enshrouded/geo/lich_skull_manifestation_1.geo.json`; `art/blockbench/lich_skull_manifestation_1.bbmodel` | Enshrouded first-party; GeckoLib presentation | authored 3D model + editable source present | pending | **REVIEW_IN_GAME** |
| Lich Skull base/emissive material | `textures/item/lich_skull_manifestation_1.png`; `textures/item/lich_skull_manifestation_1_glowmask.png` | first-party binaries listed in provenance | base + selective glowmask present | pending | **REVIEW_IN_GAME** |
| Lich Skull item render path | `LichSkullItem`; `LichSkullRenderProvider`; `LichSkullRenderer`; `LichSkullGeoModel` | split-source client presentation; no gameplay authority | `builtin/entity`, GUI/ground/fixed/first-person/third-person transforms, `AutoGlowingGeoLayer` | pending | **REVIEW_IN_GAME** |
| Lich Skull animations | `animations/lich_skull_manifestation_1.animation.json` | GeckoLib presentation-only | `idle` + `ritual_resonance` authored; animation completion cannot mutate Story/reward state | pending | **REVIEW_IN_GAME** |
| Lich manifestation VFX | `LichManifestationPresentation`; `particles/lich_arcana.json`; `textures/particle/lich_arcana.png` | Enshrouded first-party presentation downstream of canonical Stage 06 lifecycle | bounded 28/36 particles, 48-block audience radius, no chunk forcing/provider fork | pending | **REVIEW_IN_GAME** |

Stage 10.04 technical verification is complete on implementation PR #87 and post-merge `main@a54e8f32e85c6b07dd3ace89a301743bfeb669ca`. This does **not** promote these rows to `KEEP`: the P0 approval rule still requires in-game screenshots at realistic FOV/distance, reduced-effects readability and the external full 607-mod-pack visual smoke recorded for that checkpoint.

## Stage 10.05 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Sanctuary ward focus on Flame Altar | `assets/enshrouded/geo/flame_altar.geo.json`; `art/blockbench/flame_altar.bbmodel` | Enshrouded first-party geometry; GeckoLib presentation; canonical ward remains `FlameWardRuntime` / `FlameWardService` | `ward_focus`, `ward_ring`, `purification_aperture` and `ward_fragments` authored; editable source is contract-checked against every runtime cuboid | pending | **REVIEW_IN_GAME** |
| Sanctuary / purification altar animation language | `assets/enshrouded/animations/flame_altar.animation.json`; `FlameAltarBlockEntity` | GeckoLib presentation-only; no Flame/ward state mutation | `animation.flame_altar.sanctuary_active` plus reserved one-shot `purification_release`; animation state cannot become gameplay authority | pending | **REVIEW_IN_GAME** |
| Sanctuary local VFX | `SanctuaryPresentationController`; `particles/sanctuary_mote.json`; `ModParticles.SANCTUARY_MOTE` | client presentation reads only synchronized `ClientShroudState`; particle descriptor reuses an existing first-party sprite | max 6 motes every 6 ticks, existing particle config respected, no scan, server packet or chunk force | pending | **REVIEW_IN_GAME** |
| Canonical terminal purification release | `ShroudPurificationPresentation`; `ShroudPurificationRuntime` | server presentation strictly downstream of persisted `DESTROYED -> PURIFIED` | max 24 motes once per canonical terminal transition, loaded-center check, no state mutation or chunk force | pending | **REVIEW_IN_GAME** |
| Sanctuary over latent Shroud | `ShroudSample.sanctuarySuppressed`; `SanctuaryPresentationController`; canonical query/sync path | underlying intensity remains authoritative Shroud data; Sanctuary is a synchronized suppression fact | protected contamination remains visually legible instead of pretending the Shroud was deleted | pending | **REVIEW_IN_GAME** |

Stage 10.05 technical verification is complete on implementation PR #89, merged as `771341394045bdef09eb9d9fbb4743aaad1c39f6`. Post-merge Level 1 Release Readiness `34074967881 / 101599175586` and Enshrouded CI `34074967864 / 101599175335` both passed on that exact `main` baseline. No new third-party binary or second Sanctuary/purification authority was introduced. These rows remain `REVIEW_IN_GAME` until active ward, latent-Shroud, terminal-release, reduced-effects and the full 607-mod-pack visual evidence required at that checkpoint exists.

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

Stage 10.06 technical verification is complete on final PR #91 HEAD `1bf6dca57f8ff3548ef41955b0db06b8eb46b1c8`: Release Readiness `34079531152` and Enshrouded CI `34079531285 / 101612091620` both completed successfully after the two P2 contract hardenings. PR #91 merged as `19b9cee08cf5b5f369497ad4c8c0329eff65253d`; post-merge Release Readiness `34079910949` and Enshrouded CI `34079910911 / 101613106937` passed the complete matrix again on that exact `main`. At the Stage 10.06 checkpoint the physical pack reconciliation was **612 mods**, GeckoLib `4.9.2`, Sodium `0.8.13+mc1.21.1` and Fusion `1.3.15+a`. Manual Ordinary/Deadly surface captures, seam inspection, reduced-effects readability, Sodium/Fusion coexistence and that checkpoint's full-pack client smoke remained open; no row was promoted to `KEEP` from CI alone and `ART APPROVED` remained open.

## Stage 10.07 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Shroud HUD authored frame atlas | `textures/gui/shroud_hud_icons.png`; `ShroudHudOverlay` | Enshrouded first-party GUI atlas; Stage 03 synchronized Exposure state remains authority | bounded 384×64 atlas with separate Ordinary/Deadly 160×60 frame cells; presentation-only render path | pending | **REVIEW_IN_GAME** |
| Ordinary vs Deadly HUD identity | `shroud_hud_icons.png`; `scripts/ci/test_stage10_hud_ui_art.py` | first-party presentation; no gameplay state | CI decodes RGBA and normalizes `alpha > 0` to occupied topology masks, so palette/opacity-only aliases cannot satisfy the structural distinction gate | pending | **REVIEW_IN_GAME** |
| HUD status symbols | `shroud_hud_icons.png` cells at U=320/336/352/368 | first-party Ordinary, Deadly, Passage and Madness symbols | four 16×16 symbols are required to have distinct occupied masks rather than palette aliases | pending | **REVIEW_IN_GAME** |
| Madness presentation | `ExposureHudModel.madnessSegments()`; `ShroudHudOverlay.renderMadnessBar()` | projection of synchronized `MadnessStage`; Stage 03 remains sole authority | five-step bar derives only from server-authored Madness stage; no client threshold/reducer recomputation | pending | **REVIEW_IN_GAME** |
| Passage warning presentation | `ExposureHudModel.passageWarning()`; `ShroudHudOverlay` | projection of synchronized Deadly barrier state | warning/icon renders from existing synchronized `deadlyBarrierActive`; no client passage eligibility authority | pending | **REVIEW_IN_GAME** |
| MINIMAL accessibility path | `ShroudHudOverlay.renderMinimalHud()`; `AccessibilityProfile.MINIMAL` | client-only presentation/config | removes optional ornament while retaining zone identity, countdown, Madness and Passage warning; Ordinary/Deadly remains shape-distinct | pending reduced-effects comparison | **REVIEW_IN_GAME** |

Stage 10.07 technical verification is complete on final PR #93 HEAD `cda923713f6228e3c34885fc7da3d8592081c78b`: Release Readiness `34140834386 / 101802215801` and Enshrouded CI `34140834405 / 101802215875` both completed successfully after the P2 alpha-topology hardening. PR #93 merged as `341aa508bcd997cf32b7d550c9a37fca19f36ff3`; post-merge Release Readiness `34141675351` and Enshrouded CI `34141675549 / 101804820155` passed the complete matrix again on that exact `main`, including dedicated-server save/reload smoke. At the Stage 10.07 checkpoint the pack baseline was **612 mods** with GeckoLib `4.9.2`, Sodium `0.8.13+mc1.21.1` and Fusion `1.3.15+a`. Screenshots at representative GUI scales, MINIMAL/reduced-effects comparison, coexistence with other HUD overlays and that checkpoint's full-pack visual smoke remained open; `ART APPROVED` was not inferred from CI.

## Stage 10.08 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Final visual state |
|---|---|---|---|---|---|
| Snapshot-derived transition sequences | `AdvancedVfxTransitionPlanner`; `AdvancedVfxController`; `ClientAdvancedVfxState` | presentation projection of synchronized Stage 03 exposure/Madness state; no gameplay authority | `CLEAR → SHROUD`, `SHROUD → DEADLY`, Sanctuary entry and Madness escalation are baseline-safe, cooldown-bounded and do not replay on first snapshot/reload | pending | **REVIEW_IN_GAME** |
| Discrete authoritative event cues | `AdvancedVfxPayload`; `AdvancedVfxServerEmitter`; `AdvancedVfxCue` | ephemeral clientbound presentation strictly downstream of canonical Core destruction, Flame ritual success and Lich encounter activation | radius-targeted bounded cues; no serverbound VFX path, SavedData, chunk forcing, world scan or provider replacement | pending | **REVIEW_IN_GAME** |
| Temporal sequence budget / reduced effects | `AdvancedVfxSequenceBudget`; `AdvancedVfxController`; `AccessibilityPresetController` | client-only presentation using the single Stage 07 config authority | total cue allowance is spread across bounded lifetime; `REDUCED_SENSORY` caps the complete sequence at 4 particles; `MINIMAL` emits 0 advanced particles | pending full/reduced/minimal comparison | **REVIEW_IN_GAME** |
| Core-proximity density ramp | `ShroudSourceParticlePlanner` | existing Stage 07 local source planner; authoritative Core/exposure state remains server-side | local requested density is 2/3/4 by distance tier and remains clamped by the canonical client particle count/distance settings | pending near/far comparison | **REVIEW_IN_GAME** |
| Lodestone dependency decision | `build.gradle`; `neoforge.mods.toml`; `10-08-advanced-vfx.md`; `scripts/ci/test_stage10_advanced_vfx.py` | Lodestone 1.8.2 is installed in the audited pack snapshot but is not an Enshrouded runtime/compile provider for 10.08 | native NeoForge/GeckoLib presentation passed the full automation matrix without Lodestone API imports; future use remains task-gated | dependency decision complete; real client coexistence still pending | **OPTIONAL_ENHANCEMENT** |

Stage 10.08 technical verification is complete on final PR #95 HEAD `9a4ff6ef53ff192bb1d28cf1c0e2c5dc8662e5a7`: Release Readiness `34153873230 / 101841530155` and Enshrouded CI `34153873227 / 101841529843` both completed successfully. PR #95 merged as `00439c1593cf8e0699f6abe4e19d4848d1e97149`; post-merge Release Readiness `34154332957 / 101842896487` and Enshrouded CI `34154332819 / 101842895697` passed the complete matrix again on that exact `main`, including GameTests, SavedData two-boot reload, Ars Zero 2.0.2 real-distribution profile and dedicated-server save/reload smoke. At the Stage 10.08 checkpoint the physical pack had **612 mods**; Lodestone 1.8.2 was present but deliberately not adopted by Enshrouded for 10.08. Full client screenshots, Sodium/shader coexistence, fog modes, reconnect/dimension behavior, rapid-boundary crossing and that checkpoint's full-pack visual smoke remained manual gates; no row was promoted to `KEEP` from CI alone and `ART APPROVED` remained open.

## Stage 10.09 reconciled audit

| Asset/system | Exact repository evidence | Runtime owner / provenance | Technical state | Screenshot evidence | Current state |
|---|---|---|---|---|---|
| Flame Altar 3×3 lifecycle | `FlameAltarBlockEntity`; `FlameAltarStructureValidator`; `FlameAltarStructureLifecycle`; `FlameAltarChunkRecoveryEvents`; `FlameAltarRestartGameTests` | existing Flame Altar controller + canonical Flame Ward/Sanctuary runtime | explicit controller activation; four inward-facing cardinal braces + four corner runes; bounded loaded-only validation; `FORMED` shell mirrors controller state; deterministic unform/reform and fail-closed restart revalidation | gameplay automation complete; visual capture still pending | **REVIEW_IN_GAME** |
| Flame shell survival acquisition | `data/enshrouded/recipe/flame_altar_brace.json`; `data/enshrouded/recipe/flame_altar_rune.json`; `data/enshrouded/loot_table/blocks/flame_altar_brace.json`; `data/enshrouded/loot_table/blocks/flame_altar_rune.json`; `ModItems` | no new authority; existing BlockItems remain the item surface | PR #100 supplies exactly four braces + four runes per recipe set and self-drop paths; RED→GREEN regression coverage proves resources are present | gameplay acquisition automated/verified | **KEEP** for acquisition contract |
| Flame shell formed-state render package | `FlameAltarBraceBlock.FORMED`; `FlameAltarRuneBlock.FORMED`; repository review confirms no committed brace/rune client resource package yet | presentation-only; must not own ritual/Sanctuary state | Java formed/unformed state exists, but required blockstates, block/item models, textures and visible state distinction remain intentionally unimplemented here | blocked until user-authored assets are supplied | **USER_ART_HANDOFF** |
| Sanctuary / purification physical focus | `FlameAltarBlockEntity.commitFormation()` / `unform()`; `FlameWardRuntime`; Stage 10.05 Flame Altar ward-focus assets | existing Flame Ward/Sanctuary authority only; no second shrine/provider | Sanctuary activates only after valid formation and is revoked on structural unform; separate Purification Shrine controller intentionally deferred | pending active-Sanctuary/purification review | **REVIEW_IN_GAME** |
| Shroud Core Nest composition contract | `ShroudCoreNestLayout` | exactly one existing canonical Shroud Core anchor; decorative roles own no Shroud state | Ordinary/Deadly bounded layouts with one `CORE_ANCHOR`; no world access, chunk forcing, placement or worldgen consumer | cannot be reviewed in-world until a production consumer is approved/implemented | **RUNTIME_CONSUMER_OPEN** |
| Lich manifestation landmark composition contract | `LichManifestationLandmarkLayout` | Stage 06 manifestation/Story service remains canonical | one `ENCOUNTER_ORIGIN`; bounded ritual/bone/portal composition; no encounter ID, boss lifecycle, reward, placement or worldgen authority | cannot be reviewed in-world until a production consumer is approved/implemented | **RUNTIME_CONSUMER_OPEN** |

The original Stage 10.09 logic/authority implementation was verified on PR #97 HEAD `7d9317fd0d211aaf6dce36298b84767408b2be20`: Release Readiness `34188303297 / 101940989577` and Enshrouded CI `34188303299 / 101941000504` completed successfully; PR #97 merged as `452766e29c9de00fc0cb441c6bc397cb990a6d9f`, and independent post-merge runs `34189278455` and `34189278499` passed again. The later restart-harness correction #99 is preserved in the current baseline. PR #100 closed the separate survival-acquisition blocker: final HEAD `2b647d9e4dde39f4205b5082eca1ba9481b1e7d6` passed Release Readiness `34225536910` and Enshrouded CI `34225536909 / 102058621586`, merged as `ed122c42f0eee0e706219361e30c9e1f05416a6d`, and independent post-merge Release Readiness `34226166892` plus Enshrouded CI `34226166913 / 102060710827` passed the complete matrix on that exact `main`.

The latest physical modlist for this reconciliation contains **603 mods** on NeoForge `21.1.248`. Stage 10.09 is **not closed** by these rows. The missing Flame shell render package is a user art handoff, while Core Nest/Lich placement are open production-consumer decisions. Those are implementation/art handoffs, not Stage 10.10 manual-QA items; Stage 10.10 must not infer that missing resources or world placement exist because the Java/layout contracts compile.

## Audit expansion rule

During implementation, every new or touched asset is added here with:
- exact repository path;
- runtime owner;
- source/provenance;
- model/texture/animation status;
- optional dependency use;
- screenshot evidence link/path;
- final approval state.

No asset should silently skip from `REVIEW_IN_GAME`, `USER_ART_HANDOFF` or `RUNTIME_CONSUMER_OPEN` to complete because it compiled.