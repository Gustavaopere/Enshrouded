package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;

import java.util.Objects;

/** Pure distribution of one cue's hard particle budget across its bounded lifetime. */
public final class AdvancedVfxSequenceBudget {
    private AdvancedVfxSequenceBudget() {}

    public static int targetEmitted(
            AdvancedVfxCue cue,
            EnshroudedClientConfig.ParticleSettings settings,
            int elapsedTicks) {
        Objects.requireNonNull(cue, "cue");
        Objects.requireNonNull(settings, "settings");
        int budget = cue.particleBudget(settings.enabled(), settings.maxCount());
        if (budget <= 0) {
            return 0;
        }
        int elapsed = Math.max(0, Math.min(cue.lifetimeTicks(), elapsedTicks));
        return (int) ((long) budget * elapsed / cue.lifetimeTicks());
    }
}
