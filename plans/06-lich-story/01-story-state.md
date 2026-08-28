# Enshrouded Plan — Story State

**Milestone:** Level 1 required.

**Goal:** persist Lich manifestation progression, encounter IDs and narrative checkpoints separately from physical boss entities.

**Planned types:** `StorySavedData`, `StorySchema`, `LichStoryState`, `ManifestationRecord`, `EncounterRecord`, `EncounterOutcome`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/state/*`.

## Dependencies

- 00 Foundation story contracts, especially stable `ProgressionOwner` identity.

Stage 06 must not import Stage 05 Flame-state implementation classes merely to identify an encounter owner. The caller resolves/obtains the canonical `ProgressionOwner` and supplies that stable key when story/encounter state is created.

## Implementation contract

- `StorySavedData` carries an explicit schema version/stable codec evolution policy from its first persisted form; Stage 09 migration tests build on that contract rather than introducing versioning after encounter saves exist.
- Unknown future schema fails closed with a clear diagnostic and must not silently reset manifestation victory, encounter identity or reward-issued state.
- Story state records current/defeated manifestation indices, unique encounter IDs, the Foundation `ProgressionOwner` key and reward-issued flag.
- `StorySavedData` persists the stable owner key directly; it does not duplicate Flame progression state or require a concrete Stage 05 state type for ownership.
- Physical entity UUID is a transient encounter field and never the canonical identity of the immortal Lich.
- Encounter transitions are explicit: `AVAILABLE -> ACTIVE -> DEFEATED/ABORTED`; only `DEFEATED` is reward eligible.
- Server restart during an active encounter reconciles missing/dead entity safely and cannot duplicate reward.
- Level 1 defines only manifestation index `1`; schema permits additional stable IDs later.

## TDD / verification

- [ ] Unit-test schema version round-trip plus unknown-future-schema diagnostic/fail-closed behavior.
- [ ] Unit-test legal state transitions and duplicate defeat/reward rejection.
- [ ] Round-trip player/team/world `ProgressionOwner` keys without importing Stage 05 implementation classes.
- [ ] Round-trip active and defeated encounter persistence.
- [ ] GameTest simulated server reload with active encounter reconciles according to documented policy.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The immortal-antagonist narrative has version-aware durable state independent of any one boss body and does not couple story persistence to Flame-state implementation classes.
