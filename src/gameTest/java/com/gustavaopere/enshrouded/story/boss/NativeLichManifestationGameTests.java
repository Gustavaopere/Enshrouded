package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.ecology.state.CorruptionEligibility;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import com.gustavaopere.enshrouded.registry.ModEntities;
import com.gustavaopere.enshrouded.story.state.LichStoryState;
import com.gustavaopere.enshrouded.story.state.StorySavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class NativeLichManifestationGameTests {
    private static final String BATCH = "nativeLichManifestation";

    private NativeLichManifestationGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void standaloneRuntimeSpawnsRegisteredNativeFallbackWithoutOwningStoryTransition(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        BlockPos origin = helper.absolutePos(new BlockPos(1, 1, 1));
        EncounterContext context = new EncounterContext(
                UUID.fromString("60602002-0000-4000-8000-000000000001"),
                origin,
                1,
                60602002L
        );
        LichStoryState storyBefore = StorySavedData.get(level).state();

        ManifestationDirector.ActiveManifestation active = LichBossRuntime.director()
                .spawn(level, context)
                .orElseThrow(() -> new AssertionError("standalone Enshrouded must always have a native Lich fallback"));
        LivingEntity entity = active.entity();

        helper.assertTrue(active.providerId().equals(NativeLichManifestationProvider.ID),
                "standalone runtime must resolve the native provider when no optional provider is installed");
        helper.assertTrue(entity instanceof NativeShroudLichEntity,
                "native provider must spawn the Enshrouded-owned Lich manifestation class");
        helper.assertTrue(entity.getType() == ModEntities.SHROUD_LICH.get(),
                "native manifestation must use the registered enshrouded:shroud_lich EntityType");
        helper.assertTrue(ManifestationDirector.encounterId(entity).orElseThrow().equals(context.encounterId()),
                "native actor must retain the stable encounter UUID assigned by the director");
        helper.assertTrue(entity.getType().is(CorruptionEligibility.BOSS_EXCLUDED),
                "the recurring Lich must be excluded from Stage 04 corruption/buff processing");
        helper.assertTrue(!CorruptionEligibility.isEligible(entity),
                "the native Lich must never become an ecology-corruption target");
        helper.assertTrue(StorySavedData.get(level).state().equals(storyBefore),
                "06.02 physical spawn must not claim 06.03 Story State transition ownership");

        entity.setHealth(1.0F);
        entity.hurt(level.damageSources().generic(), 100.0F);
        helper.assertTrue(!entity.isAlive(), "native Level-1 fallback must remain physically beatable");
        helper.assertTrue(StorySavedData.get(level).state().equals(storyBefore),
                "physical death alone must not fabricate a narrative defeat before 06.03 wires the hook");
        helper.succeed();
    }
}
