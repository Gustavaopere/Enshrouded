package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ShroudExpansionSchedulerRedTest {
    private static final ShroudGridGeometry GEOMETRY = new ShroudGridGeometry(8);

    @Test
    void sharedBudgetProcessesCoresFairlyAcrossTicks() {
        Fixture fixture = fixture(3, 64, 0L, CoreLifecycleState.ACTIVE);
        ShroudExpansionScheduler scheduler = scheduler();
        fixture.cores().forEach((id, core) -> assertTrue(scheduler.enqueue(
                id,
                new ShroudFrontierEntry(GEOMETRY.cellAt(core.center()), core.expansionEpoch(), 0L))));

        ShroudExpansionScheduler.TickResult first = scheduler.tick(fixture.state(), new ShroudWorkBudget(1, 1));
        ShroudExpansionScheduler.TickResult second = scheduler.tick(first.state(), new ShroudWorkBudget(1, 1));
        ShroudExpansionScheduler.TickResult third = scheduler.tick(second.state(), new ShroudWorkBudget(1, 1));

        UUID firstId = fixture.cores().keySet().stream().sorted().toList().get(0);
        UUID secondId = fixture.cores().keySet().stream().sorted().toList().get(1);
        UUID thirdId = fixture.cores().keySet().stream().sorted().toList().get(2);
        assertEquals(Map.of(firstId, 1), first.processedPerCore());
        assertEquals(Map.of(secondId, 1), second.processedPerCore());
        assertEquals(Map.of(thirdId, 1), third.processedPerCore());
        assertEquals(1, first.processedEntries());
        assertEquals(1, second.processedEntries());
        assertEquals(1, third.processedEntries());
    }

    @Test
    void perCoreAndGlobalBudgetsAreStrict() {
        Fixture fixture = fixture(2, 128, 0L, CoreLifecycleState.ACTIVE);
        ShroudExpansionScheduler scheduler = scheduler();
        fixture.cores().forEach((id, core) -> {
            for (int index = 0; index < 20; index++) {
                assertTrue(scheduler.enqueue(id, new ShroudFrontierEntry(
                        new ShroudCellPos(index, 0, id.hashCode() & 1), core.expansionEpoch(), index)));
            }
        });

        ShroudExpansionScheduler.TickResult result = scheduler.tick(fixture.state(), new ShroudWorkBudget(5, 3));

        assertEquals(5, result.processedEntries());
        assertTrue(result.processedPerCore().values().stream().allMatch(count -> count <= 3));
        assertEquals(5, result.processedPerCore().values().stream().mapToInt(Integer::intValue).sum());
    }

    @Test
    void staleEpochAndDestroyedCoreCannotRegrowLogicalField() {
        Fixture active = fixture(1, 64, 1L, CoreLifecycleState.ACTIVE);
        UUID activeId = active.cores().keySet().iterator().next();
        ShroudExpansionScheduler staleScheduler = scheduler();
        assertTrue(staleScheduler.enqueue(activeId,
                new ShroudFrontierEntry(new ShroudCellPos(0, 0, 0), 0L, 0L)));

        ShroudExpansionScheduler.TickResult stale = staleScheduler.tick(active.state(), new ShroudWorkBudget(8, 8));
        assertEquals(1, stale.processedEntries());
        assertEquals(0, stale.appliedCells());
        assertEquals(active.state(), stale.state());
        assertEquals(0, staleScheduler.queuedEntries(activeId));

        Fixture destroyed = fixture(1, 64, 0L, CoreLifecycleState.DESTROYED);
        UUID destroyedId = destroyed.cores().keySet().iterator().next();
        ShroudExpansionScheduler destroyedScheduler = scheduler();
        assertTrue(destroyedScheduler.enqueue(destroyedId,
                new ShroudFrontierEntry(new ShroudCellPos(0, 0, 0), 0L, 0L)));

        ShroudExpansionScheduler.TickResult destroyedResult = destroyedScheduler.tick(
                destroyed.state(), new ShroudWorkBudget(8, 8));
        assertEquals(0, destroyedResult.appliedCells());
        assertEquals(destroyed.state(), destroyedResult.state());
        assertEquals(0, destroyedScheduler.queuedEntries(destroyedId));
    }

    @Test
    void candidateOutsideMaximumInfluenceRadiusIsRejected() {
        Fixture fixture = fixture(1, 8, 0L, CoreLifecycleState.ACTIVE);
        UUID id = fixture.cores().keySet().iterator().next();
        ShroudExpansionScheduler scheduler = scheduler();
        assertTrue(scheduler.enqueue(id,
                new ShroudFrontierEntry(new ShroudCellPos(2, 0, 0), 0L, 0L)));

        ShroudExpansionScheduler.TickResult result = scheduler.tick(fixture.state(), new ShroudWorkBudget(8, 8));

        assertEquals(1, result.processedEntries());
        assertEquals(0, result.appliedCells());
        assertTrue(result.state().regions().values().iterator().next().cells().isEmpty());
    }

    @Test
    void sameStateSeedAndEnqueueOrderProduceSameLogicalCells() {
        Fixture fixture = fixture(1, 64, 0L, CoreLifecycleState.ACTIVE);
        UUID id = fixture.cores().keySet().iterator().next();
        ShroudCoreState core = fixture.cores().get(id);
        ShroudFrontierEntry seed = new ShroudFrontierEntry(GEOMETRY.cellAt(core.center()), 0L, 0L);
        ShroudExpansionScheduler left = scheduler();
        ShroudExpansionScheduler right = scheduler();
        assertTrue(left.enqueue(id, seed));
        assertTrue(right.enqueue(id, seed));

        ShroudExpansionScheduler.TickResult leftResult = left.tick(fixture.state(), new ShroudWorkBudget(16, 16));
        ShroudExpansionScheduler.TickResult rightResult = right.tick(fixture.state(), new ShroudWorkBudget(16, 16));

        assertEquals(leftResult.state(), rightResult.state());
        assertEquals(leftResult.appliedCells(), rightResult.appliedCells());
        assertFalse(leftResult.state().regions().get(core.regionId()).cells().isEmpty());
    }

    private static ShroudExpansionScheduler scheduler() {
        return new ShroudExpansionScheduler(GEOMETRY, ShroudPropagationPolicy.terrainNeutral(), 256);
    }

    private static Fixture fixture(int count, int radius, long epoch, CoreLifecycleState lifecycle) {
        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>();
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        for (int index = 0; index < count; index++) {
            UUID coreId = new UUID(0L, index + 1L);
            UUID regionId = new UUID(1L, index + 1L);
            BlockPos center = new BlockPos(index * 256 + 4, 4, 4);
            ShroudCoreState core = new ShroudCoreState(
                    coreId, center, 1, lifecycle, radius, 0x5EEDL + index, epoch, regionId);
            cores.put(coreId, core);
            regions.put(regionId, new ShroudRegionState(regionId, coreId, Map.of()));
        }
        return new Fixture(new ShroudWorldState(ShroudSchema.CURRENT_VERSION, cores, regions), cores);
    }

    private record Fixture(ShroudWorldState state, Map<UUID, ShroudCoreState> cores) {
    }
}
