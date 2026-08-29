package com.gustavaopere.enshrouded.ecology.state;

public enum CorruptionStage {
    CLEAR,
    TAINTED,
    CORRUPTED;

    public static CorruptionStage fromIntensity(float intensity) {
        if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
            throw new IllegalArgumentException("intensity must be finite and within [0, 1]");
        }
        if (intensity <= 0.0F) {
            return CLEAR;
        }
        return intensity < 0.5F ? TAINTED : CORRUPTED;
    }
}
