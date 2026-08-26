# Enshrouded Plan — Goety, Malum and Eidolon Flavor

**Milestone:** Optional for Level 1 standalone release.

**Goal:** reserve narrow optional recipe/loot/lore bridges to installed necromancy mods without importing their progression into Level 1.

**Planned types:** `GoetyFlavorAdapter`, `MalumFlavorAdapter`, `EidolonFlavorAdapter`.

## Files

- Create integration packages only when a concrete Level 1 recipe/loot use is approved by implementation review.
- Data-pack conditionals live under `src/main/resources/data/enshrouded/*`.

## Dependencies

- Standalone Level 1 loop already functional.

## Implementation contract

- Goety 3.1.4 may provide optional thematic ingredients/loot references, but souls/rituals/summons do not gate Enshrouded progression.
- Malum 1.8.2 and Eidolon:Repraised 0.5.0.2 may provide optional flavor tags/recipes only when verified APIs/items exist in the current versions.
- No hard dependency, no duplicated necromancy subsystem, and no assumption that these mods remain installed.
- This task is optional for standalone Level 1 and may remain open if it adds no concrete value.

## TDD / verification

- [ ] For each adapter actually added, add a present/absent datapack/bootstrap test.
- [ ] Prove standalone recipes/progression remain complete with all three mods absent.
- [ ] If no adapter survives value review, record the explicit no-op decision and close the task as intentionally unnecessary rather than adding decorative coupling.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** Optional necromancy mods may enrich flavor, but the correct outcome is also “no adapter” if there is no concrete gameplay benefit.
