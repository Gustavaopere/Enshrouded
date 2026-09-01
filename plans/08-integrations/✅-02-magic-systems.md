# Enshrouded Plan — Ars Nouveau and Iron's Magic Classification

**Milestone:** Level 1 required.

**Goal:** enrich magic-damage classification for the two installed magic ecosystems without duplicating their spell systems.

**Implemented types:** `ArsNouveauMagicAdapter`, `IronsSpellbooksMagicAdapter`, `CompositeMagicDamageClassifier`.

## Files

- `src/main/java/com/gustavaopere/enshrouded/integration/arsnouveau/ArsNouveauMagicAdapter.java`.
- `src/main/java/com/gustavaopere/enshrouded/integration/irons/IronsSpellbooksMagicAdapter.java`.
- `src/main/java/com/gustavaopere/enshrouded/combat/magic/CompositeMagicDamageClassifier.java`.
- Existing `MagicResistanceRuntime` composes the adapters through the single Stage 04 classification/reduction path.

## Dependencies

- 04 magic resistance core.

## Implemented contract

- Ars Nouveau 5.13.0 and Iron's Spells 3.16.3 publish their canonical spell/school damage types through NeoForge `#neoforge:is_magic`; Enshrouded's existing `enshrouded:magic` bridge remains the primary classification path.
- Optional adapters add only narrow exact-registry-ID fallback/evidence. No namespace-wide classification is allowed.
- Ars evidence is limited to `ars_nouveau:spell`, `ars_nouveau:frost`, `ars_nouveau:flare`, `ars_nouveau:crush` and `ars_nouveau:windshear`; `sourceberry_bush` and unrelated IDs remain non-evidence.
- Iron's evidence is limited to the nine school damage types `fire_magic`, `ice_magic`, `lightning_magic`, `holy_magic`, `ender_magic`, `blood_magic`, `evocation_magic`, `eldritch_magic` and `nature_magic`; effect/entity damage such as `heartstop`, `blood_cauldron`, `dragon_breath_pool`, `fire_field` and `poison_cloud` remains non-evidence.
- `DefaultMagicDamageClassifier` remains the standalone Stage 04 baseline. `CompositeMagicDamageClassifier` resolves baseline plus optional evidence into one Foundation-owned `MagicDamageClassification`.
- Explicit baseline `NON_MAGIC`/`enshrouded:magic_bypass` remains authoritative. Contradictory strongest evidence fails closed to `UNKNOWN`.
- Adapters and `CompositeMagicDamageClassifier` never mutate damage and register no event hook.
- `MagicResistanceRuntime` still owns the single `LivingDamageEvent.Pre` path and `MagicResistanceService` remains the only reducer, consuming exactly one final classification per event.
- Both adapters depend only on Minecraft/Enshrouded contracts and registry IDs, so startup remains valid when either optional magic mod is absent.

## TDD / verification

- [x] Integration tests prove all five current Ars spell damage IDs classify as magic and `sourceberry_bush`, fake Ars IDs and vanilla player attack do not gain adapter evidence.
- [x] Integration tests prove all nine current Iron's school damage IDs classify as magic while current non-school/effect IDs, fake Iron's IDs and vanilla player attack remain non-evidence.
- [x] Composite unit tests prove multiple adapter signals collapse into one classification, unknown evidence falls back to core and contradictory certain evidence fails closed.
- [x] Exactly-once reduction coverage proves the composed final classification is fed once to `MagicResistanceService`; adapters themselves never invoke resistance or damage mutation.
- [x] `DefaultMagicDamageClassifier` remains the standalone tag-only baseline from Stage 04.

## Verification provenance

- RED contract checkpoint: `deab1aa1e777a73b599b53c5391bce79c3d394ff`. The test contract deliberately referenced the absent adapters; GitHub Actions attempts were superseded/queued before executing, so this checkpoint is structural source-level RED evidence rather than a claimed completed RED workflow.
- Initial implementation checkpoint: `78acc220aea5cb3387c580a17b102756c76a2a17`.
- Contract-alignment refactor restored the Stage 04 baseline and introduced the planned explicit composite before final verification.
- Final implementation HEAD: `85ba070753031d6b8e41351b2cdc5055a34d47d7`.
- PR: #61 — `Stage 08.02: integrate Ars Nouveau and Iron's magic classification`.
- Final exact PR-head workflow/job: `33539127624` / `99960767541` — `completed/success` across wrapper provenance, unit tests, frontier benchmark, diff sanity, NeoForge build, production JAR verification, GameTest server, SavedData two-boot reload and dedicated-server save/reload smoke.
- Implementation merge SHA: `916ccf16c10fc521c89475f7cbd67e6efbe81751`.
- Independent post-merge `main` workflow/job: `33539723280` / `99962689287` — `completed/success` across the same complete gate set.
- `ENSH-L1-MAGIC-CLASSIFY-001` is closed by the combined Stage 04 core evidence and this Stage 08.02 adapter/composition evidence.

## Merge gate

- [x] All task-specific tests are GREEN on the final branch HEAD.
- [x] `./gradlew test` is GREEN through the exact-head CI gate.
- [x] NeoForge build, GameTests, SavedData two-boot reload and dedicated-server smoke are GREEN.
- [x] No unresolved task-local cross-stage contract is hidden; `ENSH-L1-MAGIC-CLASSIFY-001` is closed in `plans/PENDING.md`.
- [x] Implementation is merged into `main`; this file is renamed with `✅-` and `plans/STATUS.md` is updated in the closeout checkpoint.

**Acceptance:** Corrupted-mob magic resistance behaves correctly with the pack’s major magic mods, classification never becomes a second reducer, and standalone behavior remains valid when either integration is removed.
