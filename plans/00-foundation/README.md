# 00 — Foundation

Foundation establishes the build, stable cross-stage contracts, upstream provenance and verification infrastructure required by every later Level 1 subsystem.

Completed tasks:

1. [`✅-01-build-scaffold.md`](✅-01-build-scaffold.md)
2. [`✅-02-domain-contracts.md`](✅-02-domain-contracts.md)
3. [`✅-03-upstream-provenance.md`](✅-03-upstream-provenance.md)
4. [`✅-04-test-infrastructure.md`](✅-04-test-infrastructure.md)

Foundation contract ownership includes `ProgressionOwnerResolver`, `FlamePassageQuery` and `FlameWardQuery`. Their standalone defaults let Stages 01–04 remain independent of later Flame/FTB implementation classes; Stage 05 supplies persistence/index-backed services and Stage 08 may substitute optional team/integration adapters.

Acceptance requires a clean committed-wrapper pipeline covering unit tests, diff sanity, NeoForge build/JAR checks, GameTests and dedicated-server **save → graceful stop → second boot → reload**. Implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d` passed that full pipeline in workflow `33165771852`, job `98830694040`; documentation HEAD `94843a022171108386148f48974d3167d6ae7801` passed again in workflow `33168410234`.

Reviewed causal rules remain binding for later stages: Stage 02 is safety-first, Stage 03 owns a Deadly-exposure policy seam before its Flame-gated implementation, Stage 05 implements the generic ritual engine before the physical altar, and Stage 06 alone owns the authentic Lich Skull/concrete ritual binding. `plans/PENDING.md` tracks the cross-stage halves that later branches still need to prove.

After this closing checkpoint is GREEN and merged, create `feat/01-shroud-state` from the resulting latest `main`.
