package com.gustavaopere.enshrouded.client.ecology;

import com.gustavaopere.enshrouded.ecology.state.CorruptionStage;

/** Client-side projection of the canonical synced entity-corruption intensity. */
public record CorruptionVisualState(
        boolean visible,
        Cue cue,
        int particleIntervalTicks,
        int particleCount) {
    private static final CorruptionVisualState HIDDEN = new CorruptionVisualState(false, Cue.NONE, 0, 0);
    private static final CorruptionVisualState TAINTED = new CorruptionVisualState(true, Cue.TAINTED, 20, 1);
    private static final CorruptionVisualState CORRUPTED = new CorruptionVisualState(true, Cue.CORRUPTED, 8, 2);

    public CorruptionVisualState {
        if (cue == null) {
            throw new NullPointerException("cue");
        }
        if (particleIntervalTicks < 0 || particleCount < 0) {
            throw new IllegalArgumentException("particle cadence/count must be non-negative");
        }
    }

    public static CorruptionVisualState fromIntensity(float intensity) {
        if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
            return HIDDEN;
        }
        return switch (CorruptionStage.fromIntensity(intensity)) {
            case CLEAR -> HIDDEN;
            case TAINTED -> TAINTED;
            case CORRUPTED -> CORRUPTED;
        };
    }

    public enum Cue {
        NONE,
        TAINTED,
        CORRUPTED
    }
}
