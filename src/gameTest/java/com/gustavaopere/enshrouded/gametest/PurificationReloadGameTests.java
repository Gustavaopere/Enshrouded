package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.purification.ShroudPurificationRuntime;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;
import com.gustavaopere.enshrouded.shroud.state.ShroudCellState;
import com.gustavaopere.enshrouded.shroud.state.ShroudRegionState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class PurificationReloadGameTests {
    private static final UUID MID_CORE_ID = UUID.fromString("33333333-4444-5555-6666-777777777777");
    private static final UUID MID_REGION_ID = UUID.fromString("44444444-5555-6666-7777-888888888888");
    private static final UUID TERMINAL_CORE_ID = UUID.fromString("55555555-6666-7777-8888-999999999999");
    private static final UUID TERMINAL_REGION_ID = UUID.fromString("66666666-7777-8888-9999-aaaaaaaaaaaa");
    private static final BlockPos TERMINAL_VISUAL_POS = new BlockPos(352, 96, 352);

    // The GameTest server continues ticking production runtimes after this test succeeds and before
    // its final shutdown save. Keep enough logical work in the sentinel that the real bounded
    // purification runtime cannot finish it during the remainder of the suite. At the default
    // Level-1 decay/budget this represents 4096 server ticks of regression work, while remaining
    // small enough for the two-boot CI fixture.
    private static final int MID_CELL_COUNT = 32768;

    private PurificationReloadGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void purificationStateSurvivesRealServerRestart(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        ShroudSavedData data = ShroudSavedData.get(level);

        if (!data.state().cores().containsKey(MID_CORE_ID)) {
            ShroudWorldState withMid = addMidPurificationSentinel(data.state());
            ShroudWorldState withTerminal = addPurifiedSentinel(withMid);
            data.replace(withTerminal);
            level.getChunkAt(TERMINAL_VISUAL_POS);
            level.setBlock(TERMINAL_VISUAL_POS, ModBlocks.SHROUD_GROWTH.get().defaultBlockState(), Block.UPDATE_ALL);
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_PURIFICATION_MID_CREATED");
            System.out.println("ENSHROUDED_PURIFIED_LEFTOVER_CREATED");
            helper.succeed();
            return;
        }

        ShroudWorldState loaded = data.state();
        helper.assertTrue(
                loaded.cores().get(MID_CORE_ID).lifecycleState() == CoreLifecycleState.DESTROYED,
                "Real second boot must resume the large sentinel while it is still DESTROYED"
        );
        helper.assertTrue(
                !loaded.regions().get(MID_REGION_ID).cells().isEmpty(),
                "Real second boot must retain remaining mid-purification logical cells"
        );
        double intensityBefore = totalIntensity(loaded.regions().get(MID_REGION_ID));
        ShroudPurificationRuntime.advance(level);
        ShroudWorldState advanced = data.state();
        double intensityAfter = totalIntensity(advanced.regions().get(MID_REGION_ID));
        helper.assertTrue(
                intensityAfter < intensityBefore,
                "Reloaded DESTROYED state must resume bounded logical regression instead of re-expanding or stalling"
        );

        helper.assertTrue(
                advanced.cores().get(TERMINAL_CORE_ID).lifecycleState() == CoreLifecycleState.PURIFIED,
                "A PURIFIED sentinel must remain terminal across a real server restart"
        );
        helper.assertTrue(
                advanced.regions().get(TERMINAL_REGION_ID).cells().isEmpty(),
                "PURIFIED sentinel must not reconstruct logical frontier cells after restart"
        );
        helper.assertTrue(
                level.getBlockState(TERMINAL_VISUAL_POS).is(ModBlocks.SHROUD_GROWTH.get()),
                "Safe visual leftovers may persist without resurrecting logical Shroud"
        );
        System.out.println("ENSHROUDED_PURIFICATION_MID_RELOADED");
        System.out.println("ENSHROUDED_PURIFIED_LEFTOVER_RELOADED");
        helper.succeed();
    }

    private static ShroudWorldState addMidPurificationSentinel(ShroudWorldState existing) {
        ShroudWorldState state = ShroudCoreService.registerDormant(
                existing,
                MID_CORE_ID,
                MID_REGION_ID,
                new BlockPos(8192, 64, 8192),
                1,
                128,
                0x0BAD5EEDL
        ).state();
        state = ShroudCoreService.activate(state, MID_CORE_ID).state();
        state = ShroudCoreService.destroy(state, MID_CORE_ID).state();

        LinkedHashMap<ShroudCellPos, ShroudCellState> cells = new LinkedHashMap<>();
        for (int index = 0; index < MID_CELL_COUNT; index++) {
            ShroudCellPos pos = new ShroudCellPos(1024 + index, 8, 1024);
            cells.put(pos, new ShroudCellState(pos, 1.0D, ShroudSeverity.SHROUD));
        }
        LinkedHashMap<UUID, ShroudRegionState> regions = new LinkedHashMap<>(state.regions());
        regions.put(MID_REGION_ID, new ShroudRegionState(MID_REGION_ID, MID_CORE_ID, cells));
        return new ShroudWorldState(state.schemaVersion(), state.cores(), regions);
    }

    private static ShroudWorldState addPurifiedSentinel(ShroudWorldState existing) {
        ShroudWorldState state = ShroudCoreService.registerDormant(
                existing,
                TERMINAL_CORE_ID,
                TERMINAL_REGION_ID,
                TERMINAL_VISUAL_POS,
                1,
                128,
                0x0C1EA4EDL
        ).state();
        state = ShroudCoreService.activate(state, TERMINAL_CORE_ID).state();
        state = ShroudCoreService.destroy(state, TERMINAL_CORE_ID).state();
        return ShroudCoreService.markPurified(state, TERMINAL_CORE_ID).state();
    }

    private static double totalIntensity(ShroudRegionState region) {
        return region.cells().values().stream().mapToDouble(ShroudCellState::intensity).sum();
    }
}
