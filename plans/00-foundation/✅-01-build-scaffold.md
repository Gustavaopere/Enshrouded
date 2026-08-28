# ✅ Enshrouded Plan — Build Scaffold

**Milestone:** Level 1 required — completed.

**Goal:** bootstrap the actual mod project and CI on the pack-compatible NeoForge/Java baseline.

## Completed implementation

- Minecraft 1.21.1, NeoForge 21.1.248 and Java 21 are pinned.
- Mod id `enshrouded`, common bootstrap, registry seam and config namespace are present without client-only classloading in common code.
- Official Gradle 8.14 wrapper is committed; launcher/JAR provenance and distribution SHA-256 are enforced in CI.
- NeoForge dependency metadata uses the 1.21.1 `type="required"` schema.
- Repository license is BSD-2-Clause; `LICENSE` and `THIRD_PARTY_NOTICES.md` are packaged in the production JAR.
- CI verifies wrapper integrity, unit tests, diff sanity, NeoForge build, JAR contents, GameTests and dedicated-server save/reload.
- Dedicated server runs headless and supports FIFO-fed `save-all`/`stop` through explicit stdin forwarding.

## TDD / verification

- [x] Bootstrap smoke test was written before the main mod class/metadata existed.
- [x] RED observed in workflow `33000394152`: `BootstrapContractTest` failed because the main mod class did not exist.
- [x] Final `./gradlew test` GREEN on implementation HEAD `0b1940012628ff0d762961cccb480dc72989455d`.
- [x] Final NeoForge build and production-JAR sanity GREEN.
- [x] Final dedicated-server two-boot save/reload profile GREEN.

## Final acceptance evidence

- Branch: `round-1-foundation`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- PR: #2
- Workflow: `33165771852`
- Job: `98830694040`
- Result: GREEN for all committed Foundation gates.
- Merge SHA on `main`: `0b3c345673b81adbbc34a61505cb16200f689ba2`

**Acceptance:** satisfied. A clean checkout builds the NeoForge 1.21.1 artifact under Java 21 and executable CI proves server-safe bootstrap/reload.
