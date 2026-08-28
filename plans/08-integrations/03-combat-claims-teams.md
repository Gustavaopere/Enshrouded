# Enshrouded Plan — Epic Fight, Claims, Teams and MineColonies

**Milestone:** Level 1 required.

**Goal:** integrate combat presentation/protection and optional shared progression with current multiplayer infrastructure.

**Planned types:** `EpicFightAdapter`, `FtbTeamsOwnerResolver`, `FtbChunksProtectionAdapter`, `MineColoniesProtectionAdapter`.

`FtbTeamsOwnerResolver` implements the Foundation `ProgressionOwnerResolver` contract; it does not introduce a second owner abstraction.

## Files

- Create integration packages under `src/main/java/com/gustavaopere/enshrouded/integration/{epicfight,ftb,minecolonies}/*`.

## Dependencies

- Foundation `ProgressionOwner` / `ProgressionOwnerResolver` contracts.
- 02 terrain safety and its `ProtectionDecision` / `ProtectedAreaService` semantics.
- 05 persistent progression state/service.
- 06 boss encounter.

## Implementation contract

- Epic Fight adapter only supplies compatibility hooks/animation or combat event bridging needed for native Lich/corrupted mobs; core AI must work without it.
- FTB Teams may replace standalone player owner resolution with a stable team owner when configured, through the same Foundation `ProgressionOwnerResolver` interface.
- Resolver results are snapshots for the operation that requested them: rituals, encounters and reward transactions resolve once and keep that stable owner until completion. Joining/leaving/changing FTB teams affects only future operations and cannot redirect an in-flight ritual/encounter/reward.
- Enabling team ownership over an existing player-owned world does not silently rewrite or merge progression. Any player↔team migration must be an explicit migration/admin flow with deterministic conflict/idempotence rules.
- Stage 03 exposure and Stage 05 progression consumers must not import FTB Teams classes; swapping the resolver must be sufficient.
- FTB Chunks and MineColonies adapters plug into Stage 02 `ProtectedAreaService`; they never mutate terrain or bypass `DefaultMutationAuthority` themselves.
- Each protection adapter must distinguish a definite unprotected result from query uncertainty: protected -> `PROTECTED`, definite no-claim/no-colony -> `UNPROTECTED`, present/enabled adapter unable to answer -> `INDETERMINATE`.
- An absent target mod registers no adapter and therefore contributes no uncertainty. A present target mod with API mismatch/query failure must not be treated as unprotected; the aggregate authority fails closed on `INDETERMINATE` unless an explicit expert override is enabled.
- Claim/protection lookup is cached/indexed and never executed as an expensive global search per mutation candidate.
- Diagnostics for adapter mismatch/query failure are rate-limited/bounded and identify which adapter produced uncertainty.

## TDD / verification

- [ ] Integration test claimed position returns `PROTECTED` and adjacent definitely-unclaimed position returns `UNPROTECTED`.
- [ ] Integration test MineColonies protected position returns `PROTECTED`.
- [ ] Integration test enabled adapter query/API failure returns `INDETERMINATE`; `DefaultMutationAuthority` vetoes it by default and logs a bounded diagnostic.
- [ ] Integration test target mod absent registers no adapter and does not globally block ordinary standalone-safe terrain.
- [ ] Owner resolver tests player vs team key stability and no duplicate ritual progression.
- [ ] Contract test FTB resolver is substitutable for the Foundation resolver without changing passage/exposure consumers.
- [ ] Transaction test changes team membership/resolver output after ritual or encounter start and proves the in-flight operation remains bound to the original owner.
- [ ] Migration test proves existing player-owned progression is never silently transferred/merged when team mode becomes available.
- [ ] Epic Fight smoke fights native/provider boss without crash or double damage hooks.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The pack’s multiplayer claims and colonies are protected with explicit fail-closed uncertainty semantics, team progression can be shared deliberately through the Foundation owner boundary without silent ownership transfer, and Epic Fight does not break combat.
