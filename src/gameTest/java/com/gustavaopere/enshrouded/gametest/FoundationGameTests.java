package com.gustavaopere.enshrouded.gametest;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class FoundationGameTests {
    private FoundationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void foundationTemplateServerLevelAndBlockMutationAreAvailable(GameTestHelper helper) {
        GameTestBootstrap.requireServerLevel(helper);
        GameTestBootstrap.setAndAssertBlock(helper, new BlockPos(1, 0, 1), Blocks.STONE);
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void foundationPlayerEntityAndSaveFixturesAreAvailable(GameTestHelper helper) {
        Player player = GameTestBootstrap.makeMockPlayer(helper, GameType.SURVIVAL);
        player.setHealth(10.0F);
        helper.assertTrue(player.getHealth() == 10.0F, "Mock player state must be mutable");

        ArmorStand armorStand = GameTestBootstrap.spawnAndRequireAlive(
                helper,
                EntityType.ARMOR_STAND,
                new BlockPos(1, 1, 1));
        armorStand.setInvisible(true);
        helper.assertTrue(armorStand.isInvisible(), "Spawned entity state must be mutable");

        GameTestBootstrap.forceSaveForReload(helper);
        helper.succeed();
    }
}
