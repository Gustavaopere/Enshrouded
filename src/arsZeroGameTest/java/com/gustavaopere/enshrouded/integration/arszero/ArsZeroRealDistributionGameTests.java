package com.gustavaopere.enshrouded.integration.arszero;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationEncounterService;
import com.gustavaopere.enshrouded.story.manifestation.ManifestationRuntime;
import com.gustavaopere.enshrouded.story.reward.LichSkullItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.phys.AABB;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.List;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ArsZeroRealDistributionGameTests {
    private static final String BATCH = "arsZeroRealDistribution";

    private ArsZeroRealDistributionGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void realDistributionUsesArsZeroLichAndKeepsRewardExactlyOnce(GameTestHelper helper) {
        if (!ModList.get().isLoaded(ArsZeroCompatibilityProbe.MOD_ID)) {
            System.out.println("ENSHROUDED_ARS_ZERO_REAL_FIXTURE_SKIPPED_MOD_ABSENT");
            helper.fail("dedicated Ars Zero fixture requires ars_zero to be loaded");
            return;
        }

        ArsZeroCompatibilityProbe probe = ArsZeroCompatibilityProbe.detect();
        helper.assertTrue(probe.status() == ArsZeroCompatibilityProbe.Status.READY,
                "co-loaded Ars Zero distribution must expose the expected monster registry contract");
        EntityType<?> lichType = probe.lichType()
                .orElseThrow(() -> new AssertionError("READY Ars Zero probe must resolve lich type"));
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(lichType).equals(ArsZeroCompatibilityProbe.LICH_ID),
                "real distribution must resolve exactly ars_zero:lich");

        ServerLevel level = helper.getLevel();
        ArsZeroLichProvider provider = new ArsZeroLichProvider(probe);
        LivingEntity natural = (LivingEntity) lichType.create(level);
        helper.assertTrue(natural != null, "real ars_zero:lich must be constructible on the server");
        if (natural == null) {
            return;
        }
        BlockPos naturalPos = helper.absolutePos(new BlockPos(1, 1, 1));
        natural.moveTo(naturalPos.getX() + 0.5D, naturalPos.getY(), naturalPos.getZ() + 0.5D, 0.0F, 0.0F);
        helper.assertTrue(level.addFreshEntity(natural), "natural Ars Zero Lich fixture must enter the level");
        helper.assertTrue(!provider.matches(natural, UUID.randomUUID()),
                "naturally spawned real Ars Zero Lich must not satisfy Enshrouded encounter identity");
        AABB naturalDrops = natural.getBoundingBox().inflate(3.0D);
        natural.setHealth(0.0F);
        NeoForge.EVENT_BUS.post(new LivingDeathEvent(natural, level.damageSources().generic()));
        helper.assertTrue(authenticDrops(level, naturalDrops, null).isEmpty(),
                "unmarked real Ars Zero Lich death must not emit Enshrouded story reward");
        if (!natural.isRemoved()) {
            natural.discard();
        }

        UUID playerId = UUID.randomUUID();
        ManifestationEncounterService.ActiveEncounter active = ManifestationRuntime.service().start(
                level,
                playerId,
                helper.absolutePos(new BlockPos(4, 1, 1))
        ).orElseThrow(() -> new AssertionError("co-loaded manifestation must start"));
        AABB markedDrops = active.entity().getBoundingBox().inflate(3.0D);
        try {
            helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.getKey(active.entity().getType())
                            .equals(ArsZeroCompatibilityProbe.LICH_ID),
                    "co-loaded runtime must prefer the real Ars Zero Lich provider over native fallback");
            helper.assertTrue(provider.matches(active.entity(), active.encounterId()),
                    "real provider actor must receive the Enshrouded encounter identity");

            active.entity().setHealth(0.0F);
            NeoForge.EVENT_BUS.post(new LivingDeathEvent(active.entity(), level.damageSources().generic()));
            helper.assertTrue(authenticDrops(level, markedDrops, active.encounterId()).size() == 1,
                    "marked real Ars Zero manifestation must emit exactly one authentic Enshrouded skull");

            NeoForge.EVENT_BUS.post(new LivingDeathEvent(active.entity(), level.damageSources().generic()));
            helper.assertTrue(authenticDrops(level, markedDrops, active.encounterId()).size() == 1,
                    "replayed death callback for real provider must not duplicate Enshrouded reward");
            System.out.println("ENSHROUDED_ARS_ZERO_REAL_FIXTURE_PASSED");
            helper.succeed();
        } finally {
            level.getEntitiesOfClass(ItemEntity.class, markedDrops).forEach(ItemEntity::discard);
            if (!active.entity().isRemoved()) {
                active.entity().discard();
            }
        }
    }

    private static List<ItemEntity> authenticDrops(ServerLevel level, AABB bounds, UUID encounterId) {
        return level.getEntitiesOfClass(
                ItemEntity.class,
                bounds,
                entity -> LichSkullItem.isAuthenticLevelOne(entity.getItem())
                        && (encounterId == null
                        || LichSkullItem.encounterId(entity.getItem()).filter(encounterId::equals).isPresent())
        );
    }
}
