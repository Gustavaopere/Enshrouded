# Enshrouded Plan — Flame Altar

**Milestone:** Level 1 required.

**Goal:** add the physical ritual block/block entity/menu that accepts registered ritual offerings without owning progression truth or depending on Stage 06 story items.

**Planned types:** `FlameAltarBlock`, `FlameAltarBlockEntity`, `FlameAltarMenu`, `FlameAltarService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/altar/*`.
- Create menu/screen registration and assets/data for `enshrouded:flame_altar`.
- Create recipe/crafting path appropriate to Level 1 availability.

## Dependencies

- 01 Flame state.

## Implementation contract

- Altar inventory uses normal server container rules and validates ritual offerings server-side through the generic ritual framework/service boundary.
- This task does not import, register or require the real Lich Skull. Stage 05 tests use a synthetic/test ritual offering; Stage 06 later binds the authentic skull.
- Breaking an altar returns/handles inventory according to explicit container policy but never rolls back earned Flame state.
- Multiple altars cannot duplicate one ritual outcome for the same progression owner.
- Altar interaction displays current Flame/Passage Level and ritual readiness from authoritative state.
- Client screen sends intents only; server revalidates item, owner, ritual and progression prerequisites.

## TDD / verification

- [ ] GameTest crafting/placing/reloading altar.
- [ ] GameTest forged client-like ritual request without the registered synthetic test offering is denied.
- [ ] GameTest double activation with one synthetic test offering grants at most one generic ritual outcome and consumes the offering exactly once.
- [ ] GameTest breaking/replacing altar preserves owner progression.
- [ ] No Stage 05 test references the production Lich Skull; defeat -> skull -> altar coverage belongs to `06-lich-story/04-lich-skull.md`.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A functional Flame Altar provides the physical progression interface without becoming an exploitable save authority or depending on Stage 06 story-item implementation.
