# Third-Party Notices and Source Provenance

Enshrouded is implemented as a standalone NeoForge 1.21.1 mod. The current native Enshrouded source is original unless a source file explicitly carries an `UPSTREAM-DERIVED` marker described below.

This file is the canonical attribution gate for source-derived implementation. A contributor must not add adapted third-party source without first adding the corresponding upstream identifier, snapshot and license here.

## Source-derived marker convention

A Java source line of the form:

```text
// UPSTREAM-DERIVED: source-id
```

means that the marked implementation contains adapted source-level material from the named upstream. `source-id` must appear as a backticked identifier in this file. Merely implementing the same gameplay idea from a clean-room design does not require the marker.

No production Java source in the Foundation checkpoint is marked source-derived.

## Sculk Horde

- Provenance id: `sculk-horde-github-491aaa7e`
- Repository: `TeamPeril/Sculk-Horde`
- Audited source snapshot: `491aaa7e3c8c01eff0a7859ccfe2a62500ed15bc`
- Audited repository license file at that snapshot: **Apache License 2.0**.
- Relevant reference concepts: persistent infestation state, bounded chunk/frontier processing and Gravemind/node orchestration.
- Compatibility note: the audited project targets Forge 1.20.1-era APIs, so its platform code is not a drop-in implementation for NeoForge 1.21.1.

Policy: Enshrouded may adapt isolated source from this pinned Apache-2.0 snapshot only when doing so is preferable to a simpler native implementation. Any such adaptation must carry `// UPSTREAM-DERIVED: sculk-horde-github-491aaa7e` and preserve attribution/license obligations. No Sculk Horde source has been incorporated as of the Foundation checkpoint.

## Ars Zero

- Provenance id: `ars-zero-runtime-2.0.2`
- Current pack version: **2.0.2** for Minecraft 1.21.1.
- License of the current project/release audited for this integration: **GPLv3**.
- Relevant runtime entity: `ars_zero:lich`.
- Relevant observed behavior: the Lich implementation supports flight, blink-style movement, regeneration and multiple spell behaviours.

Policy: Ars Zero is an optional runtime/provider integration. Enshrouded does **not** copy the GPL Lich implementation into its core artifact. Story ownership, rewards and progression remain native Enshrouded responsibilities even when an Ars Zero entity is selected as the encounter body.

## Optional-mod linking policy

Optional mod names and versions may appear in compatibility inventories and adapter code, but Foundation core contracts must not import their classes. Adapters are introduced only in their integration stage and must fail safely when the provider is absent or changes.

Transitive/JarJar implementation libraries exposed by another mod, including presentation libraries such as Veil when only transitively present, are not promoted to mandatory Enshrouded dependencies without a separate reviewed decision.
