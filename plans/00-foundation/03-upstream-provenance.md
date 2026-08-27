# Enshrouded Plan — Upstream Provenance

**Milestone:** Level 1 required.

**Goal:** record exactly what may be adapted, what may only be integrated and which current-pack mods are relevant.

**Planned types:** `UpstreamInventory`.

## Files

- Create `THIRD_PARTY_NOTICES.md`.
- Create `plans/00-foundation/UPSTREAM.md`.
- Create `docs/compat/current-pack-2026-08-26.md`.

## Dependencies

- 01 build scaffold.

## Implementation contract

- Record Sculk Horde GitHub snapshot `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc` and Apache-2.0 source licensing; preserve attribution for any adapted source.
- Record Ars Zero 2.0.2 as GPLv3 and use integration/runtime-provider strategy rather than copying its Lich implementation into core.
- Record current pack candidates from `modlist agora atual.txt`: Ars Nouveau 5.13.0, Ars Zero 2.0.2, Iron's Spells 3.16.3, Epic Fight 21.17.3.1, Goety 3.1.4, Malum 1.8.2, Eidolon:Repraised 0.5.0.2, FTB Chunks 2101.1.21, FTB Teams 2101.1.11, JourneyMap 6.0.5, MineColonies 1.1.1374 snapshot, GeckoLib 4.9.2.
- Record Spore 2.2.0j and Infnexus 2.0.4 only in an explicit exclusion section; no integration work derives from them.
- Do not promote transitive/JarJar libraries such as Veil to mandatory dependencies.

## TDD / verification

- [ ] Add a documentation verification script/test that checks every source-derived code path listed later has a provenance entry.
- [ ] Verify `THIRD_PARTY_NOTICES.md` contains the upstream license reference before any adapted source enters the project.

## Current implementation checkpoint — 2026-08-27

Implemented on `round-1-foundation`:

- `THIRD_PARTY_NOTICES.md`, `plans/00-foundation/UPSTREAM.md`, current-pack compatibility inventory and `UpstreamInventory` exist;
- Sculk Horde source snapshot/licensing and Ars Zero version/licensing/integration strategy are pinned;
- Spore and Infnexus are recorded only as explicit exclusions and are not core dependencies, providers or design authorities;
- the compatibility inventory records current optional integration candidates without promoting them to mandatory dependencies;
- `ProvenanceDocumentationTest` verifies required documents, audited source/version markers, exclusion entries and all future `// UPSTREAM-DERIVED:` source markers against `THIRD_PARTY_NOTICES.md`.

No source-derived production code has entered the branch at this stage. Current final-HEAD GREEN verification remains blocked by GitHub Actions terminating before checkout, so this task stays open and unrenamed.

## Merge gate

- [ ] All task-specific tests are GREEN on the final branch HEAD.
- [ ] `./gradlew test` is GREEN.
- [ ] NeoForge build is GREEN; run GameTests/dedicated-server smoke when this task touches runtime/bootstrap/world state.
- [ ] No unresolved cross-stage contract introduced by this task is hidden; `plans/PENDING.md` is updated when necessary.
- [ ] After merge, rename this file with `✅-` and update `plans/STATUS.md` in the same merge/checkpoint.

**Acceptance:** The repository contains an auditable source/integration inventory and no copied code can enter without provenance.
