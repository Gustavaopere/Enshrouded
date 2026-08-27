# Enshrouded Plan — Materialization Rules

**Milestone:** Level 1 required.

**Goal:** define data-driven block/growth conversion rules and a bounded loaded-chunk materialization queue that can mutate only through the already-merged terrain safety gate.

**Planned types:** `ShroudMaterializationService`, `ShroudMutationJob`, `CorruptionRule`, `CorruptionRuleRegistry`.

Stage 02 reuses the Foundation-owned `MutationKind`; this task must not create a second mutation-kind enum/model.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/terrain/*`.
- Create datapack JSON codec/registry under `data/enshrouded/shroud_corruption/*`.
- Create block tags under `data/enshrouded/tags/block/*`.

## Dependencies

- 01 Shroud Field complete.
- `04 terrain safety` merged first, providing `DefaultMutationAuthority` behind the Foundation `MutationAuthority` contract.

## Implementation contract

- Materialization is scheduled only for loaded positions/chunks and has global/per-chunk mutation caps.
- Every candidate world mutation is authorized through injected `MutationAuthority` before any `setBlock`/placement operation; there is no temporary bypass path.
- Rules explicitly declare source predicate/tag, corrupted result or growth action, reversal rule and safety class.
- Unknown modded blocks fail closed; opt-in comes from tags/datapacks/config.
- No rule mutates containers, block entities, portals, machines or unclassified structural blocks by default.
- Chunk load reconciles visual state lazily from the logical field rather than bulk-converting the whole chunk in one tick.
- Materialization consumes the Foundation-owned `MutationKind` values when asking authority; no local safety taxonomy may diverge from the gate.

## TDD / verification

- [ ] Codec tests accept valid rules and reject non-reversible destructive rules lacking an explicit safety mode.
- [ ] Queue tests prove global/per-chunk budgets are never exceeded.
- [ ] Unit/static test proves all materialization mutation sinks call `MutationAuthority` before world mutation.
- [ ] GameTest a loaded natural patch gradually materializes while an adjacent unknown/protected block remains untouched.
- [ ] GameTest chunk unload cancels/defer jobs without forcing reload.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Loaded terrain reflects Shroud intensity progressively through reversible, data-driven, budgeted mutations, with every world change passing through the pre-existing fail-closed authority.
