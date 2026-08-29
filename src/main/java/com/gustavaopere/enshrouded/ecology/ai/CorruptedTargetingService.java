package com.gustavaopere.enshrouded.ecology.ai;

import com.gustavaopere.enshrouded.ecology.combat.CorruptionCombatPolicy;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.WeakHashMap;

/**
 * Narrow, reversible player-target injection for corrupted passive/neutral mobs.
 * Existing native/external targets are never overwritten.
 */
public final class CorruptedTargetingService {
    private static final Map<Mob, UUID> OWNED_TARGETS = new WeakHashMap<>();

    private CorruptedTargetingService() {
    }

    public static void synchronize(Mob mob, CorruptionCombatPolicy policy, double intensity) {
        Objects.requireNonNull(mob, "mob");
        Objects.requireNonNull(policy, "policy");
        if (!(mob.level() instanceof ServerLevel level)) {
            releaseOwnedTarget(mob);
            return;
        }
        synchronize(mob, policy, intensity, level.players());
    }

    public static void synchronize(
            Mob mob,
            CorruptionCombatPolicy policy,
            double intensity,
            Iterable<? extends Player> candidates) {
        Objects.requireNonNull(mob, "mob");
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(candidates, "candidates");

        if (mob instanceof Enemy || !policy.shouldAcquirePlayerTarget(intensity)) {
            releaseOwnedTarget(mob);
            return;
        }

        UUID ownedTargetId = OWNED_TARGETS.get(mob);
        LivingEntity currentTarget = mob.getTarget();
        if (ownedTargetId != null && (currentTarget == null || !ownedTargetId.equals(currentTarget.getUUID()))) {
            OWNED_TARGETS.remove(mob);
            ownedTargetId = null;
        }

        if (currentTarget != null && ownedTargetId == null) {
            return;
        }

        double maxDistanceSquared = policy.playerTargetRange() * policy.playerTargetRange();
        if (currentTarget instanceof Player currentPlayer
                && ownedTargetId != null
                && isEligibleTarget(mob, currentPlayer, maxDistanceSquared)) {
            return;
        }

        releaseOwnedTarget(mob);
        Player nearest = null;
        double nearestDistanceSquared = Double.POSITIVE_INFINITY;
        for (Player candidate : candidates) {
            if (candidate == null || !isEligibleTarget(mob, candidate, maxDistanceSquared)) {
                continue;
            }
            double distanceSquared = mob.distanceToSqr(candidate);
            if (nearest == null
                    || distanceSquared < nearestDistanceSquared
                    || (Double.compare(distanceSquared, nearestDistanceSquared) == 0
                    && candidate.getUUID().compareTo(nearest.getUUID()) < 0)) {
                nearest = candidate;
                nearestDistanceSquared = distanceSquared;
            }
        }

        if (nearest != null) {
            mob.setTarget(nearest);
            OWNED_TARGETS.put(mob, nearest.getUUID());
        }
    }

    static boolean ownsCurrentTarget(Mob mob) {
        UUID ownedTargetId = OWNED_TARGETS.get(mob);
        LivingEntity current = mob.getTarget();
        return ownedTargetId != null && current != null && ownedTargetId.equals(current.getUUID());
    }

    private static boolean isEligibleTarget(Mob mob, Player player, double maxDistanceSquared) {
        return player.isAlive()
                && !player.isCreative()
                && !player.isSpectator()
                && player.level() == mob.level()
                && !mob.isAlliedTo(player)
                && mob.distanceToSqr(player) <= maxDistanceSquared;
    }

    private static void releaseOwnedTarget(Mob mob) {
        UUID ownedTargetId = OWNED_TARGETS.remove(mob);
        LivingEntity current = mob.getTarget();
        if (ownedTargetId != null && current != null && ownedTargetId.equals(current.getUUID())) {
            mob.setTarget(null);
        }
    }
}
