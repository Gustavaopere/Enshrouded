package com.gustavaopere.enshrouded.shroud.query;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/**
 * Stable Level-1 intensity boundaries used by the authoritative Shroud query.
 * Stored DEADLY metadata remains authoritative even below the numeric threshold.
 */
public record ShroudSeverityThresholds(float deadlyAtOrAbove) {
    public static final float DEFAULT_DEADLY_AT_OR_ABOVE = 0.75f;

    public ShroudSeverityThresholds {
        if (!Float.isFinite(deadlyAtOrAbove) || deadlyAtOrAbove <= 0.0f || deadlyAtOrAbove > 1.0f) {
            throw new IllegalArgumentException("deadlyAtOrAbove must be finite and within (0, 1]");
        }
    }

    public static ShroudSeverityThresholds levelOneDefaults() {
        return new ShroudSeverityThresholds(DEFAULT_DEADLY_AT_OR_ABOVE);
    }

    public ShroudSeverity classify(double intensity, ShroudSeverity storedSeverity) {
        Objects.requireNonNull(storedSeverity, "storedSeverity");
        if (!Double.isFinite(intensity) || intensity < 0.0D || intensity > 1.0D) {
            throw new IllegalArgumentException("intensity must be finite and within [0, 1]");
        }
        if (intensity <= 0.0D) {
            return ShroudSeverity.CLEAR;
        }
        if (storedSeverity == ShroudSeverity.DEADLY || intensity >= deadlyAtOrAbove) {
            return ShroudSeverity.DEADLY;
        }
        return ShroudSeverity.SHROUD;
    }
}
