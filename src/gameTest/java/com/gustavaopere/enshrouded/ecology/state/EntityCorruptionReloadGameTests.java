package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cow;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EntityCorruptionReloadGameTests {
    private static final UUID SENTINEL_ID = UUID.fromString("74444444-5555-6666-7777-888888888888");
    private static final BlockPos SENTINEL_POS = new BlockPos(448, 96, 448);
    private static final float SENTINEL_INTENSITY = 0.75F;
    private static final long ENTITY_LOAD_SETTLE_TICKS = 20L;

    private EntityCorruptionReloadGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void entityCorruptionAttachmentSurvivesRealServerRestart(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        level.getChunkAt(SENTINEL_POS);
        helper.runAfterDelay(ENTITY_LOAD_SETTLE_TICKS, () -> verifyReloadOrCreate(helper, level));
    }

    private static void verifyReloadOrCreate(GameTestHelper helper, ServerLevel level) {
        Entity existing = level.getEntity(SENTINEL_ID);

        if (existing == null) {
            Cow cow = EntityType.COW.create(level);
            helper.assertTrue(cow != null, "sentinel cow must be constructible");
            cow.setUUID(SENTINEL_ID);
            cow.moveTo(SENTINEL_POS.getX() + 0.5D, SENTINEL_POS.getY(), SENTINEL_POS.getZ() + 0.5D);
            cow.setPersistenceRequired();
            cow.setData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                    new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, SENTINEL_INTENSITY));
            helper.assertTrue(level.addFreshEntity(cow),
                    "sentinel cow must enter the test level; duplicate UUID means persisted entity loading was not observed");
            GameTestBootstrap.forceSaveForReload(helper);
            System.out.println("ENSHROUDED_ENTITY_CORRUPTION_CREATED");
            helper.succeed();
            return;
        }

        helper.assertTrue(existing instanceof Cow, "reloaded corruption sentinel must preserve entity type");
        helper.assertTrue(existing.getType() == EntityType.COW, "reloaded corruption sentinel must still be a cow");
        helper.assertTrue(SENTINEL_ID.equals(existing.getUUID()), "reloaded corruption sentinel must preserve UUID identity");
        EntityCorruptionAttachment loaded = existing.getData(EntityCorruptionAttachment.ENTITY_CORRUPTION.get());
        helper.assertTrue(Math.abs(loaded.intensity() - SENTINEL_INTENSITY) < 0.0001F,
                "real restart must preserve entity corruption intensity");
        helper.assertTrue(loaded.stage() == CorruptionStage.CORRUPTED,
                "real restart must preserve derived corruption stage");
        System.out.println("ENSHROUDED_ENTITY_CORRUPTION_RELOADED");
        helper.succeed();
    }
}
