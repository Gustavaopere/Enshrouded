# Enshrouded Plan — Epic Fight, Claims, Teams and MineColonies

**Milestone:** Level 1 required.

**Goal:** integrate combat presentation/protection and optional shared progression with current multiplayer infrastructure.

**Planned types:** `EpicFightAdapter`, `FtbTeamsOwnerResolver`, `FtbChunksProtectionAdapter`, `MineColoniesProtectionAdapter`.

`FtbTeamsOwnerResolver` implements the Foundation `ProgressionOwnerResolver` contract; it does not introduce a second owner abstraction.

## Files

- Create integration packages under `src/main/java/com/gustavaopere/enshrouded/integration/{epicfight,ftb,minecolonies}/*`.

## Dependencies

- Foundation `ProgressionOwner` / `ProgressionOwnerResolver` contracts.
- 02 terrain safety.
- 05 persistent progression state/service.
- 06 boss encounter.

## Implementation contract

- Epic Fight adapter only supplies compatibility hooks/animation or combat event bridging needed for native Lich/corrupted mobs; core AI must work without it.
- FTB Teams may replace standalone player owner resolution with a stable team owner when configured, through the same Foundation `ProgressionOwnerResolver` interface, with explicit migration/no-silent-owner-change policy.
- Stage 03 exposure and Stage 05 progression consumers must not import FTB Teams classes; swapping the resolver must be sufficient.
- FTB Chunks claims veto terrain mutation through `ProtectedAreaService`.
- MineColonies colony/building areas veto terrain mutation through the same service.
- Claim/protection lookup is cached/indexed and never executed as an expensive global search per mutation candidate.
- If adapter API is unavailable/mismatched, mutations in uncertain protected contexts fail closed when practical and a diagnostic is recorded.

## TDD / verification

- [ ] Integration test claimed position denies mutation and adjacent unclaimed position follows normal authority.
- [ ] Integration test MineColonies protected position denies mutation.
- [ ] Owner resolver tests player vs team key stability and no duplicate ritual progression.
- [ ] Contract test FTB resolver is substitutable for the Foundation resolver without changing passage/exposure consumers.
- [ ] Epic Fight smoke fights native/provider boss without crash or double damage hooks.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The pack’s multiplayer claims and colonies are protected, team progression can be shared deliberately through the Foundation owner boundary, and Epic Fight does not break combat.
