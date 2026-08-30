package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.gametest.GameTestBootstrap;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;

import java.util.Optional;
import java.util.UUID;

@GameTestHolder(Enshrouded.MOD_ID)
@PrefixGameTestTemplate(false)
public final class ManifestationBossBarGameTests {
    private static final String BATCH = "manifestationBossBar";

    private ManifestationBossBarGameTests() {
    }

    @GameTest(template = "foundation_empty", batch = BATCH)
    public static void directorOwnsOneProviderNeutralBossBarThatMirrorsActorHealth(GameTestHelper helper) {
        ServerLevel level = GameTestBootstrap.requireServerLevel(helper);
        EncounterContext context = new EncounterContext(
                UUID.fromString("60602002-0000-4000-8000-000000000003"),
                helper.absolutePos(new BlockPos(1, 1, 1)),
                1,
                60602004L
        );
        LichManifestationProvider provider = new LichManifestationProvider() {
            @Override
            public String id() {
                return "optional:test-bossbar";
            }

            @Override
            public boolean isAvailable() {
                return true;
            }

            @Override
            public Optional<LivingEntity> spawn(ServerLevel requestedLevel, EncounterContext requestedContext) {
                Zombie zombie = EntityType.ZOMBIE.create(requestedLevel);
                if (zombie == null) {
                    return Optional.empty();
                }
                zombie.moveTo(requestedContext.origin().getX() + 0.5D, requestedContext.origin().getY(), requestedContext.origin().getZ() + 0.5D);
                return requestedLevel.addFreshEntity(zombie) ? Optional.of(zombie) : Optional.empty();
            }

            @Override
            public boolean matches(Entity entity, UUID encounterId) {
                return ManifestationDirector.encounterId(entity).filter(encounterId::equals).isPresent();
            }
        };

        ManifestationDirector director = new ManifestationDirector(new LichManifestationProviderRegistry(provider));
        ManifestationDirector.ActiveManifestation active = director.spawn(level, context)
                .orElseThrow(() -> new AssertionError("synthetic provider must spawn for bossbar contract"));

        helper.assertTrue(active.bossEvent().getName().equals(active.entity().getDisplayName()),
                "director-owned bossbar must use the accepted physical actor display name regardless of provider");
        helper.assertTrue(Math.abs(active.bossEvent().getProgress() - 1.0F) < 0.001F,
                "director-owned bossbar must begin synchronized to full actor health");

        active.entity().setHealth(active.entity().getMaxHealth() * 0.25F);
        active.syncBossEvent();

        helper.assertTrue(Math.abs(active.bossEvent().getProgress() - 0.25F) < 0.001F,
                "provider-neutral bossbar must mirror accepted actor health after synchronization");

        active.entity().discard();
        helper.succeed();
    }
}
