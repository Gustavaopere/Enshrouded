# Stage 10.04 — Lich Skull + Manifestation Presentation Design Sheet

## Purpose

Stage 10.04 replaces the vanilla Wither Skeleton Skull placeholder with an original first-party Lich trophy and adds bounded presentation cues to the already-canonical manifestation lifecycle.

This task is presentation-only. It does not create a new Lich provider, Story State, encounter lifecycle, defeat authority or reward authority.

## Lich Skull visual identity

The Manifestation I trophy is a ritualized elongated skull/mask with an intentionally broken silhouette rather than a vanilla skull derivative.

Primary geometry language:
- `skull_root` as the overall transform anchor;
- `bone_mask` for the elongated ritual face/mask mass;
- `broken_crown` for asymmetric upper framing;
- `halo` for fragmented ritual identity;
- `fragments` for detached spectral/broken pieces;
- `fractured_arcana` for the supernatural residue layer.

The editable source is `art/blockbench/lich_skull_manifestation_1.bbmodel`. The runtime geometry is `assets/enshrouded/geo/lich_skull_manifestation_1.geo.json`.

## Materials and emissive treatment

Runtime textures:
- `textures/item/lich_skull_manifestation_1.png`;
- `textures/item/lich_skull_manifestation_1_glowmask.png`.

The base material uses a dark bone/ritual-mask hierarchy with selective spectral fracture accents. The glowmask is intentionally selective and is rendered through GeckoLib `AutoGlowingGeoLayer`; the entire trophy must not become a fullbright emissive object.

Both PNGs are project-authored binaries and are explicitly listed in `provenance/third-party-provenance.json`.

## Item render path

`LichSkullItem` implements GeckoLib `GeoItem` while preserving common/server architecture boundaries.

The renderer follows GeckoLib's split-source pattern:
- common item code stores a `GeoRenderProvider` without importing `net.minecraft.client.*`;
- `LichSkullRenderProvider` lives under the physical-client package;
- `EnshroudedClient` injects the renderer during `EntityRenderersEvent.RegisterRenderers`;
- `LichSkullRenderer` uses `LichSkullGeoModel` plus `AutoGlowingGeoLayer`.

The item model uses `builtin/entity` and supplies authored transforms for GUI, ground, fixed/display, first-person hand and third-person hand presentation.

## Animation package

Runtime animation file: `assets/enshrouded/animations/lich_skull_manifestation_1.animation.json`.

Authored clips:
- `animation.lich_skull.idle` — subtle continuous trophy motion only;
- `animation.lich_skull.ritual_resonance` — reserved presentation clip for explicit safe/event-driven resonance.

Animation completion is never a Story/encounter/reward mutation boundary.

## Manifestation presentation bridge

`LichManifestationPresentation` is a bounded downstream presentation bridge attached to the canonical Stage 06 lifecycle.

Spawn presentation occurs only after:
1. provider spawn succeeds;
2. Story encounter creation/activation succeeds;
3. optional arena activation succeeds.

Defeat presentation occurs only after the persisted encounter validates the dead bound actor and `StorySavedData.defeatEncounter(encounterId)` succeeds.

Hard budgets:
- spawn: maximum 28 `lich_arcana` particles;
- defeat: maximum 36 `lich_arcana` particles;
- audience/audio radius: maximum 48 blocks;
- no chunk forcing;
- no global entity/world scans;
- no actor spawning, progression writes or reward issuance inside the presentation bridge.

The particle asset `textures/particle/lich_arcana.png` is project-authored and provenance-tracked.

## Authority invariants

Stage 10.04 preserves all prior canonical authority:
- `ManifestationDirector` + provider own physical manifestation spawning;
- `StorySavedData` owns Story/encounter state;
- existing defeat routing owns valid actor-to-encounter resolution;
- existing Lich Skull reward path owns exactly-once trophy issuance;
- GeckoLib/item render/VFX code owns presentation only.

A presentation failure may reduce visual feedback, but it must not create, defeat, reward, replay or resurrect an encounter.

## Automated validation evidence

TDD RED:
- commit `7f45cbafa94c438919569acc419ddc2686c607ca`;
- Enshrouded CI `34068621021 / 101581789264` failed at the new Stage 10.04 contract before implementation.

Final implementation PR:
- PR #87 — `Stage 10.04 — Lich Skull and manifestation presentation`;
- final HEAD `2c79c7e9b6673b89117f38154267aa323962b50c`;
- Release Readiness `34071068581 / 101588392524` — success;
- Enshrouded CI `34071068613 / 101588392587` — success across Stage 10 contracts, 333 unit tests, performance baselines, NeoForge build, GameTests, two-boot reload, real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.

Merge/post-merge:
- merge `a54e8f32e85c6b07dd3ace89a301743bfeb669ca`;
- post-merge Release Readiness `34071344201 / 101589147118` — success;
- post-merge Enshrouded CI `34071344252 / 101589147239` — success across the complete matrix.

## Manual P0 acceptance still required

Technical completion does not equal `ART APPROVED`.

Required manual evidence remains:
- GUI screenshot at normal inventory scale;
- first-person hand screenshot;
- third-person hand screenshot;
- ground/fixed display screenshot;
- manifestation spawn/defeat readability in normal combat surroundings;
- reduced-effects readability;
- external full 607-mod-pack visual smoke.

Until that evidence exists, the Lich Skull remains technically complete but visually awaiting P0 in-game review.
