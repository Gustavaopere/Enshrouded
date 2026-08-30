package com.gustavaopere.enshrouded.flame.ritual;

import com.gustavaopere.enshrouded.flame.state.FlameProgressionSchema;

/** Immutable progression result produced by a successful ritual. */
public record RitualOutcome(
        int flameLevel,
        int passageLevel,
        boolean nextLevelReady) {

    public RitualOutcome {
        FlameProgressionSchema.validateLevel(flameLevel, "flameLevel");
        FlameProgressionSchema.validateLevel(passageLevel, "passageLevel");
    }

    /**
     * Level 1 story checkpoint: records readiness for future content while deliberately preserving
     * Flame and Passage Level 1.
     */
    public static RitualOutcome levelOneCheckpoint() {
        return new RitualOutcome(1, 1, true);
    }
}
