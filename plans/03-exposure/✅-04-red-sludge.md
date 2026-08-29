# Enshrouded Plan — Red Sludge

**Milestone:** Level 1 required.

**Goal:** add a concentrated Deadly-Shroud fluid/terrain hazard that cannot bypass passage gating.

**Planned types:** `RedSludgeFluid`, `RedSludgeBlock`, `RedSludgeExposureHandler`.

## Files

- Create fluid/block registrations under `src/main/java/com/gustavaopere/enshrouded/content/fluid/*`.
- Create textures/models/tags/loot data for `enshrouded:red_sludge`.
- Integrate with Shroud materialization rules only in Deadly-intensity profiles.

## Dependencies

- 03 Deadly Shroud.
- 02 corruption materialization.

## Implementation contract

- Red Sludge samples/forces Deadly exposure semantics on contact and adds direct hazard damage only as a secondary effect.
- It is not generated in ordinary Shroud cells.
- Fluid spread is bounded by normal fluid physics/config and cannot itself create new logical Shroud cells.
- Buckets/transport behavior is explicitly controlled so carrying sludge cannot accidentally create permanent remote Shroud regions.
- Safe/sanctuary cleanup behavior is defined and server-authoritative.

## TDD / verification

- [x] GameTest contact under Flame 1 triggers Deadly exposure immediately.
- [x] GameTest fluid moved outside a region remains hazardous locally but does not create a new core/region.
- [x] GameTest ordinary Shroud materializer never emits Red Sludge.
- [x] Secondary contact damage emits exactly one bounded `1.0F` vanilla-generic damage request per player/server tick even when collision callbacks repeat.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, two-boot reload and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; no new `plans/PENDING.md` entry was required.
- [x] After merge, this task is recorded with a `✅-` filename and `plans/STATUS.md` is updated.

## Verification record

- Branch: `feat/03-red-sludge`
- Base main SHA: `49b99116b7d36cec9d65fff1c7d60abdc2b7c206`
- Contact-behavior RED: `bd2cca9492fc9a431888592ed66007500830e613`, workflow `33267826053`.
- DEADLY-only materialization RED: `4c16ca581c6ac51da49b5292dd71736fda1092ca`, workflow `33268123277`.
- Resource/data RED: `386382fd1b32a9004467c14e08be8d44c750c72e`, workflow `33268785863`.
- Secondary-damage behavioral RED: `3adbe6570a8279b51a7061b346e33440cddf8491`; the ordinary-SHROUD and logical non-propagation assertions already passed while only the secondary damage assertions failed.
- Exposure-processing seam RED: `31047581e37e2e67155c546cf30dd8c893406c0c`, workflow `33270395325`.
- Damage-sink seam RED: `589bb5daeff991a4858a51fa04dbd43b6abd219c`, workflow/job `33270632993` / `99148356894`; `compileGameTestJava` failed solely because the three-argument seam did not yet exist.
- Final implementation HEAD: `fc0ef4ba3c838165c44ed60fe67a001073719503`.
- Push verification: workflow/job `33270727369` / `99148609949` — GREEN.
- PR: #25 — `03 — Red Sludge`.
- Final PR-head verification: workflow/job `33270929411` / `99149151268` — GREEN.
- Squash merge SHA: `6c4f54a91a8bb65657bc8a720065b8f14ce2c32c`.

## Final semantics

- `enshrouded:red_sludge` is a physical projection of canonical `DEADLY` Shroud, not a second logical Shroud authority.
- Materialization is gated by canonical severity and uses a unique reversible Level-1 mapping: `minecraft:red_sand -> enshrouded:red_sludge -> minecraft:red_sand`.
- Physical sludge remains locally hazardous after movement, but neither placement nor fluid flow writes cores, regions, cells or synthetic intensity into `ShroudSavedData`.
- Contact immediately uses the existing server-authoritative forced-DEADLY exposure path.
- Secondary damage is normal vanilla `generic` damage, `1.0F`, deduplicated by player/server tick; no bypass damage type was introduced.
- No bucket is registered in this Level-1 slice, so transport remains intentionally constrained.
- Existing Stage 02 purification/restoration remains the authoritative safe cleanup path for materialized terrain; no independent cleanup state was added.

Fixture-only diagnostics involving NeoForge `FakePlayer` health and a mock client without `enshrouded:exposure` payload negotiation were not treated as product failures, and production networking/damage semantics were not weakened to satisfy those fixtures.

**Acceptance:** Deadly zones contain a visually distinct lethal sludge hazard tied to, but not authoritative over, the Shroud field.
