package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Selects one usable physical manifestation actor without owning Story State transitions.
 */
public final class ManifestationDirector {
    private static final String ENCOUNTER_ID_TAG = "EnshroudedEncounterId";

    private final LichManifestationProviderRegistry registry;

    public ManifestationDirector(LichManifestationProviderRegistry registry) {
        this.registry = Objects.requireNonNull(registry, "registry");
    }

    public Optional<ActiveManifestation> spawn(ServerLevel level, EncounterContext context) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(context, "context");

        for (LichManifestationProvider provider : registry.availableProviders()) {
            Optional<LivingEntity> spawned = Objects.requireNonNull(
                    provider.spawn(level, context),
                    () -> "Lich manifestation provider returned null Optional: " + provider.id());
            if (spawned.isEmpty()) {
                continue;
            }

            LivingEntity entity = spawned.orElseThrow();
            if (!isUsable(level, entity)) {
                continue;
            }

            bindEncounter(entity, context.encounterId());
            return Optional.of(new ActiveManifestation(provider.id(), entity));
        }

        return Optional.empty();
    }

    public static Optional<UUID> encounterId(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        var data = entity.getPersistentData();
        if (!data.hasUUID(ENCOUNTER_ID_TAG)) {
            return Optional.empty();
        }
        return Optional.of(data.getUUID(ENCOUNTER_ID_TAG));
    }

    private static boolean isUsable(ServerLevel level, LivingEntity entity) {
        return entity.level() == level
                && entity.isAlive()
                && !entity.isRemoved()
                && level.getEntity(entity.getUUID()) == entity;
    }

    private static void bindEncounter(LivingEntity entity, UUID encounterId) {
        entity.getPersistentData().putUUID(ENCOUNTER_ID_TAG, encounterId);
    }

    public record ActiveManifestation(String providerId, LivingEntity entity) {
        public ActiveManifestation {
            providerId = Objects.requireNonNull(providerId, "providerId");
            entity = Objects.requireNonNull(entity, "entity");
        }
    }
}
