# ✅ Enshrouded Plan — Zone Query and Sync

Merged into `main` as PR #13 after exact-head pull-request CI completed GREEN.

**Milestone:** Level 1 required.

**Goal:** provide one canonical, server-authoritative query for local Shroud intensity/severity/source and a minimal clientbound snapshot used by later HUD/fog/audio systems.

**Implemented types/services:** `DefaultShroudQuery`, `ShroudSpatialIndex`, `ShroudQueryConfig`, `ShroudSamplePayload`, `ClientShroudState`, `ShroudPlayerSyncTracker`, `ShroudSampleSyncService`, `ShroudSyncRuntime`, `ModNetworking`.

## Runtime contract delivered

- Canonical samples are derived from the persisted logical Shroud field; the query does not create a second authoritative world model.
- Spatial lookup uses a dimension-local cached index keyed to immutable `ShroudWorldState` snapshots rather than scanning every core on every sample.
- Overlapping influence resolves deterministically: higher intensity wins and UUID ordering is the stable tie-breaker.
- Severity thresholds remain server-authoritative and are read from bounded server config at sampling time.
- Foundation `FlameWardQuery` is applied as an effective overlay only: latent intensity, severity and source remain intact while `sanctuarySuppressed=true` records effective protection.
- The client receives only a compact, versioned `ShroudSamplePayload`; there is no serverbound Shroud query/mutation payload.
- Per-player synchronization is change-driven and rate-limited on the server tick clock.
- Client sequence IDs reject stale/out-of-order payloads and connection lifecycle resets prevent a previous server session from poisoning a new one.
- Renderer-specific presentation remains client-local and is not synchronized as authoritative state.

## TDD / verification

- [x] Initial RED contract captured before production query/sync implementation: `4c1dc0d44cc1dc9493c239cb70c96950c1051136`.
- [x] Spatial semantics RED captured before indexed query implementation: `b071ba032f4cbc80741cb8f3c54eabe3783a933d`.
- [x] Sync ordering/rate-limit RED captured before runtime synchronization implementation: `192402664738f4e400d1ff5442873e7ad87df39a`.
- [x] Query vectors cover clear/shroud/deadly thresholds, deterministic overlap resolution and Flame Ward latent-field semantics.
- [x] `ShroudSpatialIndexBenchmarkTest` guards local lookup against linear all-core scanning.
- [x] Sync tests cover payload round-trip, stale sequence rejection, change detection, rate limiting and connection-scoped client reset.
- [x] `NetworkDirectionContractTest` enforces clientbound-only Shroud synchronization and rejects serverbound/bidirectional registration.
- [x] Final implementation HEAD: `844760a4cbc411b3cce2409fa99f4b20e19bddaa`.

## Merge gate

- [x] All task-specific tests GREEN on final branch HEAD.
- [x] `./gradlew test` GREEN.
- [x] Frontier performance baseline GREEN.
- [x] Diff sanity GREEN.
- [x] NeoForge build GREEN.
- [x] Production JAR sanity GREEN.
- [x] GameTest server GREEN.
- [x] Shroud SavedData two-boot reload GREEN.
- [x] Dedicated-server two-boot save/reload smoke GREEN.
- [x] Pull-request-triggered verification GREEN on exact final HEAD: workflow `33199383533`, job `98944636449`.
- [x] PR #13 merged into `main` as `39ed2b1a689b1560a06b6a9e961fde50c68c18a1`.
- [x] No unresolved task-local blocker remains; cross-stage ward/exposure/terrain obligations continue in `plans/PENDING.md`.

**Acceptance:** One authoritative, indexed Shroud query now feeds server gameplay and a minimal read-only client snapshot without forcing chunk loads, granting client authority, or erasing latent Shroud beneath future Sanctuary protection.
