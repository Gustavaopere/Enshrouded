# 08 — Optional Integrations

**Goal:** use current-pack mods where they add concrete value without allowing any of them to become core authorities.

## Task order

1. [`01 Ars Zero Lich`](✅-01-ars-zero.md)
2. [`02 Ars/Iron magic`](✅-02-magic-systems.md)
3. [`03 Epic Fight/claims/teams`](✅-03-combat-claims-teams.md)
4. [`04 JourneyMap`](✅-04-journeymap.md)
5. [`05 Goety/Malum/Eidolon flavor`](✅-05-necromancy-flavor.md)

## Runtime contracts

- All adapters are optional and isolated behind Enshrouded APIs.
- Missing/mismatched target mods fail closed with diagnostics and standalone behavior.
- No reflection loop or per-tick registry probing on hot paths.
- Stage 08.05 intentionally adds no adapter: Goety, Malum and Eidolon retain their native progression authorities because the reviewed Level-1 loop had no concrete gap that justified coupling.

**Stage 08 status:** 5/5 tasks reviewed, verified and merged.

Tasks were implemented and merged in the order above. Any future optional-provider expansion requires a new reviewed contract rather than reopening a completed Stage 08 task implicitly.