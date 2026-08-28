package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    @Test
    void recordsTenThousandAndHundredThousandEntryBaselines() throws IOException {
        Baseline tenThousand = runBaseline(10_000);
        Baseline hundredThousand = runBaseline(100_000);

        assertEquals(10_000, tenThousand.processed());
        assertEquals(100_000, hundredThousand.processed());
        assertEquals(0, tenThousand.applied());
        assertEquals(0, hundredThousand.applied());

        Path report = Path.of("build", "reports", "frontier-expansion-benchmark.txt");
        Files.createDirectories(report.getParent());
        Files.writeString(report, String.format(
                "synthetic-frontier baseline (Java %s)%n10k: %d ns total, %.2f ns/entry%n100k: %d ns total, %.2f ns/entry%n",
                System.getProperty("java.version"),
                tenThousand.elapsedNanos(), tenThousand.nanosPerEntry(),
                hundredThousand.elapsedNanos(), hundredThousand.nanosPerEntry()));
        assertTrue(Files.size(report) > 0L);
    }

    private static Baseline runBaseline(int entries) {
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, REJECT_ALL_POLICY, entries);
        ShroudWorldState state = state();
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

    private static ShroudWorldState state() {
        ShroudCoreState core = new ShroudCoreState(
                CORE_ID,
                new BlockPos(4, 4, 4),
                1,
                CoreLifecycleState.ACTIVE,
                64,
                0xBEEFL,
                0L,
                REGION_ID);
        ShroudRegionState region = new ShroudRegionState(REGION_ID, CORE_ID, Map.of());
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE_ID, core),
                Map.of(REGION_ID, region));
    }

    private record Baseline(int processed, int applied, long elapsedNanos) {
        private double nanosPerEntry() {
            return (double) elapsedNanos / processed;
        }
    }
}
