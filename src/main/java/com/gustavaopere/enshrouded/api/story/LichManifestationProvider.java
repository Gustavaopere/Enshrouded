package com.gustavaopere.enshrouded.api.story;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import java.util.Optional;
import java.util.UUID;

/**
 * Selects, spawns and identifies a Lich manifestation entity. Providers never grant
 * Enshrouded story rewards or progression; those remain owned by the core story runtime.
 */
public interface LichManifestationProvider {
    String id();

    boolean isAvailable();

    Optional<LivingEntity> spawn(ServerLevel level, EncounterContext context);

    boolean matches(Entity entity, UUID encounterId);
}
