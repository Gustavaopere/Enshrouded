package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
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
import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudExpansionSchedulerBenchmarkTest {
    private static final UUID CORE_ID = UUID.fromString("00000000-0000-0000-0000-00000000b001");
    private static final UUID REGION_ID = UUID.fromString("00000000-0000-0000-0000-00000000b002");
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);
    private static final ShroudPropagationPolicy REJECT_ALL_POLICY = new ShroudPropagationPolicy() {
        @Override
        public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
            return 0.0D;
        }

        @Override
        public List<ShroudCellPos> neighbors(ShroudCellPos source) {
            return List.of();
        }
    };
    private static final ShroudPropagationPolicy APPLY_ALL_NO_NEIGHBORS_POLICY = new ShroudPropagationPolicy() {
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
    void recordsTenThousandAndHundredThousandEntryBaselines() throws IOException {
        Baseline tenThousand = runRejectBaseline(10_000);
        Baseline hundredThousand = runRejectBaseline(100_000);

        assertEquals(10_000, tenThousand.processed());
        assertEquals(100_000, hundredThousand.processed());
        assertEquals(0, tenThousand.applied());
        assertEquals(0, hundredThousand.applied());

        Path report = Path.of("build", "reports", "frontier-expansion-benchmark.txt");
        Files.createDirectories(report.getParent());
        Files.writeString(report, String.format(
                "synthetic-frontier baseline (Java %s)%n10k reject: %d ns total, %.2f ns/entry%n100k reject: %d ns total, %.2f ns/entry%n",
                System.getProperty("java.version"),
                tenThousand.elapsedNanos(), tenThousand.nanosPerEntry(),
                hundredThousand.elapsedNanos(), hundredThousand.nanosPerEntry()));
        assertTrue(Files.size(report) > 0L);
    }

    @Test
    void applyHeavyBaselineFreezesRegionOncePerTick() throws IOException {
        Baseline applyHeavy = assertTimeoutPreemptively(
                Duration.ofSeconds(5),
                () -> runApplyHeavyBaseline(20_000, 5_000));

        assertEquals(5_000, applyHeavy.processed());
        assertEquals(5_000, applyHeavy.applied());

        Path report = Path.of("build", "reports", "frontier-expansion-apply-benchmark.txt");
        Files.createDirectories(report.getParent());
        Files.writeString(report, String.format(
                "apply-heavy frontier baseline (Java %s)%n20k existing + 5k applied: %d ns total, %.2f ns/applied%n",
                System.getProperty("java.version"),
                applyHeavy.elapsedNanos(), applyHeavy.nanosPerEntry()));
        assertTrue(Files.size(report) > 0L);
    }

    private static Baseline runRejectBaseline(int entries) {
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, REJECT_ALL_POLICY, entries);
        ShroudWorldState state = emptyState(64);
        for (int index = 0; index < entries; index++) {
            assertTrue(scheduler.enqueue(CORE_ID,
                    new ShroudFrontierEntry(new ShroudCellPos(index, 0, 0), 0L, index)));
        }

        long started = System.nanoTime();
        ShroudExpansionScheduler.TickResult result = scheduler.tick(state, new ShroudWorkBudget(entries, entries));
        long elapsed = System.nanoTime() - started;

        assertEquals(0, scheduler.queuedEntries(CORE_ID));
        return new Baseline(result.processedEntries(), result.appliedCells(), elapsed);
    }

    private static Baseline runApplyHeavyBaseline(int existingCells, int entries) {
        LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
        for (int index = 0; index < existingCells; index++) {
            ShroudCellPos position = new ShroudCellPos(index, 0, 0);
            cells.put(position, new ShroudCellState(position, 1.0D, ShroudSeverity.SHROUD));
        }

        ShroudCoreState core = core(1_000_000);
        ShroudRegionState region = new ShroudRegionState(REGION_ID, CORE_ID, cells);
        ShroudWorldState state = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE_ID, core),
                Map.of(REGION_ID, region));
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(
                GEOMETRY, APPLY_ALL_NO_NEIGHBORS_POLICY, entries);

        for (int index = 0; index < entries; index++) {
            assertTrue(scheduler.enqueue(CORE_ID, new ShroudFrontierEntry(
                    new ShroudCellPos(existingCells + index, 0, 0), 0L, index)));
        }

        long started = System.nanoTime();
        ShroudExpansionScheduler.TickResult result = scheduler.tick(state, new ShroudWorkBudget(entries, entries));
        long elapsed = System.nanoTime() - started;

        assertEquals(existingCells + entries, result.state().regions().get(REGION_ID).cells().size());
        return new Baseline(result.processedEntries(), result.appliedCells(), elapsed);
    }

    private static ShroudWorldState emptyState(int radius) {
        ShroudCoreState core = core(radius);
        ShroudRegionState region = new ShroudRegionState(REGION_ID, CORE_ID, Map.of());
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE_ID, core),
                Map.of(REGION_ID, region));
    }

    private static ShroudCoreState core(int radius) {
        return new ShroudCoreState(
                CORE_ID,
                new BlockPos(4, 4, 4),
                1,
                CoreLifecycleState.ACTIVE,
                radius,
                0xBEEFL,
                0L,
                REGION_ID);
    }

    private record Baseline(int processed, int applied, long elapsedNanos) {
        private double nanosPerEntry() {
            return (double) elapsedNanos / processed;
        }
    }
}
