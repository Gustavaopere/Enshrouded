# ✅ Enshrouded — World Upgrade and Recovery

**Stage:** 09.03 — Hardening

**Disposition:** implemented, verified, merged and independently post-merge verified.

## Goal

Provide explicit, centralized and fail-closed world-upgrade behavior for every persisted Level-1 Enshrouded store, with exact legacy-value preservation, idempotent reload evidence and narrowly scoped operator recovery that does not bypass canonical gameplay authorities.

## Implemented scope

- Added centralized persistence migration ownership under `com.gustavaopere.enshrouded.datafix` through `EnshroudedDataFixer`, `SchemaMigration` and `PersistentSubsystem`.
- Versioned the current Level-1 persisted formats as schema v2 while preserving schema v1 as the oldest explicitly migratable format.
- Covered all six persisted Level-1 stores:
  - Shroud;
  - Shroud Discovery;
  - Exposure;
  - Entity Corruption;
  - Flame Progression;
  - Story.
- Added deterministic v1 → v2 migration registration for each store.
- Preserved exact identity/value contracts through legacy fixtures, including Shroud core/region identity, owner-scoped discovered-core knowledge, exposure reserve, entity corruption intensity, Flame levels/ritual identity and Story reward-issued state.
- Flame progression v1 → v2 adds `next_level_ready=false` only when the legacy field is absent and rejects malformed incompatible values.
- Missing `schema_version`, schema 0/pre-versioned data, malformed representative data and unknown future schemas fail closed with subsystem-specific diagnostics instead of silently resetting persistent gameplay state.
- `ShroudDiscoverySavedData` was promoted to a first-class migration subsystem after automated review identified it as an omitted persisted store.
- Added operator-only `/enshrouded recovery` commands for `diagnose`, `core_requeue`, `core_retire_missing` and `story_reconcile`.
- Recovery is intentionally narrow: it does not provide reset-all behavior, does not force-load chunks and does not replace canonical Shroud lifecycle or Story encounter transitions with ad-hoc mutations.
- Added migration/reload GameTest coverage proving supported migration is idempotent and does not duplicate Shroud cores, progression rituals or Story rewards.
- Removed obsolete schema-v0 fixtures from the migratable legacy corpus; schema 0 remains explicitly tested as unsupported/fail-closed rather than being reclassified as a valid old format.

## Persistence authority invariants

Stage 09.03 owns format evolution and operator recovery only. It does **not** create another gameplay state authority.

- Shroud state remains owned by the canonical Shroud SavedData/services.
- Shroud discovery remains owner-scoped and retains its existing discovery authority; migration only preserves that persisted knowledge.
- Exposure remains server-authoritative through its existing attachment/state contract.
- Entity corruption remains owned by the existing ecology runtime/state path.
- Flame progression/ritual identity remains owned by the Stage 05 progression services.
- Story encounter/reward semantics remain owned by the Stage 06 Story path.
- Recovery commands replay or reconcile canonical transitions and refuse ambiguous collisions instead of inventing replacement state.

Unsupported or malformed world-scale persistence is treated as a controlled load failure/diagnostic boundary. Silent default replacement is not an accepted recovery strategy.

## Review and regression corrections

Stabilization against the pre-existing regression suite exposed three historical contracts that the initial migration implementation had disturbed:

1. Entity Corruption future-schema diagnostics had to preserve the historical message contract.
2. `ShroudCoreBlockEntity.onLoad()` had to retain explicit queue ownership instead of obscuring that lifecycle contract behind a refactor.
3. Story schema 0 had to remain fail-closed rather than being treated as a supported migratable version.

Those contracts were restored without weakening their existing tests. The legacy GameTest fixture path was then corrected from invalid schema-v0 fixtures to real schema-v1 fixtures.

Automated review subsequently identified a P2 omission: `ShroudDiscoverySavedData` was a separate persisted/versioned store but was absent from the centralized migration contract. The final implementation adds `ShroudDiscoverySchema`, `PersistentSubsystem.SHROUD_DISCOVERY`, v1 → v2 migration registration, codec routing through `EnshroudedDataFixer`, exact owner/core legacy fixture coverage and future-schema fail-closed diagnostics. The review thread was resolved only after the corrected final HEAD passed the complete CI matrix.

## Verification record

- Base before implementation: `main@b65c9a9ee310a75b62d3390bb2e87ecc4f41707f`.
- Implementation branch: `feat/09-world-upgrade`.
- Final implementation HEAD: `aec0f6ad227642c096478c77343cf79efed7f88a`.
- PR: #74 — `Stage 09.03 — World upgrade and recovery`.
- Exact final PR-head workflow/job: `34003017774` / `101405148483` — `completed/success`.
- Final PR-head gates GREEN: wrapper provenance, unit tests, performance benchmark baselines, diff sanity, NeoForge build, canonical + external GameTest compilation, production-JAR integrity, standalone GameTests, SavedData two-boot reload, isolated real Ars Zero 2.0.2 profile and dedicated-server save/reload smoke.
- Implementation merge SHA: `0d2b07f4cc3bf60627acab60237f087a1e102b58`.
- Independent post-merge `main` workflow/job: `34003313686` / `101405934457` — `completed/success` across the same complete gate set.
- Post-merge verified implementation `main`: `0d2b07f4cc3bf60627acab60237f087a1e102b58`.
- Automated review P2 for omitted Shroud Discovery persistence: corrected and resolved.
- New cross-stage pending contracts: none.

## Merge gate

- [x] Every persisted Level-1 subsystem has an explicit current/oldest-migratable schema contract.
- [x] Every persisted Level-1 subsystem has a schema-v1 legacy fixture and executable migration coverage.
- [x] Supported v1 → v2 migration preserves identity/progression/reward values exactly where required.
- [x] Schema 0/pre-versioned, missing-schema and unknown-future-schema inputs fail closed rather than resetting state.
- [x] Representative malformed persistence produces controlled subsystem diagnostics.
- [x] Migration followed by current-schema save/reload is idempotent and does not duplicate core, ritual or Story reward state.
- [x] Recovery tooling is permission-gated, bounded and refuses force-loading/global repair scans.
- [x] Historical Entity Corruption, Shroud-core lifecycle and Story fail-closed regression contracts remain intact.
- [x] `./gradlew test` is GREEN.
- [x] Performance baseline gate is GREEN.
- [x] NeoForge build is GREEN.
- [x] Canonical GameTests are GREEN.
- [x] SavedData two-boot reload matrix is GREEN.
- [x] Real Ars Zero 2.0.2 isolated profile is GREEN.
- [x] Dedicated-server save/reload smoke is GREEN.
- [x] Production JAR integrity gate is GREEN.
- [x] PR #74 merged into `main`.
- [x] Independent post-merge `main` CI is GREEN.
- [x] No unresolved cross-stage contract was introduced.

**Acceptance:** Stage 09.03 is complete. All six persisted Level-1 stores participate in an explicit centralized migration contract, supported legacy data is migrated without identity/progression/reward duplication, incompatible persistence fails closed with diagnostics, recovery remains narrowly scoped, and the implementation has both exact-head and independent post-merge GREEN evidence.