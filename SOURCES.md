# Source provenance

This file indexes third-party projects used by Enshrouded as architectural references, gameplay inspiration, runtime providers or compatibility targets. The machine-readable authority is [`provenance/third-party-provenance.json`](provenance/third-party-provenance.json).

**A source link, installed mod or observed behavior is not a license grant.** No source/assets may be copied or adapted unless the ledger contains an approved material entry with exact local-file mapping and immutable upstream provenance.

## Stage 09 release-candidate baseline

- verified repository baseline entering Stage 09.04: `main@24b7df890777eccca7148b27b4e5dc0a028abc34`;
- pack authority: user-supplied 607-entry `modlist.txt` snapshot reconciled 2026-09-06;
- current compatibility profile: [`docs/compat/current-pack-2026-09-06.md`](docs/compat/current-pack-2026-09-06.md);
- provenance audit: [`docs/compat/third-party-provenance-audit-2026-09-05.md`](docs/compat/third-party-provenance-audit-2026-09-05.md);
- current copied/derived/vendored third-party production material: **none**.

## Current direct integration targets

| Target | Current evidence | Posture |
| --- | --- | --- |
| Ars Nouveau | `5.13.1` | independent API/compatibility code; no assets/source copied |
| Ars Zero | `2.0.2`; audited source `9478291a9f331ee2b4a391c4581a342d342ac7dc` | provider/API only; no GPL implementation copied |
| Epic Fight | `21.17.3.1` | API/compatibility only |
| FTB Chunks | `2101.1.22` | API/claims facts only; upstream source is visible-source/ARR |
| FTB Teams | `2101.1.11` | API/team facts only; ARR |
| Iron's Spells 'n Spellbooks | `3.16.3` | addon/API use under upstream terms; no source/assets copied |
| JourneyMap | runtime `6.0.7`; API `2.0.0-1.21.1` | compile-only API, client presentation only |
| MineColonies | `1.1.1376-1.21.1-snapshot` | API/protected-area facts only |

## Mandatory inspected/runtime/reference set

The ledger also records GeckoLib `4.9.2`, Goety `3.1.4`, Malum `1.8.2`, Eidolon: Repraised `0.5.0.2`, Sculk Horde, The Forest public alpha, Minecraft Dungeons and the commercial Enshrouded game. These entries do not declare copied material.

## Explicit exclusions

- Spore
- Infnexus

Their presence in the development pack does not make either project an Enshrouded provider or source. Production references are rejected by the provenance gate.

## Material reuse convention

When actual source-level material is incorporated, the local source carries:

```text
// UPSTREAM-DERIVED: provenance-id
```

and the same provenance id must be a `copied`, `derived` or `vendored` ledger entry mapping that exact local file. Restricted, unknown or unresolved material is not mergeable/releasable.
