# Level 1 Architecture Overview

The Level 1 dependency flow is:

`Shroud Core -> persistent logical field -> canonical ShroudQuery -> logical ShroudSample`

`Foundation FlameWardQuery -> ShroudSample.sanctuarySuppressed overlay -> effective player exposure/client presentation`

`logical field + MutationAuthority(ward + protection inputs) -> terrain materialization/purification`

`Foundation ProgressionOwnerResolver + FlamePassageQuery -> Deadly Shroud passage policy`

`Flame state -> Flame Altar -> persistence-backed passage query + indexed FlameWardService -> existing Foundation boundaries`

`Story state -> ManifestationDirector -> selected Lich provider -> encounter defeat -> Enshrouded Lich Skull -> Flame Altar ritual binding -> Level 1 checkpoint`

Key separation rules:

- The persistent logical Shroud field is authoritative. Sanctuary never rewrites or deletes logical intensity/severity; it only suppresses effective interaction while the ward is active.
- Terrain mutation has one gate: `MutationAuthority`. `ProtectedAreaService` and Flame ward state are inputs to that authority, not parallel mutation paths.
- Stage 01/02/03 consume Foundation-owned no-op/default ward/progression boundaries and do not depend on Stage 05 implementation classes.
- Stage 05 replaces those defaults with persistence/index-backed implementations without changing earlier consumers.
- World visuals, external bosses and client effects consume authoritative Enshrouded state; none of them are the source of truth.
