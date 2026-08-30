package com.gustavaopere.enshrouded.ecology.purification;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.ecology.combat.CorruptedCombatRuntime;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionSchema;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class EntityPurificationGameTests {
    private static final double EPSILON = 1.0E-9D;

    private EntityPurificationGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void purificationRemovesOnlyEnshroudedEcologyState(GameTestHelper helper) {
        var cow = helper.spawn(EntityType.COW, 2, 2, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(player != null, "GameTest must create a survival player fixture");
        player.setPos(cow.getX() + 2.0D, cow.getY(), cow.getZ());

        double baselineMaxHealth = cow.getAttributeValue(Attributes.MAX_HEALTH);
        cow.getPersistentData().putString("third_party_marker", "preserve-me");
        cow.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 200, 1));
        cow.setData(
                EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 1.0F)
        );
        CorruptedCombatRuntime.synchronize(cow, 1.0D, List.of(player));

        helper.assertTrue(cow.getTarget() == player,
                "fixture must own an Enshrouded corruption target before purification");
        helper.assertTrue(cow.getAttributeValue(Attributes.MAX_HEALTH) > baselineMaxHealth,
                "fixture must own Enshrouded corruption modifiers before purification");

        EntityPurificationService.purify(cow);

        helper.assertTrue(cow.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION.get()) == null,
                "purification must remove canonical corruption state so synced visual cues disappear");
        helper.assertTrue(cow.getTarget() == null,
                "purification must release the target owned by Enshrouded corruption");
        helper.assertTrue(Math.abs(cow.getAttributeValue(Attributes.MAX_HEALTH) - baselineMaxHealth) <= EPSILON,
                "purification must remove Enshrouded-owned transient attributes");
        helper.assertTrue(cow.hasEffect(MobEffects.MOVEMENT_SPEED),
                "purification must preserve unrelated potion effects");
        helper.assertTrue("preserve-me".equals(cow.getPersistentData().getString("third_party_marker")),
                "purification must preserve unrelated third-party persistent data");
        helper.succeed();
    }
}
