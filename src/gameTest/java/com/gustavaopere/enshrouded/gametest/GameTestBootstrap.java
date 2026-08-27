package com.gustavaopere.enshrouded.gametest;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;

/** Reusable world-level fixtures for Enshrouded GameTests. */
public final class GameTestBootstrap {
    private GameTestBootstrap() {
    }

    public static ServerLevel requireServerLevel(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        helper.assertTrue(level != null, "GameTest must expose a server level");
        return level;
    }

    public static void setAndAssertBlock(GameTestHelper helper, BlockPos relativePos, Block block) {
        helper.setBlock(relativePos, block);
        helper.assertBlockPresent(block, relativePos);
    }

    public static Player makeMockPlayer(GameTestHelper helper, GameType gameType) {
        Player player = helper.makeMockPlayer(gameType);
        helper.assertTrue(player != null, "GameTest must create a mock player");
        return player;
    }

    public static <T extends Entity> T spawnAndRequireAlive(
            GameTestHelper helper,
            EntityType<T> type,
            BlockPos relativePos) {
        T entity = helper.spawn(type, relativePos.getX(), relativePos.getY(), relativePos.getZ());
        helper.assertTrue(entity.isAlive(), "Spawned GameTest entity must be alive");
        helper.assertTrue(entity.level() == helper.getLevel(), "Spawned GameTest entity must belong to the test level");
        return entity;
    }

    /**
     * Flushes current server state to storage so a dedicated-server restart harness can verify reload behavior.
     * A single GameTest process cannot perform a true server restart safely, so restart/reload is deliberately
     * owned by the external dedicated-server test harness.
     */
    public static void forceSaveForReload(GameTestHelper helper) {
        boolean saved = requireServerLevel(helper).getServer().saveEverything(true, true, true);
        helper.assertTrue(saved, "GameTest server must report a successful forced save");
    }
}
