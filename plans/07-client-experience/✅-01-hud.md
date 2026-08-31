# Enshrouded Plan — Exposure HUD ✅

**Milestone:** Level 1 required.

**Status:** IMPLEMENTED, VERIFIED AND MERGED.

**Goal:** establish the shared client-side configuration/state seam and display remaining Shroud time, severity, Madness stage and passage warning accurately without moving gameplay authority to the client.

## Implemented surface

- `EnshroudedClientConfig` is the single shared Stage 07 client-config container.
- `ExposureHudModel` consumes only synchronized `ExposureSnapshot` state.
- `ShroudHudOverlay` renders Ordinary Shroud / Deadly Shroud semantics, remaining reserve, Madness stage and passage warning.
- HUD visibility, scale and anchor are presentation-only settings.
- Safe/CLEAR and Sanctuary-suppressed snapshots hide the overlay.
- Ordinary vs Deadly presentation is differentiated by text/icon semantics, not color alone.
- EN/pt-BR localization and the HUD icon texture are present.
- Client bootstrap is isolated behind a physical `Dist.CLIENT` entrypoint; common/server bootstrap does not import the HUD implementation.

## Authority / interpolation contract

The server remains the only gameplay authority for exposure. The renderer does not derive reserve depletion from wall-clock time. It presents the latest server-authored reserve until a newer synchronized snapshot arrives, so integrated pause, low TPS or stalls cannot make the HUD race ahead of authoritative exposure state.

Stale payload rejection remains owned by the existing client exposure state cache. The HUD duplicates neither Madness thresholds nor passage calculations; it displays the server-authored presentation state.

## Layout / accessibility contract

The passage-blocked warning occupies a dedicated row and uses `drawWordWrap` with an explicit `PANEL_WIDTH - 12` bound. This keeps localized warning text inside both left- and right-anchored viewports. Later Stage 07 tasks must extend or consume the same shared config rather than creating parallel client config containers.

## TDD evidence

- HUD structural RED: `06d139d44cc3b57fb21057238005ec08e06b5991` — test compilation failed because `ExposureHudModel` did not yet exist.
- Config RED: `ab0934251a5cd2028cef2035e7c6fbb2bc5f0294`, workflow `33350545365` / job `99362962397` — eight compile errors only because `EnshroudedClientConfig` did not yet exist.
- Client bootstrap RED: `bc55b9f64ddab102123a5b689cabda3484fdeeaa`, workflow `33350762986` / job `99363577813` — 221 tests ran with the single new boundary test failing because the physical client entrypoint did not yet exist.
- Review regression RED: `94232884940023450488d8c5184902604034214a`, workflow `33353179198` / job `99370277476` — 225 tests ran with exactly two failures covering wall-clock exposure prediction and right-edge passage-warning overflow.

## Review corrections

Automated review identified two valid P2 findings on the original PR head:

1. Wall-clock interpolation using `System.nanoTime()` could continue draining the displayed reserve during pause/low TPS.
2. A fixed `x + 82` passage-warning origin could truncate localized text on right anchors.

Both were corrected in final HEAD `c8b6b24685c51a4ebb006bfb0a86c6877487fa21`. The two review threads were answered and resolved after fresh GREEN verification.

## Verification / merge record

- Implementation branch: `feat/07-hud`.
- Final implementation HEAD: `c8b6b24685c51a4ebb006bfb0a86c6877487fa21`.
- Implementation PR: #50 — `Stage 07.01: Exposure HUD and shared client config`.
- Final exact PR-head workflow/job: `33353296420` / `99370594625` — GREEN.
- Exact gates: wrapper provenance, 225 unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, 74/74 GameTests, SavedData two-boot reload and dedicated-server save/reload smoke.
- Implementation merge SHA: `ffc5007cc66c74cd6ae8087293955b865dc79e90`.
- Post-merge `main` workflow: `33353628136` — GREEN on the implementation merge SHA.

An earlier candidate HEAD `31f6db0d23814c60ceafe1b2580ed245046af758` hit the pre-existing purification two-boot timing fixture once after 74/74 GameTests had passed. An unchanged-SHA rerun (`99367993774`) completed the full pipeline GREEN, confirming the event as an unrelated fixture flake; no product-code change was made for that failure.

## Completion gate

- [x] Shared client config registration/defaults and HUD presentation settings are covered.
- [x] HUD model formatting/visibility/Deadly warning behavior is covered.
- [x] Safe/Sanctuary suppression hides the overlay from authoritative snapshot state.
- [x] Newer synchronized snapshots remain authoritative; no client wall-clock gameplay prediction exists.
- [x] Dedicated-server verification proves the client implementation remains server-safe.
- [x] Review P2 regressions have dedicated RED -> GREEN evidence.
- [x] Final implementation PR merged with exact-head GREEN CI.
- [x] No new cross-stage pending contract was introduced.

**Acceptance:** Stage 07.01 is complete. The client now has one authoritative presentation-config seam, and the HUD communicates remaining Shroud reserve and passage danger without changing or predicting server gameplay state.
