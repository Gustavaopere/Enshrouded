package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

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

        long ownedAtPosition = ShroudSavedData.get(level).state().cores().values().stream()
                .filter(core -> core.center().equals(absolute))
                .count();
        helper.assertTrue(ownedAtPosition == 1L,
                "Placed Shroud core must register exactly one persistent identity at its absolute position");
        helper.succeed();
    }
}
