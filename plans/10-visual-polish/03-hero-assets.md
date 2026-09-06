# Stage 10 — Hero Assets

Hero assets are the pieces that define Enshrouded's identity in screenshots and normal gameplay. They receive the highest modeling, texturing, animation and review budget.

## P0 — Flame Altar

### Problem
The current Level 1 asset is functionally valid but visually below final-production quality.

### Target composition
- central ritual anchor;
- raised stone/metal cradle;
- visible open flame chamber;
- four directional braces or pedestals;
- partial/broken ritual halo;
- engraved rune channels;
- strong negative-space silhouette;
- optional surrounding multiblock architecture.

### Recommended footprint
Prototype both:
- compact 3×3 presentation around one authoritative center;
- premium 5×5 architecture if the extra scale remains practical indoors and does not harm UX.

The controller remains one canonical block/BE even if the visible ritual structure is larger.

### Animation states
- `idle`;
- `ritual_available` subtle cue;
- `ritual_charge`;
- `ritual_success`;
- `level_transition`;
- `inactive` where canonical state calls for it.

### Progression visualization
Flame Level should visibly build the altar. Candidate changes:
- additional halo segments;
- newly lit rune channels;
- taller/richer flame geometry;
- floating ritual fragments;
- stronger but still controlled emissive seams.

Do not represent progression only by increasing particle count.

### Technical target
Use GeckoLib BlockEntity rendering for animated central components. Static structural shell should use normal block models when possible.

## P0 — Shroud Core

### Target fantasy
A living heart of corruption, not a cube with particles.

### Geometry
- asymmetric core body;
- rib/husk framing;
- exposed inner heart;
- branching attachment points;
- hanging strands/tendrils;
- rooted or suspended posture depending on placement profile.

### Animation
- independent inner/outer pulse phases;
- breathing contraction;
- subtle tendril movement;
- threat/proximity intensification only from presentation-safe inputs;
- collapse/death sequence after authoritative destruction event.

### Ordinary vs Deadly
Use separate silhouette/material cues as well as palette. Deadly may expose more inner energy, add thorn/split geometry and use more aggressive motion.

### World integration
Nearby veins/growth should visually appear to originate from or converge toward the core. Fusion-connected overlays are an optional tool for this continuity.

## P0 — Lich Skull

### Goal
Replace the vanilla-Wither-Skeleton-skull presentation with an original trophy worthy of a narrative boss reward.

### Design
- ritualized elongated skull/mask;
- broken crown/halo fragments;
- asymmetric fracture;
- carved identity rune;
- small spectral residue;
- clear profile in GUI and held view.

### Rendering
Prefer a custom 3D item model. Animation, if any, remains subtle and event/state driven; no constant distracting inventory motion.

Required transforms:
- GUI;
- ground;
- fixed/display;
- first-person hand;
- third-person hand.

## P1 — Sanctuary / Purification Focus

### Goal
Safe Shroud space must have a physical focal language instead of relying only on HUD/fog suppression.

### Candidate form
- ward lantern / obelisk hybrid;
- radial rune base;
- vertical clean-light aperture;
- small floating Flame-related fragments;
- upward particles;
- obvious family relation to Flame Altar at a lower hierarchy level.

### States
- dormant;
- active;
- contested/unsafe only if canonical gameplay already exposes such a state;
- purification success.

## P1 — Lich manifestation presentation

Keep the Stage 06 provider contract. Visual polish may add:
- spawn aura;
- encounter-bound spectral ring;
- phase/teleport/readability effects if the provider exposes a safe hook;
- defeat transition;
- reward materialization cue.

Do not fork provider behavior or create an Enshrouded-owned replacement boss solely for art.

## P1 — Core nest / Shroud set piece

Create an environmental composition kit around Shroud cores:
- ribs/arches of corrupted matter;
- sludge pools and banks;
- thick root/vein channels;
- ruined architecture fragments;
- elevated landmarks to improve navigation;
- severity-dependent variants.

## Asset package requirements

Each hero asset task must deliver:
- design sheet / orthographic or clear concept reference;
- Blockbench source when applicable;
- runtime model export;
- textures + emissive mask;
- animation file(s);
- provenance entry;
- performance notes;
- in-game screenshots;
- reduced-effects screenshot;
- acceptance checklist.

## Approval rule

No P0 hero asset is approved solely from source files. It must be reviewed in-game against actual modpack surroundings, at realistic FOV and distance.
