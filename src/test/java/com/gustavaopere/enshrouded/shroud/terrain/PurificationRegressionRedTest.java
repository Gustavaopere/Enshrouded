package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PurificationRegressionRedTest {
    private static final UUID CORE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID REGION_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Test
    void destroyedCoreRegressesDeterministicallyWithinBudgetAndPurifiesOnlyWhenLogicalFieldIsGone() {
        ShroudWorldState destroyed = destroyedStateWithCells(List.of(
                new ShroudCellPos(0, 0, 0),
                new ShroudCellPos(1, 0, 0),
                new ShroudCellPos(2, 0, 0)
        ));
        ShroudRegressionScheduler scheduler = new ShroudRegressionScheduler(ShroudGridGeometry.levelOne());

        ShroudRegressionScheduler.TickResult first = scheduler.tick(destroyed, 1, 1);
        ShroudRegressionScheduler.TickResult repeated = scheduler.tick(destroyed, 1, 1);

        assertEquals(first.regressedCells(), repeated.regressedCells(), "Equivalent persisted state must choose the same regression cell");
        assertEquals(2, first.state().regions().get(REGION_ID).cells().size(), "One work unit must remove at most one logical cell");
        assertEquals(CoreLifecycleState.DESTROYED, first.state().cores().get(CORE_ID).lifecycleState());

        ShroudRegressionScheduler.TickResult second = scheduler.tick(first.state(), 1, 1);
        assertEquals(1, second.state().regions().get(REGION_ID).cells().size());
        assertEquals(CoreLifecycleState.DESTROYED, second.state().cores().get(CORE_ID).lifecycleState());

        ShroudRegressionScheduler.TickResult third = scheduler.tick(second.state(), 1, 1);
        assertEquals(0, third.state().regions().get(REGION_ID).cells().size());
        assertEquals(CoreLifecycleState.PURIFIED, third.state().cores().get(CORE_ID).lifecycleState(),
                "Logical convergence must transition DESTROYED to PURIFIED without waiting for visual cleanup");
    }

    private static ShroudWorldState destroyedStateWithCells(List<ShroudCellPos> positions) {
        ShroudWorldState state = ShroudCoreService.registerDormant(
                ShroudWorldState.empty(), CORE_ID, REGION_ID, new BlockPos(4, 4, 4), 1, 128, 0x5EEDL
        ).state();
        state = ShroudCoreService.activate(state, CORE_ID).state();
        state = ShroudCoreService.destroy(state, CORE_ID).state();

        LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
        for (ShroudCellPos position : positions) {
            cells.put(position, new ShroudCellState(position, 0.75D, ShroudSeverity.SHROUD));
        }
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
        regions.put(REGION_ID, new ShroudRegionState(REGION_ID, CORE_ID, cells));
        return new ShroudWorldState(state.schemaVersion(), state.cores(), regions);
    }
}
