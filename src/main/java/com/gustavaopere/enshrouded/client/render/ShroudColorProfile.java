package com.gustavaopere.enshrouded.client.render;

/** Presentation-only fog tint and plane-distance profile. */
public record ShroudColorProfile(
        float red,
        float green,
        float blue,
        float nearPlaneFactor,
        float farPlaneFactor) {
    private static final ShroudColorProfile CLEAR = new ShroudColorProfile(1.0F, 1.0F, 1.0F, 1.0F, 1.0F);
    private static final ShroudColorProfile ORDINARY = new ShroudColorProfile(0.38F, 0.46F, 0.55F, 0.22F, 0.55F);
    private static final ShroudColorProfile DEADLY = new ShroudColorProfile(0.63F, 0.12F, 0.14F, 0.08F, 0.32F);

    public ShroudColorProfile {
        red = clamp01(red);
        green = clamp01(green);
        blue = clamp01(blue);
        nearPlaneFactor = clamp01(nearPlaneFactor);
        farPlaneFactor = clamp01(farPlaneFactor);
    }

    public static ShroudColorProfile clear() {
        return CLEAR;
    }

    public static ShroudColorProfile ordinary() {
        return ORDINARY;
    }

    public static ShroudColorProfile deadly() {
        return DEADLY;
    }

    public static ShroudColorProfile blend(float ordinaryWeight, float deadlyWeight, double intensity) {
        float scale = (float) clamp01(intensity);
        float ordinary = clamp01(ordinaryWeight) * scale;
        float deadly = clamp01(deadlyWeight) * scale;
        float total = Math.min(1.0F, ordinary + deadly);
        float clear = 1.0F - total;

        return new ShroudColorProfile(
                CLEAR.red * clear + ORDINARY.red * ordinary + DEADLY.red * deadly,
                CLEAR.green * clear + ORDINARY.green * ordinary + DEADLY.green * deadly,
                CLEAR.blue * clear + ORDINARY.blue * ordinary + DEADLY.blue * deadly,
                CLEAR.nearPlaneFactor * clear + ORDINARY.nearPlaneFactor * ordinary + DEADLY.nearPlaneFactor * deadly,
                CLEAR.farPlaneFactor * clear + ORDINARY.farPlaneFactor * ordinary + DEADLY.farPlaneFactor * deadly
        );
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }

    private static double clamp01(double value) {
        if (!Double.isFinite(value)) {
            return 1.0D;
        }
        return Math.max(0.0D, Math.min(1.0D, value));
    }
}
