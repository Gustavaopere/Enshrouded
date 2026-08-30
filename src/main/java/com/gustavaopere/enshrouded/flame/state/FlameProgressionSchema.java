package com.gustavaopere.enshrouded.flame.state;

/** Stable persistence-format bounds for owner-scoped Flame progression. */
public final class FlameProgressionSchema {
    public static final int CURRENT_VERSION = 1;
    public static final int MIN_LEVEL = 1;

    /**
     * Persistence safety bound, not a gameplay promise that Level 2+ content exists.
     * Keeping a generous finite bound prevents corrupted saves from injecting unbounded values.
     */
    public static final int MAX_LEVEL = 1024;

    private FlameProgressionSchema() {
    }

    public static void validateLevel(int level, String field) {
        if (level < MIN_LEVEL || level > MAX_LEVEL) {
            throw new IllegalArgumentException(field + " must be within [" + MIN_LEVEL + ", " + MAX_LEVEL + "]: " + level);
        }
    }
}
