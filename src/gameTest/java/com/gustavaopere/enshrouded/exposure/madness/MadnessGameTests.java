package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.DeadlyExposurePolicy;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureService;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class MadnessGameTests {
    private static final UUID FATAL_PLAYER_ID = UUID.fromString("dbec3dfd-5c80-4375-b71a-8f4619928f67");
    private static final UUID RECOVERY_PLAYER_ID = UUID.fromString("d099cf89-8117-4612-a9ae-7e111c7ce0df");

    private MadnessGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void armoredResistancePlayerStillDiesAtExposureExhaustion(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(FATAL_PLAYER_ID, "MadnessFatal"));
        player.setHealth(player.getMaxHealth());
        player.setItemSlot(EquipmentSlot.HEAD, new ItemStack(Items.NETHERITE_HELMET));
        player.setItemSlot(EquipmentSlot.CHEST, new ItemStack(Items.NETHERITE_CHESTPLATE));
        player.setItemSlot(EquipmentSlot.LEGS, new ItemStack(Items.NETHERITE_LEGGINGS));
        player.setItemSlot(EquipmentSlot.FEET, new ItemStack(Items.NETHERITE_BOOTS));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 200, 4));

        ExposureSnapshot exhausted = new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                0,
                1000,
                0.75F,
                ShroudSeverity.SHROUD,
                false,
                false
        );
        helper.assertTrue(exhausted.madnessStage() == MadnessStage.FATAL,
                "Zero authoritative exposure reserve must derive FATAL Madness");

        MadnessRuntime.apply(player, exhausted);
        helper.assertTrue(!player.isAlive(),
                "FATAL Madness must kill an armored Resistance V survival player authoritatively");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void leavingShroudBeforeZeroRecoversAndClearsSprintPenalty(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(RECOVERY_PLAYER_ID, "MadnessRecovery"));
        player.setHealth(player.getMaxHealth());
        ExposureSnapshot critical = new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                80,
                1000,
                0.75F,
                ShroudSeverity.SHROUD,
                false,
                false
        );
        helper.assertTrue(critical.madnessStage() == MadnessStage.CRITICAL,
                "Eight percent reserve must be CRITICAL");

        player.setSprinting(true);
        MadnessRuntime.apply(player, critical);
        helper.assertTrue(!player.isSprinting(),
                "Configured CRITICAL Madness must restrain sprint server-side without a persistent effect");

        ExposureService recovery = new ExposureService(
                1000,
                1,
                10,
                100,
                DeadlyExposurePolicy.levelOneBarrier()
        );
        ExposureSnapshot recovered = recovery.tick(
                player.getUUID(),
                critical.attachmentState(),
                ShroudSample.clear(),
                50
        );
        helper.assertTrue(recovered.remainingTicks() == 580,
                "Clear-space recovery must operate on the same exposure reserve");
        helper.assertTrue(recovered.madnessStage() == MadnessStage.STABLE,
                "Recovered reserve above fifty percent must clear escalating Madness stage");

        player.setSprinting(true);
        MadnessRuntime.apply(player, recovered);
        helper.assertTrue(player.isSprinting(),
                "Recovered non-critical Madness must not leave a lingering sprint penalty");
        helper.succeed();
    }
}
