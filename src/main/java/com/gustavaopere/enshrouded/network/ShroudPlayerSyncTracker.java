package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Per-player server-side rate limiter for authoritative local Shroud presentation snapshots.
 * Identical samples never resend; changed samples become eligible once the minimum interval from
 * the last send has elapsed. The caller may invoke {@link #update(UUID, long, ShroudSample)} every
 * tick without producing packet spam.
 */
public final class ShroudPlayerSyncTracker {
    private final long minTicksBetweenSends;
    private final Map<UUID, PlayerState> players = new HashMap<>();

    public ShroudPlayerSyncTracker(long minTicksBetweenSends) {
        if (minTicksBetweenSends <= 0L) {
            throw new IllegalArgumentException("minTicksBetweenSends must be > 0");
        }
        this.minTicksBetweenSends = minTicksBetweenSends;
    }

    public Optional<ShroudSamplePayload> update(UUID playerId, long gameTime, ShroudSample sample) {
        Objects.requireNonNull(playerId, "playerId");
        Objects.requireNonNull(sample, "sample");
        if (gameTime < 0L) {
            throw new IllegalArgumentException("gameTime must be >= 0");
        }

        PlayerState current = players.get(playerId);
        if (current == null) {
            ShroudSamplePayload payload = ShroudSamplePayload.fromSample(0L, sample);
            players.put(playerId, new PlayerState(gameTime, gameTime, 1L, sample));
            return Optional.of(payload);
        }
        if (gameTime < current.lastObservedTick()) {
            throw new IllegalArgumentException("gameTime must not move backwards for a tracked player");
        }

        PlayerState observed = current.withLastObservedTick(gameTime);
        players.put(playerId, observed);
        if (sample.equals(current.lastSentSample())) {
            return Optional.empty();
        }
        if (gameTime - current.lastSentTick() < minTicksBetweenSends) {
            return Optional.empty();
        }

        ShroudSamplePayload payload = ShroudSamplePayload.fromSample(current.nextSequence(), sample);
        players.put(playerId, new PlayerState(
                gameTime,
                gameTime,
                current.nextSequence() + 1L,
                sample));
        return Optional.of(payload);
    }

    public void forget(UUID playerId) {
        players.remove(Objects.requireNonNull(playerId, "playerId"));
    }

    public int trackedPlayerCount() {
        return players.size();
    }

    private record PlayerState(
            long lastObservedTick,
            long lastSentTick,
            long nextSequence,
            ShroudSample lastSentSample) {
        private PlayerState {
            Objects.requireNonNull(lastSentSample, "lastSentSample");
        }

        private PlayerState withLastObservedTick(long tick) {
            return new PlayerState(tick, lastSentTick, nextSequence, lastSentSample);
        }
    }
}
