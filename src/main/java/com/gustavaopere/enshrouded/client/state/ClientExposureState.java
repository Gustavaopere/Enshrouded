package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.ExposurePayload;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;

import java.util.Objects;

/** Client-only presentation cache for server-authored exposure snapshots. */
public final class ClientExposureState {
    public static final ClientExposureState INSTANCE = new ClientExposureState();

    private long lastSequence = -1L;
    private ExposureSnapshot snapshot = safeBaseline();

    public synchronized boolean accept(ExposurePayload payload) {
        Objects.requireNonNull(payload, "payload");
        if (payload.sequence() <= lastSequence) {
            return false;
        }
        lastSequence = payload.sequence();
        snapshot = payload.snapshot();
        return true;
    }

    public synchronized long lastSequence() {
        return lastSequence;
    }

    public synchronized ExposureSnapshot snapshot() {
        return snapshot;
    }

    public synchronized void reset() {
        lastSequence = -1L;
        snapshot = safeBaseline();
    }

    private static ExposureSnapshot safeBaseline() {
        return new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                0.0F,
                ShroudSeverity.CLEAR,
                false,
                false
        );
    }
}
