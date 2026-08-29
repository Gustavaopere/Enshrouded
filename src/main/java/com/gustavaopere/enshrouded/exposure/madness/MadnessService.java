package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;

import java.util.Objects;

/** Stateless Madness reducer over the one authoritative exposure reserve. */
public final class MadnessService {
    private static final MadnessService LEVEL_ONE = new MadnessService();

    private MadnessService() {
    }

    public static MadnessService levelOne() {
        return LEVEL_ONE;
    }

    public MadnessStage stage(ExposureSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        return stage(snapshot.remainingTicks(), snapshot.maxReserveTicks());
    }

    public MadnessStage stage(int remainingTicks, int maxReserveTicks) {
        if (maxReserveTicks <= 0) {
            throw new IllegalArgumentException("maxReserveTicks must be > 0");
        }
        if (remainingTicks < 0 || remainingTicks > maxReserveTicks) {
            throw new IllegalArgumentException("remainingTicks must be within [0,maxReserveTicks]");
        }
        if (remainingTicks == 0) {
            return MadnessStage.FATAL;
        }

        long scaledRemaining = (long) remainingTicks * 100L;
        long maximum = maxReserveTicks;
        if (scaledRemaining <= maximum * 10L) {
            return MadnessStage.CRITICAL;
        }
        if (scaledRemaining <= maximum * 25L) {
            return MadnessStage.DISTORTED;
        }
        if (scaledRemaining <= maximum * 50L) {
            return MadnessStage.UNEASY;
        }
        return MadnessStage.STABLE;
    }
}
