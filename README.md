# Enshrouded

Standalone NeoForge 1.21.1 implementation of an expanding magical Shroud, corruption systems, Flame progression and recurring Lich encounters.

Current development checkpoint: `00 — Foundation` is implemented on `round-1-foundation` / PR #2 but remains unaccepted until the final wrapper, unit, build/JAR, GameTest and two-boot dedicated-server reload gates execute GREEN.

Foundation contracts already include provider-neutral progression owner/passage reads and a no-ward `FlameWardQuery` fallback so Stage 01/02/03 do not depend on later Flame implementation classes. Sanctuary is an effective overlay (`ShroudSample.sanctuarySuppressed`) and never erases the underlying logical Shroud field.

Project license: BSD-2-Clause. See `LICENSE`. Third-party source provenance and future adaptation notices are tracked in `THIRD_PARTY_NOTICES.md`.
