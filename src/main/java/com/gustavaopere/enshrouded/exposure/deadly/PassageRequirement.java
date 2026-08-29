package com.gustavaopere.enshrouded.exposure.deadly;

/** Immutable Flame passage requirement for one Deadly Shroud profile. */
public record PassageRequirement(int requiredLevel) {
    public PassageRequirement {
        if (requiredLevel <= 0) {
            throw new IllegalArgumentException("requiredLevel must be > 0");
        }
    }

    public boolean isMetBy(int passageLevel) {
        return passageLevel >= requiredLevel;
    }
}
