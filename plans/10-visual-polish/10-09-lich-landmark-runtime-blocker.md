# Stage 10.09 — Lich Landmark Runtime Blocker

## Purpose

This checkpoint records the exact boundary that still blocks **encounter wiring** for the Stage 10.09 Lich manifestation landmark without inventing gameplay behavior that is absent from the canonical runtime.

It does **not** block a separately approved, bounded placement/worldgen-only consumer. The existing Stage 10.09 contract explicitly permits production placement/worldgen consumption independently from encounter-location consumption, and `LichManifestationLandmarkLayout` exposes its structural roles as presentation hints for such a future structure/worldgen consumer.

This is a documentation/audit checkpoint only. It does not add a trigger, encounter authority, worldgen, persistence, networking, provider behavior or presentation fallback.

## Reconciled baseline

The original audit was performed on 2026-09-09 against:

- repository `main`: `c4b555cb4f0d199bed2ebef80b7cd06b306913c0`;
- physical `modlist.txt`: **595 mods**;
- NeoForge: **21.1.248**;
- pertinent Enshrouded Notion dossier;
- Stage 10.09 plans and the current production source tree;
- historical Stage 06 first-manifestation implementation PR #46.

That original baseline is retained as audit provenance and is not rewritten as if the later merges already existed at audit time.

The physical modlist is the pack authority. The current attached file remains **595 mods** on NeoForge `21.1.248`; older 602/603/607/612 counts in Stage 10 records are historical checkpoint evidence and do not override the current physical file.

### Post-audit repository reconciliation

After this audit:

- PR #104 final HEAD `3b96578850aa23b5eae3df76aa95e5171398faf2` passed exact-head Level 1 Release Readiness `34369500109` and Enshrouded CI `34369500120`, then merged as `9e1b4a77d1d24e2c00ebaec5160458ffbe1184c3`; independent post-merge Release Readiness `34370682361` and Enshrouded CI `34370682102` passed on that exact `main`.
- PR #105 reconciled the canonical Stage 10 merged state after #103/#104. Exact PR HEAD `b471165c4d6155908e11998fea36b9c823901a5b` passed Release Readiness `34372629522` and Enshrouded CI `34372629554`, then squash-merged as `005b195fc058234be159e37018f57ebf77731f62`.
- Independent post-merge Release Readiness `34373476741` and Enshrouded CI `34373476952` both completed `success` on exact `main@005b195fc058234be159e37018f57ebf77731f62`.
- The physical modlist was rechecked again before this dossier reconciliation and remains **595 mods** on NeoForge `21.1.248`.
- The pertinent Notion dossier was rechecked. It confirms Stage 06 manifestation/Lich authority is canonical but does not define the missing concrete gameplay trigger or a Lich landmark placement/material/distribution contract.

No later merge in this lineage establishes a gameplay trigger for the first manifestation. Therefore the encounter-wiring blocker below remains active.

## Canonical authority that already exists

### Encounter start

`ManifestationEncounterService.start(ServerLevel, UUID, BlockPos)` is the canonical first-manifestation start boundary.

It already owns the encounter transition that must remain unique:

- resolves the canonical `ProgressionOwner` fail-closed;
- creates the encounter identity/context;
- asks the existing manifestation director/provider path to spawn the actor;
- persists the encounter/story transition;
- activates the bounded temporary arena rule;
- triggers manifestation presentation only after the authoritative start succeeds.

An **encounter-location/wiring consumer** must call into this boundary only after a real gameplay trigger has been authoritatively established. The landmark must not reproduce any of those responsibilities.

A **placement/worldgen-only consumer** does not need to call this service merely to materialize the landmark. It may only establish bounded physical presentation and an encounter origin that can later be supplied to the canonical service when gameplay independently decides to start the encounter.

### Runtime bridge

`ManifestationRuntime` currently:

- constructs/exposes the canonical service through `service()`;
- decorates the canonical Exposure Shroud query with the encounter arena overlay;
- listens for `LivingDeathEvent` at the existing defeat boundary;
- routes a valid defeat into the existing exactly-once reward service.

It does **not** initiate a manifestation.

### Landmark layout

`LichManifestationLandmarkLayout` is an immutable presentation/layout contract. It owns spatial composition only and exposes an encounter origin derived from the supplied landmark origin. It owns no Story state, encounter ID, provider spawn, reward, lifecycle or gameplay trigger.

Its structural roles are explicitly presentation hints for a future structure/worldgen consumer. Therefore physical landmark placement and encounter initiation are separate concerns: placement may be implemented without silently initiating Story/Lich gameplay.

## Production-caller audit

The production tree was inspected rather than relying on GitHub's code-search index, because the index returned `incomplete_results=true` for manifestation queries.

The audit established:

1. `Enshrouded.java` registers `ManifestationRuntime`; bootstrap registration is not a gameplay trigger.
2. `ManifestationRuntime` has no start listener or start call; its NeoForge listener is the defeat/death path.
3. `FirstManifestationDefinition` contains encounter tuning, not trigger conditions.
4. Flame altar/ritual execution (`FlameRitualExecutor`, `FlameAltarBlockEntity`, `FlameAltarService`, `FlameRitualRegistry`) does not start the first manifestation.
5. `LevelOneLichSkullRitual` is necessarily downstream of the first manifestation because it consumes the authentic skull rewarded by that encounter.
6. Shroud discovery (`ShroudDiscoveryRuntime`) observes canonical Shroud samples and Core lifecycle for owner-scoped marker synchronization; it does not start Story/Lich encounters.
7. The current `story` production tree contains boss, manifestation, reward, ritual and state code but no separate gameplay-trigger/event package.
8. The current `command` tree contains recovery/Core commands and no manifestation gameplay command.
9. The current `network` tree contains presentation/synchronization payloads and no manifestation-start request surface.
10. No concurrent open PR or branch matching a Lich landmark / first-manifestation trigger implementation was found before this checkpoint branch was created.

