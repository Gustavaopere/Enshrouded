package com.gustavaopere.enshrouded.ecology.ai;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.ecology.combat.CorruptionCombatPolicy;
import com.gustavaopere.enshrouded.ecology.combat.CorruptedAttributeModifiers;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CorruptedHostilityGameTests {
    private static final double EPSILON = 1.0E-9D;

    private CorruptedHostilityGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void corruptedPassiveTargetsSurvivalPlayerAndPurificationReleasesOwnedTarget(GameTestHelper helper) {
        CorruptionCombatPolicy policy = CorruptionCombatPolicy.levelOne();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(player != null, "GameTest must create a survival player fixture");

        var cleanCow = helper.spawn(EntityType.COW, 2, 2, 2);
        player.setPos(cleanCow.getX() + 2.0D, cleanCow.getY(), cleanCow.getZ());
        CorruptedTargetingService.synchronize(cleanCow, policy, 0.0D, List.of(player));
        helper.assertTrue(cleanCow.getTarget() == null,
                "Clean passive counterpart must not gain Enshrouded player aggression");

        var corruptedCow = helper.spawn(EntityType.COW, 6, 2, 2);
        player.setPos(corruptedCow.getX() + 2.0D, corruptedCow.getY(), corruptedCow.getZ());
        CorruptedTargetingService.synchronize(corruptedCow, policy, 1.0D, List.of(player));
        helper.assertTrue(corruptedCow.getTarget() == player,
                "CORRUPTED passive mob must acquire the nearby survival player as its server target");

        CorruptedTargetingService.synchronize(corruptedCow, policy, 0.0D, List.of(player));
        helper.assertTrue(corruptedCow.getTarget() == null,
                "Purification must remove only Enshrouded-owned target behavior");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void hostileRetainsNativeTargetWhileCorruptionChangesStatsIdempotently(GameTestHelper helper) {
        CorruptionCombatPolicy policy = CorruptionCombatPolicy.levelOne();
        var hostile = helper.spawn(EntityType.ZOMBIE, 2, 2, 6);
        var nativeTarget = helper.spawn(EntityType.COW, 5, 2, 6);
        hostile.setTarget(nativeTarget);
        double baseMaxHealth = hostile.getAttributeValue(Attributes.MAX_HEALTH);

        CorruptedTargetingService.synchronize(hostile, policy, 1.0D, List.of());
        CorruptedAttributeModifiers.synchronize(hostile, policy.attributeProfile(1.0D));
        double once = hostile.getAttributeValue(Attributes.MAX_HEALTH);
        CorruptedAttributeModifiers.synchronize(hostile, policy.attributeProfile(1.0D));
        double twice = hostile.getAttributeValue(Attributes.MAX_HEALTH);

        helper.assertTrue(hostile.getTarget() == nativeTarget,
                "Native hostile targeting must remain untouched by Enshrouded corruption AI");
        helper.assertTrue(once > baseMaxHealth,
                "Corrupted hostile must receive stronger bounded stats without replacement AI");
        helper.assertTrue(Math.abs(once - twice) <= EPSILON,
                "Repeated corruption synchronization must remain idempotent and never stack duplicate modifiers");
        helper.assertTrue(once <= baseMaxHealth * (1.0D + CorruptionCombatPolicy.MAX_HEALTH_CAP) + EPSILON,
                "Corrupted max health must remain inside the Level-1 hard cap");

        CorruptedAttributeModifiers.synchronize(hostile, policy.attributeProfile(0.0D));
        helper.assertTrue(Math.abs(hostile.getAttributeValue(Attributes.MAX_HEALTH) - baseMaxHealth) <= EPSILON,
                "Purification must remove Enshrouded-owned attribute modifiers cleanly");
        helper.succeed();
    }
}
