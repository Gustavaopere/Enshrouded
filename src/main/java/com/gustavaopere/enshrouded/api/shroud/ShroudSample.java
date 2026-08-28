package com.gustavaopere.enshrouded.api.shroud;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record ShroudSample(
        float intensity,
        ShroudSeverity severity,
        Optional<UUID> sourceId,
        boolean sanctuarySuppressed) {

    public ShroudSample {
        if (!Float.isFinite(intensity) || intensity < 0.0f || intensity > 1.0f) {
            throw new IllegalArgumentException("intensity must be finite and within [0, 1]");
        }
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(sourceId, "sourceId");
    }

    public static ShroudSample clear() {
        return new ShroudSample(0.0f, ShroudSeverity.CLEAR, Optional.empty(), false);
    }
}
