package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;
import java.util.UUID;

/**
 * Server-side exposure reducer. World/query ownership stays outside this class so ordinary
 * drain/recovery math remains deterministic, while Deadly behavior is fully delegated through
 * the stable DeadlyExposurePolicy extension point.
 */
public final class ExposureService {
    private final int maxReserveTicks;
    private final int ordinaryDrainTicksPerTick;
    private final int recoveryTicksPerTick;
    private final int maxDeltaTicks;
    private final DeadlyExposurePolicy deadlyPolicy;

    public ExposureService(
            int maxReserveTicks,
            int ordinaryDrainTicksPerTick,
            int recoveryTicksPerTick,
            int maxDeltaTicks,
            DeadlyExposurePolicy deadlyPolicy) {
        if (maxReserveTicks <= 0) {
            throw new IllegalArgumentException("maxReserveTicks must be > 0");
        }
        if (ordinaryDrainTicksPerTick <= 0) {
            throw new IllegalArgumentException("ordinaryDrainTicksPerTick must be > 0");
        }
        if (recoveryTicksPerTick <= 0) {
            throw new IllegalArgumentException("recoveryTicksPerTick must be > 0");
        }
        if (maxDeltaTicks <= 0) {
            throw new IllegalArgumentException("maxDeltaTicks must be > 0");
        }
        this.maxReserveTicks = maxReserveTicks;
        this.ordinaryDrainTicksPerTick = ordinaryDrainTicksPerTick;
        this.recoveryTicksPerTick = recoveryTicksPerTick;
        this.maxDeltaTicks = maxDeltaTicks;
        this.deadlyPolicy = Objects.requireNonNull(deadlyPolicy, "deadlyPolicy");
    }

    public ExposureSnapshot tick(
            ShroudExposureAttachment state,
            ShroudSample sample,
            int elapsedTicks) {
        return tick(null, state, sample, elapsedTicks);
    }

    public ExposureSnapshot tick(
            UUID playerId,
            ShroudExposureAttachment state,
            ShroudSample sample,
            int elapsedTicks) {
        Objects.requireNonNull(state, "state");
        Objects.requireNonNull(sample, "sample");
        if (elapsedTicks < 0) {
            throw new IllegalArgumentException("elapsedTicks must be >= 0");
        }

        int delta = Math.min(elapsedTicks, maxDeltaTicks);
        int current = Math.min(state.remainingTicks(), maxReserveTicks);
        int next;
        boolean deadlyBarrierActive = false;

        if (sample.sanctuarySuppressed() || sample.severity() == ShroudSeverity.CLEAR) {
            next = saturatingAdd(current, recoveryTicksPerTick, delta, maxReserveTicks);
        } else if (sample.severity() == ShroudSeverity.SHROUD) {
            next = saturatingSubtract(current, ordinaryDrainTicksPerTick, delta);
        } else {
            DeadlyExposurePolicy.Decision decision = deadlyPolicy.evaluate(
                    playerId,
                    new ShroudExposureAttachment(state.schemaVersion(), current),
                    delta,
                    maxReserveTicks
            );
            Objects.requireNonNull(decision, "deadly policy decision");
            next = Math.min(decision.remainingTicks(), maxReserveTicks);
            deadlyBarrierActive = decision.barrierActive();
        }

        return new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                next,
                maxReserveTicks,
                sample.intensity(),
                sample.severity(),
                sample.sanctuarySuppressed(),
                deadlyBarrierActive
        );
    }

    private static int saturatingAdd(int current, int perTick, int delta, int maximum) {
        long candidate = (long) current + (long) perTick * delta;
        return (int) Math.min(candidate, maximum);
    }

    private static int saturatingSubtract(int current, int perTick, int delta) {
        long candidate = (long) current - (long) perTick * delta;
        return (int) Math.max(candidate, 0L);
    }
}
