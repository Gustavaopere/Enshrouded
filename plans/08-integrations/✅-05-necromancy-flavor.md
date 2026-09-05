# ✅ Enshrouded — Goety, Malum and Eidolon Flavor

**Stage:** 08.05 — Optional Integrations

**Disposition:** intentionally complete as **NO-OP** after approved value review.

## Goal

Evaluate whether narrow optional recipe/loot/lore bridges to the installed necromancy/occult mods add concrete Level-1 value without importing their progression into Enshrouded.

## Current-pack evidence

The Stage 08.05 review used the current NeoForge 1.21.1 pack and the verified standalone Enshrouded loop.

- Goety 3.1.4 remains its own Soul Energy / ritual / summon authority.
- Malum 1.8.2 remains its own spirit-arcana resource and ritual authority.
- Eidolon: Repraised 0.5.0.2 remains its own occult/alchemy/ritual authority.
- Enshrouded already owns a standalone Flame Altar recipe using vanilla materials.
- The authentic Level-1 Lich skull has durable Enshrouded identity and remains the canonical ritual offering.
- No existing Level-1 recipe, loot table or lore path has a concrete gap requiring one of these three providers.

## Final decision — intentional no-op

No `GoetyFlavorAdapter`, `MalumFlavorAdapter` or `EidolonFlavorAdapter` is created.

No provider dependency, conditional recipe, conditional loot entry, runtime hook, progression bridge, conversion layer or provider-specific state is added.

Rationale:

- arbitrary provider ingredients/drops would be decorative coupling rather than gameplay value;
- Goety souls, Malum spirits and Eidolon ritual resources remain owned by their respective mods and do not become Enshrouded gates, currencies or offering substitutes;
- the authentic Lich skull remains the canonical Level-1 offering;
- removing any or all three providers leaves the Enshrouded standalone loop unchanged by construction;
- unused adapters would add classloading/API-drift surface with no compensating gameplay benefit.

## Authority boundaries preserved

- Enshrouded does not duplicate necromancy, spirit-arcana or occult progression.
- Optional provider systems remain playable on their native terms.
- No provider becomes an authority for Shroud, Exposure, Flame progression, Lich identity or Level-1 completion.
- No second event/mutation/progression authority was introduced.

## TDD / verification disposition

Adapter present/absent tests are not applicable because no adapter survived the value review. The correct executable verification is therefore regression proof of the unchanged standalone repository.

The exact PR-head CI ran the full repository gate set and passed:

- unit tests;
- frontier benchmark baseline;
- diff sanity;
- NeoForge build;
- built-JAR verification;
- GameTest server;
- SavedData two-boot reload GameTest;
- dedicated-server save/reload smoke.

## Provenance

- Base verified `main`: `03db94044b903628e51808de18a93134be9ad300`.
- Decision branch: `feat/08-necromancy-flavor`.
- Final decision HEAD: `0a7aa14709bc370f6b8b85ea779eee3e10cc18f9`.
- PR: #68 — `Stage 08.05: close necromancy flavor as intentional no-op`.
- Exact PR-head workflow/job: `33665328518` / `100365540768` — `completed/success`.
- Decision merge SHA: `737834816e7fac5b10284e1484536a6f3e5f5a3e`.
- Independent post-merge `main` workflow/job: `33665970274` / `100367685054` — `completed/success`.
- Production Java changes: none.
- Provider-specific datapack changes: none.
- Build/runtime dependency changes: none.
- New cross-stage pending contracts: none.

## Merge gate

- [x] Current modlist and Notion provider versions reviewed.
- [x] Concrete value review completed.
- [x] Intentional no-op design approved.
- [x] No production integration or dependency added.
- [x] No unresolved cross-stage contract introduced.
- [x] Exact PR-head CI GREEN.
- [x] PR #68 merged into `main`.
- [x] Independent post-merge `main` CI GREEN.

**Acceptance:** Stage 08.05 is intentionally complete with no adapter. Optional necromancy/occult mods retain their own authorities, while Enshrouded remains fully standalone and uncoupled.