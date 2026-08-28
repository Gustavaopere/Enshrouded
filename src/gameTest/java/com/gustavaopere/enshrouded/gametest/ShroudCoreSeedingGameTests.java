package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudCoreSeedingGameTests {
    private ShroudCoreSeedingGameTests() {
    }

    @GameTest(template = "foundation_empty", timeoutTicks = 80)
    public static void automaticCoreSeedActivatesAndCreatesInitialLogicalCellExactlyOnce(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);
        UUID[] coreId = new UUID[1];
        UUID[] regionId = new UUID[1];

        helper.setBlock(relative, ModBlocks.SHROUD_CORE.get());
        helper.assertTrue(level.getBlockEntity(absolute) instanceof ShroudCoreBlockEntity,
                "Automatic seed test requires a ShroudCoreBlockEntity");
        ((ShroudCoreBlockEntity) level.getBlockEntity(absolute)).requestAutomaticActivation();

        helper.runAtTickTime(5L, () -> {
            ShroudWorldState state = ShroudSavedData.get(level).state();
            var atPosition = state.cores().values().stream()
                    .filter(core -> core.center().equals(absolute))
                    .toList();
            helper.assertTrue(atPosition.size() == 1,
                    "Automatic seed must register exactly one canonical core identity");

            ShroudCoreState core = atPosition.getFirst();
            helper.assertTrue(core.lifecycleState() == CoreLifecycleState.ACTIVE,
                    "Automatic worldgen/admin seed must become ACTIVE after deferred registration");
            var region = state.regions().get(core.regionId());
            helper.assertTrue(region != null, "Automatic seed must own a logical region");
            helper.assertTrue(!region.cells().isEmpty(),
                    "Automatic seed must create its initial logical Shroud cell through the frontier runtime");

            coreId[0] = core.id();
            regionId[0] = core.regionId();
            ((ShroudCoreBlockEntity) level.getBlockEntity(absolute)).requestAutomaticActivation();
        });

        helper.runAtTickTime(9L, () -> {
            ShroudWorldState state = ShroudSavedData.get(level).state();
            long atPosition = state.cores().values().stream()
                    .filter(core -> core.center().equals(absolute))
                    .count();
            helper.assertTrue(atPosition == 1L,
                    "Repeating automatic registration must not duplicate a seeded core");
            helper.assertTrue(state.cores().containsKey(coreId[0]),
                    "Repeated automatic registration must preserve the original core identity");
            helper.assertTrue(state.regions().containsKey(regionId[0]),
                    "Repeated automatic registration must preserve the original region identity");
            helper.succeed();
        });
    }

    @GameTest(template = "foundation_empty", timeoutTicks = 30)
    public static void ordinaryLoadedTestChunkDoesNotSeedWithoutPhysicalCore(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);

        helper.assertBlockNotPresent(ModBlocks.SHROUD_CORE.get(), relative);
        helper.assertTrue(ShroudSavedData.get(level).state().cores().values().stream()
                        .noneMatch(core -> core.center().equals(absolute)),
                "No canonical core should exist at the empty test position before ticking");

        helper.runAtTickTime(8L, () -> {
            helper.assertBlockNotPresent(ModBlocks.SHROUD_CORE.get(), relative);
            helper.assertTrue(ShroudSavedData.get(level).state().cores().values().stream()
                            .noneMatch(core -> core.center().equals(absolute)),
                    "Loading/ticking an existing chunk without a physical seed must not register a core at that position");
            helper.succeed();
        });
    }
}
