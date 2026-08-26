# Enshrouded Plan — Materialization Rules

**Milestone:** Level 1 required.

**Goal:** define data-driven block/growth conversion rules and a bounded loaded-chunk materialization queue.

**Planned types:** `ShroudMaterializationService`, `ShroudMutationJob`, `CorruptionRule`, `CorruptionRuleRegistry`, `MutationKind`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/terrain/*`.
- Create datapack JSON codec/registry under `data/enshrouded/shroud_corruption/*`.
- Create block tags under `data/enshrouded/tags/block/*`.

## Dependencies

- 01 Shroud Field complete.

## Implementation contract

- Materialization is scheduled only for loaded positions/chunks and has global/per-chunk mutation caps.
- Rules explicitly declare source predicate/tag, corrupted result or growth action, reversal rule and safety class.
- Unknown modded blocks fail closed; opt-in comes from tags/datapacks/config.
- No rule mutates containers, block entities, portals, machines or unclassified structural blocks by default.
- Chunk load reconciles visual state lazily from the logical field rather than bulk-converting the whole chunk in one tick.

## TDD / verification

- [ ] Codec tests accept valid rules and reject non-reversible destructive rules lacking an explicit safety mode.
- [ ] Queue tests prove global/per-chunk budgets are never exceeded.
- [ ] GameTest a loaded natural patch gradually materializes while an adjacent unknown block remains untouched.
- [ ] GameTest chunk unload cancels/defer jobs without forcing reload.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Loaded terrain reflects Shroud intensity progressively through reversible, data-driven, budgeted mutations.
