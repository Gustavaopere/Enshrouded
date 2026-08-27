# Cross-Stage Pending Contracts

This file records blockers that must not be silently papered over by later branches.

## External verification blockers

- `ENSH-CI-ACTIONS-001`: Foundation acceptance is blocked by GitHub Actions jobs terminating before runner initialization. Enshrouded push and pull-request runs create a `verify` job but expose `steps=null`; reruns reproduce the same result. A private cross-repository control on `Gustavaopere/Volcanoes` (`33099719939` / job `98615923587`) showed the same `failure` + `steps=null` pattern during the same period, so the current blocker is not specific to Enshrouded's workflow or repository. GitHub Status has reported service/billing incidents in this window, but the repository APIs available here do not expose account billing/quota state, so no specific billing cause is asserted. The conversation-local fallback also cannot perform a real NeoForge build because `services.gradle.org` is not resolvable and no suitable Gradle/NeoForge cache is present. This blocker is external to Gradle/test execution: no final-HEAD command has run on a real executor. Do not mark Foundation complete, rename tasks with `✅-`, or create Stage 01 branches until a real runner executes wrapper integrity, unit tests, diff sanity, NeoForge build/JAR sanity, GameTests and the two-boot dedicated-server save/reload harness GREEN.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-FLAME-WARD-001`: sanctuary must veto both player exposure and terrain mutation through explicit contracts, not duplicated radius logic.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: FTB Chunks/MineColonies adapters must prove fail-closed mutation protection when installed; standalone safe-tag behavior must remain valid without them.

## Closure rule

A pending contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
