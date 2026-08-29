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
    public static void hostileRetainsNativeTargetWhileCorruptionChangesStats(GameTestHelper helper) {
        CorruptionCombatPolicy policy = CorruptionCombatPolicy.levelOne();
        var hostile = helper.spawn(EntityType.ZOMBIE, 2, 2, 6);
        var nativeTarget = helper.spawn(EntityType.COW, 5, 2, 6);
        hostile.setTarget(nativeTarget);
        double baseMaxHealth = hostile.getAttributeValue(Attributes.MAX_HEALTH);

        CorruptedTargetingService.synchronize(hostile, policy, 1.0D, List.of());
        CorruptedAttributeModifiers.synchronize(hostile, policy.attributeProfile(1.0D));

        helper.assertTrue(hostile.getTarget() == nativeTarget,
                "Native hostile targeting must remain untouched by Enshrouded corruption AI");
        helper.assertTrue(hostile.getAttributeValue(Attributes.MAX_HEALTH) > baseMaxHealth,
                "Corrupted hostile must receive stronger bounded stats without replacement AI");
        helper.succeed();
    }
}
