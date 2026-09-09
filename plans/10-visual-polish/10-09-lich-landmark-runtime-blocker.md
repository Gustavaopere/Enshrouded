# Stage 10.09 — Lich Landmark Runtime Blocker

## Purpose

This checkpoint records why the Stage 10.09 Lich manifestation landmark cannot receive a production consumer yet without inventing gameplay behavior that is absent from the canonical runtime.

It is a documentation/audit checkpoint only. It does not add a trigger, encounter authority, worldgen, persistence, networking, provider behavior or presentation fallback.

## Reconciled baseline

Audited on 2026-09-09 against:

- repository `main`: `c4b555cb4f0d199bed2ebef80b7cd06b306913c0`;
- physical `modlist.txt`: **595 mods**;
- NeoForge: **21.1.248**;
- pertinent Enshrouded Notion dossier;
- Stage 10.09 plans and the current production source tree;
- historical Stage 06 first-manifestation implementation PR #46.

The physical modlist is the pack authority. Any surviving Stage 10 reference to **600 mods** is stale on that point and must not override the physical file; historical checkpoint counts remain historical evidence only.

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

A Stage 10 landmark consumer must call into this boundary only after a real gameplay trigger has been authoritatively established. The landmark must not reproduce any of those responsibilities.

### Runtime bridge

`ManifestationRuntime` currently:

- constructs/exposes the canonical service through `service()`;
- decorates the canonical Exposure Shroud query with the encounter arena overlay;
- listens for `LivingDeathEvent` at the existing defeat boundary;
- routes a valid defeat into the existing exactly-once reward service.

It does **not** initiate a manifestation.

### Landmark layout

`LichManifestationLandmarkLayout` is an immutable presentation/layout contract. It owns spatial composition only and exposes an encounter origin derived from the supplied landmark origin. It owns no Story state, encounter ID, provider spawn, reward, lifecycle or gameplay trigger.

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

## Historical 06.03 evidence

PR #46 (`06.03 — First Manifestation Encounter`) introduced the canonical encounter service/runtime and wired `ManifestationRuntime.register()` into the mod bootstrap.

Its first-manifestation GameTests invoke `ManifestationEncounterService.start(...)` directly to verify encounter semantics. The production portion of that implementation provides the explicit server-side **start boundary**, but the audited PR file changes do not provide a separate concrete gameplay trigger that decides *when* normal gameplay should invoke that boundary.

This distinction matters for Stage 10.09: an executable service API is not itself a player-facing/world-facing trigger.

## Blocker

**BLOCKED — CANONICAL GAMEPLAY TRIGGER NOT DEFINED IN RUNTIME**

The Lich landmark production consumer must not be implemented by silently choosing one of the following behaviors:

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

None of those trigger semantics is established by the audited `main` runtime or by the Stage 06/10 contracts reviewed for this checkpoint.

Inventing one here would change Story/gameplay design, multiplayer behavior and lifecycle semantics under a visual-polish task.

## Required contract before implementation

A future implementation may proceed only after the gameplay trigger contract explicitly defines at least:

1. the server-side event/action that is allowed to request the first manifestation;
2. the exact eligibility predicate, including prerequisite Story/Flame/Shroud state;
3. how the initiating player maps to the canonical `ProgressionOwner` in solo/team play;
4. one-shot/retry semantics when spawn fails or an encounter is already open;
5. whether the trigger is per player, per progression owner or world-global;
6. how the landmark origin is selected and whether it must already exist before the encounter starts;
7. chunk/loading rules, with no implicit chunk forcing or unbounded search;
8. claim/protection/ward behavior for any physical landmark placement;
9. persistence/restart behavior if the trigger has pending state;
10. logout, death, dimension change and multiplayer race behavior;
11. the causal ordering between landmark placement, `ManifestationEncounterService.start(...)`, arena activation and presentation;
12. negative/fail-closed behavior when a required provider or placement condition is unavailable.

Once that contract exists, the Stage 10 consumer must remain downstream of `ManifestationEncounterService.start(...)` and must not create a second Story/encounter authority.

## Current Stage 10.09 separation

- Flame 3×3 shell art: remains a user-authored/manual art handoff.
- Shroud Core Nest production consumer: **merged in PR #103 as `c4b555cb4f0d199bed2ebef80b7cd06b306913c0` after exact-head Release Readiness `34355655800` and Enshrouded CI `34355655793` completed GREEN**. The Ordinary consumer remains bounded, canonical-authority-gated and excludes Deadly/fluid materials.
- Lich landmark layout contract: implemented.
- Lich landmark production consumer: **blocked by the missing canonical gameplay trigger contract described above**.
- Stage 10.10: remains gated/not started.

This blocker must not be hidden by marking Stage 10.09 complete or by replacing the missing trigger with a generic visual/worldgen effect.
