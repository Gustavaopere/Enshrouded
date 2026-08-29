package com.gustavaopere.enshrouded.ecology.combat;

import com.gustavaopere.enshrouded.ecology.ai.CorruptedTargetingService;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;

import java.util.Collections;
import java.util.Set;
import java.util.WeakHashMap;

/** Bridges persisted corruption intensity to reversible combat behavior. */
public final class CorruptedCombatRuntime {
    private static final Set<LivingEntity> ACTIVE_EFFECTS =
            Collections.newSetFromMap(new WeakHashMap<>());

    private CorruptedCombatRuntime() {
    }

    public static void synchronize(LivingEntity entity, double intensity) {
        if (intensity <= 0.0D) {
            clearIfActive(entity);
            return;
        }

        CorruptionCombatPolicy policy = CorruptionCombatPolicy.configured();
        CorruptedAttributeModifiers.synchronize(entity, policy.attributeProfile(intensity));
        if (entity instanceof Mob mob) {
            CorruptedTargetingService.synchronize(mob, policy, intensity);
        }
        ACTIVE_EFFECTS.add(entity);
    }

    public static void clearIfActive(LivingEntity entity) {
        if (!ACTIVE_EFFECTS.remove(entity)) {
            return;
        }
        CorruptedAttributeModifiers.synchronize(entity, CorruptionAttributeProfile.clean());
        if (entity instanceof Mob mob) {
            CorruptedTargetingService.synchronize(mob, CorruptionCombatPolicy.levelOne(), 0.0D);
        }
    }
}
