# Enshrouded Plan — Flame Altar

**Milestone:** Level 1 required.

**Status:** ✅ Verified and merged.

**Goal:** add the physical ritual block/block entity/menu as a server-authoritative adapter over the already-merged generic ritual engine, without owning progression truth or depending on Stage 06 story items.

**Implemented types:** `FlameAltarBlock`, `FlameAltarBlockEntity`, `FlameAltarMenu`, `FlameAltarService`, `FlameAltarRuntime`, `FlameAltarOffering`, plus client screen and registry/data assets.

## Files

- [x] Created `src/main/java/com/gustavaopere/enshrouded/flame/altar/*`.
- [x] Added menu/screen registration and assets/data for `enshrouded:flame_altar`.
- [x] Added Level-1 crafting recipe and explicit block loot.
- [x] Added EN/pt-BR localization.

## Dependencies

- [x] 01 Flame state merged first.
- [x] 04 Level 1 ritual framework merged first, providing `FlameRitualRegistry`/`FlameRitualExecutor`.

## Implementation contract

- [x] Altar inventory uses server-owned container state and submits offerings to the merged `FlameRitualExecutor`.
- [x] The altar does not duplicate ritual eligibility, idempotence or progression mutation logic locally.
- [x] Stage 05 does not import, register or require the real Lich Skull; Stage 06 still owns the authentic skull and concrete ritual binding.
- [x] Breaking the altar returns the stored offering according to explicit container policy and never rolls back earned Flame state.
- [x] Multiple altars cannot duplicate one ritual outcome for the same progression owner because all execution passes through the same ritual engine.
- [x] Altar interaction exposes current Flame Level, Passage Level and readiness from authoritative state.
- [x] The client sends only the interaction/button intent; the server re-reads inventory and revalidates owner, offering, ritual and progression prerequisites.

## TDD / verification

- [x] GameTest crafting/placing/reloading altar.
- [x] GameTest forged client-like ritual request without a registered valid offering is denied by the ritual engine.
- [x] GameTest double activation across two altars grants at most one ritual outcome and consumes exactly one offering.
- [x] Contract test proves altar service delegates to `FlameRitualExecutor` rather than maintaining a second ritual state machine.
- [x] GameTest breaking/replacing altar preserves owner progression.
- [x] No Stage 05 test references the production Lich Skull.

## Merge evidence

- Implementation branch: `feat/05-flame-altar`.
- Final implementation HEAD: `b9fbc27a41eddaadc3515ac47bd1fc8bd7a751d0`.
- PR #34: closed unmerged only because the connector could not transition the draft PR to ready-for-review.
- Final replacement PR: #35 — `05.02 — Flame Altar`.
- Prior exact-head verification: workflow `33292226154` / run #962 — GREEN.
- Final PR-head verification: workflow `33292568059` / run #963 — GREEN.
- Final PR-head gates: unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR sanity, 55/55 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke — GREEN.
- Merge SHA: `f47bcd70247acbbbb550e3af02baadc0f41eab63`.

## Runtime findings fixed before merge

- [x] Minecraft 1.21.1 shaped-recipe ingredients were corrected to the object schema required by the real `RecipeManager` parser.
- [x] GameTest fixtures were corrected to use a real server-side mock player rather than a generic `Player`.
- [x] Test rituals use independent synthetic offerings/IDs so GameTest execution order cannot contaminate idempotence assertions.

## Cross-stage contracts

No new task-local pending contract was introduced. `ENSH-L1-OWNER-SNAPSHOT-001` remains open only for its Stage 06 encounter/reward and Stage 08 membership-change evidence. The authentic Lich Skull binding remains Stage 06-owned.

**Acceptance:** ✅ A functional Flame Altar provides the physical progression interface over the merged generic ritual engine without becoming a second progression/ritual authority or depending on Stage 06 story-item implementation.
