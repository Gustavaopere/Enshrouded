package com.gustavaopere.enshrouded.story.manifestation;

import com.gustavaopere.enshrouded.registry.ModParticles;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;
import java.util.UUID;

/**
 * Bounded, downstream presentation for the canonical manifestation lifecycle.
 * This class never spawns actors, changes progression, or issues rewards.
 */
public final class LichManifestationPresentation {
    static final int MAX_SPAWN_PARTICLES = 28;
    static final int MAX_DEFEAT_PARTICLES = 36;
    static final double MAX_AUDIBLE_DISTANCE = 48.0D;

    private LichManifestationPresentation() {
    }

    public static void onSpawned(ServerLevel level, LivingEntity actor, UUID encounterId) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(encounterId, "encounterId");
        if (!hasAudience(level, actor)) {
            return;
        }

        level.sendParticles(ModParticles.LICH_ARCANA.get(),
                actor.getX(), actor.getY() + actor.getBbHeight() * 0.55D, actor.getZ(),
                MAX_SPAWN_PARTICLES, 0.85D, 1.15D, 0.85D, 0.025D);
        level.playSound(null, actor.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE,
                0.85F, 0.72F);
    }

    public static void onDefeated(ServerLevel level, LivingEntity actor, UUID encounterId) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(actor, "actor");
        Objects.requireNonNull(encounterId, "encounterId");
        if (!hasAudience(level, actor)) {
            return;
        }

        level.sendParticles(ModParticles.LICH_ARCANA.get(),
                actor.getX(), actor.getY() + actor.getBbHeight() * 0.45D, actor.getZ(),
                MAX_DEFEAT_PARTICLES, 1.05D, 0.90D, 1.05D, 0.045D);
        level.playSound(null, actor.blockPosition(), SoundEvents.SOUL_ESCAPE, SoundSource.HOSTILE,
                0.70F, 0.52F);
    }

    private static boolean hasAudience(ServerLevel level, LivingEntity actor) {
        double maximumDistanceSquared = MAX_AUDIBLE_DISTANCE * MAX_AUDIBLE_DISTANCE;
        return level.players().stream().anyMatch(player -> player.distanceToSqr(actor) <= maximumDistanceSquared);
    }
}
