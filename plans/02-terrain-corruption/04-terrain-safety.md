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
- Foundation `MutationAuthority` and `FlameWardQuery` contracts/defaults.

## Implementation contract

- `SAFE` is default: only explicitly safe natural tags/growth placement mutate.
- `AGGRESSIVE` expands the opt-in tags but still never bypasses sanctuary or installed claim adapters.
- Every terrain mutation call site invokes the same `MutationAuthority` service.
- `DefaultMutationAuthority` receives the Foundation `FlameWardQuery`; before Stage 05 the no-ward fallback returns false, and after Stage 05 the indexed ward implementation vetoes mutation without changing terrain call sites.
- `ProtectedAreaService` is an input to `DefaultMutationAuthority`, not a second terrain gate. Later FTB/MineColonies adapters plug into it without changing terrain code.
- Protected block entities/containers are denied regardless of tag mistakes unless an explicit expert override is configured.
- Ward/claim lookups must not force chunk loads or scan all altars/claims per candidate.

## TDD / verification

- [ ] Unit-test safety matrix across mutation modes/kinds.
- [ ] Unit-test Foundation no-ward fallback does not veto otherwise-safe terrain.
- [ ] Unit-test injected ward query vetoes candidate mutation through the same authority path.
- [ ] Static/test scan asserts no Stage 02 world `setBlock` mutation path bypasses the authority wrapper.
- [ ] GameTest sanctuary/explicit-protected predicate prevents jobs while neighboring unprotected terrain mutates.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Terrain mutation has one auditable fail-closed gate, Sanctuary plugs into that gate through the Foundation ward boundary, and safe defaults remain suitable for a large modpack world.
