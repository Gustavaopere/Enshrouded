package com.gustavaopere.enshrouded.exposure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.OptionalInt;
import java.util.UUID;

/**
 * Ephemeral per-session cadence. The first observation produces a zero-delta presentation
 * snapshot; subsequent work runs at most once when the configured sample interval elapses.
 */
public final class ExposureSamplingCadence {
    private final int intervalTicks;
    private final Map<UUID, Long> lastProcessedTick = new HashMap<>();

    public ExposureSamplingCadence(int intervalTicks) {
        if (intervalTicks <= 0) {
            throw new IllegalArgumentException("intervalTicks must be > 0");
        }
        this.intervalTicks = intervalTicks;
    }

    public OptionalInt elapsedTicks(UUID playerId, long serverTick) {
        Objects.requireNonNull(playerId, "playerId");
        if (serverTick < 0L) {
            throw new IllegalArgumentException("serverTick must be >= 0");
        }

        Long previous = lastProcessedTick.get(playerId);
        if (previous == null) {
            lastProcessedTick.put(playerId, serverTick);
            return OptionalInt.of(0);
        }

        long elapsed = serverTick - previous;
        if (elapsed < 0L) {
            // A server tick counter reset must not manufacture a giant exposure delta.
            lastProcessedTick.put(playerId, serverTick);
            return OptionalInt.of(0);
        }
        if (elapsed < intervalTicks) {
            return OptionalInt.empty();
        }

        lastProcessedTick.put(playerId, serverTick);
        return OptionalInt.of((int) Math.min(elapsed, Integer.MAX_VALUE));
    }

    public void forget(UUID playerId) {
        lastProcessedTick.remove(Objects.requireNonNull(playerId, "playerId"));
    }
}
