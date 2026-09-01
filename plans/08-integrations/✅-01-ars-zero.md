# Enshrouded Plan — Ars Zero Lich Provider

**Milestone:** Level 1 required.

**Goal:** use the installed `ars_zero:lich` as the preferred first-manifestation actor while keeping Enshrouded story/reward authority.

**Planned types:** `ArsZeroLichProvider`, `ArsZeroCompatibilityProbe`.

## Files

- [x] Created `src/main/java/com/gustavaopere/enshrouded/integration/arszero/*`.
- [x] Added a registry-proxy GameTest fixture because the existing Enshrouded CI has no optional-mod co-load profile.
- [ ] A full co-loaded Ars Zero 2.0.2 fixture remains external verification under `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` in `plans/PENDING.md`.

## Dependencies

- 06 boss provider abstraction.
- Ars Zero 2.0.2 compatibility inventory.

## Implementation contract

- [x] Probe `ModList`/registry once at common setup; provider is available only when `ars_zero:lich` resolves to the expected monster entity type.
- [x] Spawn through registry/generic Minecraft/NeoForge APIs, then let the provider-neutral `ManifestationDirector` attach Enshrouded encounter metadata.
- [x] No Ars Zero implementation class is imported or copied into Enshrouded core.
- [x] Ars Zero native actor behavior/loot remains provider-owned; Enshrouded retains Story State, bossbar, encounter identity, death routing and exactly-once reward authority.
- [x] Missing Ars Zero is silent standalone fallback; a loaded-but-incompatible registry contract emits one concise diagnostic and the native provider remains authoritative.

## TDD / verification

- [x] Unit test: unavailable Ars Zero provider leaves the native fallback selected.
- [x] Registry-proxy GameTest resolves the exact `ars_zero:lich` lookup seam to a real bootstrapped living entity type, spawns through the preferred provider and proves `ManifestationDirector` encounter identity binding.
- [x] Registry-proxy GameTest proves an unrelated same-type entity without Enshrouded encounter metadata does not match the manifestation provider.
- [x] Existing provider-neutral Stage 06 reward regression remains GREEN and proves a valid marked boss death emits exactly one authentic Enshrouded Lich Skull while replay emits none.
- [ ] Full co-loaded test with the actual `ars_zero-1.21.1-2.0.2.jar` and its required mod chain is not part of the current CI profile; see `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001`.

## Verification record

- TDD RED HEAD: `a8d0bd9e2e9c86e383a2199ba60a906e55b7c387`.
- RED workflow/job: `33503022267` / `99840500558` — `compileTestJava` failed only for the deliberately absent Stage 08.01 integration classes.
- Final implementation HEAD: `77f1b285e7ba1c2c0290c31f95873559fb599010`.
- Final push workflow/job: `33503998435` / `99843629417` — GREEN.
- PR: #59 — `Stage 08.01: integrate Ars Zero Lich provider`.
- Final exact PR-head workflow/job: `33508994529` / `99859813891` — GREEN.
- GameTest server: 76/76 required tests passed, including batch `arsZeroProvider` with 2 tests.
- SavedData two-boot reload: GREEN.
- Dedicated-server save/reload smoke: GREEN.
- Implementation merge SHA: `95b0189ca4421b688294a6cee2b9f06983159790`.
- Independent post-merge `main` workflow/job: `33509695387` / `99862094519` — `completed/success` across the complete gate set.

## Merge gate

- [x] All task-local executable tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, SavedData two-boot and dedicated-server smoke are GREEN.
- [x] The unresolved real-mod co-load verification is not hidden and is recorded in `plans/PENDING.md`.
- [x] Implementation merged to `main` and independent post-merge CI is GREEN.
- [x] Closeout renames this file with `✅-` and updates `plans/STATUS.md` in the closeout checkpoint.

**Acceptance:** Stage 08.01's optional adapter is implemented, fail-closed, provider-neutral and merged. Standalone/native fallback is executable and verified. The registry/provider seam is executable under a bootstrapped GameTest fixture. A claim that the actual Ars Zero 2.0.2 distribution JAR has been co-loaded by Enshrouded CI is intentionally withheld until `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` is closed.
