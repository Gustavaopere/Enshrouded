# 00 — Foundation

Foundation establishes the build, stable cross-stage contracts, upstream provenance and verification infrastructure required by every later Level 1 subsystem.

Tasks:

1. `01-build-scaffold.md`
2. `02-domain-contracts.md`
3. `03-upstream-provenance.md`
4. `04-test-infrastructure.md`

Foundation is not complete merely because the code is present. Acceptance requires a clean final-HEAD execution of the committed Gradle wrapper, unit tests, diff sanity, NeoForge build/JAR checks, GameTests and the dedicated-server **save → graceful stop → second boot → reload** harness.

Until all four tasks satisfy their merge gates, keep `00 Foundation` open, do not rename task files with `✅-`, do not merge PR #2 and do not create the first Stage 01 implementation branch.
