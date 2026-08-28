# Cross-Stage Pending Contracts

This file records contracts that must not be silently papered over by later branches.

The former external Foundation blocker `ENSH-CI-ACTIONS-001` is closed: hosted runners resumed, workflow `33165771852` / job `98830694040` executed the exact final Foundation implementation HEAD and completed all committed gates GREEN before PR #2 merged.

## Foundation-owned progression boundary — Foundation side complete, cross-stage closure open

- `ENSH-L1-FLAME-PASSAGE-001`: Foundation owns `ProgressionOwnerResolver` and `FlamePassageQuery`. Keep open until Stage 03 consumes only these interfaces, Stage 05 proves its persistence-backed implementation through the same boundary, and Stage 08 optional FTB Teams substitution preserves future-operation-only ownership semantics. No Stage 05-owned stub is allowed.

## Foundation-owned ward boundary — Foundation side complete, cross-stage closure open

- `ENSH-L1-FLAME-WARD-001`: Foundation owns `FlameWardQuery`. `ShroudSample` retains canonical logical intensity/severity/source and Sanctuary is an effective suppression overlay. `MutationAuthority` must be mutation-kind-aware: warded `CORRUPTION`/`CORE_PLACEMENT` are vetoed, while safe `PURIFICATION` is not rejected merely because of a ward; `RITUAL_STRUCTURE` remains subject to normal authorization. Keep open until Stages 01/02/03/05 prove both sides with executable tests.

## Progression owner snapshot semantics — cross-stage closure open

- `ENSH-L1-OWNER-SNAPSHOT-001`: transactional progression resolves `ProgressionOwner` exactly once at operation start and retains that stable key. Keep open until Stage 05 ritual execution, Stage 06 active encounter/reward ownership and Stage 08 FTB Teams membership-change behavior are proven. No in-flight transaction may silently re-resolve or migrate ownership.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce exactly one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: Stage 02 `ProtectedAreaService` must use tri-state protection semantics. Installed/enabled adapter uncertainty is `INDETERMINATE` and is vetoed by default; absent optional mods register no adapter. Keep open until Stage 02 authority behavior and Stage 08 adapters are both proven.

## Closure rule

A pending contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
