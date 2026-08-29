# Enshrouded Plan — Player Exposure State

**Milestone:** Level 1 required.

**Status:** ✅ implemented, verified and merged.

**Goal:** give each player a persistent/synchronized Shroud exposure reserve with deterministic drain/recovery and a stable policy seam for Deadly Shroud.

**Implemented types:** `ShroudExposureAttachment`, `ExposureSchema`, `ExposureService`, `ExposureSnapshot`, `ExposurePayload`, `DeadlyExposurePolicy`, `ExposureSamplingCadence`, `ExposurePlayerSyncTracker`, `ExposureRuntime`, `ClientExposureState`, `ClientExposureLifecycle`.

## Runtime contract

- The persistent NeoForge player attachment carries explicit `schema_version` and `remaining_ticks` fields from its first implementation.
- Unknown future attachment schema fails closed through the codec with a diagnostic instead of silently resetting unsafe state.
- Default ordinary-Shroud maximum reserve is configurable and initially 300 seconds, with bounded configuration values.
- `ShroudSample.sanctuarySuppressed=true` is treated as effective safe/recovery while the underlying logical severity/intensity remains present in the authoritative snapshot.
- Without Sanctuary suppression: `CLEAR` regenerates reserve; `SHROUD` drains it; `DEADLY` delegates through injected `DeadlyExposurePolicy`.
- `DeadlyExposurePolicy.levelOneBarrier()` is the standalone fail-closed Level-1 fallback and clamps remaining reserve to a bounded emergency window before rapid drain.
- `ExposureService` contains no Flame/progression implementation dependency; Task `03-deadly-shroud` can substitute a passage-aware policy through the same interface.
- Drain/recovery uses server-authoritative tick delta with a hard lag cap.
- The attachment is not `copyOnDeath`: a fresh respawn receives a safe baseline. Disconnect/reconnect preserves the existing unsafe attachment state and resets only ephemeral cadence/sync state.
- Client presentation receives changed snapshots only. Exposure networking is `playToClient` only; the client has no payload that can submit reserve values upstream.
- Client logout resets the local exposure sequence epoch so fresh server sequence numbers after reconnect cannot be rejected as stale.

## TDD / verification

- [x] Unit-test schema version round-trip plus rejection/diagnostic for an unknown future schema.
- [x] Unit-test drain/recovery boundary math and configured max clamping.
- [x] Unit-test suppressed `SHROUD`/`DEADLY` samples recover as safe while their logical sample data remains unchanged.
- [x] Unit-test `DeadlyExposurePolicy.levelOneBarrier()` is fail-closed and clamps exposure to the configured emergency window.
- [x] Unit-test `ExposureService` uses the injected Deadly policy exactly once for an unsuppressed `DEADLY` sample.
- [x] GameTest crossing zone boundaries changes exposure exactly once per sampled interval using the canonical `DefaultShroudQuery`.
- [x] GameTest player serialization/reload preserves unsafe exposure state; a fresh respawn receives the safe baseline.
- [x] Network contract confirms client cannot submit exposure values.
- [x] Client lifecycle regression proves the reconnect sequence epoch is reset on logical-client logout.

## Evidence

- Branch: `feat/03-player-exposure-state`
- Observed structural RED workflow: `33244211798`.
- Deadly-policy RED HEAD/workflow: `569d341f47864461ebbd66bc84bd7f72307eaffc` / `33244734634`.
- Persistence-codec RED HEAD/workflow: `1d4f4272f3f218717a38882a56bdc17e720a72c9` / `33244853207`.
- NeoForge attachment/config RED HEAD/workflow: `6a1f44120d2cc5fda0d57cad2ca4401447617899` / `33244989474`.
- Client reconnect RED HEAD/workflow: `c1c6dab1dcee0bcda4a34d281dc29a8ff174fbc4` / `33246349587`.
- Final implementation HEAD: `99cb4174e047fe0c85e351a4758a869a1d812af0`.
- Push verification: workflow `33246472651` — full GREEN.
- PR: #22 — `03 — Player Exposure State`.
- Final PR-head verification: workflow `33246650603`, job `99085167187` — full GREEN.
- Exact PR-head gates: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, GameTest server, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.
- Merge SHA: `73396291680aebe9d45e7f6d6347579d04010dd1`.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN through CI.
- [x] NeoForge build, GameTests, two-boot persistence harness and dedicated-server smoke are GREEN.
- [x] Cross-stage contracts remain explicit; `ENSH-L1-CORE-TO-EXPOSURE-001` is now satisfied by executable Stage 01 + Stage 03 evidence.
- [x] Plan renamed with `✅-` and project status checkpointed after merge.

**Acceptance:** Ordinary Shroud now runs a reliable server-authoritative timer, Sanctuary suppression makes effective exposure safe without erasing latent Shroud state, Deadly behavior is injected through a stable fail-closed policy seam, safe air recovers reserve, and versioned state survives normal multiplayer lifecycle correctly.
