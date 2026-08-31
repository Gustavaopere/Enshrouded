package com.gustavaopere.enshrouded.client.ambient;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/** Pure bounded particle presentation profile selected from synchronized Shroud state. */
public enum ShroudParticleProfile {
    NONE(0, 0),
    ORDINARY(8, 3),
    DEADLY(4, 6);

    private final int intervalTicks;
    private final int baseCount;

    ShroudParticleProfile(int intervalTicks, int baseCount) {
        this.intervalTicks = intervalTicks;
        this.baseCount = baseCount;
    }

    public int intervalTicks() {
        return intervalTicks;
    }

    public int baseCount() {
        return baseCount;
    }

    public static ShroudParticleProfile forState(ShroudSeverity severity, boolean sanctuarySuppressed) {
        Objects.requireNonNull(severity, "severity");
        if (sanctuarySuppressed || severity == ShroudSeverity.CLEAR) {
            return NONE;
        }
        return severity == ShroudSeverity.DEADLY ? DEADLY : ORDINARY;
    }
}
