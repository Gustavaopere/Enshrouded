package com.gustavaopere.enshrouded.shroud.core;

public final class CoreSafetyLimits {
    public static final int MIN_MAX_INFLUENCE_RADIUS = 16;
    public static final int DEFAULT_MAX_INFLUENCE_RADIUS = 128;
    public static final int MAX_MAX_INFLUENCE_RADIUS = 512;

    public static final int MIN_GROWTH_WORK_PER_TICK = 1;
    public static final int DEFAULT_GROWTH_WORK_PER_TICK = 32;
    public static final int MAX_GROWTH_WORK_PER_TICK = 512;

    private CoreSafetyLimits() {
    }

    public static int clampMaxInfluenceRadius(int requested) {
        return clamp(requested, MIN_MAX_INFLUENCE_RADIUS, MAX_MAX_INFLUENCE_RADIUS);
    }

    public static int clampGrowthWorkPerTick(int requested) {
        return clamp(requested, MIN_GROWTH_WORK_PER_TICK, MAX_GROWTH_WORK_PER_TICK);
    }

    private static int clamp(int value, int minimum, int maximum) {
        return Math.max(minimum, Math.min(maximum, value));
    }
}
