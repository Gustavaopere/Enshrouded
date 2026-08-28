package com.gustavaopere.enshrouded.shroud.query;

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

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTimeoutPreemptively;

class ShroudSpatialIndexBenchmarkTest {
    @Test
    void indexedLookupRemainsBoundedWithThousandsOfUnrelatedCores() {
        int entries = 2_048;
        Map<UUID, ShroudCoreState> cores = new LinkedHashMap<>();
        Map<UUID, ShroudRegionState> regions = new LinkedHashMap<>();

        for (int i = 0; i < entries; i++) {
            UUID coreId = new UUID(10L, i + 1L);
            UUID regionId = new UUID(20L, i + 1L);
            ShroudCellPos position = new ShroudCellPos(i, 0, 0);
            ShroudCoreState core = new ShroudCoreState(
                    coreId,
                    new BlockPos(i * 8, 0, 0),
                    1,
                    CoreLifecycleState.ACTIVE,
                    128,
                    i,
                    0L,
                    regionId);
            ShroudRegionState region = new ShroudRegionState(
                    regionId,
                    coreId,
                    Map.of(position, new ShroudCellState(position, 0.5D, ShroudSeverity.SHROUD)));
            cores.put(coreId, core);
            regions.put(regionId, region);
        }

        ShroudSpatialIndex index = ShroudSpatialIndex.from(
                new ShroudWorldState(ShroudSchema.CURRENT_VERSION, cores, regions));
        assertEquals(entries, index.indexedCellCount());

        assertTimeoutPreemptively(Duration.ofSeconds(2), () -> {
            for (int i = 0; i < 100_000; i++) {
                index.lookup(new ShroudCellPos(i & (entries - 1), 0, 0));
            }
        });
    }
}
