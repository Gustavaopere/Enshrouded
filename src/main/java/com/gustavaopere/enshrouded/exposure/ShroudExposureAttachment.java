package com.gustavaopere.enshrouded.exposure;

/**
 * Minimal persistent player exposure state. The authoritative runtime stores only the
 * versioned reserve; effective zone data is sampled from the canonical Shroud query.
 */
public record ShroudExposureAttachment(int schemaVersion, int remainingTicks) {
    public ShroudExposureAttachment {
        if (schemaVersion != ExposureSchema.CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported exposure schema version: " + schemaVersion);
        }
        if (remainingTicks < 0) {
            throw new IllegalArgumentException("remainingTicks must be >= 0");
        }
    }

    public static ShroudExposureAttachment full(int maxReserveTicks) {
        if (maxReserveTicks <= 0) {
            throw new IllegalArgumentException("maxReserveTicks must be > 0");
        }
        return new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, maxReserveTicks);
    }
}
