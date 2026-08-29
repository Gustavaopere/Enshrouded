package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

/**
 * Authoritative server-side Madness gameplay effects. Madness itself is derived from the existing
 * exposure reserve and does not persist a second timer or independent progression state.
 */
public final class MadnessRuntime {
    private MadnessRuntime() {
    }

    /** Applies the full gameplay outcome for a freshly authored exposure snapshot. */
    public static MadnessStage apply(ServerPlayer player, ExposureSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        MadnessStage stage = snapshot.madnessStage();
        applyStage(player, stage, true);
        return stage;
    }

    /**
     * Re-applies only reversible non-fatal penalties from persisted reserve state. This is safe to
     * run every player tick so a client cannot immediately re-enable sprint between exposure samples.
     */
    public static MadnessStage enforcePenalty(
            ServerPlayer player,
            int remainingTicks,
            int maxReserveTicks) {
        Objects.requireNonNull(player, "player");
        MadnessStage stage = MadnessService.stageFor(remainingTicks, maxReserveTicks);
        applyStage(player, stage, false);
        return stage;
    }

    private static void applyStage(ServerPlayer player, MadnessStage stage, boolean allowFatalOutcome) {
        Objects.requireNonNull(player, "player");
        Objects.requireNonNull(stage, "stage");

        if (stage.sprintLocked() && EnshroudedConfig.madnessPreventSprintingAtCritical()) {
            player.setSprinting(false);
        }

        if (!allowFatalOutcome || !stage.fatal() || !player.isAlive()) {
            return;
        }

        var source = ModDamageTypes.madness(player.serverLevel());
        player.hurt(source, Float.MAX_VALUE);

        // The DamageType bypasses ordinary armor/resistance/cooldown. The direct death fallback
        // keeps zero-reserve Madness deterministic if another generic damage hook absorbs the hit;
        // future Enshrouded-specific protection must gate this call explicitly rather than relying
        // on unrelated vanilla mitigation.
        if (player.isAlive()) {
            player.setHealth(0.0F);
            player.die(source);
        }
    }
}
