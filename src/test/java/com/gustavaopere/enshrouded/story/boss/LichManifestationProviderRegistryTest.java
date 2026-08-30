package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class LichManifestationProviderRegistryTest {
    @Test
    void availableProvidersAreOrderedByPriorityThenStableIdWithNativeFallbackLast() {
        FakeProvider nativeFallback = new FakeProvider("enshrouded:native", true);
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(nativeFallback);

        registry.register(new FakeProvider("example:zeta", true), 100);
        registry.register(new FakeProvider("example:alpha", true), 100);
        registry.register(new FakeProvider("example:unavailable", false), 1000);
        registry.register(new FakeProvider("example:lower", true), 10);

        assertEquals(
                List.of("example:alpha", "example:zeta", "example:lower", "enshrouded:native"),
                registry.availableProviders().stream().map(LichManifestationProvider::id).toList()
        );
    }

    @Test
    void duplicateProviderIdsAreRejectedRatherThanSilentlyChangingSelection() {
        FakeProvider nativeFallback = new FakeProvider("enshrouded:native", true);
        LichManifestationProviderRegistry registry = new LichManifestationProviderRegistry(nativeFallback);
        registry.register(new FakeProvider("example:boss", true), 100);

        assertThrows(
                IllegalArgumentException.class,
                () -> registry.register(new FakeProvider("example:boss", true), 200)
        );
    }

    private record FakeProvider(String id, boolean available) implements LichManifestationProvider {
        @Override
        public boolean isAvailable() {
            return available;
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
