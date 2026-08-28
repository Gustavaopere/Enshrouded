# ✅ Enshrouded Plan — Domain Contracts

**Milestone:** Level 1 required — completed.

**Goal:** define the stable Enshrouded-owned interfaces/value types shared by later subsystems.

## Completed contract surface

- `ShroudSeverity`, `ShroudSample`, `ShroudQuery`.
- `MutationAuthority` and `MutationKind` as the only terrain-safety boundary.
- `FlameWardQuery` with no-ward fallback; Sanctuary remains an effective overlay and never rewrites latent logical Shroud to `CLEAR`.
- `ProgressionOwner`, `ProgressionOwnerResolver`, `FlamePassageQuery` with standalone player-UUID / Passage-Level-1 fallbacks.
- `MagicDamageClassifier` and Enshrouded-owned magic classification/confidence values.
- `LichManifestationProvider` as entity provider only, never reward/progression authority.
- `EncounterContext` requires an explicit caller-supplied origin and snapshots it immutably; no implicit `BlockPos.ZERO` constructor exists.
- Public API shape and architecture guards prevent optional-mod/implementation leakage into Foundation APIs and client-only leakage into common/server code.

## TDD evidence

- [x] Stable ID/value round-trip and invariant tests.
- [x] Progression boundary RED `c714af1db5256537ea5e8a9f89c680f2c3e32d6a`; minimal implementations `66d4e5c...` / `d9e5edc...` made the same contract GREEN.
- [x] Flame ward RED `93127ff748029cd39fa33502eb704b10817308f0`; implementation `787d9c36c25459e8815066b8bd03fc20ff4285aa` made the same contract GREEN.
- [x] Explicit encounter-origin RED `3794568bbaebf105b070e08055e92715d30c1a3c`; production fix `5fb833e1b9e912819b0c1438a923dfa8ce7f0981` made it GREEN.
- [x] Final committed Gradle/JUnit suite GREEN on the final implementation HEAD.

## Cross-stage contracts

Foundation ownership is complete. Cross-stage consumption/implementation closures remain intentionally tracked in `plans/PENDING.md`, including Flame passage, Flame ward, owner snapshots, terrain/exposure coupling, Lich reward, magic classification and claim safety.

## Final acceptance evidence

- Branch: `round-1-foundation`
- Final implementation HEAD: `0b1940012628ff0d762961cccb480dc72989455d`
- PR: #2
- Workflow/job: `33165771852` / `98830694040`
- Unit tests: 33/33 GREEN.
- NeoForge build/JAR, GameTests and dedicated-server reload: GREEN.
- Merge SHA: `0b3c345673b81adbbc34a61505cb16200f689ba2`

**Acceptance:** satisfied. Later stages compile against one stable, provider-neutral Foundation surface.
