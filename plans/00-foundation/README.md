# 00 — Foundation

Foundation establishes the build, stable cross-stage contracts, upstream provenance and verification infrastructure required by every later Level 1 subsystem.

Tasks:

1. `01-build-scaffold.md`
2. `02-domain-contracts.md`
3. `03-upstream-provenance.md`
4. `04-test-infrastructure.md`

Foundation contract ownership includes the progression read boundaries required before later stages diverge: `ProgressionOwnerResolver` and `FlamePassageQuery` belong to Task 02. Their standalone defaults must resolve a player to player-UUID ownership and report Passage Level 1. Stage 03 consumes those contracts; Stage 05 supplies the persistence-backed implementation; Stage 08 may substitute a team-aware resolver.

Foundation is not complete merely because most code is present. Acceptance requires:

- all Task 02 contracts/defaults implemented test-first, including `ENSH-L1-FLAME-PASSAGE-001`;
- a clean final-HEAD execution of the committed Gradle wrapper;
- unit tests and diff sanity;
- NeoForge build/JAR checks;
- GameTests;
- the dedicated-server **save → graceful stop → second boot → reload** harness.

The reviewed Stage 05/06 causal split is binding: Stage 05 owns only the generic ritual engine/checkpoint semantics, while Stage 06 owns the authentic first Lich Skull and concrete ritual binding. No Stage 05 -> Stage 06 reverse dependency is allowed.

Until all four Foundation tasks satisfy their merge gates, keep `00 Foundation` open, do not rename task files with `✅-`, do not merge PR #2 and do not create the first Stage 01 implementation branch.
