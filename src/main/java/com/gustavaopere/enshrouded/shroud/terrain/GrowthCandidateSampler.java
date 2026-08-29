package com.gustavaopere.enshrouded.shroud.terrain;

/** Pure deterministic density sampler for visual Shroud growth candidates. */
public final class GrowthCandidateSampler {
    private static final double UNIT_53 = 0x1.0p-53;

    private GrowthCandidateSampler() {
    }

    public static boolean shouldPlace(
            long worldSeed,
            long positionKey,
            float intensity,
            float maximumDensity,
            long salt) {
        float boundedIntensity = clamp01(intensity);
        float boundedDensity = clamp01(maximumDensity);
        double threshold = (double) boundedIntensity * boundedDensity;
        if (threshold <= 0.0D) {
            return false;
        }
        if (threshold >= 1.0D) {
            return true;
        }

        long mixed = mix64(worldSeed ^ Long.rotateLeft(positionKey, 21) ^ Long.rotateLeft(salt, 43));
        double roll = (mixed >>> 11) * UNIT_53;
        return roll < threshold;
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) {
            throw new IllegalArgumentException("density inputs must be finite");
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static long mix64(long value) {
        value += 0x9E3779B97F4A7C15L;
        value = (value ^ (value >>> 30)) * 0xBF58476D1CE4E5B9L;
        value = (value ^ (value >>> 27)) * 0x94D049BB133111EBL;
        return value ^ (value >>> 31);
    }
}
