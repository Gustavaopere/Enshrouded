package com.gustavaopere.enshrouded.ecology.combat;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;

/** Level-1 policy for corruption-driven aggression and bounded stat scaling. */
public final class CorruptionCombatPolicy {
    public static final double MAX_HEALTH_CAP = 0.50D;
    public static final double ATTACK_DAMAGE_CAP = 0.35D;
    public static final double MOVEMENT_SPEED_CAP = 0.20D;
    public static final double KNOCKBACK_RESISTANCE_CAP = 0.25D;
    public static final double PLAYER_TARGET_RANGE = 16.0D;
    public static final double CORRUPTED_TARGET_THRESHOLD = 0.50D;

    private static final CorruptionCombatPolicy LEVEL_ONE = new CorruptionCombatPolicy(
            CORRUPTED_TARGET_THRESHOLD,
            PLAYER_TARGET_RANGE,
            MAX_HEALTH_CAP,
            ATTACK_DAMAGE_CAP,
            MOVEMENT_SPEED_CAP,
            KNOCKBACK_RESISTANCE_CAP
    );

    private final double targetThreshold;
    private final double targetRange;
    private final double maxHealthCap;
    private final double attackDamageCap;
    private final double movementSpeedCap;
    private final double knockbackResistanceCap;

    private CorruptionCombatPolicy(
            double targetThreshold,
            double targetRange,
            double maxHealthCap,
            double attackDamageCap,
            double movementSpeedCap,
            double knockbackResistanceCap) {
        this.targetThreshold = bounded(targetThreshold, 0.0D, 1.0D, "targetThreshold");
        this.targetRange = bounded(targetRange, 1.0D, 64.0D, "targetRange");
        this.maxHealthCap = bounded(maxHealthCap, 0.0D, MAX_HEALTH_CAP, "maxHealthCap");
        this.attackDamageCap = bounded(attackDamageCap, 0.0D, ATTACK_DAMAGE_CAP, "attackDamageCap");
        this.movementSpeedCap = bounded(movementSpeedCap, 0.0D, MOVEMENT_SPEED_CAP, "movementSpeedCap");
        this.knockbackResistanceCap = bounded(
                knockbackResistanceCap,
                0.0D,
                KNOCKBACK_RESISTANCE_CAP,
                "knockbackResistanceCap"
        );
    }

    public static CorruptionCombatPolicy levelOne() {
        return LEVEL_ONE;
    }

    public static CorruptionCombatPolicy configured() {
        return new CorruptionCombatPolicy(
                EnshroudedConfig.corruptionTargetThreshold(),
                EnshroudedConfig.corruptionTargetRange(),
                EnshroudedConfig.corruptionMaxHealthCap(),
                EnshroudedConfig.corruptionAttackDamageCap(),
                EnshroudedConfig.corruptionMovementSpeedCap(),
                EnshroudedConfig.corruptionKnockbackResistanceCap()
        );
    }

    public boolean shouldAcquirePlayerTarget(double intensity) {
        double normalized = normalizeIntensity(intensity);
        return normalized > 0.0D && normalized >= targetThreshold;
    }

    public CorruptionAttributeProfile attributeProfile(double intensity) {
        double normalized = normalizeIntensity(intensity);
        return new CorruptionAttributeProfile(
                normalized * maxHealthCap,
                normalized * attackDamageCap,
                normalized * movementSpeedCap,
                normalized * knockbackResistanceCap
        );
    }

    public double playerTargetRange() {
        return targetRange;
    }

    private static double normalizeIntensity(double intensity) {
        if (!Double.isFinite(intensity)) {
            throw new IllegalArgumentException("corruption intensity must be finite");
        }
        return Math.clamp(intensity, 0.0D, 1.0D);
    }

    private static double bounded(double value, double min, double max, String name) {
        if (!Double.isFinite(value) || value < min || value > max) {
            throw new IllegalArgumentException(name + " must be finite and within [" + min + ", " + max + "]");
        }
        return value;
    }
}
