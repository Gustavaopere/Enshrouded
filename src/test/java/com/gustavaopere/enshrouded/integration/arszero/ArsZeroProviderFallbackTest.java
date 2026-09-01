package com.gustavaopere.enshrouded.integration.arszero;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import com.gustavaopere.enshrouded.story.boss.LichManifestationProviderRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

final class ArsZeroProviderFallbackTest {
    @Test
    void unavailableArsZeroProviderLeavesNativeFallbackSelected() {
        NativeStub nativeFallback = new NativeStub();
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(nativeFallback);
        ArsZeroLichProvider arsZero = new ArsZeroLichProvider(
                ArsZeroCompatibilityProbe.inspect(false, ignored -> Optional.empty())
        );
        registry.register(arsZero, ArsZeroIntegrationRuntime.PROVIDER_PRIORITY);

        assertFalse(arsZero.isAvailable());
        assertEquals(
                List.of("enshrouded:test-native"),
                registry.availableProviders().stream().map(LichManifestationProvider::id).toList()
        );
    }

    private static final class NativeStub implements LichManifestationProvider {
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
        public boolean matches(Entity entity, UUID encounterId) {
            return false;
        }
    }
}
