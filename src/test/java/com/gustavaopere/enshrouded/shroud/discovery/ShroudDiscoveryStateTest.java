package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ShroudDiscoveryStateTest {
    @Test
    void discoveryIsScopedToTheResolvedProgressionOwnerAndDoesNotLeak() {
        ProgressionOwner alice = ProgressionOwner.player(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        ProgressionOwner bob = ProgressionOwner.player(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        DiscoveredCore core = new DiscoveredCore(
                UUID.fromString("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa"),
                "minecraft:overworld",
                new BlockPos(120, 64, -40),
                CoreLifecycleState.ACTIVE
        );

        ShroudDiscoveryState state = ShroudDiscoveryState.empty().discover(alice, core);

        assertEquals(java.util.List.of(core), state.visibleTo(alice));
        assertTrue(state.visibleTo(bob).isEmpty(), "undiscovered cores must never leak to another owner");
    }

    @Test
    void rediscoveryUpdatesKnownLifecycleWithoutDuplicatingTheMarkerIdentity() {
        ProgressionOwner owner = ProgressionOwner.team("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee");
        UUID coreId = UUID.fromString("bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb");
        BlockPos pos = new BlockPos(8, 70, 16);

        ShroudDiscoveryState active = ShroudDiscoveryState.empty().discover(owner,
                new DiscoveredCore(coreId, "minecraft:overworld", pos, CoreLifecycleState.ACTIVE));
        ShroudDiscoveryState purified = active.discover(owner,
                new DiscoveredCore(coreId, "minecraft:overworld", pos, CoreLifecycleState.PURIFIED));

        assertEquals(1, purified.visibleTo(owner).size());
        assertEquals(CoreLifecycleState.PURIFIED, purified.visibleTo(owner).getFirst().lifecycle());
    }

    @Test
    void changingOwnerDoesNotSilentlyMigratePreviouslyDiscoveredKnowledge() {
        ProgressionOwner player = ProgressionOwner.player(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        ProgressionOwner team = ProgressionOwner.team("cccccccc-cccc-cccc-cccc-cccccccccccc");
        DiscoveredCore core = new DiscoveredCore(
                UUID.fromString("dddddddd-dddd-dddd-dddd-dddddddddddd"),
                "minecraft:the_nether",
                new BlockPos(-32, 80, 48),
                CoreLifecycleState.ACTIVE
        );

        ShroudDiscoveryState state = ShroudDiscoveryState.empty().discover(player, core);

        assertEquals(java.util.List.of(core), state.visibleTo(player));
        assertTrue(state.visibleTo(team).isEmpty());
    }
}
