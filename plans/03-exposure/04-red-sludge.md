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

- [ ] GameTest contact under Flame 1 triggers Deadly exposure immediately.
- [ ] GameTest fluid moved outside a region remains hazardous locally but does not create a new core/region.
- [ ] GameTest ordinary Shroud materializer never emits Red Sludge.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Deadly zones contain a visually distinct lethal sludge hazard tied to, but not authoritative over, the Shroud field.
