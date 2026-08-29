package com.gustavaopere.enshrouded.ecology.combat;

/** Level-1 policy for corruption-driven aggression and bounded stat scaling. */
public final class CorruptionCombatPolicy {
    public static final double MAX_HEALTH_CAP = 0.50D;
    public static final double ATTACK_DAMAGE_CAP = 0.35D;
    public static final double MOVEMENT_SPEED_CAP = 0.20D;
    public static final double KNOCKBACK_RESISTANCE_CAP = 0.25D;
    public static final double PLAYER_TARGET_RANGE = 16.0D;
    public static final double CORRUPTED_TARGET_THRESHOLD = 0.50D;

    private static final CorruptionCombatPolicy LEVEL_ONE = new CorruptionCombatPolicy();

    private CorruptionCombatPolicy() {
    }

    public static CorruptionCombatPolicy levelOne() {
        return LEVEL_ONE;
    }

    public boolean shouldAcquirePlayerTarget(double intensity) {
        return normalizeIntensity(intensity) >= CORRUPTED_TARGET_THRESHOLD;
    }

    public CorruptionAttributeProfile attributeProfile(double intensity) {
        double normalized = normalizeIntensity(intensity);
        return new CorruptionAttributeProfile(
                normalized * MAX_HEALTH_CAP,
                normalized * ATTACK_DAMAGE_CAP,
                normalized * MOVEMENT_SPEED_CAP,
                normalized * KNOCKBACK_RESISTANCE_CAP
        );
    }

    public double playerTargetRange() {
        return PLAYER_TARGET_RANGE;
    }

    private static double normalizeIntensity(double intensity) {
        if (!Double.isFinite(intensity)) {
            throw new IllegalArgumentException("corruption intensity must be finite");
        }
        return Math.clamp(intensity, 0.0D, 1.0D);
    }
}
