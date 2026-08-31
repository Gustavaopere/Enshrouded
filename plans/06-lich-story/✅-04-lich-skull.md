# Enshrouded Plan — Lich Skull Reward and Concrete Flame Binding

**Milestone:** Level 1 required.

**Status:** ✅ verified, merged and closed.

**Goal:** emit one authentic Level 1 skull trophy for a valid first-manifestation defeat and bind that item to the generic Flame ritual framework from Stage 05.

**Implemented types:** `LichSkullIdentity`, `LichSkullItem`, `LichRewardService`, `RewardReceipt`, `LevelOneLichSkullRitual`.

## Files

- `src/main/java/com/gustavaopere/enshrouded/story/reward/*` owns reward authority and authentic skull identity.
- `src/main/java/com/gustavaopere/enshrouded/story/ritual/LevelOneLichSkullRitual.java` supplies the concrete Level-1 binding without moving reward authority into the altar.
- `enshrouded:lich_skull_manifestation_1` is registered with item model and EN/pt-BR translations.
- Concrete ritual ID `enshrouded:lich_manifestation_1` is registered into the Stage 05 `FlameRitualRegistry` through runtime bootstrap.

## Dependencies

- ✅ 06.03 First Manifestation.
- ✅ 05.04 generic Level 1 ritual framework.
- ✅ 05.02 physical Flame Altar adapter.

There is no reverse dependency from Stage 05 to this task. Stage 06 owns the authentic skull type, authenticity/component validation and the concrete binding that closes the Level 1 story-to-Flame loop.

## Implemented contract

- `LichRewardService` is the sole first-manifestation reward authority.
- Reward preparation checks encounter ID, manifestation index, `DEFEATED` state and reward-issued flag through the persisted Story State transaction boundary.
- The immutable `ProgressionOwner` stored when the encounter started remains the reward authority; reward issuance never re-runs the owner resolver.
- Exactly one Enshrouded skull can be committed per valid encounter. `rewardIssued=true` is persisted only after the physical item entity is accepted by `ServerLevel.addFreshEntity`; failed insertion leaves the reward pending and retryable rather than losing progression.
- External provider loot is neither suppressed nor replaced by Enshrouded reward handling.
- The skull uses persistent stack data under `DataComponents.CUSTOM_DATA`, encoded by `LichSkullIdentity`; display name, tooltip and lore are presentation only and never authenticity authority.
- `LichSkullItem` uses the vanilla Wither Skeleton skull standing/wall item render path while refusing placement, preventing block placement from discarding encounter identity and avoiding remapping the vanilla skull block item.
- Unstamped Enshrouded skull stacks, vanilla Wither Skeleton skulls, malformed data, wrong format versions and wrong manifestation indices fail closed.
- `LevelOneLichSkullRitual` accepts only the authentic first-manifestation skull and delegates idempotence, consumption and checkpoint mutation to the existing Stage 05 ritual engine.
- Successful offering consumes exactly one authentic skull, records `enshrouded:lich_manifestation_1` once and sets `nextLevelReady=true` while Flame Level remains 1 and Passage Level remains 1.
- Replayed death callbacks and real server reload cannot issue another skull after successful delivery.

## TDD / verification

- [x] Unit-test reward receipt idempotence.
- [x] Unit-test authentic identity codec and fail-closed malformed/wrong-format data.
- [x] NeoForge GameTest validates authentic registered item data and rejects unstamped/lookalike skulls.
- [x] Unit-test concrete ritual binding delegates to Stage 05 engine and cannot advance twice.
- [x] GameTest valid death emits one authentic skull and replayed callback emits zero additional skulls.
- [x] Two-boot SavedData GameTest proves a persisted issued reward cannot be replayed after restart.
- [x] End-to-end GameTest proves defeat -> obtain authentic skull -> altar offering -> Level 1 checkpoint.
- [x] End-to-end test proves Passage Level remains 1 after Level 1 completion.
- [x] Delivery-failure regression proves reward state is not committed when physical insertion fails and a later retry can succeed exactly once.

## TDD evidence

- Initial structural RED: commit `89161fbcf714362d4e6d7ccf19407f1ddd428a71`, workflow `33341180113` — compilation failed because the planned skull/reward/ritual types did not yet exist.
- Runtime reward RED: workflow `33341598157` / job `99338139040` on pre-runtime HEAD `4c3c21d4cf96dfdc98200aa8148143c32de26866` — unit/build/JAR were GREEN; the valid-death and defeat-to-altar GameTests failed because no physical skull was emitted.
- Runtime integration checkpoint: workflow `33341835760` / job `99338755781` on `ce2b8a578e1b5b53e4daede80e8f7771f32e9056` reduced the suite to one historical 06.03 expectation that still asserted reward issuance must remain false; the test was updated to the 06.04 contract while preserving 06.03 defeat/cleanup guarantees.
- Render-path correction: commit `e3af9171564747e348c3121ec767de7bb4edbc13` aligned the custom trophy with the vanilla skull renderer while blocking placement.
- Pre-review full GREEN: workflow `33341975198` passed the full committed pipeline on `e3af9171564747e348c3121ec767de7bb4edbc13`.
- Review/TDD P1: pre-merge review identified that reward state could be persisted before `addFreshEntity` succeeded. A dedicated RED established the missing delivery-aware contract; the implementation was changed so successful physical insertion is required before `rewardIssued=true` is committed, and failed delivery remains retryable.
- The existing purification reload fixture then exposed a timing race: its `DESTROYED` sentinel could finish normal runtime regression after that individual test succeeded but before server shutdown. The fix is harness-only: the two-boot test world uses a world-local SERVER regression budget of 1 while production defaults and the ordinary GameTest gate remain unchanged.
- Final implementation HEAD: `b7a90c52c3c11b17958d83adfb5554996e29140b`.
- Final exact PR-head verification: workflow `33348592278`, job `99357385794` — GREEN across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Automated P1 review thread was answered with the delivery-aware fix and resolved before merge.
- PR: #48 — `Stage 06.04: Lich Skull and Flame binding`.
- Merge SHA: `368f30c710246580e47e262462118f8b9e4a03ea`.

## Cross-stage closure

- `ENSH-L1-LICH-REWARD-001` is closed: provider-neutral defeat, authentic skull identity, exactly-once physical delivery, replay/reload protection and concrete Flame binding now have executable evidence.
- The Stage 06 side of `ENSH-L1-OWNER-SNAPSHOT-001` is complete through encounter start, defeat and reward issuance using the same immutable stored owner. The contract remains open only for Stage 08 FTB Teams membership-change behavior.
- No Stage 08 integration semantics were implemented or assumed here.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN.
- [x] NeoForge build, GameTests, two-boot reload and dedicated-server smoke are GREEN on the final PR HEAD.
- [x] No unresolved 06.04 cross-stage contract is hidden; `plans/PENDING.md` records the reward closure and the remaining Stage 08 ownership work.
- [x] Implementation merged to `main` in PR #48.
- [x] This task file is renamed with the `✅-` prefix in the documentation closeout.

**Acceptance:** satisfied. The first valid Lich manifestation defeat produces one authentic, persistent-identity trophy through a delivery-aware exactly-once reward path, and Stage 06 binds that trophy to the already-complete Stage 05 Flame ritual engine. The offering closes the Level-1 story-to-Flame loop by recording readiness for the next level without granting Flame or Passage Level 2.
