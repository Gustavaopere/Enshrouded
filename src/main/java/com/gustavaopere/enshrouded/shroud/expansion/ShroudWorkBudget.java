package com.gustavaopere.enshrouded.shroud.expansion;

/**
 * Immutable global/per-core limits for one logical expansion tick.
 */
public record ShroudWorkBudget(int globalPerTick, int perCorePerTick) {
    public ShroudWorkBudget {
        if (globalPerTick <= 0) {
            throw new IllegalArgumentException("globalPerTick must be > 0");
        }
        if (perCorePerTick <= 0) {
            throw new IllegalArgumentException("perCorePerTick must be > 0");
        }
    }
}
