package com.gustavaopere.enshrouded.performance;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudExpansionScheduler;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudFrontierEntry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudPropagationPolicy;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudWorkBudget;
import com.gustavaopere.enshrouded.shroud.purification.PurificationPolicy;
import com.gustavaopere.enshrouded.shroud.purification.ShroudRegressionScheduler;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PerformanceBudgetMatrixRedTest {
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);
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

    @Test
    void manyActiveCoresNeverExceedGlobalOrPerCoreExpansionBudgetAndRecordEvidence() throws IOException {
        int cores = 64;
        int entriesPerCore = 64;
        int globalBudget = 256;
        int perCoreBudget = 8;
        Fixture fixture = activeFixture(cores);
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, APPLY_NO_NEIGHBORS, 256);

        for (int coreIndex = 0; coreIndex < cores; coreIndex++) {
            UUID coreId = coreId(coreIndex);
            for (int entry = 0; entry < entriesPerCore; entry++) {
                assertTrue(scheduler.enqueue(coreId, new ShroudFrontierEntry(
                        new ShroudCellPos(coreIndex * 1000 + entry, 0, 0), 0L, entry)));
            }
        }

        long started = System.nanoTime();
        ShroudExpansionScheduler.TickResult result = scheduler.tick(
                fixture.state(), new ShroudWorkBudget(globalBudget, perCoreBudget));
        long elapsed = System.nanoTime() - started;

        assertEquals(globalBudget, result.processedEntries());
        assertTrue(result.processedPerCore().values().stream().allMatch(count -> count <= perCoreBudget));
        assertTrue(result.appliedCells() <= result.processedEntries());

        writeEvidence("64-core expansion", elapsed, result.processedEntries(), globalBudget, perCoreBudget);
    }

    @Test
    void manyDestroyedCoresNeverExceedRegressionBudget() {
        int cores = 32;
        int cellsPerCore = 32;
        int globalBudget = 96;
        int perCoreBudget = 6;
        ShroudWorldState state = destroyedFixture(cores, cellsPerCore);
        ShroudRegressionScheduler scheduler = new ShroudRegressionScheduler(GEOMETRY, PurificationPolicy.levelOne());

        ShroudRegressionScheduler.TickResult result = scheduler.tick(state, globalBudget, perCoreBudget);

        assertTrue(result.regressedCells().size() <= globalBudget);
        Map<UUID, Long> perCore = result.regressedCells().stream()
                .collect(java.util.stream.Collectors.groupingBy(
                        ShroudRegressionScheduler.RegressedCell::coreId,
                        java.util.stream.Collectors.counting()));
        assertTrue(perCore.values().stream().allMatch(count -> count <= perCoreBudget));
    }

    @Test
    void canonicalBudgetDescriptorsRejectUnboundedOrInvalidValues() {
        assertEquals(256, PerformanceBudgetMatrix.requirePositiveBounded("expansion", 256, 4096));
        assertEquals(4096, PerformanceBudgetMatrix.requirePositiveBounded("expansion", 8192, 4096));
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PerformanceBudgetMatrix.requirePositiveBounded("expansion", 0, 4096));
        org.junit.jupiter.api.Assertions.assertThrows(
                IllegalArgumentException.class,
                () -> PerformanceBudgetMatrix.requirePositiveBounded("expansion", 1, 0));
    }

    private static Fixture activeFixture(int cores) {
        LinkedHashMap<UUID, ShroudCoreState> coreStates = new LinkedHashMap<>();
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        for (int index = 0; index < cores; index++) {
            UUID coreId = coreId(index);
            UUID regionId = regionId(index);
            coreStates.put(coreId, new ShroudCoreState(
                    coreId, new BlockPos(index * 8000, 4, 4), 1, CoreLifecycleState.ACTIVE,
                    2_000_000, index + 1L, 0L, regionId));
            regions.put(regionId, new ShroudRegionState(regionId, coreId, Map.of()));
        }
        return new Fixture(new ShroudWorldState(ShroudSchema.CURRENT_VERSION, coreStates, regions));
    }

    private static ShroudWorldState destroyedFixture(int cores, int cellsPerCore) {
        LinkedHashMap<UUID, ShroudCoreState> coreStates = new LinkedHashMap<>();
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        for (int index = 0; index < cores; index++) {
            UUID coreId = coreId(index);
            UUID regionId = regionId(index);
            coreStates.put(coreId, new ShroudCoreState(
                    coreId, new BlockPos(index * 8000, 4, 4), 1, CoreLifecycleState.DESTROYED,
                    2_000_000, index + 1L, 0L, regionId));
            LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
            for (int cell = 0; cell < cellsPerCore; cell++) {
                ShroudCellPos pos = new ShroudCellPos(index * 1000 + cell, 0, 0);
                cells.put(pos, new ShroudCellState(pos, 1.0D, ShroudSeverity.SHROUD));
            }
            regions.put(regionId, new ShroudRegionState(regionId, coreId, cells));
        }
        return new ShroudWorldState(ShroudSchema.CURRENT_VERSION, coreStates, regions);
    }

    private static void writeEvidence(String label, long nanos, int work, int globalBudget, int perCoreBudget)
            throws IOException {
        Path report = Path.of("build", "reports", "level1-performance-benchmark.txt");
        Files.createDirectories(report.getParent());
        Files.writeString(report, String.format(
                "%s%njava=%s%nwork=%d%nwall-nanos=%d%nwall-ms=%.3f%nglobal-budget=%d%nper-core-budget=%d%n",
                label, System.getProperty("java.version"), work, nanos, nanos / 1_000_000.0D,
                globalBudget, perCoreBudget));
        assertTrue(Files.size(report) > 0L);
    }

    private static UUID coreId(int index) {
        return new UUID(0x0902000000000000L, index + 1L);
    }

    private static UUID regionId(int index) {
        return new UUID(0x0902000000000001L, index + 1L);
    }

    private record Fixture(ShroudWorldState state) {}
}
