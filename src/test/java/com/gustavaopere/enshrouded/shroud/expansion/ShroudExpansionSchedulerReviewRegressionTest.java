package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudExpansionSchedulerReviewRegressionTest {
    private static final UUID CORE_ID = UUID.fromString("00000000-0000-0000-0000-00000000c001");
    private static final UUID REGION_ID = UUID.fromString("00000000-0000-0000-0000-00000000c002");
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);

    @Test
    void schedulerEnforcesHardRadiusEvenForPermissiveCustomPolicy() {
        ShroudPropagationPolicy permissive = new ShroudPropagationPolicy() {
            @Override
            public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
                return 1.0D;
            }

            @Override
            public List<ShroudCellPos> neighbors(ShroudCellPos source) {
                return List.of();
            }
        };
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, permissive, 8);
        ShroudWorldState state = state(8);
        assertTrue(scheduler.enqueue(CORE_ID,
                new ShroudFrontierEntry(new ShroudCellPos(2, 0, 0), 0L, 0L)));

        ShroudExpansionScheduler.TickResult result = scheduler.tick(state, new ShroudWorkBudget(8, 8));

        assertEquals(1, result.processedEntries());
        assertEquals(0, result.appliedCells());
        assertTrue(result.state().regions().get(REGION_ID).cells().isEmpty());
    }

    @Test
    void fullFrontierRetriesEligibleNeighborsInsteadOfDroppingThem() {
        ShroudCellPos seed = new ShroudCellPos(0, 0, 0);
        ShroudCellPos east = new ShroudCellPos(1, 0, 0);
        ShroudCellPos south = new ShroudCellPos(0, 0, 1);
        ShroudPropagationPolicy branching = new ShroudPropagationPolicy() {
            @Override
            public double intensity(ShroudCoreState core, ShroudGridGeometry geometry, ShroudCellPos candidate) {
                return 1.0D;
            }

            @Override
            public List<ShroudCellPos> neighbors(ShroudCellPos source) {
                return source.equals(seed) ? List.of(east, south) : List.of();
            }
        };
        ShroudExpansionScheduler scheduler = new ShroudExpansionScheduler(GEOMETRY, branching, 1);
        ShroudWorldState state = state(64);
        assertTrue(scheduler.enqueue(CORE_ID, new ShroudFrontierEntry(seed, 0L, 0L)));

        for (int tick = 0; tick < 6; tick++) {
            state = scheduler.tick(state, new ShroudWorkBudget(1, 1)).state();
        }

        assertEquals(Map.of(seed, state.regions().get(REGION_ID).cells().get(seed),
                        east, state.regions().get(REGION_ID).cells().get(east),
                        south, state.regions().get(REGION_ID).cells().get(south)).keySet(),
                state.regions().get(REGION_ID).cells().keySet());
        assertEquals(3, state.regions().get(REGION_ID).cells().size());
    }

    private static ShroudWorldState state(int radius) {
        ShroudCoreState core = new ShroudCoreState(
                CORE_ID,
                new BlockPos(4, 4, 4),
                1,
                CoreLifecycleState.ACTIVE,
                radius,
                0xC0FFEE,
                0L,
                REGION_ID);
        ShroudRegionState region = new ShroudRegionState(REGION_ID, CORE_ID, Map.of());
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(CORE_ID, core),
                Map.of(REGION_ID, region));
    }
}
