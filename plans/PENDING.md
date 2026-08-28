# Cross-Stage Pending Contracts

This file records blockers that must not be silently papered over by later branches.

## Foundation-owned progression boundary — Foundation side implemented, cross-stage closure still open

- `ENSH-L1-FLAME-PASSAGE-001`: Foundation owns and implements `ProgressionOwnerResolver` and `FlamePassageQuery` per `DECISIONS.md` decision 31. The standalone resolver maps player UUID -> `ProgressionOwner.player(UUID)` and the standalone passage fallback returns level `1` for any valid owner. Keep this contract open until Stage 03 consumes only these interfaces, Stage 05 proves its persistence-backed implementation through the same boundary, and Stage 08 optionally proves FTB Teams substitution. Do not create a Stage 05-owned stub or let Stage 03 depend on Stage 05 implementation classes.

## Foundation-owned ward boundary — Foundation side implemented, cross-stage closure still open

- `ENSH-L1-FLAME-WARD-001`: Foundation owns and implements `FlameWardQuery` per `DECISIONS.md` decisions 33 and 43. `ShroudSample` retains canonical logical intensity/severity/source and uses `sanctuarySuppressed` as the effective overlay. `MutationAuthority` interprets the ward by `MutationKind`: warded `CORRUPTION`/`CORE_PLACEMENT` are vetoed, while `PURIFICATION` and `RITUAL_STRUCTURE` are not rejected merely because of the ward and remain subject to all other safety rules. Keep this open until Stage 01 proves logical sample preservation, Stage 02 proves the mutation-kind-aware authority matrix, Stage 03 proves suppressed samples are effectively safe, and Stage 05 proves indexed altar-backed `FlameWardService` implements the same interface.

## Progression owner snapshot semantics — cross-stage closure open

- `ENSH-L1-OWNER-SNAPSHOT-001`: `DECISIONS.md` decision 40 requires transactional progression operations to resolve `ProgressionOwner` exactly once at operation start and retain that stable key through completion. Keep this open until Stage 05 proves ritual execution cannot be redirected by resolver/team changes after invocation begins, Stage 06 proves active Lich encounters/rewards remain bound to the stored encounter owner, and Stage 08 proves FTB Teams membership changes affect only future operations while player↔team migration is explicit and idempotent.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: Stage 02 `ProtectedAreaService` must use the tri-state `ProtectionDecision` contract from `DECISIONS.md` decision 41. FTB Chunks/MineColonies adapters must prove claimed/protected -> `PROTECTED`, definite no-claim/no-colony -> `UNPROTECTED`, and installed/enabled adapter uncertainty -> `INDETERMINATE`; `DefaultMutationAuthority` vetoes both `PROTECTED` and `INDETERMINATE` by default. An absent optional mod registers no adapter and must not block standalone safe-tag behavior.

## Resolved external blockers

`ENSH-CI-ACTIONS-001` is resolved. After the earlier pre-runner failures, GitHub-hosted runner allocation resumed. Foundation implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` completed PR workflow `33165771852`, job `98830694040`, GREEN through wrapper integrity, unit tests, diff sanity, NeoForge build/JAR sanity, GameTests and the dedicated-server two-boot save/reload harness. This resolved item is retained here only as historical context and is not a blocker.

## Closure rule

A pending cross-stage contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task records that partial completion rather than claiming the later consumer is already proven.
