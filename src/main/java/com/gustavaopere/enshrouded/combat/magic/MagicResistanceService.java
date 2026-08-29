package com.gustavaopere.enshrouded.combat.magic;

import com.gustavaopere.enshrouded.api.combat.MagicDamageClassification;

import java.util.Objects;

/** Pure one-pass damage reducer for positively classified magic. */
public final class MagicResistanceService {
    public static final double HARD_MAX_RESISTANCE = 0.75D;

    private final double maximumResistance;

    public MagicResistanceService(double maximumResistance) {
        if (!Double.isFinite(maximumResistance)
                || maximumResistance < 0.0D
                || maximumResistance > HARD_MAX_RESISTANCE) {
            throw new IllegalArgumentException("maximumResistance must be finite and between 0 and " + HARD_MAX_RESISTANCE);
        }
        this.maximumResistance = maximumResistance;
    }

    public double maximumResistance() {
        return maximumResistance;
    }

    public float reduceDamage(float incomingDamage, float corruptionIntensity, MagicDamageClassification classification) {
        Objects.requireNonNull(classification, "classification");
        float boundedDamage = Float.isFinite(incomingDamage) ? Math.max(0.0F, incomingDamage) : 0.0F;
        if (!classification.magical()) {
            return boundedDamage;
        }
        if (!Float.isFinite(corruptionIntensity) || corruptionIntensity < 0.0F || corruptionIntensity > 1.0F) {
            return boundedDamage;
        }

        double resistance = maximumResistance * corruptionIntensity;
        return (float) (boundedDamage * (1.0D - resistance));
    }
}
