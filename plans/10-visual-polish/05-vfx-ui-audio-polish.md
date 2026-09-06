# Stage 10 — VFX, UI and Audio Polish

## Existing baseline

Stage 07 already owns the safe presentation seams for fog, HUD, particles and audio. Stage 10 polishes those surfaces rather than creating a second client gameplay model.

## VFX sequences

Required authored event sequences:

### CLEAR → SHROUD
- restrained peripheral wisps;
- local depth increase;
- short audio transition;
- no screen-obscuring flash.

### SHROUD → DEADLY
- stronger directional/edge cue;
- hostile red internal-energy accent;
- distinct sound cue;
- HUD warning remains the primary explicit communication.

### Enter Sanctuary over latent Shroud
- fog settles/opens;
- upward clean motes;
- warm ward highlight;
- Shroud geometry may remain visible when canonical latent state remains.

### Core proximity
- local particle density ramps within bounded settings;
- core pulse/ambient cue strengthens;
- no gameplay drain calculation client-side.

### Core destruction
- server-authored destroyed state starts collapse presentation;
- emissive pressure fades/bursts;
- tendrils lose tension;
- particles collapse inward/outward according to chosen art direction;
- final terrain/state mutation remains server authority.

### Flame ritual
Sequence:
1. interaction acknowledgement;
2. channels ignite;
3. ritual fragments/rings align;
4. controlled charge;
5. success burst only on authoritative success receipt;
6. altar settles into new progression visual state.

### Lich manifestation
- pre-appearance distortion/motes;
- ritual geometry or broken halo;
- provider entity appears through the established Stage 06 path;
- visual effect never spawns a substitute boss.

### Madness escalation
- use synchronized Madness stage;
- short bounded audio/visual cues;
- avoid nausea-inducing full-screen effects by default;
- reduced-motion/accessibility option required.

## Lodestone decision gate

Before adding Lodestone as an Enshrouded dependency, implement/prototype the hardest targeted VFX with the existing Stage 07 pipeline.

Adopt Lodestone only when it materially improves quality/maintainability for effects such as ribbons, ritual energy trails or layered particles. Record:
- exact API used;
- client-only boundary;
- fallback behavior;
- performance comparison;
- Sodium compatibility result.

## HUD art pass

Preserve the existing HUD state model, anchoring, scale and localization behavior.

New art requirements:
- original frame skin;
- reserve icon;
- Ordinary/Deadly severity iconography;
- Madness-stage mark;
- passage-blocked warning treatment;
- Sanctuary/protection cue if shown;
- nine-slice/stretch-safe background where needed.

### Accessibility
- state is never communicated by color alone;
- minimal mode;
- scale control preserved;
- reduced motion option;
- warning text retains high contrast;
- PT-BR/EN strings fit without clipping.

## Audio polish

Keep Stage 07's original/procedural asset provenance and bounded one-shot ownership.

Art pass may add/refine:
- low Shroud bed pulses;
- Deadly severity stingers;
- core heartbeat/pressure cues;
- Flame ritual tonal layer;
- purification release cue;
- Lich manifestation cue.

Avoid persistent loops that survive state exit/logout. AmbientSounds remains a neighboring presentation mod, not Enshrouded authority.

## Budgets

Every sequence documents:
- maximum spawned particle count;
- maximum lifetime;
- maximum audible distance;
- cooldown/rate limit;
- LOD/distance reduction;
- reduced-effects behavior;
- optional-library fallback.

No effect may force chunks or broadcast per render tick.

## QA

Review each sequence:
- normal graphics;
- Sodium path;
- fog enabled/disabled;
- particles full/reduced;
- day/night;
- reconnect/dimension switch;
- rapid boundary crossing to expose duplicate/spam bugs.
