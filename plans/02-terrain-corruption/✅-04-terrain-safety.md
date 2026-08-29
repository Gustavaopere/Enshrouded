# ✅ Enshrouded Plan — Terrain Safety and Protection

Merged into `main` as PR #15 after exact-head pull-request CI completed GREEN.

**Milestone:** Level 1 required.

**Goal:** centralize sanctuary/claim/structure vetoes and aggressive-mode opt-ins before any Stage 02 service is allowed to mutate world blocks.

**Implemented types/services:** `DefaultMutationAuthority`, `ProtectedAreaService`, `ProtectionDecision`, `MutationSafetyMode`, `MutationSafetyPolicy`, `TerrainSafetyTags`.

## Runtime contract delivered

- `SAFE` is the default mutation mode; corruption requires explicitly safe-tagged terrain.
- `AGGRESSIVE` additionally permits aggressive-tagged terrain but does not bypass Sanctuary, claims, block-entity safety or indeterminate protection.
- `DefaultMutationAuthority` is the sole Stage 02 mutation gate and consumes Foundation `MutationAuthority`, `MutationKind` and `FlameWardQuery`.
- Ward behavior is mutation-kind-aware: `CORRUPTION` and `CORE_PLACEMENT` fail closed under a ward, while `PURIFICATION` is not rejected merely because a ward is active and `RITUAL_STRUCTURE` remains subject to ordinary authorization.
- `ProtectedAreaService` returns `UNPROTECTED`, `PROTECTED` or `INDETERMINATE`; adapter failures/null answers become `INDETERMINATE` and are denied by default.
- Optional protection mods that are absent contribute no uncertainty; future adapters plug into the same service without changing terrain mutation call sites.
- Block entities/containers are denied by default; expert overrides for indeterminate protection and block-entity mutation are explicit server config values and are off by default.
- `corruptible_safe` and `corruptible_aggressive` block tags define opt-in terrain surfaces.
- Ward/protection query failures emit bounded diagnostics and fail closed.
- No duplicate Stage 02 mutation-kind model was introduced.

## TDD / verification

- [x] Unit-tested safety matrix across mutation modes/kinds.
- [x] Unit-tested Foundation no-ward fallback permits otherwise-safe terrain.
- [x] Unit-tested injected ward vetoes `CORRUPTION` and `CORE_PLACEMENT`.
- [x] Unit-tested warded `PURIFICATION` remains eligible when other safety checks pass and `RITUAL_STRUCTURE` is not denied solely by the ward.
- [x] Unit-tested `UNPROTECTED` vs `PROTECTED`/`INDETERMINATE` fail-closed behavior.
- [x] Unit-tested absent protection service vs query failure/null result semantics with bounded diagnostics.
- [x] Unit-tested block entities fail closed without explicit expert override.
- [x] Resource tests guard SAFE/AGGRESSIVE terrain tag files.
- [x] GameTest exercised real loaded-world block state, Sanctuary veto, protection veto, block entity rejection and replaceable core-placement target.

## Merge gate

- [x] All task-specific tests GREEN on final branch HEAD.
- [x] `./gradlew test` GREEN.
- [x] Frontier benchmark baseline GREEN.
- [x] Diff sanity GREEN.
- [x] NeoForge build GREEN.
- [x] Production JAR sanity GREEN.
- [x] GameTest server GREEN.
- [x] Shroud SavedData two-boot reload GREEN.
- [x] Dedicated-server two-boot save/reload smoke GREEN.
- [x] Exact push-head verification GREEN: workflow `33220960726`, job `99014693027`.
- [x] Exact PR-head verification GREEN: workflow `33222433328`, job `99019124609`.
- [x] Final implementation HEAD: `230a89c7d6ce9def0ead75a635200418a0e6e7b9`.
- [x] PR #15 merged into `main` as `f398c13bac776f4f0c7b130153d69124e6970431`.
- [x] No unresolved task-local blocker remains; cross-stage ward/claim contracts remain tracked in `plans/PENDING.md`.

**Acceptance:** A fail-closed mutation authority exists before any Stage 02 world mutation implementation, providing one auditable tri-state gate for tags, Sanctuary, claims and protected structures without trapping legitimate purification behind the ward that prevented new corruption.
