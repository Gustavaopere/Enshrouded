package com.gustavaopere.enshrouded.ecology.combat;

import com.gustavaopere.enshrouded.ecology.ai.CorruptedTargetingService;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;

/** Bridges persisted corruption intensity to reversible combat behavior. */
public final class CorruptedCombatRuntime {
    private static final Set<LivingEntity> ACTIVE_EFFECTS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private CorruptedCombatRuntime() {
    }

    public static void synchronize(LivingEntity entity, double intensity) {
        synchronize(entity, intensity, null);
    }

    /** Test/integration seam that keeps production targeting on ServerLevel.players(). */
    public static void synchronize(
            LivingEntity entity,
            double intensity,
            Iterable<? extends Player> candidateOverride) {
        Objects.requireNonNull(entity, "entity");
        if (intensity <= 0.0D) {
            clearIfActive(entity);
            return;
        }

        CorruptionCombatPolicy policy = CorruptionCombatPolicy.configured();
        CorruptedAttributeModifiers.synchronize(entity, policy.attributeProfile(intensity));
        if (entity instanceof Mob mob) {
            if (candidateOverride == null) {
                CorruptedTargetingService.synchronize(mob, policy, intensity);
            } else {
                CorruptedTargetingService.synchronize(mob, policy, intensity, candidateOverride);
            }
        }
        ACTIVE_EFFECTS.add(entity);
    }

    public static void clearIfActive(LivingEntity entity) {
        Objects.requireNonNull(entity, "entity");
        if (!ACTIVE_EFFECTS.remove(entity)) {
            return;
        }
        CorruptedAttributeModifiers.synchronize(entity, CorruptionAttributeProfile.clean());
        if (entity instanceof Mob mob) {
            CorruptedTargetingService.synchronize(mob, CorruptionCombatPolicy.levelOne(), 0.0D);
        }
    }
}
