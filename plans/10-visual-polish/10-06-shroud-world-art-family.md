# Stage 10.06 — Shroud World-Art Family

Status: IMPLEMENTATION PRESENT / AWAITING FINAL PR VALIDATION

## Purpose

Stage 10.06 replaces the remaining placeholder environmental presentation with a bounded first-party Shroud material family while preserving the existing gameplay model.

The scope is presentation only. **Stage 02 remains the sole terrain-mutation authority.** This checkpoint adds no SavedData, packet, gameplay registry, spread owner, chunk scan, chunk forcing, exposure rule, severity rule, or purification lifecycle.

## Canonical authority boundary

- `ShroudMaterializationService`, `GrowthPlacementService`, `MutationAuthority`, and the canonical Shroud query continue to decide whether terrain mutation/growth exists.
- There is **no parallel visual spread** and no visual-only world state.
- Vanilla weighted blockstate selection supplies baked positional variation without persistence or synchronization.
- `withered_growth is Deadly/Red` ecology presentation. It is **not purification** and must not be documented or rendered as a purified residue.
- Purification/regression remains the existing canonical restoration/cleanup path. This pass adds **no persistent purified residue** block or provider.

## Material families

### Ordinary

- `shroud_growth`: three bounded authored geometry variants.
- `shroud_vein`: three bounded surface/root variants.
- `shroud_membrane_ordinary`: membrane material used inside authored geometry.
- `shroud_crust_ordinary`: mineralized/organic crust material used as supporting mass.

### Deadly / Red

- `withered_growth`: three authored Deadly variants with a more angular/open silhouette than Ordinary growth.
- `shroud_membrane_deadly`: hotter/torn membrane material.
- `shroud_crust_deadly`: denser red-black crust material.
- Red Sludge still/flow textures are upgraded to the same Deadly family and remain owned by the existing `RedSludgeFluidType`.

Ordinary and Deadly are differentiated by silhouette/material composition as well as palette; the design does not rely on color alone.

## Variant and performance budget

Each environmental block uses exactly three weighted baked variants. No runtime randomizer, model entity, block entity, ticker, neighbor scan, network payload, or world scan is added.

Stage 10.06 texture budget is 32×32 per world-art material in this pass, with the CI contract rejecting unexpected sizes above 64×64. The new models are static baked JSON geometry and remain compatible with the existing bounded terrain placement pipeline.

## Fusion boundary

Fusion is optional. The complete baseline is a vanilla weighted blockstate + vanilla baked model path. No Fusion-only resource is required for correct presentation, and absence/failure of Fusion cannot remove a gameplay fact or make the block disappear.

A later optional Fusion enhancement may improve connected/continuous surfaces only after its exact 1.21.1 / 1.3.15 resource contract is independently proven. It must retain the same vanilla fallback and cannot become Shroud authority.

## TDD evidence

RED checkpoint:
- branch HEAD `af8f6140491e3304f1f4b60bf2a40f7affb51b29`;
- Enshrouded CI `34077847446 / 101607351530` failed at `Stage 10 visual stack contract tests` after provenance passed;
- Level 1 Release Readiness `34077847501 / 101607351673` failed at `Stage 10 presentation contract tests`.

The RED state intentionally contained the contract test before the required world-art resources.

## Manual visual gates

`ART APPROVED remains open`.

The following evidence is still **pending** and cannot be promoted by CI:
- in-game screenshots of dense Ordinary growth/vein surfaces at realistic FOV and distance;
- in-game screenshots of Deadly `withered_growth` plus Red Sludge;
- seam/tile/checkerboard inspection on large visible surfaces;
- reduced-effects readability check;
- Sodium/Fusion coexistence visual check using the current pack versions;
- full 607-mod client visual smoke.

## Exit condition

Technical completion requires the Stage 10 world-art contract, release-readiness workflow, full Enshrouded CI, NeoForge build, canonical GameTests, two-boot SavedData reload, real Ars Zero 2.0.2 isolated profile, and dedicated-server save/reload smoke to be green on the exact PR HEAD and again on the merged `main`.

Manual art gates remain separate and must stay `REVIEW_IN_GAME` until real captures exist.
