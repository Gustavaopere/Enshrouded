package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import com.gustavaopere.enshrouded.shroud.core.CoreMutationResult;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreService;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ShroudCorePhysicalLifecycleRedGameTests {
    private ShroudCorePhysicalLifecycleRedGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void placedCoreCreatesBlockEntityAndPersistentIdentity(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);

        helper.setBlock(relative, ModBlocks.SHROUD_CORE.get());

        BlockEntity blockEntity = level.getBlockEntity(absolute);
        helper.assertTrue(blockEntity instanceof ShroudCoreBlockEntity,
                "Placed enshrouded:shroud_core must create ShroudCoreBlockEntity");

        helper.succeedWhen(() -> {
            long ownedAtPosition = ShroudSavedData.get(level).state().cores().values().stream()
                    .filter(core -> core.center().equals(absolute))
                    .count();
            helper.assertTrue(ownedAtPosition == 1L,
                    "Placed Shroud core must register exactly one persistent identity at its absolute position");
        });
    }

    @GameTest(template = "foundation_empty", timeoutTicks = 40)
    public static void removingDormantPhysicalCoreDiscardsUnactivatedPersistentRegistration(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);
        UUID[] coreId = new UUID[1];
        UUID[] regionId = new UUID[1];

        helper.setBlock(relative, ModBlocks.SHROUD_CORE.get());

        helper.runAtTickTime(2L, () -> {
            ShroudCoreState core = ShroudSavedData.get(level).state().cores().values().stream()
                    .filter(candidate -> candidate.center().equals(absolute))
                    .findFirst()
                    .orElse(null);
            helper.assertTrue(core != null, "Placed dormant core did not register before removal");
            helper.assertTrue(core.lifecycleState() == CoreLifecycleState.DORMANT,
                    "Freshly placed core must still be DORMANT before activation");
            coreId[0] = core.id();
            regionId[0] = core.regionId();
            helper.destroyBlock(relative);
        });

        helper.runAtTickTime(4L, () -> {
            helper.assertTrue(coreId[0] != null && regionId[0] != null,
                    "Dormant core identity must be captured before physical removal");
            var persisted = ShroudSavedData.get(level).state();
            helper.assertTrue(!persisted.cores().containsKey(coreId[0]),
                    "Removing an unactivated DORMANT physical core must not leave an orphan core registration");
            helper.assertTrue(!persisted.regions().containsKey(regionId[0]),
                    "Removing an unactivated DORMANT physical core must not leave an orphan region registration");
            helper.assertBlockNotPresent(ModBlocks.SHROUD_CORE.get(), relative);
            helper.succeed();
        });
    }

    @GameTest(template = "foundation_empty", timeoutTicks = 40)
    public static void destroyingActivePhysicalCoreTransitionsPersistentCoreToDestroyed(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos relative = new BlockPos(1, 1, 1);
        BlockPos absolute = helper.absolutePos(relative);
        UUID[] coreId = new UUID[1];

        helper.setBlock(relative, ModBlocks.SHROUD_CORE.get());

        helper.runAtTickTime(2L, () -> {
            ShroudSavedData data = ShroudSavedData.get(level);
            ShroudCoreState core = data.state().cores().values().stream()
                    .filter(candidate -> candidate.center().equals(absolute))
                    .findFirst()
                    .orElse(null);
            helper.assertTrue(core != null, "Placed core did not register before destruction");
            coreId[0] = core.id();

            CoreMutationResult activation = ShroudCoreService.activate(data.state(), core.id());
            data.replace(activation.state());
            helper.destroyBlock(relative);
        });

        helper.runAtTickTime(4L, () -> {
            helper.assertTrue(coreId[0] != null, "Core id must be captured before physical destruction");
            ShroudCoreState persisted = ShroudSavedData.get(level).state().cores().get(coreId[0]);
            helper.assertTrue(persisted != null, "Destroying the physical core must not delete its persistent identity");
            helper.assertTrue(persisted.lifecycleState() == CoreLifecycleState.DESTROYED,
                    "Destroying an ACTIVE physical core must transition its persistent lifecycle to DESTROYED");
            helper.assertBlockNotPresent(ModBlocks.SHROUD_CORE.get(), relative);
            helper.succeed();
        });
    }
}
