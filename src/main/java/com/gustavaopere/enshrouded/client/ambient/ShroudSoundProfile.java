package com.gustavaopere.enshrouded.client.ambient;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/** Pure presentation profile selected only from the latest server-authored Shroud state. */
public enum ShroudSoundProfile {
    NONE(0, 0.0F),
    ORDINARY(180, 0.45F),
    DEADLY(80, 0.80F);

    private final int cooldownTicks;
    private final float baseVolume;

    ShroudSoundProfile(int cooldownTicks, float baseVolume) {
        this.cooldownTicks = cooldownTicks;
        this.baseVolume = baseVolume;
    }

    public int cooldownTicks() {
        return cooldownTicks;
    }

    public float baseVolume() {
        return baseVolume;
    }

    public static ShroudSoundProfile forState(ShroudSeverity severity, boolean sanctuarySuppressed) {
        Objects.requireNonNull(severity, "severity");
        if (sanctuarySuppressed || severity == ShroudSeverity.CLEAR) {
            return NONE;
        }
        return severity == ShroudSeverity.DEADLY ? DEADLY : ORDINARY;
    }
}
