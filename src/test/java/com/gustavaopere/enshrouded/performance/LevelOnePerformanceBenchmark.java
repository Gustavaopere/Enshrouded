package com.gustavaopere.enshrouded.performance;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionService;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudExpansionScheduler;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudFrontierEntry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudPropagationPolicy;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudWorkBudget;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudStateCodec;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/** Test-only deterministic workload generator for the Stage 09.02 evidence report. */
final class LevelOnePerformanceBenchmark {
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);
    private static final int ENTRIES_PER_CORE = 64;
    private static final int GLOBAL_BUDGET = 256;
    private static final int PER_CORE_BUDGET = 8;
    private static final int ENTITY_SAMPLES = 10_000;
    private static final int PERSISTENCE_CORES = 50;
    private static final int PERSISTENCE_CELLS_PER_CORE = 64;

    private static final ShroudPropagationPolicy APPLY_NO_NEIGHBORS = new ShroudPropagationPolicy() {
        @Override
        public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
            return 1.0D;
        }

        @Override
        public List<ShroudCellPos> neighbors(ShroudCellPos source) {
            return List.of();
        }
    };

    private LevelOnePerformanceBenchmark() {
    }

    static Report run() throws IOException {
        List<CoreScenario> coreScenarios = List.of(
                runCoreScenario(1),
                runCoreScenario(10),
                runCoreScenario(50));
        EntityScenario entityScenario = runEntityScenario();
        PersistenceScenario persistenceScenario = runPersistenceScenario();
        Report report = new Report(coreScenarios, entityScenario, persistenceScenario);
        writeEvidence(report);
        return report;
    }

    private static CoreScenario runCoreScenario(int cores) {
        ShroudWorldState state = fixture(cores, 0, CoreLifecycleState.ACTIVE);
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(
                GEOMETRY, APPLY_NO_NEIGHBORS, ENTRIES_PER_CORE + 8);
        for (int coreIndex = 0; coreIndex < cores; coreIndex++) {
            UUID coreId = coreId(coreIndex);
            for (int entry = 0; entry < ENTRIES_PER_CORE; entry++) {
                boolean accepted = scheduler.enqueue(
                        coreId,
                        new ShroudFrontierEntry(
                                new ShroudCellPos(coreIndex * 1000 + entry, 0, 0),
                                0L,
                                entry));
                if (!accepted) {
                    throw new AssertionError("benchmark frontier unexpectedly rejected deterministic entry");
                }
            }
        }

        long started = System.nanoTime();
        ShroudExpansionScheduler.TickResult result = scheduler.tick(
                state,
                new ShroudWorkBudget(GLOBAL_BUDGET, PER_CORE_BUDGET));
        long elapsed = System.nanoTime() - started;
        int maxPerCore = result.processedPerCore().values().stream().mapToInt(Integer::intValue).max().orElse(0);
        return new CoreScenario(
                cores,
                result.processedEntries(),
                result.appliedCells(),
                maxPerCore,
                GLOBAL_BUDGET,
                PER_CORE_BUDGET,
                elapsed);
    }

    private static EntityScenario runEntityScenario() {
        EntityCorruptionService service = new EntityCorruptionService(1.0F / 1200.0F, 1.0F / 600.0F, 100);
        ShroudSample unsafe = new ShroudSample(0.75F, ShroudSeverity.SHROUD, Optional.empty(), false);
        EntityCorruptionAttachment clean = EntityCorruptionAttachment.clean();
        int updates = 0;

        long started = System.nanoTime();
        for (int index = 0; index < ENTITY_SAMPLES; index++) {
            EntityCorruptionAttachment next = service.tick(clean, unsafe, 20);
            if (!next.equals(clean)) {
                updates++;
            }
        }
        long elapsed = System.nanoTime() - started;
        return new EntityScenario(ENTITY_SAMPLES, updates, elapsed);
    }

    private static PersistenceScenario runPersistenceScenario() throws IOException {
        long heapBefore = usedHeap();
        ShroudWorldState state = fixture(
                PERSISTENCE_CORES,
                PERSISTENCE_CELLS_PER_CORE,
                CoreLifecycleState.ACTIVE);
        CompoundTag encoded = ShroudStateCodec.encode(state);
        long heapAfter = usedHeap();

        Path temp = Files.createTempFile("enshrouded-level1-performance-", ".nbt");
        long compressedBytes;
        try {
            NbtIo.writeCompressed(encoded, temp);
            compressedBytes = Files.size(temp);
        } finally {
            Files.deleteIfExists(temp);
        }
        return new PersistenceScenario(
                PERSISTENCE_CORES,
                PERSISTENCE_CORES * PERSISTENCE_CELLS_PER_CORE,
                compressedBytes,
                heapBefore,
                heapAfter);
    }

    private static ShroudWorldState fixture(int cores, int cellsPerCore, CoreLifecycleState lifecycle) {
        LinkedHashMap<UUID, ShroudCoreState> coreStates = new LinkedHashMap<>();
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        for (int index = 0; index < cores; index++) {
            UUID coreId = coreId(index);
            UUID regionId = regionId(index);
            coreStates.put(coreId, new ShroudCoreState(
                    coreId,
                    new BlockPos(index * 8000, 4, 4),
                    1,
                    lifecycle,
                    2_000_000,
                    index + 1L,
                    0L,
                    regionId));

            LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
            for (int cell = 0; cell < cellsPerCore; cell++) {
                ShroudCellPos pos = new ShroudCellPos(index * 1000 + cell, 0, 0);
                cells.put(pos, new ShroudCellState(pos, 1.0D, ShroudSeverity.SHROUD));
            }
            regions.put(regionId, new ShroudRegionState(regionId, coreId, cells));
        }
        return new ShroudWorldState(ShroudSchema.CURRENT_VERSION, coreStates, regions);
    }

    private static long usedHeap() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    private static void writeEvidence(Report report) throws IOException {
        Path evidence = Path.of("build", "reports", "level1-performance-benchmark.txt");
        Files.createDirectories(evidence.getParent());
        ArrayList<String> lines = new ArrayList<>();
        lines.add("stage=09.02");
        lines.add("java=" + System.getProperty("java.version"));
        lines.add("note=wall timings are hosted-runner observations, not portable pass/fail thresholds or full server MSPT");
        for (CoreScenario scenario : report.coreScenarios()) {
            lines.add("core-scenario=" + scenario.cores());
            lines.add("processed-entries=" + scenario.processedEntries());
            lines.add("applied-cells=" + scenario.appliedCells());
            lines.add("max-processed-per-core=" + scenario.maxProcessedPerCore());
            lines.add("global-budget=" + scenario.globalBudget());
            lines.add("per-core-budget=" + scenario.perCoreBudget());
            lines.add("scheduler-wall-nanos=" + scenario.wallNanos());
            lines.add(String.format("scheduler-wall-ms=%.3f", scenario.wallNanos() / 1_000_000.0D));
        }
        lines.add("entity-samples=" + report.entityScenario().samples());
        lines.add("entity-updates=" + report.entityScenario().updates());
        lines.add("entity-wall-nanos=" + report.entityScenario().wallNanos());
        lines.add("persistence-cores=" + report.persistenceScenario().cores());
        lines.add("persistence-cells=" + report.persistenceScenario().cells());
        lines.add("compressed-bytes=" + report.persistenceScenario().compressedBytes());
        lines.add("heap-observed-before-bytes=" + report.persistenceScenario().observedHeapBytesBefore());
        lines.add("heap-observed-after-bytes=" + report.persistenceScenario().observedHeapBytesAfter());
        Files.writeString(evidence, String.join(System.lineSeparator(), lines) + System.lineSeparator());
    }

    private static UUID coreId(int index) {
        return new UUID(0x0902000000000000L, index + 1L);
    }

    private static UUID regionId(int index) {
        return new UUID(0x0902000000000001L, index + 1L);
    }

    record Report(
            List<CoreScenario> coreScenarios,
            EntityScenario entityScenario,
            PersistenceScenario persistenceScenario) {
        Report {
            coreScenarios = List.copyOf(coreScenarios);
        }
    }

    record CoreScenario(
            int cores,
            int processedEntries,
            int appliedCells,
            int maxProcessedPerCore,
            int globalBudget,
            int perCoreBudget,
            long wallNanos) {
    }

    record EntityScenario(int samples, int updates, long wallNanos) {
    }

    record PersistenceScenario(
            int cores,
            int cells,
            long compressedBytes,
            long observedHeapBytesBefore,
            long observedHeapBytesAfter) {
    }
}
