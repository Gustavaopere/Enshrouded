# Stage 10 — Shroud World Art

## Objective

Make Shroud territory visually convincing even when enhanced fog is disabled. The corruption must exist in geometry, materials, surface continuity and environmental composition rather than being only a screen tint.

## Asset families

Build coherent families for:
- Shroud vein/root;
- membrane/webbing;
- active growth;
- crust/scab material;
- withered growth;
- red sludge still/flow/shore;
- core-adjacent material;
- Deadly-specific growth accents;
- purified scars/residue.

Existing Level 1 textures are inputs to an audit, not automatically discarded.

## Surface continuity

### Veins
Veins should visually traverse multiple blocks and connect through clusters. Avoid hard tile resets at every block boundary.

Preferred enhancement:
- Fusion connected/continuous texture path where useful;
- safe ordinary base texture/model when Fusion is absent.

### Growth overlays
Where corruption creeps over host terrain, consider model/texture overlays rather than replacing every host material with one uniform purple/red block.

Rules:
- must stay consistent with canonical terrain mutation data;
- presentation cannot visually imply a spread that did not happen in gameplay state;
- overlays must not cause unbounded model complexity in large fields.

### Red Sludge
Review:
- still/flow texture language;
- shore/bank transition;
- emissive use;
- depth/color readability under Shroud fog;
- visibility against nearby growths.

## Environmental hierarchy

### Far field
- fog/color mass;
- corrupted skyline silhouettes;
- occasional major growth/ruin landmark.

### Mid field
- core nest;
- thick vein routes;
- sludge pools;
- large arches/ribs;
- broken ritual architecture.

### Near field
- branching roots;
- small nodules;
- cracked material;
- local motes/spores;
- sludge edge detail.

## Ordinary vs Deadly composition

Ordinary:
- slower, softer organic clusters;
- more negative space;
- lower internal brightness;
- colder materials.

Deadly:
- denser core-adjacent mass;
- sharper protrusions;
- more exposed luminous interiors;
- heavier near-black crust;
- aggressive red/magenta energy;
- greater visual obstruction, still gameplay-readable.

## Purification art

Purification should not instantly make terrain look untouched unless canonical regression actually restores it that way.

Desired visual language where compatible with gameplay:
- Shroud emissive extinguishes;
- organic growth withers/collapses;
- residue desaturates;
- ash/scar remnants can remain briefly/permanently if canonical terrain state supports them;
- clean upward motes communicate release.

## Ruin set dressing

Use original Enshrouded-authored ruin composition. Mods such as Domum Ornamentum, Supplementaries, Create Deco and similar pack content are quality/reference benchmarks; do not require them merely to build every Enshrouded landmark unless a dependency is intentionally approved.

If Domum Ornamentum is used in a generated/manual structure, document the provider dependency and confirm the exact pack version before implementation.

## Tiling and repetition gate

Large corrupted regions must be screenshot-tested for:
- obvious repeating texture stamps;
- grid-aligned vein patterns;
- identical growth orientation;
- particle density stacking;
- ugly transitions at chunk boundaries;
- Sodium/Fusion rendering artifacts.

## Performance constraints

- no per-frame global terrain scan for decoration;
- no decorative chunk forcing;
- bounded source-local VFX;
- prefer baked/static models for non-animated environment pieces;
- use block entities only for assets that need runtime state or animation.

## Acceptance

A screenshot of Shroud terrain with HUD hidden and enhanced fog disabled must still look unmistakably like a corrupted Enshrouded region.
