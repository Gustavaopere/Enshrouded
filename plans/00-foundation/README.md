# 00 — Foundation

**Goal:** create a clean NeoForge 1.21.1/Java 21 project with CI, explicit domain contracts, provenance and test infrastructure.

## Task order

1. [`01 build scaffold`](01-build-scaffold.md)
2. [`02 domain contracts`](02-domain-contracts.md)
3. [`03 upstream provenance`](03-upstream-provenance.md)
4. [`04 test infrastructure`](04-test-infrastructure.md)

## Runtime contracts

- No optional mod is a mandatory Gradle/runtime dependency of the core artifact.
- Every later subsystem depends on stable interfaces established here rather than directly reaching into other subsystems.
- Build/test/CI must be reproducible from a clean checkout.

Tasks are implemented and merged in the order above unless `STATUS.md` explicitly records a reviewed dependency change.
