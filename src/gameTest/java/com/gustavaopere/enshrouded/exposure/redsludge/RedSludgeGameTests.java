package com.gustavaopere.enshrouded.exposure.redsludge;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.mojang.authlib.GameProfile;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.common.util.FakePlayer;
import net.neoforged.neoforge.common.util.FakePlayerFactory;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class RedSludgeGameTests {
    private static final UUID CONTACT_PLAYER_ID = UUID.fromString("ae48d882-6aac-43d4-94f0-92737e73b21d");

    private RedSludgeGameTests() {
    }

    @GameTest(template = "foundation_empty")
    public static void levelOneContactTriggersDeadlyExposureImmediately(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        FakePlayer player = FakePlayerFactory.get(level, new GameProfile(CONTACT_PLAYER_ID, "RedSludgeContact"));
        var attachmentType = ShroudExposureAttachment.PLAYER_EXPOSURE.get();
        player.setData(attachmentType, new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, 1_000));

        RedSludgeExposureHandler.onContact(player);

        ShroudExposureAttachment after = player.getData(attachmentType);
        helper.assertTrue(after.remainingTicks() < 1_000,
                "Level-1 Red Sludge contact must immediately force Deadly exposure instead of waiting for logical region sampling");
        helper.succeed();
    }
}
