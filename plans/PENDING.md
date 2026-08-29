# Cross-Stage Pending Contracts

This file records contracts that must not be silently papered over by later branches.

The former external Foundation blocker `ENSH-CI-ACTIONS-001` is closed: hosted runners resumed, workflow `33165771852` / job `98830694040` executed the exact final Foundation implementation HEAD and completed all committed gates GREEN before PR #2 merged.

`ENSH-L1-CORE-TO-TERRAIN-001` is closed by Stage 01 + Stage 02 executable evidence. PR #16 (`feat/02-materialization-rules`) merged as `3a0ea59cb548d373b4b181f2c4bd2e77bd9ff925` after exact-head workflow `33227119616` / job `99032887579` completed GREEN. Materialization re-samples canonical `ShroudQuery` immediately before mutation, stale queued work is rejected, and no independent terrain-spread state exists.

`ENSH-L1-CORE-TO-EXPOSURE-001` is closed by Stage 01 + Stage 03 executable evidence. PR #22 (`feat/03-player-exposure-state`) merged as `73396291680aebe9d45e7f6d6347579d04010dd1` after exact PR-head workflow `33246650603` / job `99085167187` completed GREEN. Player exposure samples the same canonical `DefaultShroudQuery`/`ShroudSample` field used by terrain/client state, preserves Sanctuary suppression as an effective overlay and owns no independent Shroud severity state.

## Foundation-owned progression boundary — Foundation and Stage 03 consumer sides complete; provider closure open

- `ENSH-L1-FLAME-PASSAGE-001`: Foundation owns `ProgressionOwnerResolver` and `FlamePassageQuery`. Stage 03 PR #24 (`feat/03-deadly-shroud`) proves `FlameGatedDeadlyExposurePolicy` consumes only those interfaces: standalone Level 1 resolves player ownership through the Foundation resolver, uses the Foundation level-1 passage fallback, fails closed on uncertain resolution and permits an injected passage level 2 without changing DEADLY cell/schema data. Exact PR-head workflow `33266387083` / job `99137076697` completed GREEN before merge `036664ea35747ae3bb16f556f1cd1dc0b1d89669`. Keep open until Stage 05 proves its persistence-backed implementation through the same boundary and Stage 08 optional FTB Teams substitution preserves future-operation-only ownership semantics. No Stage 05-owned stub is allowed.

## Foundation-owned ward boundary — Foundation, Stage 02 and Stage 03 exposure sides complete; provider closure open

- `ENSH-L1-FLAME-WARD-001`: Foundation owns `FlameWardQuery`. `ShroudSample` retains canonical logical intensity/severity/source and Sanctuary is an effective suppression overlay. Stage 02 PR #15 proved that `DefaultMutationAuthority` vetoes warded `CORRUPTION`/`CORE_PLACEMENT` while not rejecting safe `PURIFICATION` merely because the ward is active; `RITUAL_STRUCTURE` remains subject to normal authorization. Stage 03 PR #22 proves suppressed `SHROUD`/`DEADLY` samples recover exposure safely without erasing the latent logical sample. Keep open until Stage 05 proves the real Sanctuary-provider behavior through the same boundary.

## Progression owner snapshot semantics — cross-stage closure open

- `ENSH-L1-OWNER-SNAPSHOT-001`: transactional progression resolves `ProgressionOwner` exactly once at operation start and retains that stable key. Keep open until Stage 05 ritual execution, Stage 06 active encounter/reward ownership and Stage 08 FTB Teams membership-change behavior are proven. No in-flight transaction may silently re-resolve or migrate ownership.

## Initially open contracts

- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce exactly one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: Stage 02 PR #15 proved fail-closed tri-state authority behavior: `PROTECTED` and `INDETERMINATE` veto by default, absent optional adapters add no uncertainty, and query failure becomes bounded-diagnostic `INDETERMINATE`. Keep open only until Stage 08 real FTB Chunks/MineColonies adapters prove their side of the same `ProtectedAreaService` boundary.

## Closure rule

A pending contract is removed from the open list only after the merge that provides executable verification for both sides of the boundary. Closed contracts remain recorded above for provenance. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
