package com.gustavaopere.enshrouded.ecology.combat;

/** Immutable Level-1 attribute boost profile derived from corruption intensity. */
public record CorruptionAttributeProfile(
        double maxHealthMultiplier,
        double attackDamageMultiplier,
        double movementSpeedMultiplier,
        double knockbackResistanceBonus) {

    public CorruptionAttributeProfile {
        requireFiniteNonNegative(maxHealthMultiplier, "maxHealthMultiplier");
        requireFiniteNonNegative(attackDamageMultiplier, "attackDamageMultiplier");
        requireFiniteNonNegative(movementSpeedMultiplier, "movementSpeedMultiplier");
        requireFiniteNonNegative(knockbackResistanceBonus, "knockbackResistanceBonus");
    }

    public static CorruptionAttributeProfile clean() {
        return new CorruptionAttributeProfile(0.0D, 0.0D, 0.0D, 0.0D);
    }

    private static void requireFiniteNonNegative(double value, String name) {
        if (!Double.isFinite(value) || value < 0.0D) {
            throw new IllegalArgumentException(name + " must be finite and non-negative");
        }
    }
}
