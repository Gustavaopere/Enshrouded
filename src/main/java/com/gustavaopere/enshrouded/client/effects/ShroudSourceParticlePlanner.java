package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;

import java.util.Objects;

/** Pure source-local particle budget calculation with no world access. */
public final class ShroudSourceParticlePlanner {
    private ShroudSourceParticlePlanner() {}

    public enum SourceKind {
        CORE(2),
        GROWTH(1),
        RED_SLUDGE(3);

        private final int baseCount;

        SourceKind(int baseCount) {
            this.baseCount = baseCount;
        }
    }

    public static int emissionCount(
            SourceKind kind,
            EnshroudedClientConfig.ParticleSettings settings,
            double distanceSquared) {
        Objects.requireNonNull(kind, "kind");
        Objects.requireNonNull(settings, "settings");
        if (!settings.enabled() || settings.maxCount() <= 0 || !Double.isFinite(distanceSquared) || distanceSquared < 0.0D) {
            return 0;
        }
        double maxDistance = settings.maxDistance();
        if (distanceSquared > maxDistance * maxDistance) {
            return 0;
        }
        return Math.min(kind.baseCount, settings.maxCount());
    }
}
