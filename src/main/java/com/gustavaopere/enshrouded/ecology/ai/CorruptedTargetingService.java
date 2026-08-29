package com.gustavaopere.enshrouded.ecology.ai;

import com.gustavaopere.enshrouded.ecology.combat.CorruptionCombatPolicy;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

import java.util.Objects;

/** Narrow player-target injection surface; behavior is intentionally introduced after RED coverage. */
public final class CorruptedTargetingService {
    private CorruptedTargetingService() {
    }

    public static void synchronize(Mob mob, CorruptionCombatPolicy policy, double intensity) {
        Objects.requireNonNull(mob, "mob");
        Objects.requireNonNull(policy, "policy");
    }

    public static void synchronize(
            Mob mob,
            CorruptionCombatPolicy policy,
            double intensity,
            Iterable<? extends Player> candidates) {
        Objects.requireNonNull(mob, "mob");
        Objects.requireNonNull(policy, "policy");
        Objects.requireNonNull(candidates, "candidates");
    }
}
