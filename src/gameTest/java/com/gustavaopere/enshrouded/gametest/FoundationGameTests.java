package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoundationGameTests {
    private FoundationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void foundationTemplateAndServerLevelAreAvailable(GameTestHelper helper) {
        helper.assertTrue(helper.getLevel() != null, "GameTest must expose a server level");
        helper.assertBlockPresent(Blocks.STONE, new BlockPos(0, 0, 0));
        helper.succeed();
    }
}
