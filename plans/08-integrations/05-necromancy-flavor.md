# Enshrouded Plan — Goety, Malum and Eidolon Flavor

**Milestone:** Optional for Level 1 standalone release.

**Goal:** reserve narrow optional recipe/loot/lore bridges to installed necromancy mods without importing their progression into Level 1.

**Planned types:** `GoetyFlavorAdapter`, `MalumFlavorAdapter`, `EidolonFlavorAdapter` — only if a concrete value review justifies them.

## Current-pack review

The approved Stage 08.05 value review was performed against the current NeoForge 1.21.1 pack and the verified standalone Enshrouded loop.

- Goety 3.1.4 remains its own Soul Energy / ritual / summon authority.
- Malum 1.8.2 remains its own spirit-arcana resource and ritual authority.
- Eidolon: Repraised 0.5.0.2 remains its own occult/alchemy/ritual authority.
- Enshrouded already has a standalone Flame Altar recipe using vanilla materials and an authentic Level-1 Lich skull offering with durable identity.
- No existing Level-1 recipe, loot table or lore path has a concrete gap that any of these three providers needs to fill.

## Approved decision — intentional no-op

**Decision:** do not create `GoetyFlavorAdapter`, `MalumFlavorAdapter` or `EidolonFlavorAdapter`; do not add provider dependencies, conditional recipes, conditional loot or runtime hooks.

Rationale:

- adding arbitrary provider ingredients or drops would be decorative coupling rather than gameplay value;
- provider souls, spirits, ritual components and progression remain owned by their respective mods and must not become Enshrouded gates or state;
- the authentic Lich skull remains the canonical Level-1 ritual offering and is not substituted or converted by optional provider items;
- removing any or all of the three mods therefore leaves the standalone Enshrouded loop unchanged by construction;
- avoiding unused adapters reduces classloading/API drift and preserves the existing authority boundaries.

## Files

- No production Java integration package is created.
- No provider-specific datapack conditional is created.
- This planning dossier and project status are the only intended Stage 08.05 changes.

## Dependencies

- Standalone Level 1 loop already functional.
- No Goety, Malum or Eidolon dependency is added to the Enshrouded build or runtime metadata.

## Implementation contract

- Goety souls/rituals/summons do not gate Enshrouded progression.
- Malum spirits and Eidolon ritual resources do not become Enshrouded currencies or offering substitutes.
- No hard dependency, no duplicated necromancy subsystem, and no assumption that these mods remain installed.
- This task closes as intentionally unnecessary because the approved value review found no concrete Level-1 gameplay benefit.

## TDD / verification

- Adapter present/absent tests are not applicable because no adapter survives the value review.
- Existing standalone tests remain authoritative; this branch changes documentation only.
- Full repository CI must still pass on the final PR HEAD before merge.
- Independent `main` CI must pass after merge.

## Merge gate

- [x] Concrete value review completed.
- [x] Explicit no-op decision approved.
- [x] No production integration or dependency added.
- [x] No unresolved cross-stage contract introduced.
- [ ] Final PR-head CI is GREEN.
- [ ] Merge into `main`.
- [ ] Independent post-merge `main` CI is GREEN.

**Acceptance:** Stage 08.05 is intentionally complete with no adapter. Optional necromancy mods retain their own authorities, while Enshrouded remains fully standalone and uncoupled.