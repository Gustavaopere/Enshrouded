package com.gustavaopere.enshrouded.exposure.deadly;

/** Immutable Flame passage requirement for one Deadly Shroud profile. */
public record PassageRequirement(int requiredLevel) {
    public static final int MIN_REQUIRED_LEVEL = 1;
    public static final int DEFAULT_REQUIRED_LEVEL = 2;
    public static final int MAX_REQUIRED_LEVEL = 16;

    public PassageRequirement {
        if (requiredLevel < MIN_REQUIRED_LEVEL || requiredLevel > MAX_REQUIRED_LEVEL) {
            throw new IllegalArgumentException(
                    "requiredLevel must be within [" + MIN_REQUIRED_LEVEL + "," + MAX_REQUIRED_LEVEL + "]");
        }
    }

    public boolean isMetBy(int passageLevel) {
        return passageLevel >= requiredLevel;
    }
}
