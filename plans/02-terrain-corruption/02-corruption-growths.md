# Enshrouded Plan — Corruption Growths

**Milestone:** Level 1 required.

**Goal:** add original Enshrouded visual growth blocks that let corruption cover surfaces without replacing every base block.

**Planned types:** `ShroudGrowthBlock`, `ShroudVeinBlock`, `WitheredGrowthBlock`, `GrowthPlacementService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/content/block/*`.
- Create models/blockstates/loot tables/tags for growth blocks.
- Extend `ShroudMaterializationService`.

## Dependencies

- `04 terrain safety` merged.
- `01 materialization rules` merged.

## Implementation contract

- Growths prefer exposed surfaces inside corrupted cells and are replaceable/non-container decorative hazards.
- Placement density scales with intensity but is deterministically sampled and work-budgeted.
- Every growth placement/removal that mutates the world passes through the already-merged `MutationAuthority`; `GrowthPlacementService` may not create a parallel safety path.
- Growths do not become independent spread sources.
- At least one common Shroud growth and one Deadly/Red growth visual family exists for Level 1.
- Growth collision/damage is explicit; decorative veins do not accidentally suffocate normal entities unless configured.

## TDD / verification

- [ ] Unit-test deterministic candidate sampling and density bounds.
- [ ] Static/test scan proves growth mutation sinks route through `MutationAuthority`.
- [ ] GameTest growths place only on valid/authorized faces, survive save/reload and never spread outside logical Shroud cells.
- [ ] GameTest Deadly growth is never placed in ordinary-only intensity below the threshold.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Ordinary and Deadly Shroud are visually legible through native surface growths without destructive full-block replacement or a bypass around the canonical terrain-safety authority.
