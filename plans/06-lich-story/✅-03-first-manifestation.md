# Enshrouded Plan — First Manifestation Encounter

**Milestone:** Level 1 required.

**Goal:** build the Level 1 encounter loop, arena conditions and escalating first-body behavior around the selected provider.

**Planned types:** `FirstManifestationDefinition`, `ManifestationEncounterService`, `LichArenaRule`, `LichPhaseDirector`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/story/manifestation/*`.
- Create data definition `data/enshrouded/manifestations/first.json` if codec-driven implementation is selected by Foundation contracts.

## Dependencies

- 02 boss provider.
- 03 Exposure/Deadly behavior.
- 04 ecology combat services.

## Implementation contract

- Encounter trigger is explicit and server-side; random natural/external living entities are not mistaken for the story boss.
- Encounter creation resolves/receives one canonical `ProgressionOwner` and stores that immutable owner snapshot with the encounter ID. Defeat/reward paths reuse the stored owner and never re-resolve the initiating player mid-encounter.
- Encounter entity is marked with unique encounter ID and manifestation ID.
- Level 1 director supports provider-neutral phases keyed to health/time/events; external boss native behavior remains intact unless a narrowly defined modifier is applied.
- Stage 06 core tests remain standalone and do not require Ars Zero or any other optional boss provider to be installed.
- Team membership changes during an active encounter cannot transfer that encounter or its reward authority to another owner; membership changes affect only later encounters unless an explicit migration exists.
- Arena may intensify ordinary Shroud locally but cannot permanently create an unowned region or bypass core ownership rules.
- Defeat records manifestation 1 as defeated but explicitly describes the Lich as surviving through phylactery/next manifestation.

## TDD / verification

- [x] GameTest start one encounter and reject concurrent duplicate for same progression owner.
- [x] GameTest an unrelated unmarked native/test living entity death cannot complete story or issue a reward.
- [x] GameTest valid marked boss defeat transitions state to `DEFEATED` exactly once.
- [x] GameTest mutate the owner resolver/team membership after encounter start and prove defeat still targets the original stored owner exactly once; concrete reward issuance remains intentionally owned by 06.04.
- [x] GameTest arena cleanup removes temporary encounter effects while preserving pre-existing Shroud field.
- [x] Leave the explicit unrelated `ars_zero:lich` negative test to `08-integrations/✅-01-ars-zero.md`, where the optional provider is actually present.
- [x] GameTest canceled `LivingDeathEvent` cannot produce a narrative defeat or arena cleanup, protecting external-provider phase transitions that cancel death.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, SavedData two-boot reload and dedicated-server smoke are GREEN.
- [x] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` keeps reward issuance and Stage 08 membership semantics explicitly open.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same closeout checkpoint.

## Verified implementation record

- Implementation branch: `feat/06-first-manifestation`.
- Structural encounter RED: commit `f392f7849f34fea23bb9b275538a9e8402713b1d`, workflow `33331097136` — GameTest compilation failed before `ManifestationEncounterService` existed.
- Actor-manifestation identity RED: commit `294766ecaaf10a04e19ffc46a0f2d93a9c722887` — unit/build/JAR were GREEN and the GameTest failed because the accepted physical actor did not yet carry a manifestation marker.
- Actor identity GREEN checkpoint: HEAD `0dbc8658246e5b1c44b7fa7755b2322df9c29c3a` — full CI GREEN.
- Runtime-arena RED: commit `27aca162e2afb67d28a0c5aef3c5946b08fc6279` — unit/build/JAR were GREEN and the GameTest proved the real encounter was not yet composed into the authoritative Exposure query.
- Shared-fixture diagnosis separated pre-existing stronger persistent Shroud from the temporary encounter overlay; the runtime fixture now installs/restores deterministic `ShroudSavedData` state and additionally proves the arena does not persist Shroud.
- Arena/runtime GREEN checkpoint: HEAD `bfc96b82c6ad25de3404ee869626a59a4c34a7ab`, workflow `33336703751` — full gate GREEN.
- Canceled-death RED: commit `ad35290d2949b3f311b0aeaaa60bfd7ae4b84278` — unit/build/JAR were GREEN and the GameTest gate failed before cancellation-safe death routing existed.
- Final implementation HEAD: `908aeb3cc6a623596bc79b35133100d75a22e3ec`.
- Final push verification: workflow `33337899786` / run #1122 — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.
- Draft PR #45 was closed unmerged only because the connector's ready-for-review GraphQL mutation failed on GitHub schema field `Repository.fullDatabaseId`.
- Final replacement PR: #46 — `06.03 — First Manifestation Encounter`.
- Final exact PR-head verification: workflow `33338185074` / run #1124 — GREEN across the same complete gate on exact HEAD `908aeb3cc6a623596bc79b35133100d75a22e3ec`.
- Merge SHA: `5811d8f29189ed35e6608157ee5e39975bf8cbe9`.

`ManifestationEncounterService` now owns explicit first-manifestation start and defeat orchestration over the already-merged Story State and boss-provider boundaries. Start resolves one `ProgressionOwner` snapshot, creates one stable encounter UUID and persists that owner before the fight can become authoritative; a second open encounter for the same owner is rejected. The accepted physical actor is independently tagged with both encounter UUID and manifestation index, so entity/provider identity never becomes progression authority.

Valid defeat is accepted only from the exact marked physical actor recorded by the ACTIVE encounter. The defeat path reads the persisted `EncounterRecord.owner()` and never calls the owner resolver again; a GameTest mutates the resolver after start and proves progress still lands on the original owner. Unmarked deaths, mismatched actors and duplicate callbacks cannot advance the story. NeoForge death routing runs at `EventPriority.LOWEST`, excludes canceled delivery and additionally checks `event.isCanceled()`, so an external boss provider may cancel a death for a native phase transition without Enshrouded falsely recording manifestation defeat.

The Level-1 arena is an ephemeral `ShroudQuery` decorator composed into the existing Exposure sampling boundary. It may raise local effective Shroud to the configured arena intensity but does not create cores, regions or cells and never writes `ShroudSavedData`; cleanup removes only the encounter overlay and reveals the unchanged underlying field. `LichPhaseDirector` derives provider-neutral escalation from health/time/event pressure while leaving provider-native AI intact.

06.03 deliberately stops at `DEFEATED`: manifestation 1 is recorded as defeated while the recurring Lich survives through the continuing story/phylactery model, and `rewardIssued` remains false. The authentic skull, exactly-once reward issuance and concrete Flame binding remain 06.04 responsibilities. Accordingly, the Stage 06 encounter/defeat side of `ENSH-L1-OWNER-SNAPSHOT-001` is now executable, but that contract remains open for 06.04 reward issuance and Stage 08 FTB Teams membership-change semantics. `ENSH-L1-LICH-REWARD-001` remains open for 06.04.

**Acceptance:** Players can fight and defeat the first body of the recurring Lich through a provider-neutral, non-duplicable story encounter with immutable encounter ownership, while Stage 06 remains fully testable without optional boss mods.
