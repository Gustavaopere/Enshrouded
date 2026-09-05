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
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class PerformanceInstrumentationRedTest {
    private static final UUID CORE = UUID.fromString("09020000-0000-4000-8000-000000000001");
    private static final UUID REGION = UUID.fromString("09020000-0000-4000-8000-000000000002");
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);

    @AfterEach
    void resetGlobalCounters() {
        PerformanceCounters.global().reset();
    }

    @Test
    void expansionSchedulerPublishesProcessedAndAppliedWork() {
        PerformanceCounters.global().reset();
        ShroudPropagationPolicy policy = new ShroudPropagationPolicy() {
            @Override
            public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
                return 1.0D;
            }

            @Override
            public List<ShroudCellPos> neighbors(ShroudCellPos source) {
                return List.of();
            }
        };
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, policy, 16);
        ShroudWorldState state = state(CoreLifecycleState.ACTIVE, Map.of());
        assertTrue(scheduler.enqueue(CORE, new ShroudFrontierEntry(new ShroudCellPos(0, 0, 0), 0L, 0L)));

        scheduler.tick(state, new ShroudWorkBudget(1, 1));

        PerformanceCounters.Snapshot snapshot = PerformanceCounters.global().snapshot();
        assertEquals(1L, snapshot.expansionAttempts());
        assertEquals(1L, snapshot.expansionAppliedCells());
    }

    @Test
    void regressionSchedulerPublishesWorkAndClears() {
        PerformanceCounters.global().reset();
        ShroudCellPos pos = new ShroudCellPos(0, 0, 0);
        ShroudWorldState state = state(
                CoreLifecycleState.DESTROYED,
                Map.of(pos, new ShroudCellState(pos, 0.01D, ShroudSeverity.SHROUD)));
        ShroudRegressionScheduler scheduler = new ShroudRegressionScheduler(GEOMETRY, PurificationPolicy.levelOne());

        scheduler.tick(state, 1, 1);

        PerformanceCounters.Snapshot snapshot = PerformanceCounters.global().snapshot();
        assertEquals(1L, snapshot.regressionWorkUnits());
        assertEquals(1L, snapshot.regressionClearedCells());
    }

    private static ShroudWorldState state(CoreLifecycleState lifecycle, Map<ShroudCellPos, ShroudCellState> cells) {
        ShroudCoreState core = new ShroudCoreState(
                CORE, new BlockPos(4, 4, 4), 1, lifecycle, 128, 9L, 0L, REGION);
        ShroudRegionState region = new ShroudRegionState(REGION, CORE, cells);
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE, core),
                Map.of(REGION, region));
    }
}
