# Level 1 performance baseline — Stage 09.02

This document records the bounded-work contracts and the reproducible performance evidence used by Stage 09.02. It is a test-environment baseline, not a production TPS/MSPT guarantee.

## Evidence provenance

Primary measured run: GitHub Actions workflow `33997439941`, job `101390321868`, commit `59f51a85f821ce43367978b7b635a208aac7838a` on `feat/09-performance`. That exact head completed GREEN through unit tests, performance reports, NeoForge build, GameTests, SavedData two-boot reload, the isolated Ars Zero 2.0.2 profile and the dedicated-server two-boot smoke test.

Runner environment reported by that job:

- GitHub-hosted `ubuntu-24.04`, Ubuntu 24.04.4 LTS.
- Runner image version `20260831.293.1`.
- Eclipse Temurin Java `21.0.12+1`; Gradle reported JVM `21.0.12.1`.
- Linux `6.17.0-1022-azure amd64`.

CPU model and physical RAM were not exposed by the evidence used here and are intentionally not inferred.

## Authoritative work budgets

Level-1 gameplay remains owned by the existing runtime/configuration seams. Stage 09.02 adds observability and closes missing bounds; it does not create a second scheduler or a competing authority.

| Path | Default | Hard/config range | Scope |
| --- | ---: | ---: | --- |
| Logical expansion, global | 32 work units/tick | 1–512 | Per dimension scheduler tick, across active cores |
| Logical expansion, per core | 32 work units/tick | 1–512 | Per active core |
| Logical regression | 32 work units/tick | 1–512 | Runtime passes the same configured value as global and per-core regression caps |
| Purification visual cleanup | 64 attempts/tick | 1–512 | Per dimension restoration tick |
| Entity-corruption sampling | 256 updates/tick | 1–4096 | Global Minecraft server tick, across dimensions |
| Shroud sync | minimum 5 ticks between changed sends | fixed runtime constant | Per player |
| Client particle emission | 8 default, 0–16 per pulse | client config | Per client pulse |
| Client source sampling | max 192 positions/pulse | hard code cap | Per client pulse, every 4 client ticks |

The expansion global cap is separate from the per-core cap. Before Stage 09.02 the production runtime passed `ShroudWorkBudget(workPerTick, workPerTick)` with the same default of 32 for both fields, so the new `growthGlobalWorkPerTick` default preserves the previous total throughput rather than multiplying it by core count.

## Deterministic scheduler benchmark

`LevelOnePerformanceBenchmark` queues 64 frontier entries per core and runs one bounded scheduler tick with benchmark budgets of 256 global / 8 per core. These benchmark-specific budgets deliberately differ from production defaults so the 1/10/50-core matrix exercises both per-core and global saturation.

Measured on workflow `33997439941`:

| Active cores | Processed | Applied | Max/core | Global cap | Per-core cap | Wall time |
| ---: | ---: | ---: | ---: | ---: | ---: | ---: |
| 1 | 8 | 8 | 8 | 256 | 8 | 2.790 ms |
| 10 | 80 | 80 | 8 | 256 | 8 | 1.105 ms |
| 50 | 256 | 256 | 6 | 256 | 8 | 3.377 ms |

The 50-core scenario saturates the global cap at exactly 256 processed entries while remaining below the per-core cap. Increasing queued work therefore increases backlog latency rather than unbounded work in that scheduler tick.

An additional overload test exercises 64 active cores × 64 queued frontier entries at the same 256/8 expansion caps. Regression is separately stressed with 32 destroyed cores × 32 cells under 96 global / 6 per-core benchmark caps. These are logical scheduler stress fixtures; they do not alter production configuration.

The older frontier rejection micro-baseline in the same GREEN run recorded:

- 10,000 rejected entries: `11,101,002 ns` total (`1110.10 ns/entry`).
- 100,000 rejected entries: `51,338,520 ns` total (`513.39 ns/entry`).

These timings are observations, not pass/fail latency thresholds.

## Server-thread GameTest evidence

The Stage 09.02 server-thread marker ran the same 1/10/50 scheduler matrix inside the NeoForge GameTest server. The canonical invocation from workflow `33997439941` reported:

| Active cores | Processed | Applied | Max/core | Observed scheduler wall time |
| ---: | ---: | ---: | ---: | ---: |
| 1 | 8 | 8 | 8 | 0.175 ms |
| 10 | 80 | 80 | 8 | 0.252 ms |
| 50 | 256 | 256 | 6 | 0.626 ms |

Repeated invocations in that workflow remained approximately 0.189–0.199 ms for 1 core, 0.247–0.256 ms for 10 cores and 0.583–0.654 ms for 50 cores.

This is server-thread scheduler contribution measured in the test environment. It is **not** a full-world production MSPT/TPS measurement and must not be interpreted as one. The dedicated-server smoke and GameTest suite provide integration evidence, not a production load profile.

## Entity corruption