This audit establishes a missing **encounter trigger**, not a prohibition on bounded presentation-only landmark placement.

## Historical 06.03 evidence

PR #46 (`06.03 — First Manifestation Encounter`) introduced the canonical encounter service/runtime and wired `ManifestationRuntime.register()` into the mod bootstrap.

Its first-manifestation GameTests invoke `ManifestationEncounterService.start(...)` directly to verify encounter semantics. The production portion of that implementation provides the explicit server-side **start boundary**, but the audited PR file changes do not provide a separate concrete gameplay trigger that decides *when* normal gameplay should invoke that boundary.

This distinction matters for Stage 10.09: an executable service API is not itself a player-facing/world-facing trigger, and the absence of that trigger does not prevent a separate placement-only consumer from materializing the landmark.

## Blocker

**BLOCKED — ENCOUNTER WIRING / CANONICAL GAMEPLAY TRIGGER NOT DEFINED IN RUNTIME**

The Lich landmark must not be wired to start the first manifestation by silently choosing one of the following behaviors:

- proximity to the landmark;
- stepping into Shroud;
- discovery of a Shroud Core;
- Core destruction/purification;
- block interaction;
- item use;
- Flame ritual completion;
- player login/tick;
- command invocation;
- arbitrary worldgen-time encounter spawn.

None of those encounter-trigger semantics is established by the audited `main` runtime or by the Stage 06/10 contracts reviewed for this checkpoint.

Inventing one here would change Story/gameplay design, multiplayer behavior and lifecycle semantics under a visual-polish task.

This blocker does **not** prohibit a placement/worldgen-only consumer that remains presentation-only, bounded, protection-aware and independent from encounter start.

## Required contract before encounter wiring

Encounter-location/wiring implementation may proceed only after the gameplay trigger contract explicitly defines at least:

1. the server-side event/action that is allowed to request the first manifestation;
2. the exact eligibility predicate, including prerequisite Story/Flame/Shroud state;
3. how the initiating player maps to the canonical `ProgressionOwner` in solo/team play;
4. one-shot/retry semantics when spawn fails or an encounter is already open;
5. whether the trigger is per player, per progression owner or world-global;
6. how the landmark origin is selected and whether it must already exist before the encounter starts;
7. chunk/loading rules, with no implicit chunk forcing or unbounded search;
8. claim/protection/ward behavior for any physical mutations associated with the trigger;
9. persistence/restart behavior if the trigger has pending state;
10. logout, death, dimension change and multiplayer race behavior;
11. the causal ordering between landmark availability, `ManifestationEncounterService.start(...)`, arena activation and presentation;
12. negative/fail-closed behavior when a required provider or encounter condition is unavailable.

Once that contract exists, encounter wiring must remain downstream of `ManifestationEncounterService.start(...)` and must not create a second Story/encounter authority.

## Separate placement-only handoff

A production placement/worldgen-only consumer remains a valid Stage 10.09 path and is **not blocked by the missing encounter trigger**. It still requires its own explicit placement contract before implementation rather than silently inventing worldgen semantics. That contract must establish, at minimum:

- how/where landmark origins are selected and the bounded distribution/frequency policy;
- role-to-material/structure mapping for `RITUAL_DAIS`, `BROKEN_HALO`, `SPECTRAL_ANCHOR`, `PORTAL_FRAME` and `BONE_MOTIF`;
- loaded-chunk/no-force-load behavior;
- claim/protection/ward handling through the canonical mutation boundary for physical placement;
- deterministic/idempotent placement behavior and restart/chunk lifecycle expectations;
- no boss spawn, Story mutation, reward, arena activation or encounter start as a side effect of worldgen/placement.

That placement contract is a normal Stage 10.09 architecture handoff, distinct from the encounter-trigger blocker above.

## Current Stage 10.09 separation

- Flame 3×3 shell art: remains a user-authored/manual art handoff.
- Shroud Core Nest production consumer: **merged in PR #103 as `c4b555cb4f0d199bed2ebef80b7cd06b306913c0` after exact-head Release Readiness `34355655800` and Enshrouded CI `34355655793` completed GREEN**; independent post-merge Release Readiness `34368796815` and Enshrouded CI `34368796770` also passed. The Ordinary consumer remains bounded, canonical-authority-gated and excludes Deadly/fluid expansion from its current scope.
- Lich landmark layout contract: implemented.
- Lich landmark placement/worldgen-only consumer: **open and permitted**, but still requires an explicit placement/material/distribution contract; it is not blocked by the missing gameplay trigger.
- Lich landmark encounter-location/wiring consumer: **blocked by the missing canonical gameplay trigger contract described above**.
- Stage 10 merged-state reconciliation: PR #105 is merged as `005b195fc058234be159e37018f57ebf77731f62` and independently post-merge verified by Release Readiness `34373476741` plus Enshrouded CI `34373476952`.
- Stage 10.10: remains gated/not started.

This blocker must not be hidden by marking Stage 10.09 complete, and the missing encounter trigger must not be replaced with a generic visual/worldgen event. Conversely, the blocker must not be misread as forbidding a separately approved placement-only landmark consumer.
