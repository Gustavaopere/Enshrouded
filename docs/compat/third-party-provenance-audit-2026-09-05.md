# Stage 09.05 — Retroactive third-party provenance audit

Audit date: 2026-09-05 (America/Sao_Paulo)
Repository baseline reviewed: `main@cd6850abdabc016136368fd2b6a9decfa6e1df51`
Pack authority used for presence/version: attached `modlist.txt`, 607 top-level entries.

## Result

The retroactive tree audit found **no registered copied, derived or vendored third-party source/assets/audio/binaries in the Enshrouded production tree**. The release posture remains clean-room/API integration plus reference-only design inspiration.

`provenance/third-party-provenance.json` is now the machine-readable source of truth. `SOURCES.md` is the human index and `THIRD_PARTY_NOTICES.md` is the notice/release-facing ledger. A future material reuse must be declared in all applicable places and must pass the automated gate.

## Repository checks performed

- inspected the production tree for `UPSTREAM-DERIVED:` markers: none were present before this gate;
- searched production code for `spore` and `infnexus`: no provider/runtime dependency was present;
- inspected distributable resources for third-party binary/audio/image material: no unregistered material was identified;
- confirmed the build already packages repository `LICENSE` and `THIRD_PARTY_NOTICES.md` in the production JAR;
- reconciled direct production integration directories against provenance decisions: Ars Nouveau, Ars Zero, Epic Fight, FTB Chunks, FTB Teams, Iron's Spellbooks, JourneyMap and MineColonies;
- retained Sculk Horde as inspected architectural reference only;
- retained The Forest public alpha, Minecraft Dungeons and Enshrouded as proprietary behavior/design references only;
- recorded GeckoLib as runtime-provided library and Goety/Malum/Eidolon as inspected compatibility/reference targets.

## Current-pack drift reconciled for this stage

The 607-entry pack snapshot supersedes the older 573-entry guide snapshot for installed presence/version evidence. Relevant current rows include:

- FTB Chunks `2101.1.22`;
- FTB Teams `2101.1.11`;
- JourneyMap `6.0.7`;
- MineColonies `1.1.1376-1.21.1-snapshot`;
- Ars Nouveau `5.13.1`;
- Ars Zero `2.0.2`;
- Iron's Spells 'n Spellbooks `3.16.3`;
- Epic Fight `21.17.3.1`;
- GeckoLib `4.9.2`;
- Goety `3.1.4`;
- Malum `1.8.2`;
- Eidolon: Repraised `0.5.0.2`.

This stage does not rewrite the full modpack guides; it records the evidence required for Enshrouded release provenance.

## License posture

- `api_only`, `runtime_provided`, `reference_only` and `inspected_only` entries do **not** claim redistribution rights and therefore may record restrictive/proprietary/copyright statuses while their `files` mapping remains empty.
- `copied`, `derived` and `vendored` entries are material. They require author, source URL, immutable ref, explicit local file mapping and `license.status = approved`.
- Material with `restricted`, `unknown`, `REVIEW_REQUIRED` or `PERMISSION_REQUIRED` status fails closed.
- All Rights Reserved/custom-license providers are used only through allowed dependency/API behavior; their source/assets are not imported.
- Separate asset restrictions (notably Ars Nouveau, Iron's Spellbooks and MineColonies) remain binding even when code/API interaction is allowed.

## Automated enforcement

`scripts/ci/verify_third_party_provenance.py` and its contract tests enforce:

1. mandatory provenance sources and explicit Spore/Infnexus exclusion;
2. immutable refs and license/source metadata;
3. fail-closed material licensing;
4. exact local-file mapping for material;
5. no unregistered distributable binary/audio/image resources;
6. `UPSTREAM-DERIVED:` marker consistency;
7. provenance decisions for each dedicated production integration directory;
8. no production references to excluded provider IDs.

The provenance contract test runs before Gradle build acceptance in CI.

## Future contribution rule

A new provider/adaptor/compatibility path requires a provenance entry in the same change. A new copied/derived/vendored file requires immutable upstream provenance, compatible rights and local mapping before it may merge. When rights are unresolved, use `REVIEW_REQUIRED` or `PERMISSION_REQUIRED` and do not add the material.
