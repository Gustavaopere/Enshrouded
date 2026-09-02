package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudDiscoverySyncContractTest {
    @Test
    void trackerDeduplicatesSnapshotsButOwnerChangeForcesAnEmptyReplacement() {
        UUID playerId = UUID.fromString("11111111-1111-1111-1111-111111111111");
        ProgressionOwner playerOwner = ProgressionOwner.player(playerId);
        ProgressionOwner teamOwner = ProgressionOwner.team("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        DiscoveredCore core = new DiscoveredCore(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "minecraft:overworld", new BlockPos(20, 70, 20), CoreLifecycleState.ACTIVE);
        ShroudDiscoverySyncTracker tracker = new ShroudDiscoverySyncTracker();

        ShroudDiscoveryPayload first = tracker.update(playerId, playerOwner, List.of(core)).orElseThrow();
        assertEquals(0L, first.sequence());
        assertEquals(playerOwner.stableKey(), first.ownerStableKey());
        assertEquals(List.of(core), first.cores());
        assertTrue(tracker.update(playerId, playerOwner, List.of(core)).isEmpty());

        ShroudDiscoveryPayload ownerChanged = tracker.update(playerId, teamOwner, List.of()).orElseThrow();
        assertEquals(1L, ownerChanged.sequence());
        assertEquals(teamOwner.stableKey(), ownerChanged.ownerStableKey());
        assertTrue(ownerChanged.cores().isEmpty(), "owner change must actively clear stale client markers");
    }

    @Test
    void payloadCannotExposeDestroyedKnowledge() {
        ProgressionOwner owner = ProgressionOwner.team("team-one");
        DiscoveredCore destroyed = new DiscoveredCore(
                UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb"),
                "minecraft:overworld", BlockPos.ZERO, CoreLifecycleState.DESTROYED);

        assertThrows(IllegalArgumentException.class, () -> new ShroudDiscoveryPayload(
                ShroudDiscoveryPayload.CURRENT_VERSION,
                0L,
                owner.stableKey(),
                List.of(destroyed)));
    }
}
