# Upstream and Current-Pack Inventory

This file is the Foundation design inventory. Legal/source attribution is canonical in [`THIRD_PARTY_NOTICES.md`](../../THIRD_PARTY_NOTICES.md); the pack-version matrix is canonical in [`docs/compat/current-pack-2026-08-26.md`](../../docs/compat/current-pack-2026-08-26.md).

## Source upstreams

### Sculk Horde

- Repository: `TeamPeril/Sculk-Horde`.
- Audited source snapshot: `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`.
- Relevant concepts/classes include persistent saved data, `ChunkInfestationSystem` and Gravemind/node orchestration.
- GitHub source snapshot includes Apache License 2.0.
- Current public implementation is Forge 1.20.1-era code, so Forge API code is not a drop-in dependency for NeoForge 1.21.1.
- Policy: prefer an original Enshrouded implementation; adapt isolated algorithms/source only when justified and preserve all required notices.
- Any adapted Java source must declare `// UPSTREAM-DERIVED: sculk-horde-github-491aaa7e`.

### Ars Zero

- Current pack JAR/version: Ars Zero 2.0.2 for Minecraft 1.21.1.
- Entity registry/runtime evidence includes `ars_zero:lich` and `ars_zero:necromancer`.
- Audited Lich behaviour includes flight, blink, regeneration and Blight/Fire/Ice/Lightning casts.
- Project/release license audited for the integration: GPLv3.
- Policy: runtime/registry provider integration only; do not copy the GPL Lich class into Enshrouded core.

## Confirmed current-pack integration candidates — 2026-08-26

- NeoForge 21.1.248.
- Ars Nouveau 5.13.0.
- Ars Zero 2.0.2.
- Iron's Spells 'n Spellbooks 3.16.3.
- Epic Fight 21.17.3.1.
- Goety 3.1.4; Goety Cataclysm 1.8.2; Goety Iron 3.1.
- Malum 1.8.2.
- Eidolon:Repraised 0.5.0.2.
- FTB Chunks 2101.1.21; FTB Teams 2101.1.11.
- JourneyMap 6.0.5.
- MineColonies 1.1.1374-1.21.1-snapshot.
- GeckoLib 4.9.2.
- AmbientSounds 6.3.8 and Particular Reforged 1.5.7 are presentation neighbours, not core dependencies.

## Explicitly excluded

- Spore 2.2.0j.
- Spore: Infnexus / Infnexus 2.0.4.

They may still be physically present during development, but Enshrouded architecture, acceptance criteria and integrations must not rely on them. The intended final pack removes them once Enshrouded is functional.

## Source gate

Before any source-derived implementation is committed:

1. Pin the exact upstream repository and source snapshot in `THIRD_PARTY_NOTICES.md`.
2. Record the applicable license at that snapshot.
3. Add a stable provenance id.
4. Mark adapted Java source with `// UPSTREAM-DERIVED: <source-id>`.
5. Keep optional-provider imports outside Foundation/core contracts.

`ProvenanceDocumentationTest` enforces that every source-derived marker resolves to a notice entry.