The pure `EntityCorruptionService` benchmark executed 10,000 unsafe-sample reducer updates and observed 10,000 state updates in `7,697,404 ns` on the cited runner.

Production work is intentionally different from the tight reducer benchmark:

- `EntityCorruptionRuntime` is event-driven per `LivingEntity`; it does not enumerate all world entities.
- Each entity receives at most one sampling opportunity per 20 entity ticks after warm-up.
- The sampling phase is derived deterministically from the entity UUID, distributing opportunities across the 20-tick window rather than aligning every entity on the same modulo tick.
- `EntityCorruptionTickBudget` admits at most `corruptedEcology.updatesPerTick` samples in one global Minecraft-server tick; default 256, configurable 1–4096.
- The cap is acquired before the authoritative `ShroudQuery`, so excess eligible entities do not perform the expensive query/update path in that tick.
- `advanceNow()` remains a deterministic test seam and intentionally bypasses runtime admission.

This means a high entity count increases time until a later sampling opportunity rather than allowing an unbounded corruption-query burst.

## Query and networking

`DefaultShroudQuery` reads the authoritative immutable-state spatial index; Stage 09.02 instrumentation counts local queries without adding a world/chunk/entity enumeration. Static hot-path guards reject accidental `getAllChunks`/`getChunks`/`getAllEntities`-style scans in the canonical query and entity runtime.

`ShroudPlayerSyncTracker` sends an initial snapshot, suppresses identical samples and rate-limits changed samples to `ShroudSyncRuntime.MIN_TICKS_BETWEEN_SENDS = 5`. At 20 server ticks/s, a continuously changing sample therefore has a derived upper bound of 4 changed payloads/s/player after the initial send. This is a protocol bound, not a measured network throughput claim.

`PerformanceCounters.clientPayloadsSent` increments only when a payload is actually produced.

## Materialization and restoration

`ShroudMaterializationService` is a bounded, invoke-on-demand service delivered by Stage 02. Its queue polling takes explicit global and per-chunk budgets, processes only returned jobs and checks that target chunks are loaded; it does not force chunk loading. Every candidate is revalidated through the canonical Shroud query and mutation authority before mutation.

Historical PR #16 registered the corruption-rule reload runtime but did not install a production per-tick owner that automatically calls `ShroudMaterializationService.tick`. Stage 09.02 therefore does **not** invent a second gameplay scheduler. Materialization counters describe actual service invocations, and the service-level bounded-work contract remains explicit at each caller.

Purification does have a production tick owner: `ShroudPurificationRuntime` runs bounded logical regression and loaded-world restoration. Regression uses the configured 32 default (1–512 hard range) for both global and per-core arguments; restoration uses cleanup default 64 (1–512) for both total/per-chunk arguments. Restoration queue capacity is 8192 and visual cleanup never blocks the authoritative PURIFIED transition.

## Client presentation

`ShroudParticleController` is client-only and pulses every 4 client ticks. Each pulse inspects at most 192 source positions and only reads positions whose chunks are already loaded. Emission is separately limited by client `particles.maxCount`: default 8, configurable 0–16 per pulse. Source distance defaults to 10 blocks and is clamped to 2–16 blocks.

The performance counters keep visited source samples and emitted particles as independent quantities because one sampled source can legitimately emit more than one particle. No server gameplay authority is derived from client particle/fog/audio work.

## Persistence and heap observation

The representative persistence fixture encoded 50 cores × 64 cells = 3,200 logical cells. On workflow `33997439941`:

- compressed NBT size: `10,645 bytes`;
- process heap observation before fixture write: `27,528,152 bytes`;
- process heap observation after fixture write: `30,149,592 bytes`.

The test enforces a deterministic compressed-size safety check below 5,000,000 bytes. The heap values are deliberately observational: `Runtime.totalMemory() - freeMemory()` includes GC/JVM noise and is not retained-heap profiling or a per-save allocation guarantee.

## Operational recommendations

1. Tune the existing config owners instead of adding parallel budget systems. Raising a hard cap increases worst-case work and must be re-profiled.
2. Treat scheduler wall times in this document as regression context only. Do not promote them to production MSPT/TPS guarantees.
3. Watch `PerformanceCounters` together: attempts versus applied mutations, local queries, entity samples/updates, payloads and client samples/emissions reveal backlog or avoidable work without changing authority.
4. Preserve loaded-chunk-only mutation/restoration behavior and the absence of global chunk/entity scans.
5. Under pathological backlog, prefer delayed visual spread/sampling over increasing per-tick work without new measurements.

## Stage 09.02 acceptance interpretation

The measured Level-1 workloads remain within explicit bounded scheduler/entity/client budgets in the synthetic and GameTest environments. Growth in core count, queued frontier work or eligible entity count cannot linearly increase the relevant bounded per-tick work path without first hitting its explicit cap. The evidence here intentionally separates deterministic cap adherence from non-portable timing observations.
