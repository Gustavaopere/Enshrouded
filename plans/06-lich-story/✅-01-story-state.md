# Enshrouded Plan — Story State

**Milestone:** Level 1 required.

**Status:** ✅ Verified and merged.

**Goal:** persist Lich manifestation progression, encounter IDs and narrative checkpoints separately from physical boss entities.

**Implemented types:** `StorySavedData`, `StorySchema`, `LichStoryState`, `ManifestationRecord`, `EncounterRecord`, `EncounterOutcome`, `StoryCodec`, `StoryStateRuntime`, `UnsupportedStorySchemaException`.

## Files

- [x] Created `src/main/java/com/gustavaopere/enshrouded/story/state/*`.
- [x] Registered `StoryStateRuntime` from the common server bootstrap.
- [x] Extended the existing two-boot GameTest harness to verify one server-global `enshrouded_story.dat`.

## Dependencies

- [x] Reused the Foundation `ProgressionOwner` identity contract directly.
- [x] Story persistence does not import Stage 05 Flame-state implementation classes.

The caller resolves/obtains the canonical `ProgressionOwner` and supplies that stable key when story/encounter state is created.

## Implementation contract

- [x] `StorySavedData` has explicit schema versioning from its first persisted form.
- [x] Unknown/pre-versioned/future schemas fail closed rather than silently resetting manifestation, encounter or reward state.
- [x] Story state records current/defeated manifestation indices, stable encounter UUIDs, immutable Foundation `ProgressionOwner` snapshots and reward-issued state.
- [x] Stable owner keys are persisted directly through `ProgressionOwner.stableKey()` / `parse()` with PLAYER/TEAM/WORLD round-trip coverage.
- [x] Physical entity UUID exists only while an encounter is `ACTIVE` and is never the canonical identity of the recurring Lich.
- [x] Encounter transitions are explicit and one-way: `AVAILABLE -> ACTIVE -> DEFEATED|ABORTED`; only `DEFEATED` is reward eligible.
- [x] Duplicate encounter IDs, concurrent open encounters for one owner, duplicate defeat and duplicate reward issuance fail closed/idempotently.
- [x] On server start, persisted `ACTIVE` encounters whose tracked living actor is absent/dead reconcile to `ABORTED`, never `DEFEATED`, without creating reward eligibility.
- [x] Story state is server-global through Overworld `DataStorage`; it is not split by dimension.
- [x] Level 1 defines manifestation index `1`; the schema keeps stable integer indices for later manifestations without prematurely advancing the Level-1 state.

## TDD / verification

- [x] Unit tests cover schema round-trip and unknown-future/pre-versioned fail-closed behavior.
- [x] Unit tests cover legal transitions and duplicate defeat/reward rejection.
- [x] PLAYER/TEAM/WORLD `ProgressionOwner` keys round-trip without Stage 05 implementation imports.
- [x] ACTIVE and DEFEATED encounter state round-trip through deterministic NBT.
- [x] GameTest verifies Story storage is server-global across Overworld/Nether access.
- [x] Real two-boot GameTest proves persisted `ACTIVE` + missing actor becomes `ABORTED`, preserving encounter ID and owner while issuing no reward.
- [x] Existing Red Sludge relocation GameTest was isolated from shared `ShroudSavedData` state after PR-head parallel execution exposed a pre-existing fixture assumption; production behavior was unchanged.

## Merge evidence

- Implementation branch: `feat/06-story-state`.
- Initial structural RED: commit `d1ccd2bf8cb0bfb8ead2105d2c9c5d49a963c842`, workflow `33304524909` / run #1022 — `compileTestJava` failed only because Story State domain/codec types did not yet exist.
- Domain/codec GREEN: HEAD `cc05f5a034ba106bb731bb7f6adb4d12a60bd4b4`, workflow `33304707537` / run #1029 — all gates GREEN.
- SavedData compile RED: commit `98ecdbc2703db6a2f912ca274406f384e429605c`, workflow `33304953122` / run #1031 — production unit/build/JAR were GREEN; `compileGameTestJava` failed because `StorySavedData` had not yet been implemented.
- Behavioral reboot RED: HEAD `85bdace450bcfec9fe63db7583cc509c8df27db6`, workflow `33305045499` / run #1032 — first boot passed and persisted Story State, second boot failed because the orphaned encounter remained `ACTIVE`.
- Recovery GREEN: HEAD `4e110095810035f1440a681dab586cbd069cde6e`, workflow `33305215278` / run #1034 — startup reconciliation and two-boot recovery GREEN.
- PR-head regression run #1035 exposed only the unrelated/shared Red Sludge fixture assumption; Story tests had not started and Story production has no Shroud mutation path.
- Final implementation HEAD: `057b074fd1476778d748cfe53b943ed25fbf8a1b`.
- Final PR: #41 — `06.01 — Persistent Lich Story State`.
- Final push verification: workflow `33305648279` / run #1036 — GREEN.
- Final exact PR-head verification: workflow `33305649672` / run #1037 — GREEN.
- Exact final gates: wrapper provenance, unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR verification, GameTest server, Story/Flame/Shroud two-boot reload and dedicated-server save/reload smoke — GREEN.
- Merge SHA: `77552a3d7f089a47908c109f5f8c19aff8a0f97d`.

## Cross-stage contracts

`ENSH-L1-OWNER-SNAPSHOT-001` remains open: 06.01 now persists the immutable owner field correctly, but the encounter-start/defeat/reward runtime proof is owned by 06.03 and Stage 08 membership-change behavior still remains. `ENSH-L1-LICH-REWARD-001` remains owned by 06.04. No new cross-stage blocker was introduced.

**Acceptance:** ✅ The recurring-Lich narrative has version-aware durable server-global state independent of any one physical boss body, survives restart safely, preserves stable owner/encounter identity and does not couple story persistence to Flame-state implementation classes.
