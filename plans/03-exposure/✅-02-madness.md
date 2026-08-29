# Enshrouded Plan — Madness

**Milestone:** Level 1 required.

**Goal:** create escalating pre-death thresholds and authoritative fatal outcome when exposure reaches zero.

**Planned types:** `MadnessStage`, `MadnessService`, `ModDamageTypes`.

## Files

- [x] Create `src/main/java/com/gustavaopere/enshrouded/exposure/madness/*`.
- [x] Register `enshrouded:madness` damage type/data.
- [x] Create client-neutral event/snapshot fields for presentation stage.

## Dependencies

- [x] 01 player exposure.

## Implementation contract

- [x] Madness stages are threshold bands over remaining reserve and do not maintain a second competing timer.
- [x] Server gameplay penalties are restrained and configurable; visual/audio hallucination flags are sent as presentation state.
- [x] At zero reserve, server applies fatal Madness semantics robustly even against ordinary armor/resistance unless an explicit Enshrouded protection rule exists.
- [x] Death message identifies Madness/Shroud cause.
- [x] No random client hallucination can affect server targeting, inventory or world state.

## TDD / verification

- [x] Unit-test threshold transitions and zero boundary.
- [x] GameTest armored player still reaches the configured fatal outcome at exhaustion.
- [x] GameTest leaving Shroud before zero permits recovery and clears escalating server penalties according to policy.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build is GREEN; GameTests and dedicated-server smoke are GREEN because this task touches runtime/bootstrap state.
- [x] No unresolved cross-stage contract introduced by this task is hidden; no new `plans/PENDING.md` entry was required.
- [x] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same checkpoint.

## Verification record

- Branch: `feat/03-madness`
- Structural RED: `86127f1eaa47b8568cd4dce4c7c54175749500c5`, workflow `33262797262` — unit test failed because the Madness surface did not exist.
- Threshold/presentation RED: `daf4df4c59bc32e7c9b189bba93ab66d1974f2a7`, workflow `33263055822` — unit tests failed before threshold and presentation contracts existed.
- Runtime-surface RED: `f051b6b69b612c38d7e0223b2b56677529c1c2e5`, workflow `33263292918` — unit tests failed before `MadnessRuntime` and fatal resources existed.
- The first GameTest attempt on `bed3866917a52c526572c197f93d074e6623811f` was discarded as behavioral evidence because `GameTestHelper.makeMockPlayer` is not a `ServerPlayer`; the fixture was corrected to a server-backed `FakePlayer`.
- Valid behavioral RED: `e08b713c97d9f08c25a47681de22742e3525d6c6`, workflow `33263628901` — unit/build/JAR were GREEN and exactly the Madness GameTests failed because the no-op runtime did not kill at `FATAL` or restrain sprint at `CRITICAL`.
- Final implementation HEAD: `c1e4ef5e00d6d9c616cf69890556563ccb6296a8`
- Push verification: workflow `33264034907`, job `99130770574` — GREEN.
- PR: #23 — `03 — Madness`
- Final PR-head verification: workflow `33264262862`, job `99131401210` — GREEN.
- Exact final gates: unit tests, frontier benchmark baseline, diff sanity, NeoForge build, production JAR sanity, 30/30 GameTests, Shroud SavedData two-boot reload and dedicated-server save/reload smoke.
- Merge SHA: `1ac4ae537786603df87ddb252753c4402713776c`

Madness is derived exclusively from the authoritative exposure reserve as `STABLE -> UNEASY -> DISTORTED -> CRITICAL -> FATAL`; no second timer or persisted Madness progression exists. Presentation flags are server-authored and clientbound-only. `CRITICAL` applies a configurable, reversible sprint restraint with no persistent potion or attribute effect. Zero reserve applies `enshrouded:madness`, whose damage type bypasses ordinary armor/resistance/enchantment/cooldown mitigation; a direct server death fallback preserves deterministic fatal semantics if a generic damage hook absorbs the lethal hit. GameTests prove an armored Resistance V player still dies at exhaustion and that recovering before zero clears the server penalty.

Existing cross-stage Flame Ward/progression/provider contracts remain open under their existing owners; this task introduced no new hidden dependency and closes none prematurely.

**Acceptance:** Exhausting the Shroud timer causes a deterministic Madness death with escalating warnings before the endpoint.
