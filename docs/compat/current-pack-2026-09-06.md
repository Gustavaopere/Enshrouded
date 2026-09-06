# Current pack compatibility profile — 2026-09-06

## Authority and scope

This snapshot is the Stage 09.04 release-candidate compatibility baseline for the user-supplied NeoForge 1.21.1 `modlist.txt`, reported at **607 mods**. It records the installed versions relevant to Enshrouded integration and release review.

This is a static installed-version/profile record, not evidence that GitHub Actions boots all 607 third-party JARs. The repository does not vendor the complete pack distribution. Runtime evidence is supplied by Enshrouded's canonical GameTests, SavedData two-boot restart profile, isolated real Ars Zero distribution profile and dedicated-server save/reload smoke. A literal complete-pack boot remains an external/manual pack-distribution check.

## Verified current targets

| Target | Current pack version | Enshrouded posture |
| --- | --- | --- |
| Ars Nouveau | 5.13.1 | optional magic classification/provider boundary |
| Ars Zero | 2.0.2 | optional preferred Lich body; Enshrouded keeps story/reward authority |
| Iron's Spells | 3.16.3 | optional magic classification boundary |
| Epic Fight | 21.17.3.1 | optional combat compatibility |
| Goety | 3.1.4 | reviewed Level 1 no-op/flavour boundary |
| Malum | 1.8.2 | reviewed Level 1 no-op/flavour boundary |
| Eidolon: Repraised | 0.5.0.2 | reviewed Level 1 no-op/flavour boundary |
| FTB Chunks | 2101.1.22 | optional protected-area facts |
| FTB Teams | 2101.1.11 | optional owner/team facts |
| JourneyMap | 6.0.7 | optional client presentation only |
| MineColonies | 1.1.1376 | optional protected-area facts |
| GeckoLib | 4.9.2 | audited compatibility/reference target; no copied assets/source |

Spore/Infnexus: unsupported integration

Spore and Infnexus may exist in the surrounding development pack, but they are explicitly outside Enshrouded's provider/source architecture. Their presence is not an integration contract and does not authorize source or asset reuse.

## Release interpretation

- Installed compatibility does not grant source-reuse permission; `provenance/third-party-provenance.json` remains the machine-readable authority.
- Optional provider absence must not disable the standalone Enshrouded core.
- Provider/API uncertainty fails closed where authority or protection is involved.
- The complete 607-mod distribution is not checked into this repository, so no CI result may be described as a full-pack 607-JAR boot.
- The external/manual complete-pack smoke must use the same 2026-09-06 modlist baseline before public pack distribution.
