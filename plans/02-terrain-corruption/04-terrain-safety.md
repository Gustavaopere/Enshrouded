# Enshrouded Plan — Terrain Safety and Protection

**Milestone:** Level 1 required.

**Goal:** centralize sanctuary/claim/structure vetoes and aggressive-mode opt-ins for every Shroud mutation.

**Planned types:** `DefaultMutationAuthority`, `ProtectedAreaService`, `MutationSafetyMode`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/protection/*`.
- Create `data/enshrouded/tags/block/corruptible_safe.json` and `corruptible_aggressive.json`.
- Add server config for `SAFE`/`AGGRESSIVE` mutation mode.

## Dependencies

- 01 materialization rules.
- Foundation `MutationAuthority`.

## Implementation contract

- `SAFE` is default: only explicitly safe natural tags/growth placement mutate.
- `AGGRESSIVE` expands the opt-in tags but still never bypasses sanctuary or installed claim adapters.
- Every terrain mutation call site invokes the same authority service.
- Protected block entities/containers are denied regardless of tag mistakes unless an explicit expert override is configured.
- Later FTB/MineColonies adapters plug into `ProtectedAreaService` without changing terrain code.

## TDD / verification

- [ ] Unit-test safety matrix across mutation modes/kinds.
- [ ] Static/test scan asserts no Stage 02 world `setBlock` mutation path bypasses the authority wrapper.
- [ ] GameTest sanctuary/explicit-protected predicate prevents jobs while neighboring unprotected terrain mutates.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Terrain mutation has one auditable fail-closed gate and safe defaults suitable for a large modpack world.
