# Stage 10.09 — Flame Shell Art Handoff

## Purpose

This document is the execution contract for the **user-authored visual resource package** still required to close the Flame Altar 3×3 presentation portion of Stage 10.09.

It does **not** introduce gameplay authority, a second Flame/Sanctuary controller, worldgen, persistence, networking, client-side gameplay inference or a replacement for the canonical Flame Altar lifecycle. The Java/runtime implementation is already present in `main`; this handoff exists because the dedicated brace/rune client resource package is not.

Operational baseline used to author this handoff:

- repository `main`: `b7ec63b4844f1efb8706b248f26db48b8d8bca5e`;
- physical pack authority checked on 2026-09-08: **600 mods**;
- NeoForge: **21.1.248**;
- Stage state: **10.09 logic/authority implemented; shell art and set-piece consumers open; 10.10 not started**.

If `main` or the physical modlist advances before implementation, reconcile again before editing resources.

## Canonical authority and invariants

The following boundaries are non-negotiable:

1. `FlameAltarBlockEntity` and the existing Flame Altar lifecycle remain the single structure controller.
2. Existing Flame Ward/Sanctuary/Purification services remain the gameplay authority.
3. `FlameAltarBraceBlock` and `FlameAltarRuneBlock` are presentation shell components; their art cannot create a parallel state machine.
4. `FORMED` is a presentation projection of canonical formation state. Client resources may render it differently but may not decide whether the structure is formed.
5. No shell-art implementation may add gameplay ticks, server scans, chunk forcing, persistence, packet authority, ritual completion callbacks or client recomputation of Sanctuary/Flame eligibility.
6. Breaking a required shell component must continue to use the existing lifecycle and deterministic unform path.
7. A separate Purification Shrine/controller remains intentionally deferred.
8. Shroud Core Nest and Lich landmark placement/worldgen remain separate `RUNTIME_CONSUMER_OPEN` work; they are outside this handoff.

## Runtime IDs and state surface

### `enshrouded:flame_altar_brace`

Canonical Java class: `FlameAltarBraceBlock`.

Exposed blockstate properties:

- `formed=false|true`;
- `facing=north|east|south|west`.

The `facing` property is real runtime state and must drive the cardinal presentation. It points the brace toward the center/controller in a valid complex.

### `enshrouded:flame_altar_rune`

Canonical Java class: `FlameAltarRuneBlock`.

Exposed blockstate properties:

- `formed=false|true`.

There is **no `facing` property** for the rune in the current runtime. Therefore the authored corner rune must be visually valid under 90° rotation without requiring hidden directional semantics. A design that needs asymmetric corner orientation requires a separately approved runtime/state change and is not permitted to smuggle orientation into this resource-only handoff.

## Canonical 3×3 footprint

Top-down layout, with `A` = altar/controller, `B` = brace, `R` = rune:

```text
R B R
B A B
R B R
```

Relative to the controller at `(0, 0)` in X/Z:

| Position | Component | Required brace facing |
|---|---|---|
| `(0, -1)` | north brace | `south` |
| `(1, 0)` | east brace | `west` |
| `(0, 1)` | south brace | `north` |
| `(-1, 0)` | west brace | `east` |
| `(-1, -1)` | rune | n/a |
| `(1, -1)` | rune | n/a |
| `(-1, 1)` | rune | n/a |
| `(1, 1)` | rune | n/a |

The existing validator owns this contract. Art must adapt to it; resource design must not redefine the structure footprint.

## Required client resource package

The handoff is not complete until dedicated resources exist for both shell blocks.

### Blockstates

Required repository paths:

- `src/main/resources/assets/enshrouded/blockstates/flame_altar_brace.json`
- `src/main/resources/assets/enshrouded/blockstates/flame_altar_rune.json`

The brace blockstate must resolve all **8** valid combinations of `formed × horizontal facing` without missing-model fallback.

The rune blockstate must resolve both `formed=false` and `formed=true`.

### Block models

Use explicit authored models for the two visible formation states. The recommended canonical stems are:

- `src/main/resources/assets/enshrouded/models/block/flame_altar_brace_unformed.json`
- `src/main/resources/assets/enshrouded/models/block/flame_altar_brace_formed.json`
- `src/main/resources/assets/enshrouded/models/block/flame_altar_rune_unformed.json`
- `src/main/resources/assets/enshrouded/models/block/flame_altar_rune_formed.json`

Equivalent authored model decomposition is acceptable only if the final blockstate graph remains explicit, bounded and reviewable. Do not replace the missing art with a generic cube/vanilla-texture fallback merely to make resources resolve.

### Item models

`ModItems` already exposes simple `BlockItem`s for both shell blocks. Required client item resources:

- `src/main/resources/assets/enshrouded/models/item/flame_altar_brace.json`
- `src/main/resources/assets/enshrouded/models/item/flame_altar_rune.json`

The inventory/hand presentation must visually belong to the same authored family as the placed structure.

### Textures

New first-party shell textures belong under:

- `src/main/resources/assets/enshrouded/textures/block/`

Use stable `flame_altar_brace_*` / `flame_altar_rune_*` naming for dedicated shell material maps. Reuse an existing first-party Flame texture only when it is an intentional shared material, not as a shortcut that makes brace/rune indistinguishable from the fallback center block.

Do not assume an emissive/glowmask render path exists for the new shell blocks merely because the center Flame Altar has glow resources. If emissive shell treatment is desired, first prove the exact render consumer and keep it presentation-only.

### Editable source

If Blockbench is used, commit the editable first-party source under the existing Stage 10 art source area, preferably:

