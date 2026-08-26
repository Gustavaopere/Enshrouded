# Upstream and Current-Pack Inventory

## Source upstreams

### Sculk Horde

- Repository: `TeamPeril/Sculk-Horde`.
- Audited source snapshot: `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`.
- Relevant concepts/classes include persistent saved data, `ChunkInfestationSystem` and Gravemind/node orchestration.
- GitHub source snapshot includes Apache License 2.0.
- Current public binary is Forge 1.20.1, so Forge API code is not a drop-in dependency for NeoForge 1.21.1.
- Policy: adapt isolated algorithms only when they outperform a simpler native design; preserve required notices.

### Ars Zero

- Current pack JAR: `ars_zero-1.21.1-2.0.2.jar`.
- Entity registry evidence: `ars_zero:lich`, `ars_zero:necromancer`, `ars_zero:bone_golem`, `ars_zero:acolyte`.
- Source Lich behavior includes flight, blink, regeneration and Blight/Fire/Ice/Lightning casts.
- Project license: GPLv3.
- Policy: runtime/registry integration; do not copy the GPL Lich class into core.

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
- AmbientSounds 6.3.8 and Particular Reforged 1.5.7 are presentation neighbors, not core dependencies.

## Explicitly excluded

- Spore 2.2.0j.
- Spore: Infnexus 2.0.4.

They may still be physically present during development, but the Enshrouded architecture, acceptance criteria and integrations must not rely on them. The intended final pack removes them once Enshrouded is functional.
