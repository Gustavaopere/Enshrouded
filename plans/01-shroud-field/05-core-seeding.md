# Enshrouded Plan — Core Seeding and Discovery

**Milestone:** Level 1 required.

**Goal:** place sparse Level 1 core sites into newly generated Overworld terrain and provide deterministic/admin discovery tools.

**Planned types:** `ShroudCoreCandidateField`, `ShroudCoreFeature`, `ShroudCoreWorldgenRegistry`, `ShroudCoreRegistrationQueue`, `ShroudCoreCommand`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/shroud/worldgen/*`.
- Create configured/placed feature and biome-modifier data under `src/main/resources/data/enshrouded/*`.
- Create `src/main/java/com/gustavaopere/enshrouded/command/ShroudCoreCommand.java`.

## Dependencies

- 02 core lifecycle.
- 03 frontier expansion.

## Implementation contract

- Candidate placement is deterministic from world seed/coarse candidate cell and respects minimum spacing.
- Only new chunks receive automatic physical core sites; existing chunks are not retroactively carved.
- Worldgen thread does not mutate SavedData directly; canonical registration occurs safely/idempotently on server runtime after placement/load.
- Initial seeded region starts small and expands through the same scheduler as any other core.
- Admin/dev commands can create, inspect and destroy a core explicitly for testing without becoming normal progression mechanics.

## TDD / verification

- [ ] Unit-test deterministic candidate spacing and dimension filters.
- [ ] GameTest/bootstrap verify feature registry keys and placed feature data load on dedicated server.
- [ ] Generate/reload a test chunk and prove the same core site is not duplicated.
- [ ] Prove existing-chunk load alone does not seed a core.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Newly explored Overworld terrain can contain sparse discoverable Level 1 cores that register idempotently and feed the canonical expansion system.
