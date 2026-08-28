package com.gustavaopere.enshrouded.shroud.query;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
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

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShroudSpatialIndexRedTest {
    private static final ShroudCellPos CELL = new ShroudCellPos(2, 4, 6);

    @Test
    void classifiesIntensityAtStableDeadlyBoundary() {
        ShroudSeverityThresholds thresholds = new ShroudSeverityThresholds(0.75f);

        assertEquals(ShroudSeverity.CLEAR, thresholds.classify(0.0, ShroudSeverity.CLEAR));
        assertEquals(ShroudSeverity.SHROUD, thresholds.classify(0.749, ShroudSeverity.SHROUD));
        assertEquals(ShroudSeverity.DEADLY, thresholds.classify(0.75, ShroudSeverity.SHROUD));
        assertEquals(ShroudSeverity.DEADLY, thresholds.classify(0.20, ShroudSeverity.DEADLY));
        assertThrows(IllegalArgumentException.class, () -> new ShroudSeverityThresholds(0.0f));
    }

    @Test
    void resolvesOverlapByHighestIntensityAndReturnsOwningCoreId() {
        UUID weakerCore = new UUID(0L, 2L);
        UUID strongerCore = new UUID(0L, 1L);
        ShroudWorldState state = worldWithOverlappingCells(
                core(weakerCore, new UUID(1L, 2L), CoreLifecycleState.ACTIVE), 0.40,
                core(strongerCore, new UUID(1L, 1L), CoreLifecycleState.ACTIVE), 0.90);

        ShroudSpatialIndex index = ShroudSpatialIndex.from(state);
        ShroudSample sample = index.sample(CELL, new ShroudSeverityThresholds(0.75f), false);

        assertEquals(0.90f, sample.intensity(), 0.0001f);
        assertEquals(ShroudSeverity.DEADLY, sample.severity());
        assertEquals(strongerCore, sample.sourceId().orElseThrow());
        assertFalse(sample.sanctuarySuppressed());
    }

    @Test
    void tieBreaksOverlappingCellsByStableCoreUuidOrdering() {
        UUID lowerCore = new UUID(0L, 1L);
        UUID higherCore = new UUID(0L, 2L);
        ShroudWorldState state = worldWithOverlappingCells(
                core(higherCore, new UUID(1L, 2L), CoreLifecycleState.ACTIVE), 0.50,
                core(lowerCore, new UUID(1L, 1L), CoreLifecycleState.ACTIVE), 0.50);

        ShroudSample sample = ShroudSpatialIndex.from(state)
                .sample(CELL, new ShroudSeverityThresholds(0.75f), false);

        assertEquals(lowerCore, sample.sourceId().orElseThrow());
    }

    @Test
    void sanctuaryOverlayPreservesLatentLogicalSample() {
        UUID coreId = new UUID(0L, 3L);
        ShroudWorldState state = worldWithOneCell(
                core(coreId, new UUID(1L, 3L), CoreLifecycleState.ACTIVE), 0.60, ShroudSeverity.SHROUD);

        ShroudSample sample = ShroudSpatialIndex.from(state)
                .sample(CELL, new ShroudSeverityThresholds(0.75f), true);

        assertEquals(0.60f, sample.intensity(), 0.0001f);
        assertEquals(ShroudSeverity.SHROUD, sample.severity());
        assertEquals(coreId, sample.sourceId().orElseThrow());
        assertTrue(sample.sanctuarySuppressed());
    }

    @Test
    void purifiedCoreNeverContributesEffectiveLogicalField() {
        UUID coreId = new UUID(0L, 4L);
        ShroudWorldState state = worldWithOneCell(
                core(coreId, new UUID(1L, 4L), CoreLifecycleState.PURIFIED), 1.0, ShroudSeverity.DEADLY);

        ShroudSpatialIndex index = ShroudSpatialIndex.from(state);

        assertEquals(0, index.indexedCellCount());
        assertEquals(ShroudSample.clear(), index.sample(CELL, new ShroudSeverityThresholds(0.75f), false));
    }

    private static ShroudCoreState core(UUID coreId, UUID regionId, CoreLifecycleState lifecycle) {
        return new ShroudCoreState(coreId, BlockPos.ZERO, 1, lifecycle, 128, 42L, 0L, regionId);
    }

    private static ShroudWorldState worldWithOneCell(
            ShroudCoreState core,
            double intensity,
            ShroudSeverity severity) {
        ShroudCellState cell = new ShroudCellState(CELL, intensity, severity);
        ShroudRegionState region = new ShroudRegionState(core.regionId(), core.id(), Map.of(CELL, cell));
        return new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(core.id(), core),
                Map.of(region.id(), region));
    }

    private static ShroudWorldState worldWithOverlappingCells(
            ShroudCoreState firstCore,
            double firstIntensity,
            ShroudCoreState secondCore,
            double secondIntensity) {
        ShroudRegionState firstRegion = new ShroudRegionState(
                firstCore.regionId(),
                firstCore.id(),
                Map.of(CELL, new ShroudCellState(CELL, firstIntensity, ShroudSeverity.SHROUD)));
        ShroudRegionState secondRegion = new ShroudRegionState(
                secondCore.regionId(),
                secondCore.id(),
                Map.of(CELL, new ShroudCellState(CELL, secondIntensity, ShroudSeverity.SHROUD)));

        LinkedHashMap<UUID, ShroudCoreState> cores = new LinkedHashMap<>();
        cores.put(firstCore.id(), firstCore);
        cores.put(secondCore.id(), secondCore);
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>();
        regions.put(firstRegion.id(), firstRegion);
        regions.put(secondRegion.id(), secondRegion);
        return new ShroudWorldState(ShroudSchema.CURRENT_VERSION, cores, regions);
    }
}
