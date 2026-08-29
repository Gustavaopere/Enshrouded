package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudStateCodec;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PurificationRegressionRedTest {
    private static final UUID CORE_ID = UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
    private static final UUID REGION_ID = UUID.fromString("11111111-2222-3333-4444-555555555555");

    @Test
    void destroyedCoreDecaysFrontierFirstDeterministicallyAndPurifiesOnlyAfterLastCellClears() {
        ShroudCellPos center = new ShroudCellPos(0, 0, 0);
        ShroudCellPos middle = new ShroudCellPos(1, 0, 0);
        ShroudCellPos frontier = new ShroudCellPos(2, 0, 0);
        ShroudWorldState destroyed = destroyedStateWithCells(List.of(center, middle, frontier));
        ShroudRegressionScheduler scheduler = new ShroudRegressionScheduler(
                ShroudGridGeometry.levelOne(), new PurificationPolicy(0.50D)
        );

        ShroudRegressionScheduler.TickResult first = scheduler.tick(destroyed, 1, 1);
        ShroudRegressionScheduler.TickResult repeated = scheduler.tick(destroyed, 1, 1);

        assertEquals(first.regressedCells(), repeated.regressedCells(), "Equivalent persisted state must choose the same regression work");
        assertEquals(frontier, first.regressedCells().getFirst().position(), "Regression must retreat from frontier toward the former core center");
        assertEquals(0.25D, first.state().regions().get(REGION_ID).cells().get(frontier).intensity(), 1.0E-9,
                "One work unit must decay intensity before removing a still-effective cell");
        assertEquals(3, first.state().regions().get(REGION_ID).cells().size());
        assertEquals(CoreLifecycleState.DESTROYED, first.state().cores().get(CORE_ID).lifecycleState());

        ShroudWorldState current = first.state();
        for (int tick = 0; tick < 5; tick++) {
            current = scheduler.tick(current, 1, 1).state();
        }

        assertEquals(0, current.regions().get(REGION_ID).cells().size());
        assertEquals(CoreLifecycleState.PURIFIED, current.cores().get(CORE_ID).lifecycleState(),
                "Logical convergence must transition DESTROYED to PURIFIED without waiting for visual cleanup");

        ShroudRegressionScheduler.TickResult terminal = scheduler.tick(current, 8, 8);
        assertEquals(current, terminal.state(), "PURIFIED core must never re-enter regression work");
        assertTrue(terminal.regressedCells().isEmpty(), "PURIFIED core must emit no new regression work");
    }

    @Test
    void midPurificationCodecRoundTripResumesTheSameDeterministicWork() {
        ShroudCellPos frontier = new ShroudCellPos(2, 0, 0);
        ShroudWorldState destroyed = destroyedStateWithCells(List.of(frontier));
        ShroudRegressionScheduler scheduler = new ShroudRegressionScheduler(
                ShroudGridGeometry.levelOne(), new PurificationPolicy(0.25D)
        );

        ShroudWorldState partial = scheduler.tick(destroyed, 1, 1).state();
        assertEquals(CoreLifecycleState.DESTROYED, partial.cores().get(CORE_ID).lifecycleState());
        assertEquals(0.50D, partial.regions().get(REGION_ID).cells().get(frontier).intensity(), 1.0E-9);

        ShroudWorldState reloaded = ShroudStateCodec.decode(ShroudStateCodec.encode(partial));
        assertEquals(partial, reloaded, "Mid-regression SavedData codec round-trip must preserve the exact logical checkpoint");
        assertEquals(
                scheduler.tick(partial, 1, 1),
                scheduler.tick(reloaded, 1, 1),
                "Reloaded mid-regression state must choose the same next deterministic work"
        );

        ShroudWorldState finished = scheduler.tick(scheduler.tick(reloaded, 1, 1).state(), 1, 1).state();
        assertEquals(CoreLifecycleState.PURIFIED, finished.cores().get(CORE_ID).lifecycleState());
        assertTrue(finished.regions().get(REGION_ID).cells().isEmpty());
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
