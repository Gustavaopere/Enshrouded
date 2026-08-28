# Enshrouded Plan — Terrain Safety and Protection

**Milestone:** Level 1 required.

**Goal:** centralize sanctuary/claim/structure vetoes and aggressive-mode opt-ins before any Stage 02 service is allowed to mutate world blocks.

**Planned types:** `DefaultMutationAuthority`, `ProtectedAreaService`, `ProtectionDecision`, `MutationSafetyMode`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/protection/*`.
- Create `data/enshrouded/tags/block/corruptible_safe.json` and `corruptible_aggressive.json`.
- Add server config for `SAFE`/`AGGRESSIVE` mutation mode.

## Dependencies

- 01 Shroud Field complete.
- Foundation `MutationAuthority`, `MutationKind` and `FlameWardQuery` contracts/defaults.

This task is the **first implementation branch of Stage 02** despite its historical filename. It must merge before materialization, growth placement or purification can introduce world mutation paths.

## Implementation contract

- `SAFE` is default: only explicitly safe natural tags/growth placement mutate.
- `AGGRESSIVE` expands the opt-in tags but still never bypasses sanctuary or installed claim adapters.
- `DefaultMutationAuthority` is the sole implementation gate consumed by every Stage 02 world mutation call site.
- `DefaultMutationAuthority` receives the Foundation `FlameWardQuery`; before Stage 05 the no-ward fallback returns false, and after Stage 05 the indexed ward implementation vetoes mutation without changing terrain call sites.
- `ProtectedAreaService` is an input to `DefaultMutationAuthority`, not a second terrain gate. Later FTB/MineColonies adapters plug into it without changing terrain code.
- `ProtectedAreaService` returns `ProtectionDecision.UNPROTECTED`, `.PROTECTED` or `.INDETERMINATE`; it never collapses adapter uncertainty into `false`/unprotected.
- `DefaultMutationAuthority` permits protection-sensitive mutation only when the aggregate decision is definitively `UNPROTECTED`. `PROTECTED` and `INDETERMINATE` veto by default.
- Optional protection mods that are absent register no adapter and therefore do not create an `INDETERMINATE` result. A present/enabled adapter that cannot answer because of API/query failure returns `INDETERMINATE` and records one concise diagnostic rather than silently allowing mutation.
- Any expert override of `INDETERMINATE` behavior must be explicit server configuration, off by default, and covered by tests; `AGGRESSIVE` mode alone is not such an override.
- Protected block entities/containers are denied regardless of tag mistakes unless an explicit expert override is configured.
- Ward/claim lookups must not force chunk loads or scan all altars/claims per candidate.
- Stage 02 reuses the Foundation `MutationKind`; no duplicate mutation-kind enum/model is introduced.

## TDD / verification

- [ ] Unit-test safety matrix across mutation modes/kinds.
- [ ] Unit-test Foundation no-ward fallback does not veto otherwise-safe terrain.
- [ ] Unit-test injected ward query vetoes candidate mutation through the same authority path.
- [ ] Unit-test `UNPROTECTED` permits normal safety evaluation while `PROTECTED` and `INDETERMINATE` both veto by default.
- [ ] Unit-test an absent optional adapter contributes no uncertainty, while an enabled adapter query failure becomes `INDETERMINATE` and emits a bounded diagnostic.
- [ ] Unit-test protected block entities/containers fail closed in SAFE mode.
- [ ] Static/test scan asserts no Stage 02 world `setBlock` mutation path bypasses the authority wrapper once later Stage 02 branches exist.
- [ ] GameTest sanctuary/explicit-protected predicate prevents jobs while neighboring unprotected terrain mutates when the first materialization consumer is available.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A fail-closed mutation authority exists before any Stage 02 world mutation implementation, providing one auditable tri-state gate for tags, Sanctuary, claims and protected structures.
