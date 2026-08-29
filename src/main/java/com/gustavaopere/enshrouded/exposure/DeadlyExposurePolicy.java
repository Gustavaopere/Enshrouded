package com.gustavaopere.enshrouded.exposure;

import java.util.UUID;

/**
 * Stable extension point for Deadly Shroud passage. Task 01 supplies a fail-closed Level-1
 * barrier; Task 03 may inject progression-aware behavior without changing ExposureService.
 */
@FunctionalInterface
public interface DeadlyExposurePolicy {
    int DEFAULT_EMERGENCY_WINDOW_TICKS = ExposureSchema.DEFAULT_EMERGENCY_WINDOW_TICKS;
    int DEFAULT_RAPID_DRAIN_TICKS_PER_TICK = 20;

    Decision evaluate(
            UUID playerId,
            ShroudExposureAttachment state,
            int elapsedTicks,
            int maxReserveTicks);

    static DeadlyExposurePolicy levelOneBarrier() {
        return levelOneBarrier(DEFAULT_EMERGENCY_WINDOW_TICKS, DEFAULT_RAPID_DRAIN_TICKS_PER_TICK);
    }

    static DeadlyExposurePolicy levelOneBarrier(int emergencyWindowTicks, int rapidDrainTicksPerTick) {
        if (emergencyWindowTicks <= 0) {
            throw new IllegalArgumentException("emergencyWindowTicks must be > 0");
        }
        if (rapidDrainTicksPerTick <= 0) {
            throw new IllegalArgumentException("rapidDrainTicksPerTick must be > 0");
        }

        return (playerId, state, elapsedTicks, maxReserveTicks) -> {
            if (state == null) {
                throw new NullPointerException("state");
            }
            if (elapsedTicks < 0) {
                throw new IllegalArgumentException("elapsedTicks must be >= 0");
            }
            if (maxReserveTicks <= 0) {
                throw new IllegalArgumentException("maxReserveTicks must be > 0");
            }

            // Missing identity is intentionally not treated as permission. The Level-1 fallback
            // is a hard barrier regardless of identity; Task 03 will resolve owner/passage data
            // and must likewise fail closed when that resolution is uncertain.
            int cappedWindow = Math.min(emergencyWindowTicks, maxReserveTicks);
            int current = Math.min(state.remainingTicks(), cappedWindow);
            long candidate = (long) current - (long) rapidDrainTicksPerTick * elapsedTicks;
            return new Decision((int) Math.max(candidate, 0L), true);
        };
    }

    record Decision(int remainingTicks, boolean barrierActive) {
        public Decision {
            if (remainingTicks < 0) {
                throw new IllegalArgumentException("remainingTicks must be >= 0");
            }
        }
    }
}
