# 00 — Foundation

Foundation establishes the build, stable cross-stage contracts, upstream provenance and verification infrastructure required by every later Level 1 subsystem.

## Completed tasks

1. [`✅-01-build-scaffold.md`](✅-01-build-scaffold.md)
2. [`✅-02-domain-contracts.md`](✅-02-domain-contracts.md)
3. [`✅-03-upstream-provenance.md`](✅-03-upstream-provenance.md)
4. [`✅-04-test-infrastructure.md`](✅-04-test-infrastructure.md)

## Acceptance

Foundation implementation branch `round-1-foundation` completed all committed gates GREEN on workflow `33165771852`, job `98830694040`, final implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d`.

Verified gates included wrapper integrity, 33/33 unit tests, diff sanity, NeoForge build, production-JAR sanity, real GameTests and a two-boot dedicated-server save/reload scenario.

PR #2 was merged to `main` as `0b3c345673b81adbbc34a61505cb16200f689ba2`.

Foundation owns the progression/passage/ward read boundaries required by later stages. Remaining cross-stage closure obligations stay in `plans/PENDING.md` and do not reopen Foundation ownership.

Next implementation branch: `feat/01-shroud-state`, created only from the latest `main` after this closeout checkpoint.
