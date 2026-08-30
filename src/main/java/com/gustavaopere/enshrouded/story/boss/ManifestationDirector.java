package com.gustavaopere.enshrouded.story.boss;

import com.gustavaopere.enshrouded.api.story.EncounterContext;
import com.gustavaopere.enshrouded.api.story.LichManifestationProvider;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Selects one usable physical manifestation actor without owning Story State transitions.
 */
public final class ManifestationDirector {
    private static final String ENCOUNTER_ID_TAG = "EnshroudedEncounterId";
    private static final String MANIFESTATION_ID_TAG = "EnshroudedManifestationId";

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

            bindEncounter(entity, context);
            ServerBossEvent bossEvent = new ServerBossEvent(
                    entity.getDisplayName(),
                    BossEvent.BossBarColor.PURPLE,
                    BossEvent.BossBarOverlay.PROGRESS
            );
            ActiveManifestation active = new ActiveManifestation(provider.id(), entity, bossEvent);
            active.syncBossEvent();
            return Optional.of(active);
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

    public static OptionalInt manifestationId(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        var data = entity.getPersistentData();
        if (!data.contains(MANIFESTATION_ID_TAG)) {
            return OptionalInt.empty();
        }
        int manifestationId = data.getInt(MANIFESTATION_ID_TAG);
        return manifestationId >= 1 ? OptionalInt.of(manifestationId) : OptionalInt.empty();
    }

    private static boolean isUsable(ServerLevel level, LivingEntity entity) {
        return entity.level() == level
                && entity.isAlive()
                && !entity.isRemoved()
                && level.getEntity(entity.getUUID()) == entity;
    }

    private static void bindEncounter(LivingEntity entity, EncounterContext context) {
        var data = entity.getPersistentData();
        data.putUUID(ENCOUNTER_ID_TAG, context.encounterId());
        data.putInt(MANIFESTATION_ID_TAG, context.manifestationLevel());
    }

    public record ActiveManifestation(String providerId, LivingEntity entity, ServerBossEvent bossEvent) {
        public ActiveManifestation {
            providerId = Objects.requireNonNull(providerId, "providerId");
            entity = Objects.requireNonNull(entity, "entity");
            bossEvent = Objects.requireNonNull(bossEvent, "bossEvent");
        }

        public void syncBossEvent() {
            bossEvent.setName(entity.getDisplayName());
            float maxHealth = entity.getMaxHealth();
            float progress = maxHealth > 0.0F
                    ? Mth.clamp(entity.getHealth() / maxHealth, 0.0F, 1.0F)
                    : 0.0F;
            bossEvent.setProgress(progress);
        }
    }
}
