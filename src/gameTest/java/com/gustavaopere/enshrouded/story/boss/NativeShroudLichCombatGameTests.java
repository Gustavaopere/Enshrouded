package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NativeShroudLichCombatGameTests {
    private static final String BATCH = "nativeShroudLichCombat";

    private NativeShroudLichCombatGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void nativeFallbackHasShroudRangedAttackAndHealthDrivenSecondPhase(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        EncounterContext context = new EncounterContext(
                UUID.fromString("60602002-0000-4000-8000-000000000002"),
                helper.absolutePos(new BlockPos(1, 1, 1)),
                1,
                60602003L
        );
        LichStoryState storyBefore = StorySavedData.get(level).state();
        NativeShroudLichEntity lich = (NativeShroudLichEntity) LichBossRuntime.director()
                .spawn(level, context)
                .orElseThrow(() -> new AssertionError("native Lich must spawn for combat contract test"))
                .entity();

        ArmorStand target = new ArmorStand(EntityType.ARMOR_STAND, level);
        target.moveTo(context.origin().getX() + 6.5D, context.origin().getY(), context.origin().getZ() + 0.5D);
        helper.assertTrue(level.addFreshEntity(target), "stationary living target must be added for ranged contract test");

        helper.assertTrue(lich.combatPhase() == 1, "native Lich must begin in phase 1");
        double phaseOneSpeed = lich.getAttributeValue(Attributes.MOVEMENT_SPEED);
        float targetHealthBefore = target.getHealth();

        lich.castShroudBoltAt(target);

        helper.assertTrue(target.getHealth() < targetHealthBefore,
                "native ranged Shroud attack must damage a living target without requiring melee contact");
        helper.assertTrue(target.hasEffect(MobEffects.DARKNESS),
                "native ranged Shroud attack must carry an Enshrouded-owned darkness/necromantic rider");

        lich.setHealth((float) (NativeShroudLichEntity.MAX_HEALTH * 0.45D));
        lich.aiStep();

        helper.assertTrue(lich.combatPhase() == 2,
                "native Lich must transition to phase 2 at or below half health");
        helper.assertTrue(lich.getAttributeValue(Attributes.MOVEMENT_SPEED) > phaseOneSpeed,
                "phase 2 must materially increase native Lich mobility");
        helper.assertTrue(StorySavedData.get(level).state().equals(storyBefore),
                "06.02 combat behavior must not own 06.03 story transitions");

        target.discard();
        lich.discard();
        helper.succeed();
    }
}
