package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EntityCorruptionCombatRuntimeGameTests {
    private static final BlockPos CLEAR_COMBAT_POS = new BlockPos(-20480, 96, -20480);
    private static final double EPSILON = 1.0E-9D;

    private EntityCorruptionCombatRuntimeGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void runtimeAppliesAndPurificationCleansCombatEffects(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        level.getChunkAt(CLEAR_COMBAT_POS);
        Cow cow = EntityType.COW.create(level);
        helper.assertTrue(cow != null, "cow fixture must be constructible");
        cow.moveTo(CLEAR_COMBAT_POS.getX() + 0.5D, CLEAR_COMBAT_POS.getY(), CLEAR_COMBAT_POS.getZ() + 0.5D);
        helper.assertTrue(level.addFreshEntity(cow), "cow fixture must enter the test level");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(player != null, "GameTest must create a survival player fixture");
        player.setPos(cow.getX() + 2.0D, cow.getY(), cow.getZ());

        var sample = DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne())
                .sample(level, cow.blockPosition(), cow);
        helper.assertTrue(sample.severity() == ShroudSeverity.CLEAR && sample.intensity() == 0.0F,
                "runtime combat fixture must explicitly sample canonical CLEAR state");

        double baseMaxHealth = cow.getAttributeValue(Attributes.MAX_HEALTH);
        cow.setData(
                EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 1.0F)
        );

        EntityCorruptionRuntime.advanceNow(cow, List.of(player));

        helper.assertTrue(cow.getTarget() == player,
                "entity corruption runtime must connect CORRUPTED passive state to player targeting");
        helper.assertTrue(cow.getAttributeValue(Attributes.MAX_HEALTH) > baseMaxHealth,
                "entity corruption runtime must connect corruption intensity to bounded stat modifiers");

        cow.setData(
                EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 0.01F)
        );
        EntityCorruptionRuntime.advanceNow(cow, List.of(player));

        helper.assertTrue(cow.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()) == null,
                "clear-space regression below zero must remove the corruption attachment");
        helper.assertTrue(cow.getTarget() == null,
                "purification must release only the target owned by Enshrouded corruption runtime");
        helper.assertTrue(Math.abs(cow.getAttributeValue(Attributes.MAX_HEALTH) - baseMaxHealth) <= EPSILON,
                "purification must remove Enshrouded-owned transient attribute modifiers");

        cow.discard();
        helper.succeed();
    }
}
