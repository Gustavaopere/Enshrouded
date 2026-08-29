package com.gustavaopere.enshrouded.exposure.redsludge;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.shroud.expansion.ShroudGridGeometry;
import com.gustavaopere.enshrouded.shroud.query.DefaultShroudQuery;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRule;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionRuleRegistry;
import com.gustavaopere.enshrouded.shroud.terrain.CorruptionSafetyClass;
import com.gustavaopere.enshrouded.shroud.terrain.ShroudMaterializationService;
import com.mojang.authlib.GameProfile;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RedSludgeGameTests {
    private static final UUID CONTACT_PLAYER_ID = UUID.fromString("ae48d882-6aac-43d4-94f0-92737e73b21d");
    private static final UUID RELOCATED_PLAYER_ID = UUID.fromString("3075c9a7-fe66-4f22-9c6e-e72a07ebd74f");

    private RedSludgeGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void levelOneContactTriggersDeadlyExposureImmediately(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(CONTACT_PLAYER_ID, "RedSludgeContact"));
        RedSludgeExposureHandler.forget(player.getUUID());
        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        player.setData(attachmentType, new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 1_000));

        RedSludgeExposureHandler.onContact(player);

        ShroudExposureAttachment after = player.getData(attachmentType);
        helper.assertTrue(after.remainingTicks() < 1_000,
                "Level-1 Red Sludge contact must immediately force Deadly exposure instead of waiting for logical region sampling");

        RedSludgeExposureHandler.onContact(player);
        helper.assertValueEqual(player.getData(attachmentType).remainingTicks(), after.remainingTicks(),
                "multiple collision callbacks in the same server tick must not duplicate forced Deadly exposure");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void secondaryContactDamageIsBoundedPerServerTick(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        RedSludgeExposureHandler.forget(player.getUUID());
        int[] damageCalls = {0};
        float[] lastDamage = {0.0F};

        RedSludgeExposureHandler.onContact(
                player,
                ignored -> { },
                (ignored, amount) -> {
                    damageCalls[0]++;
                    lastDamage[0] = amount;
                }
        );

        helper.assertValueEqual(damageCalls[0], 1, "Red Sludge secondary damage emission count");
        helper.assertTrue(lastDamage[0] == 1.0F,
                "Red Sludge contact must emit the bounded 1.0F secondary damage amount");

        RedSludgeExposureHandler.onContact(
                player,
                ignored -> { },
                (ignored, amount) -> damageCalls[0]++
        );
        helper.assertValueEqual(damageCalls[0], 1,
                "multiple collision callbacks in the same server tick must not duplicate direct Red Sludge damage");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void relocatedSludgeRemainsLocalHazardWithoutCreatingLogicalShroud(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos relative = new BlockPos(0, 0, 1);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, ModBlocks.RED_SLUDGE.get());
        helper.assertBlockPresent(ModBlocks.RED_SLUDGE.get(), relative);

        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(RELOCATED_PLAYER_ID, "RelocatedSludge"));
        RedSludgeExposureHandler.forget(player.getUUID());
        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        player.setData(attachmentType, new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 1_000));

        var query = DefaultShroudQuery.levelOne(ShroudGridGeometry.levelOne());
        ShroudSample logicalBefore = query.sample(level, absolute, player);
        helper.assertTrue(logicalBefore.severity() == ShroudSeverity.CLEAR,
                "physical Red Sludge outside a logical region must not become authoritative Shroud state");

        RedSludgeExposureHandler.onContact(player);

        ShroudSample logicalAfter = query.sample(level, absolute, player);
        helper.assertTrue(player.getData(attachmentType).remainingTicks() < 1_000,
                "relocated physical Red Sludge must remain locally hazardous");
        helper.assertTrue(logicalAfter.severity() == ShroudSeverity.CLEAR && logicalAfter.intensity() == 0.0F,
                "physical fluid contact must not create a core, region, cell or synthetic logical intensity");
        helper.succeed();
    }

    @GameTest(template = "foundation_empty")
    public static void ordinaryShroudMaterializerNeverEmitsRedSludge(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        BlockPos relative = new BlockPos(0, 0, 1);
        BlockPos absolute = helper.absolutePos(relative);
        helper.setBlock(relative, Blocks.RED_SAND);

        CorruptionRule redSludgeRule = new CorruptionRule(
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "red_sludge"),
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "red_sludge_sources"),
                ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "red_sludge"),
                ResourceLocation.withDefaultNamespace("red_sand"),
                0.0F,
                ShroudSeverity.DEADLY,
                CorruptionSafetyClass.SAFE
        );
        ShroudSample ordinaryShroud = new ShroudSample(
                1.0F,
                ShroudSeverity.SHROUD,
                Optional.empty(),
                false
        );
        ShroudMaterializationService service = new ShroudMaterializationService(
                new CorruptionRuleRegistry(List.of(redSludgeRule)),
                (candidateLevel, pos, kind) -> true,
                (candidateLevel, pos, entity) -> ordinaryShroud,
                MutationSafetyMode.SAFE,
                8
        );

        helper.assertFalse(service.schedule(level, absolute, ordinaryShroud),
                "ordinary SHROUD severity must not queue DEADLY-only Red Sludge materialization");
        helper.assertValueEqual(service.pendingWork(), 0, "ordinary Shroud Red Sludge queue size");
        helper.assertValueEqual(service.tick(level, 8, 8), 0, "ordinary Shroud Red Sludge mutation count");
        helper.assertBlockPresent(Blocks.RED_SAND, relative);
        helper.succeed();
    }
}
