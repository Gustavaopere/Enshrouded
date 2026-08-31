package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;

import java.util.Objects;

/** Pure client presentation mapping from the server-authored Madness stage. */
public enum MadnessAudioCue {
    NONE(0, 0.0F),
    CRITICAL(120, 0.70F),
    FATAL(60, 1.0F);

    private final int cooldownTicks;
    private final float volumeMultiplier;

    MadnessAudioCue(int cooldownTicks, float volumeMultiplier) {
        this.cooldownTicks = cooldownTicks;
        this.volumeMultiplier = volumeMultiplier;
    }

    public int cooldownTicks() {
        return cooldownTicks;
    }

    public float volumeMultiplier() {
        return volumeMultiplier;
    }

    public static MadnessAudioCue forStage(MadnessStage stage) {
        Objects.requireNonNull(stage, "stage");
        return switch (stage) {
            case CRITICAL -> CRITICAL;
            case FATAL -> FATAL;
            default -> NONE;
        };
    }
}
