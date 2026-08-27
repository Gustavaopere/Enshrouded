# Cross-Stage Pending Contracts

This file records blockers that must not be silently papered over by later branches.

## External verification blockers

- `ENSH-CI-ACTIONS-001`: Foundation acceptance is blocked by GitHub Actions jobs terminating before runner initialization. The current merge candidate is Foundation HEAD `37ebf87a2c07ba8cf629aee87c755817857c4102`, synchronized with `main` after documentation PRs #3–#5. Its workflow `33125937019` failed twice before any checkout/Gradle/JUnit/GameTest/server execution. The latest controlled rerun is attempt 2, job `98705355714`, which completed in roughly three seconds with `steps=[]`, `runner_id=0`, `runner_name=""` and no runner group allocation. A private cross-repository control on `Gustavaopere/Volcanoes` showed the same private-hosted-runner symptom during the same period. GitHub Status is globally operational, so no active global incident is asserted as the current cause; the APIs available here also do not expose account Actions quota/billing/policy state, therefore no account-specific cause is asserted. The conversation-local fallback cannot perform a real NeoForge build because `services.gradle.org` remains unresolvable and no suitable Gradle/NeoForge cache is present. Do not mark Foundation complete, rename tasks with `✅-`, merge PR #2, or create Stage 01 branches until a real executor runs wrapper integrity, unit tests, diff sanity, NeoForge build/JAR sanity, GameTests and the two-boot dedicated-server save/reload harness GREEN on the exact final HEAD.

## Foundation-owned progression boundary — Foundation side implemented, cross-stage closure still open

- `ENSH-L1-FLAME-PASSAGE-001`: Foundation now owns and implements `ProgressionOwnerResolver` and `FlamePassageQuery` per `DECISIONS.md` decision 31. The standalone resolver maps player UUID -> `ProgressionOwner.player(UUID)` and the standalone passage fallback returns level `1` for any valid owner. The pure-Java boundary completed an isolated Java 21 RED -> GREEN cycle using the exact committed test source before implementation, then permanent `ProgressionBoundaryTest` passed after the minimal interfaces were added. Keep this contract open until Stage 03 consumes only these interfaces, Stage 05 proves its persistence-backed implementation through the same boundary, and the final committed Gradle/NeoForge suite validates the Foundation HEAD. Stage 08 may later substitute an FTB Teams-aware owner resolver; do not create a Stage 05-owned stub or let Stage 03 depend on Stage 05 implementation classes.

## Foundation-owned ward boundary — Foundation side implemented, cross-stage closure still open

- `ENSH-L1-FLAME-WARD-001`: Foundation now owns and implements `FlameWardQuery` per `DECISIONS.md` decision 33. The no-ward fallback returns `false`, and the boundary completed an isolated Java 21 RED -> GREEN cycle before permanent `FlameWardBoundaryTest`/`PublicApiShapeTest` coverage was installed. `ShroudSample` must retain canonical logical intensity/severity/source and use `sanctuarySuppressed` as the effective overlay; exposure treats suppression as safe and `MutationAuthority` vetoes terrain using the same ward query. Keep this contract open until Stage 01 proves logical samples are preserved while setting suppression, Stage 02 proves the ward veto flows through `MutationAuthority`, Stage 03 proves suppressed samples are effectively safe, Stage 05 proves indexed `FlameWardService` implements the same interface without logical-field rewriting, and the final committed Gradle/NeoForge suite validates the Foundation HEAD.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: FTB Chunks/MineColonies adapters must prove fail-closed mutation protection when installed; standalone safe-tag behavior must remain valid without them.

## Closure rule

A pending contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
