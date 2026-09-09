# Stage 10.09 — Open handoffs tracker

Status: **OPEN — DESIGN / USER-ART GATES ONLY; NO RUNTIME AUTHORITY CHANGE**

Verified baseline for this tracker: `main@e187899d853ef17ef399e982b6465a1d488d356b`.

Current physical-pack authority rechecked on 2026-09-09: **595 mods** on NeoForge `21.1.248`.

This file does not approve a placement algorithm, material palette, encounter trigger, or art package. It only makes the remaining Stage 10.09 handoffs independently trackable so Stage 10.10 cannot silently absorb them.

## Open gate A — Lich landmark placement-only contract

Tracking issue: **#108 — `Stage 10.09 — Define Lich landmark placement-only contract`**.

Already proven by current `main`:

- `LichManifestationLandmarkLayout.levelOne()` is immutable/data-only;
- it contains exactly one `ENCOUNTER_ORIGIN` and the narrative roles `RITUAL_DAIS`, `BROKEN_HALO`, `SPECTRAL_ANCHOR`, `PORTAL_FRAME`, and `BONE_MOTIF`;
- its maximum horizontal composition radius is 5 and authored vertical offsets remain within y `0..3`;
- the layout owns no Story state, encounter ID, boss lifecycle, provider selection, reward, world access, or chunk loading;
- `plans/10-visual-polish/06-multiblock-structures.md` approves the landmark as a narrative Lich Shrine / manifestation set piece that stages the encounter but does not own boss lifecycle;
- no production placement/worldgen consumer exists for the Lich layout on this baseline;
- `ModBlocks` has no dedicated Lich structural block family, so role-to-material mapping is not recoverable from an existing first-party Lich registry;
- the physical pack contains Domum Ornamentum `1.0.236-snapshot` (`domum_ornamentum`), but Stage 10 treats it only as a candidate structural/decor provider and explicitly forbids turning installation into an implicit dependency.

Still requires an explicit decision before implementation:

- origin source and eligible terrain/dimension context;
- distribution/frequency/spacing and deterministic seed policy;
- role-to-material/structure mapping;
- surface/height projection;
- protection/mutation boundary;
- loaded-chunk/no-force-load behavior;
- idempotence / duplicate prevention;
- partial placement, restart and chunk unload/reload lifecycle;
- any recovery/removal contract;
- fail-closed behavior for protected/incompatible/indeterminate targets.

Important separation: `FirstManifestationDefinition.levelOne()` defines an **encounter** arena radius of 12 and intensity `0.65`. `LichArenaRule` applies that as an ephemeral, dimension-local Shroud overlay only after a canonical encounter starts. Those values do not implicitly define worldgen clearance or landmark spacing.

## Open gate B — first-manifestation gameplay trigger

Tracking issue: **#109 — `Stage 10.09 — Define first-manifestation gameplay trigger`**.

Already proven by current `main`:

- `ManifestationEncounterService.start(ServerLevel, UUID, BlockPos)` is the canonical explicit server-side encounter-start mutation boundary;
- it resolves the canonical `ProgressionOwner` fail-closed;
- it creates the encounter context and delegates spawn to the canonical `ManifestationDirector`;
- it persists Story encounter + active actor binding before presentation;
- optional arena activation uses the same caller-supplied origin;
- failed Story/arena activation discards the spawned actor and fails safely;
- defeat later accepts only the exact dead actor bound to an ACTIVE persisted encounter;
- current production bootstrap/runtime still does not define which normal-gameplay cause supplies the `playerId` + `origin` to `start(...)`.

Still requires an explicit decision before encounter wiring:

- initiating gameplay action/event;
- eligibility/progression preconditions;
- origin resolution;
- causal ordering at the caller boundary;
- deduplication/replay behavior;
- multiplayer owner/party behavior;
- fake-player/non-player handling where applicable;
- cancellation/failure semantics;
- reconnect, death, logout, dimension change, restart and orphan handling;
- relationship, if any, to the separately approved placement-only landmark.

Do not substitute proximity, login/tick, generic Shroud/Core presence, worldgen-time activation, command-only gameplay, item use, interaction, or Flame ritual semantics without an approved contract.

## Open gate C — Flame shell render package and manual approval

Tracking issue: **#110 — `Stage 10.09 — Supply and approve Flame shell render assets`**.

The authoritative 3×3 formation lifecycle, recipes/self-drop acquisition, restart recovery, Sanctuary gating and controller boundaries are already implemented and verified. Remaining work is the user-authored presentation handoff defined in `10-09-flame-shell-art-handoff.md`:

- brace/rune blockstates;
- block models;
- item models;
- textures/material package;
- visibly distinct `FORMED=false` / `FORMED=true` presentation;
- coherent formed 3×3 silhouette rather than a visible cube-grid/checkerboard;
- in-game screenshot review and current full 595-mod client visual smoke;
- explicit user `ART APPROVED`.

Automated CI, generated placeholders, compile success, or inferred acceptance must never set `ART APPROVED`.

## Stage boundary

Stage 10.09 remains operationally **OPEN** until gates A–C are satisfied or the user explicitly re-scopes them.

Stage 10.10 remains **NOT STARTED / GATED**. It is final visual/compatibility acceptance, not a bucket for unresolved 10.09 architecture or manual art work.
