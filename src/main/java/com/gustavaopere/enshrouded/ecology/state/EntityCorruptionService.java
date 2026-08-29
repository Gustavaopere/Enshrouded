package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/** Pure bounded reducer for continuous entity corruption intensity. */
public final class EntityCorruptionService {
    private final float accumulationPerTick;
    private final float regressionPerTick;
    private final int maxElapsedTicks;

    public EntityCorruptionService(float accumulationPerTick, float regressionPerTick, int maxElapsedTicks) {
        if (!Float.isFinite(accumulationPerTick) || accumulationPerTick <= 0.0F) {
            throw new IllegalArgumentException("accumulationPerTick must be finite and > 0");
        }
        if (!Float.isFinite(regressionPerTick) || regressionPerTick <= 0.0F) {
            throw new IllegalArgumentException("regressionPerTick must be finite and > 0");
        }
        if (maxElapsedTicks <= 0) {
            throw new IllegalArgumentException("maxElapsedTicks must be > 0");
        }
        this.accumulationPerTick = accumulationPerTick;
        this.regressionPerTick = regressionPerTick;
        this.maxElapsedTicks = maxElapsedTicks;
    }

    public EntityCorruptionAttachment tick(
            EntityCorruptionAttachment state,
            ShroudSample sample,
            int elapsedTicks) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(sample, "sample");
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("elapsedTicks must be >= 0");
        }

        int delta = Math.min(elapsedTicks, maxElapsedTicks);
        float current = state.intensity();
        float next;
        if (sample.sanctuarySuppressed() || sample.severity() == ShroudSeverity.CLEAR) {
            next = current - regressionPerTick * delta;
        } else {
            next = current + accumulationPerTick * sample.intensity() * delta;
        }
        next = Math.max(0.0F, Math.min(1.0F, next));
        return new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, next);
    }
}
