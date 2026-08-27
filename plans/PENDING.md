# Cross-Stage Pending Contracts

This file records blockers that must not be silently papered over by later branches.

## External verification blockers

- `ENSH-CI-ACTIONS-001`: Foundation acceptance is blocked by GitHub Actions jobs terminating before runner initialization. Enshrouded push and pull-request runs create a `verify` job but expose `steps=null`; reruns reproduce the same result. A private cross-repository control on `Gustavaopere/Volcanoes` (`33099719939` / job `98615923587`) showed the same `failure` + `steps=null` pattern during the same period, so the current blocker is not specific to Enshrouded's workflow or repository. The repository APIs available here do not expose account billing/quota state, so no specific billing cause is asserted. The conversation-local fallback also cannot perform a real NeoForge build because `services.gradle.org` is not resolvable and no suitable Gradle/NeoForge cache is present. This blocker is external to Gradle/test execution: no final-HEAD command has run on a real executor. Do not mark Foundation complete, rename tasks with `✅-`, or create Stage 01 branches until a real runner executes wrapper integrity, unit tests, diff sanity, NeoForge build/JAR sanity, GameTests and the two-boot dedicated-server save/reload harness GREEN.

## Foundation-owned progression boundary — Foundation side implemented, cross-stage closure still open

- `ENSH-L1-FLAME-PASSAGE-001`: Foundation now owns and implements `ProgressionOwnerResolver` and `FlamePassageQuery` per `DECISIONS.md` decision 31. The standalone resolver maps player UUID -> `ProgressionOwner.player(UUID)` and the standalone passage fallback returns level `1` for any valid owner. The pure-Java boundary completed an isolated Java 21 RED -> GREEN cycle using the exact committed test source before implementation, then permanent `ProgressionBoundaryTest` passed after the minimal interfaces were added. Keep this contract open until Stage 03 consumes only these interfaces, Stage 05 proves its persistence-backed implementation through the same boundary, and the final committed Gradle/NeoForge suite validates the Foundation HEAD. Stage 08 may later substitute an FTB Teams-aware owner resolver; do not create a Stage 05-owned stub or let Stage 03 depend on Stage 05 implementation classes.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-FLAME-WARD-001`: sanctuary must veto both player exposure and terrain mutation through explicit contracts, not duplicated radius logic.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: FTB Chunks/MineColonies adapters must prove fail-closed mutation protection when installed; standalone safe-tag behavior must remain valid without them.

## Closure rule

A pending contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
