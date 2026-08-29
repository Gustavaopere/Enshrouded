package com.gustavaopere.enshrouded.client.ecology;

/** Client-side projection surface for canonical entity corruption state. */
public record CorruptionVisualState(
        boolean visible,
        Cue cue,
        int particleIntervalTicks,
        int particleCount) {
    private static final CorruptionVisualState HIDDEN = new CorruptionVisualState(false, Cue.NONE, 0, 0);

    public CorruptionVisualState {
        if (cue == null) {
            throw new NullPointerException("cue");
        }
        if (particleIntervalTicks < 0 || particleCount < 0) {
            throw new IllegalArgumentException("particle cadence/count must be non-negative");
        }
    }

    public static CorruptionVisualState fromIntensity(float intensity) {
        return HIDDEN;
    }

    public enum Cue {
        NONE,
        TAINTED,
        CORRUPTED
    }
}
