# 08.04 — JourneyMap

**Status:** ✅ implemented, verified, merged and confirmed on `main`.

**Milestone:** Level 1 required.

**Goal:** expose only legitimately discovered Shroud cores to JourneyMap without turning the map into an omniscient scanner or gameplay authority.

## Audited dependency

- Current pack runtime: `journeymap-neoforge-1.21.1-6.0.7.jar`.
- JourneyMap API tag audited: `TeamJM/journeymap-api` `1.21.1_2.0.0`.
- Compile artifact: `info.journeymap:journeymap-api-neoforge:2.0.0-1.21.1`.
- Enshrouded uses the API through a pinned `compileOnly` dependency; JourneyMap classes are not bundled or redistributed.
- The plugin uses the official `@JourneyMapPlugin(apiVersion = "2.0.0")` discovery path and is not referenced by the common Enshrouded bootstrap.

## Canonical discovery authority

JourneyMap owns presentation only. Enshrouded owns discovery, persistence, lifecycle knowledge, ownership and networking.

- Server-side discovery is persisted in `ShroudDiscoverySavedData`, stored globally through overworld `DataStorage` and keyed by canonical `ProgressionOwner.stableKey()`.
- Discovery reuses the exact canonical `ShroudSample` already sampled for the player's normal Shroud presentation sync.
- Only `ShroudSample.sourceId()` is considered; discovery then performs a direct `world.cores().get(sourceId)` lookup.
- No JourneyMap adapter and no discovery runtime iterates undiscovered core SavedData to reveal coordinates.
- `ProgressionRuntimeBindings.ownerResolver()` supplies current PLAYER/TEAM ownership, preserving the Stage 08.03 FTB Teams contract.
- Owner changes affect future discovery/snapshot resolution only. Knowledge is never silently migrated or merged between owners.

## Lifecycle semantics

- `ACTIVE`: known core is marker-visible.
- `DESTROYED`: core remains known to the owner but marker-hidden.
- `PURIFIED`: known core becomes marker-visible again.
- `ShroudCoreDestroyedEvent` and `ShroudCorePurifiedEvent` update only owners that already know the core; lifecycle events never create discovery.
- Purification publishes the terminal lifecycle transition from the authoritative regression runtime.

## Client synchronization

- `ShroudDiscoveryPayload` is a clientbound complete snapshot, not a serverbound query/mutation channel.
- Payloads carry an owner stable key, monotonic per-player sequence and canonical marker-visible core list.
- `ShroudDiscoverySyncTracker` suppresses unchanged snapshots.
- Owner changes force complete replacement, including an empty snapshot, so stale markers cannot leak across PLAYER/TEAM transitions.
- Server logout/stop forgets runtime tracker state.
- Client logout resets `ClientShroudDiscoveryState`, preventing a new connection's sequence from being rejected as stale.

## JourneyMap projection

`JourneyMapAdapter` consumes only `ClientShroudDiscoveryState` snapshots.

- Markers are transient: `persistent=false`.
- They are visible on the map but do not render beacon/world clutter.
- Add/update/remove is reconciled from the complete authorized snapshot.
- Mapping start clears Enshrouded-owned JourneyMap displays and rebuilds from the current authorized snapshot.
- Mapping stop removes Enshrouded displays.
- JourneyMap state is never read back as canonical gameplay/discovery data.
- No additional Shroud query, scan, damage authority or progression store exists in the adapter.

## Optional-mod / dedicated-server behavior

JourneyMap is not required at runtime. Repository CI runs GameTests and dedicated-server save/reload without JourneyMap loaded; these gates remained GREEN after the plugin and API references were added. This proves the common/dedicated path does not require JourneyMap classes at runtime.

## TDD provenance

Representative RED checkpoints:

- `a2b86ce284c43e0aba3fb5bce5048d7b2a658c8d` — initial owner-scoped discovery visibility contract.
- `91e976d3` — destroyed-core visibility contract.
- `cfdf751a` — discovery persistence contract.
- `6108d399` — discovery clientbound sync contract.
- `4f23415a` — canonical observation contract.
- `1fff157e` — known-core lifecycle contract.
- `ac9e7b967b42b803d8af5d1ce7e21b8e0d41bc29` — runtime wiring RED. Workflow/job `33634842638` / `100263003296`: production and tests compiled, 300 tests ran, and only the deliberately absent runtime contract failed.
- `4c9f459272e6c59fa90ddf631144c2e68c441c3f` — JourneyMap client boundary RED. Workflow/job `33635654507` / `100266056178`: production/test compilation succeeded, 301 tests ran, and only the new JourneyMap integration contract failed.

GREEN checkpoints:

- `e18aeb571ba1e02d7ecd620689177c77ddc203db` — real JourneyMap API adapter smoke covering add, lifecycle update, removal and `persistent=false`; workflow/job `33636971843` / `100270513234` — `completed/success`.
- Final implementation HEAD: `b82f43af85f56c89cec51f2de972acd32f70a3e4`.
- Final exact PR-head CI #1413: workflow/job `33650126425` / `100314955596` — `completed/success` across unit tests, frontier benchmark, diff sanity, NeoForge build, JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.

## Merge evidence

- Branch: `feat/08-journeymap`.
- PR: #66 — `Stage 08.04: JourneyMap discovered-core markers`.
- Implementation merge SHA: `a9450a9e773d9e15c1f8e2cd96b6b783d4bb9ef6`.
- `main` was confirmed immediately after merge at that exact SHA.
- Independent post-merge `main` CI #1414: workflow/job `33650826014` / `100317085835` — `completed/success` across the full gate set.

## Cross-stage state

Stage 08.04 introduces no unresolved cross-stage contract. `ENSH-L1-ARS-ZERO-REAL-FIXTURE-001` remains intentionally open exactly as recorded in `plans/PENDING.md`; it is unrelated to JourneyMap.

## Acceptance

✅ JourneyMap helps revisit already-known Shroud cores without trivializing exploration, leaking undiscovered coordinates or becoming an authority for Shroud/progression state.
