package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionSchema;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MagicResistanceGameTests {
    private static final float DAMAGE = 4.0F;
    private static final float EPSILON = 0.001F;

    private MagicResistanceGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void corruptedMobTakesLessTaggedMagicDamageThanCleanCounterpart(GameTestHelper helper) {
        var clean = helper.spawn(EntityType.COW, 2, 2, 2);
        var corrupted = helper.spawn(EntityType.COW, 6, 2, 2);
        corrupted.setData(
                EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 1.0F)
        );

        float cleanBefore = clean.getHealth();
        float corruptedBefore = corrupted.getHealth();
        var source = helper.getLevel().damageSources().magic();

        boolean cleanHurt = clean.hurt(source, DAMAGE);
        boolean corruptedHurt = corrupted.hurt(source, DAMAGE);
        float cleanLoss = cleanBefore - clean.getHealth();
        float corruptedLoss = corruptedBefore - corrupted.getHealth();

        helper.assertTrue(cleanHurt && corruptedHurt, "both magic-damage fixtures must accept the hit");
        helper.assertTrue(Math.abs(cleanLoss - DAMAGE) <= EPSILON,
                "clean cow must receive the full tagged magic damage");
        helper.assertTrue(corruptedLoss < cleanLoss,
                "corrupted cow must receive less positively classified magic damage than the clean counterpart");
        helper.assertTrue(Math.abs(corruptedLoss - 2.60F) <= EPSILON,
                "Level-1 default full corruption must reduce 4 magic damage by the configured 35% target");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void ordinaryMeleeDamageRemainsUnchangedByCorruption(GameTestHelper helper) {
        var clean = helper.spawn(EntityType.COW, 2, 2, 6);
        var corrupted = helper.spawn(EntityType.COW, 6, 2, 6);
        var attacker = helper.spawn(EntityType.ZOMBIE, 10, 2, 6);
        corrupted.setData(
                EntityCorruptionAttachment.ENTITY_CORRUPTION.get(),
                new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 1.0F)
        );

        float cleanBefore = clean.getHealth();
        float corruptedBefore = corrupted.getHealth();
        var source = helper.getLevel().damageSources().mobAttack(attacker);

        boolean cleanHurt = clean.hurt(source, DAMAGE);
        boolean corruptedHurt = corrupted.hurt(source, DAMAGE);
        float cleanLoss = cleanBefore - clean.getHealth();
        float corruptedLoss = corruptedBefore - corrupted.getHealth();

        helper.assertTrue(cleanHurt && corruptedHurt, "both melee fixtures must accept the hit");
        helper.assertTrue(Math.abs(cleanLoss - corruptedLoss) <= EPSILON,
                "ordinary melee damage must be identical for clean and corrupted mobs");
        helper.assertTrue(Math.abs(cleanLoss - DAMAGE) <= EPSILON,
                "ordinary unarmored melee fixture must preserve its full damage amount");
        helper.succeed();
    }
}
