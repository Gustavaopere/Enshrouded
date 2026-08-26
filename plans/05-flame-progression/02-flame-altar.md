# Enshrouded Plan — Flame Altar

**Milestone:** Level 1 required.

**Goal:** add the physical ritual block/block entity/menu that accepts valid offerings without owning progression truth.

**Planned types:** `FlameAltarBlock`, `FlameAltarBlockEntity`, `FlameAltarMenu`, `FlameAltarService`.

## Files

- Create `src/main/java/com/gustavaopere/enshrouded/flame/altar/*`.
- Create menu/screen registration and assets/data for `enshrouded:flame_altar`.
- Create recipe/crafting path appropriate to Level 1 availability.

## Dependencies

- 01 Flame state.

## Implementation contract

- Altar inventory uses normal server container rules and validates ritual offerings server-side.
- Breaking an altar returns/handles inventory according to explicit container policy but never rolls back earned Flame state.
- Multiple altars cannot duplicate one ritual reward for the same progression owner.
- Altar interaction displays current Flame/Passage Level and ritual readiness from authoritative state.
- Client screen sends intents only; server revalidates item, owner, ritual and progression prerequisites.

## TDD / verification

- [ ] GameTest crafting/placing/reloading altar.
- [ ] GameTest forged client-like ritual request without required item is denied.
- [ ] GameTest double activation with one skull grants at most one progression completion and consumes the offering exactly once.
- [ ] GameTest breaking/replacing altar preserves owner progression.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** A functional Flame Altar provides the physical progression interface without becoming an exploitable save authority.
