# Architectural Decisions

These decisions are binding until deliberately changed in a reviewed commit.

1. **Platform:** Minecraft 1.21.1, NeoForge 21.1.248 target, Java 21.
2. **Authority:** Shroud state, exposure, Flame progression, terrain mutation, core destruction, boss encounters and rewards are server-authoritative.
3. **Standalone core:** Enshrouded must function without Ars Zero, Ars Nouveau, Iron's Spells, Goety, Epic Fight, FTB, JourneyMap, MineColonies, Spore or Infnexus.
4. **Spore removal:** `spore` and `infnexus` are not providers, references or required compatibility targets. The Enshrouded infestation must replace their role in the pack.
5. **Logical field first:** corruption is represented by a sparse persistent logical field. Visual block corruption is a bounded materialization of that field, not the source of truth.
6. **No forced chunk loads:** expansion may advance through coordinates for unloaded chunks but cannot load chunks just to spread or purify.
7. **Bounded Level 1 growth:** each Level 1 core has configurable maximum influence radius and per-tick work budgets. Future tiers may add secondary nodes, but Level 1 does not.
8. **Core ownership:** every corrupted cell has a deterministic owning core/region. Core destruction stops new expansion from that owner immediately.
9. **Purification:** core death causes gradual logical regression. Terrain restoration uses reversible mappings/growth cleanup; the mod does not persist full block snapshots for whole regions.
10. **Terrain safety:** mutations are data-driven and budgeted. Claims/sanctuaries can veto mutation. Unknown/modded blocks fail closed unless explicitly tagged/mapped.
11. **Severity:** `CLEAR`, `SHROUD` and `DEADLY` are canonical Level 1 severity classes. The model stores numeric intensity so later levels do not require a save-format rewrite.
12. **Exposure timer:** ordinary Shroud consumes a player exposure reserve. Safe air regenerates it. The client only displays server state.
13. **Madness death:** exhausted exposure culminates in custom Madness damage/death; visual hallucination is presentation, not authoritative gameplay logic.
14. **Deadly Shroud:** Red/Deadly Shroud requires a sufficient Flame Passage level. When underleveled it collapses survival time rapidly rather than acting as ordinary damage-over-time.
15. **Red Sludge:** Red Sludge is a concentrated Deadly-Shroud hazard and never provides a safe bypass around passage gating.
16. **Entity corruption:** corruption is a persistent attachment/state on living entities when possible; it is not implemented by replacing every mob with a cloned entity type.
17. **Passive aggression:** corrupted passive/neutral mobs may acquire temporary server-side hostile targeting behavior while preserving their original entity identity.
18. **Magic resistance:** magical resistance is classification-driven and configurable. Iron's/Ars adapters improve classification, but the standalone core has its own damage-source/tag contract.
19. **Flame scope:** progression is keyed through `ProgressionOwner`. Standalone default is player UUID; an optional FTB Teams adapter may resolve the owner to a team without changing core save semantics.
20. **Altar authority:** the Flame Altar is the ritual/sanctuary interface. Progression authority lives in persistent progression state, so breaking/moving an altar cannot duplicate or erase earned Flame levels.
21. **Sanctuary:** active Flame Altars create a bounded safe ward that suppresses effective Shroud exposure/materialization inside the ward and prevents new corruption jobs from mutating protected positions.
22. **Lich immortality:** killing a manifestation defeats a body, not the Lich's true existence. Story state records manifestation victory and leaves the phylactery unresolved.
23. **Boss abstraction:** story progression consumes an Enshrouded-owned `LichManifestationProvider`; external bosses never own progression state.
24. **Ars Zero:** `ars_zero:lich` is the preferred optional Level 1 provider when Ars Zero 2.0.2-compatible behavior is detected. It is accessed through registry/generic APIs where possible, not copied into core source.
25. **Native fallback:** a native Enshrouded manifestation provider must exist so standalone Level 1 remains playable when Ars Zero is absent.
26. **Boss reward:** the Level 1 Lich Skull is emitted by Enshrouded encounter state exactly once for a valid first-manifestation defeat; external loot remains external.
27. **Future levels:** save schemas carry version/tier fields now, but Level 2+ encounters, Flame costs, zones and bosses are not implemented in the Level 1 milestone.
28. **Sculk Horde provenance:** source-derived code may only come from the Apache-2.0 GitHub snapshot and must preserve applicable copyright/license notices. CurseForge binary licensing is not treated as permission to copy binary assets.
29. **Client rendering:** NeoForge-native HUD/fog/particle paths are primary. Transitive libraries such as Veil are not made mandatory merely because another installed mod bundles them.
30. **Testing:** deterministic logic uses JUnit; runtime/world interactions use GameTests/integration tests; bootstrap-sensitive tasks require dedicated-server smoke.
