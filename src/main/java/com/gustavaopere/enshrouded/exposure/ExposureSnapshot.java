package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/**
 * Server-authored presentation snapshot. It carries the timer plus the canonical logical
 * zone observation that produced the update; clients never submit this state upstream.
 */
public record ExposureSnapshot(
        int schemaVersion,
        int remainingTicks,
        int maxReserveTicks,
        float intensity,
        ShroudSeverity severity,
        boolean sanctuarySuppressed,
        boolean deadlyBarrierActive) {

    public ExposureSnapshot {
        if (schemaVersion != ExposureSchema.CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported exposure schema version: " + schemaVersion);
        }
        if (maxReserveTicks <= 0) {
            throw new IllegalArgumentException("maxReserveTicks must be > 0");
        }
        if (remainingTicks < 0 || remainingTicks > maxReserveTicks) {
            throw new IllegalArgumentException("remainingTicks must be within [0,maxReserveTicks]");
        }
        if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
            throw new IllegalArgumentException("intensity must be finite and within [0,1]");
        }
        Objects.requireNonNull(severity, "severity");
    }

    public ShroudExposureAttachment attachmentState() {
        return new ShroudExposureAttachment(schemaVersion, remainingTicks);
    }
}
