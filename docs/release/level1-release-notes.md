# Enshrouded 1.0.0 — Level 1 release notes

Enshrouded Level 1 targets **Minecraft 1.21.1**, **NeoForge 1.21.1 / 21.1.248** and **Java 21**.

## Level 1 scope

The release provides the complete first vertical slice of the Enshrouded system:

- persistent, bounded Shroud fields and core lifecycle;
- reversible terrain corruption under a central fail-closed mutation authority;
- server-authoritative exposure, Madness, Deadly/Red Shroud progression and Red Sludge;
- persistent corrupted ecology and a single canonical magic-resistance reducer;
- owner-scoped Flame progression, Flame Altar, Sanctuary/ward protection and Level 1 ritual progression;
- persistent recurring Lich story state, manifestation/provider boundaries and exactly-once Lich Skull/reward handling;
- synchronized HUD/fog/audio/particle/accessibility presentation without moving gameplay authority to the client;
- optional integrations for Ars Zero, Ars Nouveau, Iron's Spells, Epic Fight, FTB Teams, FTB Chunks, MineColonies and JourneyMap;
- intentional Level 1 no-op/flavour boundaries for Goety, Malum and Eidolon: Repraised;
- hardening for test coverage, performance budgets, world migration/recovery, provenance and release readiness.

## Compatibility baseline

The release pack profile is recorded in `docs/compat/current-pack-2026-09-06.md` from the user-supplied 607-entry modlist. Important current targets include Ars Nouveau 5.13.1, Ars Zero 2.0.2, Iron's Spells 3.16.3, Epic Fight 21.17.3.1, FTB Chunks 2101.1.22, FTB Teams 2101.1.11, JourneyMap 6.0.7, MineColonies 1.1.1376 and GeckoLib 4.9.2.

The complete 607-JAR pack is not stored in this repository. CI proves the standalone mod, persistence/restart behavior, a real isolated Ars Zero provider profile and dedicated-server save/reload. A literal full-pack boot remains an external pack-distribution smoke against the recorded profile and is required before distributing the surrounding modpack.

## Persistence and upgrades

Stage 09.03 defines the supported persisted-state migration contract. Supported v1 stores migrate deterministically to v2 while preserving relevant identity, progression and reward state. Unsupported/pre-versioned, malformed or unknown-future schema data fails closed rather than silently resetting world progression.

## Optional-provider authority

Optional mods supply bounded facts, APIs or presentation bodies. They do not take ownership of Enshrouded's Shroud, Exposure, Flame, Story/rewards or canonical mutation/protection state. Provider absence must leave the standalone core functional, and uncertain protection/authority decisions fail closed.

Spore and Infnexus are outside the supported Enshrouded integration architecture.

## Localization and configuration

Level 1 ships English (`en_us`) and Brazilian Portuguese (`pt_br`) language resources with enforced key parity. The current code does not export a NeoForge `ModConfigSpec` surface; gameplay defaults remain code/data-owned and regression-tested.

## License and provenance

Enshrouded is BSD-2-Clause. `LICENSE` and `THIRD_PARTY_NOTICES.md` are packaged in the production JAR. `provenance/third-party-provenance.json` is the machine-readable authority for compatibility/reference/reuse posture. Installed mods and inspected upstream projects do not imply permission to copy code or assets.

## Verification

The recorded Level 1 implementation checkpoint is `main@47189826fe03cb633d32fd8eb695f275f4aaa96f`.

Final implementation HEAD `4c983331d8d9b5310376254fd3e20fad27604fab` passed PR-head `Level 1 Release Readiness` workflow/job `34031623079 / 101482073832` and `Enshrouded CI` workflow/job `34031623105 / 101482073811`. PR #78 then merged as `47189826fe03cb633d32fd8eb695f275f4aaa96f`. Independent push validation on that exact `main` passed `Enshrouded CI` `34033865386 / 101488274386` and `Level 1 Release Readiness` `34033865468 / 101488268597`.

Stage 09 is 5/5 once the documentation closeout checkpoint is merged and independently GREEN. That closeout changes release documentation/status only; it does not add a new gameplay/runtime authority.
