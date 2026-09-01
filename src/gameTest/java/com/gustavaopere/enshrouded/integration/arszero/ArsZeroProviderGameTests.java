package com.gustavaopere.enshrouded.integration.arszero;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.story.boss.LichManifestationProviderRegistry;
import com.gustavaopere.enshrouded.story.boss.ManifestationDirector;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

public final class ArsZeroProviderGameTests {
    @GameTest(template = "empty")
    public static void registryProxySpawnsAndReceivesEncounterIdentity(GameTestHelper helper) {
        ArsZeroLichProvider provider = proxyProvider();
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(new EmptyFallback());
        registry.register(provider, ArsZeroIntegrationRuntime.PROVIDER_PRIORITY);
        ManifestationDirector director = new ManifestationDirector(registry);
        UUID encounterId = UUID.randomUUID();
        ServerLevel level = helper.getLevel();
        BlockPos origin = helper.absolutePos(new BlockPos(1, 1, 1));

        ManifestationDirector.ActiveManifestation active = director.spawn(
                level,
                new EncounterContext(encounterId, origin, 1, 1L)
        ).orElseThrow();

        helper.assertTrue(active.providerId().equals(ArsZeroLichProvider.ID), "Ars Zero provider should outrank fallback");
        helper.assertTrue(active.entity().getType() == EntityType.ZOMBIE, "proxy must spawn the resolved registry type");
        helper.assertTrue(provider.matches(active.entity(), encounterId), "provider actor must match its bound encounter");
        helper.succeed();
    }

    @GameTest(template = "empty")
    public static void unrelatedRegistryProxyWithoutEncounterTagDoesNotMatch(GameTestHelper helper) {
        ArsZeroLichProvider provider = proxyProvider();
        LivingEntity natural = EntityType.ZOMBIE.create(helper.getLevel());
        helper.assertTrue(natural != null, "proxy entity should construct");
        if (natural == null) {
            return;
        }
        natural.moveTo(helper.absolutePos(new BlockPos(1, 1, 1)).getCenter());
        helper.getLevel().addFreshEntity(natural);

        helper.assertTrue(
                !provider.matches(natural, UUID.randomUUID()),
                "same entity type without Enshrouded encounter identity must not match"
        );
        helper.succeed();
    }

    private static ArsZeroLichProvider proxyProvider() {
        return new ArsZeroLichProvider(
                ArsZeroCompatibilityProbe.inspect(
                        true,
                        id -> id.equals(ArsZeroCompatibilityProbe.LICH_ID)
                                ? Optional.of(EntityType.ZOMBIE)
                                : Optional.empty()
                )
        );
    }

    private static final class EmptyFallback implements LichManifestationProvider {
        @Override
        public String id() {
            return "enshrouded:test-native";
        }

        @Override
        public boolean isAvailable() {
            return true;
        }

        @Override
        public Optional<LivingEntity> spawn(ServerLevel level, EncounterContext context) {
            return Optional.empty();
        }

        @Override
        public boolean matches(net.minecraft.world.entity.Entity entity, UUID encounterId) {
            return false;
        }
    }
}
