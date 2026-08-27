# Cross-Stage Pending Contracts

This file records blockers that must not be silently papered over by later branches.

## External verification blockers

- `ENSH-CI-ACTIONS-001`: Foundation acceptance is blocked by GitHub Actions jobs terminating before runner initialization. Both push and pull-request runs create a `verify` job but expose `steps=null`; rerunning the job produces the same result. The latest controlled rerun on Enshrouded workflow run `33098652025` produced job `98610872431`, again `failure` with `steps=null`. Cross-repository control evidence shows the same failure mode on the private `Gustavaopere/Volcanoes` repository: workflow run `33099719939`, job `98615923587`, also terminated with `steps=null` during the same period. This demonstrates that the current blocker is not specific to the Enshrouded workflow or repository. GitHub Status also reports a current Billing disruption on 2026-08-27, but the repository APIs available here do not expose account billing/quota state, so no specific billing cause is asserted. A local fallback verification was also attempted in the conversation environment, but outbound DNS cannot resolve `services.gradle.org` and no Gradle/NeoForge cache is present, so an offline build cannot be executed there. This blocker is external to Gradle/test execution: no final-HEAD command has run. Do not mark Foundation complete, rename tasks with `✅-`, or create Stage 01 branches until a real runner executes the full merge gate GREEN.

## Initially open contracts

- `ENSH-L1-CORE-TO-TERRAIN-001`: terrain materialization must consume the canonical Shroud field and never invent independent spread state.
- `ENSH-L1-CORE-TO-EXPOSURE-001`: player exposure must query the same canonical severity service used by terrain/client state.
- `ENSH-L1-FLAME-WARD-001`: sanctuary must veto both player exposure and terrain mutation through explicit contracts, not duplicated radius logic.
- `ENSH-L1-LICH-REWARD-001`: first-manifestation death must produce one Enshrouded skull even when an external boss provider supplies the entity.
- `ENSH-L1-MAGIC-CLASSIFY-001`: core magic-resistance classification must work standalone and adapters may only enrich it.
- `ENSH-L1-CLAIM-SAFETY-001`: FTB Chunks/MineColonies adapters must prove fail-closed mutation protection when installed; standalone safe-tag behavior must remain valid without them.

## Closure rule

A pending contract is removed only in the merge that provides executable verification for both sides of the boundary. If one side is implemented first, the owning task remains open or records the exact external blocker rather than being marked complete prematurely.
