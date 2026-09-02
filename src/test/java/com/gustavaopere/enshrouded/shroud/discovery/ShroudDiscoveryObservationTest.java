package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSchema;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudDiscoveryObservationTest {
    @Test
    void observationDiscoversOnlyTheCanonicalSourceIdActuallySeenByThePlayer() {
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        UUID seenCoreId = UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa");
        UUID otherCoreId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        UUID seenRegionId = UUID.fromString("cccccccc-cccc-cccc-cccc-cccccccccccc");
        UUID otherRegionId = UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd");
        ShroudCoreState seenCore = core(seenCoreId, seenRegionId, new BlockPos(96, 64, -48));
        ShroudCoreState otherCore = core(otherCoreId, otherRegionId, new BlockPos(1024, 70, 1024));
        ShroudWorldState world = new ShroudWorldState(
                ShroudSchema.CURRENT_VERSION,
                Map.of(seenCoreId, seenCore, otherCoreId, otherCore),
                Map.of(
                        seenRegionId, new ShroudRegionState(seenRegionId, seenCoreId, Map.of()),
                        otherRegionId, new ShroudRegionState(otherRegionId, otherCoreId, Map.of())
                )
        );
        ShroudSample sample = new ShroudSample(0.7F, ShroudSeverity.SHROUD, Optional.of(seenCoreId), false);

        ShroudDiscoveryObservation.Result result = ShroudDiscoveryObservation.observe(
                ShroudDiscoveryState.empty(), owner, "minecraft:overworld", sample, world);

        assertEquals(java.util.List.of(new DiscoveredCore(
                seenCoreId,
                "minecraft:overworld",
                seenCore.center(),
                CoreLifecycleState.ACTIVE
        )), result.visibleCores());
        assertTrue(result.state().knownTo(owner).stream().noneMatch(core -> core.coreId().equals(otherCoreId)),
                "observation must never enumerate or leak unrelated cores");
    }

    @Test
    void clearOrUnknownSamplesDoNotCreateDiscoveryKnowledge() {
        ProgressionOwner owner = ProgressionOwner.player(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        ShroudWorldState emptyWorld = ShroudWorldState.empty();

        ShroudDiscoveryObservation.Result clear = ShroudDiscoveryObservation.observe(
                ShroudDiscoveryState.empty(), owner, "minecraft:overworld", ShroudSample.clear(), emptyWorld);
        ShroudDiscoveryObservation.Result unknown = ShroudDiscoveryObservation.observe(
                ShroudDiscoveryState.empty(), owner, "minecraft:overworld",
                new ShroudSample(0.5F, ShroudSeverity.SHROUD,
                        Optional.of(UUID.fromString("eeeeeeee-eeee-eeee-eeee-eeeeeeeeeeee")), false),
                emptyWorld);

        assertTrue(clear.state().knownTo(owner).isEmpty());
        assertTrue(unknown.state().knownTo(owner).isEmpty());
        assertTrue(clear.visibleCores().isEmpty());
        assertTrue(unknown.visibleCores().isEmpty());
    }

    private static ShroudCoreState core(UUID coreId, UUID regionId, BlockPos center) {
        return new ShroudCoreState(coreId, center, 1, CoreLifecycleState.ACTIVE, 128, 1L, 0L, regionId);
    }
}
