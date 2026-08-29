package com.gustavaopere.enshrouded.exposure;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Ephemeral server-side sync tracker. Persisted exposure state remains owned by the player
 * attachment; this class only suppresses redundant client presentation packets.
 */
public final class ExposurePlayerSyncTracker {
    private final Map<UUID, Tracked> tracked = new HashMap<>();

    public Optional<ExposurePayload> update(UUID playerId, ExposureSnapshot snapshot) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(snapshot, "snapshot");

        Tracked previous = tracked.get(playerId);
        if (previous != null && previous.snapshot().equals(snapshot)) {
            return Optional.empty();
        }

        long sequence = previous == null ? 0L : previous.nextSequence();
        tracked.put(playerId, new Tracked(snapshot, sequence + 1L));
        return Optional.of(ExposurePayload.fromSnapshot(sequence, snapshot));
    }

    public void forget(UUID playerId) {
        tracked.remove(Objects.requireNonNull(playerId, "playerId"));
    }

    private record Tracked(ExposureSnapshot snapshot, long nextSequence) {
    }
}