- `art/blockbench/flame_altar_brace.bbmodel`
- `art/blockbench/flame_altar_rune.bbmodel`

The committed source must correspond to the shipped geometry rather than acting as an unrelated concept file.

## Provenance requirement

All new distributable binary resources are fail-closed under the repository provenance gate.

Project-authored PNGs must be enumerated in:

- `provenance/third-party-provenance.json`
- array: `first_party_binaries`

`scripts/ci/verify_third_party_provenance.py` recursively scans distributable binary resources under `src/main/resources` and rejects an unregistered binary. Do not weaken or bypass this gate to admit the shell art.

If any material is copied, derived or vendored rather than authored first-party, it must use the existing immutable, license-approved third-party provenance path instead of being mislabeled first-party.

## Visual language

The shell must follow the Stage 10 Visual Bible rather than become a separate art direction.

### Flame/Sanctuary shape language

Use:

- vertical/radial hierarchy around the controller;
- stable ritual geometry;
- rings, arches, triangles or stepped framing where they improve the whole-complex silhouette;
- carved channels/runes that visually pull attention inward;
- deliberate negative space;
- structured braces rather than eight unrelated decorative cubes.

### Materials/palette

Primary family:

- charcoal / blackened stone;
- aged stone gray;
- dark brass / old metal;
- deep ember orange;
- amber and restrained gold;
- warm white only at the hottest focal point.

Material hierarchy must remain legible: matte chipped stone, structured metal framing and localized energy/rune treatment should not collapse into one uniformly glowing surface.

### Formed versus unformed

`formed=false` and `formed=true` must be visually distinguishable at normal gameplay distance.

The distinction must not rely solely on a color swap. Suitable authored differences may include, within the existing state surface:

- connected versus dormant rune channels;
- stronger structural continuity toward the center;
- localized activation detail;
- a clearer shared radial rhythm across the nine-block composition.

The resource layer must remain static/state-driven; animation completion cannot become formation authority.

## Whole-complex acceptance criteria

The **formed** 3×3 is accepted only if it reads as one authored ritual construction.

Reject the package if normal play distance still reads primarily as:

- a 3×3 checkerboard;
- nine independent Minecraft cubes;
- four generic braces plus four generic corner blocks with no visual relationship to the center;
- a palette-only activation state;
- emissive edges everywhere with no hierarchy.

Required compositional properties:

1. cardinal braces visibly resolve toward the controller;
2. corner runes support the radial composition while remaining valid with no orientation state;
3. authored lines/masses create continuity across block boundaries without introducing gameplay coupling;
4. the center remains the focal point and single controller visually as well as technically;
5. the unformed structure still reads as an intentionally incomplete/dormant ritual device rather than missing-texture debris;
6. the formed state is materially more coherent and activated without visually becoming a second machine/controller layer.

## Rejection boundaries

Do not merge shell art that requires any of the following without a separately approved runtime task:

- adding a rune `facing` property;
- changing the canonical 3×3 footprint;
- adding a second controller or Sanctuary/Purification authority;
- using client animation/resource state to determine formation;
- server/world scans or chunk forcing for visuals;
- automatic Shroud Core Nest or Lich landmark world placement;
- replacing the canonical Flame/Sanctuary lifecycle with an art-specific implementation;
- weakening provenance, CI, resource or dedicated-server gates.

## Verification package required before art approval

Automated verification must prove at minimum:

- all brace `formed × facing` blockstate combinations resolve;
- both rune `formed` states resolve;
- all referenced models and textures exist;
- brace/rune item models resolve;
- no missing-model/missing-texture fallback;
- new binary resources satisfy the existing provenance gate;
- normal NeoForge build/resource validation remains green;
- no dedicated-server client-class/resource regression is introduced.

Manual evidence required before changing `USER_ART_HANDOFF` / `REVIEW_IN_GAME` to `KEEP` or declaring `ART APPROVED`:

- unformed and formed 3×3 screenshots;
- daylight and night;
- near and mid distance;
- representative FOV;
- multiple cardinal viewing directions;
- Sanctuary/purification readability in full, reduced-sensory and minimal presentation modes;
- Sodium/shader coexistence;
- resource reload and reconnect;
- current full **600-mod** client visual smoke.

Compilation or CI alone cannot promote the art to approved.

## Implementation sequence

1. Reconcile the art branch with the latest `origin/main` and recheck the physical modlist.
2. Author brace/rune Blockbench sources and first-party textures.
3. Add blockstates, block models and item models against the **existing** runtime properties.
4. Register every new binary in the existing provenance ledger.
5. Add/extend bounded Stage 10 resource-contract tests without weakening existing gates.
6. Run the normal automated verification matrix on the reconciled HEAD.
7. Capture the required in-game visual evidence.
8. Run Sodium/shader/resource-reload/reconnect coexistence and the current full-pack client visual smoke.
9. Update `09-asset-review-matrix.md` and `STATUS.md` only to the level actually proven by evidence.
10. Close Stage 10.09 shell art only after the above gates pass. Stage 10.10 remains the subsequent final visual/compatibility acceptance gate.

## Current handoff state

At the baseline recorded above:

- Flame 3×3 logic/lifecycle: implemented and previously verified;
- survival acquisition for brace/rune: implemented and previously verified;
- dedicated brace/rune client art package: **`USER_ART_HANDOFF`**;
- Shroud Core Nest production consumer: **`RUNTIME_CONSUMER_OPEN`**;
- Lich landmark production consumer: **`RUNTIME_CONSUMER_OPEN`**;
- Stage 10.10: **not started**;
- `ART APPROVED`: **open**.
