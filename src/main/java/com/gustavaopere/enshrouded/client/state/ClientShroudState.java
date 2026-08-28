package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.network.ShroudSamplePayload;

import java.util.Objects;

/**
 * Client presentation cache. It accepts only monotonically newer authoritative snapshots and has
 * no path back to server state.
 */
public final class ClientShroudState {
    public static final ClientShroudState INSTANCE = new ClientShroudState();

    private long lastSequence = -1L;
    private ShroudSample sample = ShroudSample.clear();

    public synchronized boolean accept(ShroudSamplePayload payload) {
        Objects.requireNonNull(payload, "payload");
        if (payload.sequence() <= lastSequence) {
            return false;
        }
        lastSequence = payload.sequence();
        sample = payload.sample();
        return true;
    }

    public synchronized long lastSequence() {
        return lastSequence;
    }

    public synchronized ShroudSample sample() {
        return sample;
    }

    public synchronized void reset() {
        lastSequence = -1L;
        sample = ShroudSample.clear();
    }
}
