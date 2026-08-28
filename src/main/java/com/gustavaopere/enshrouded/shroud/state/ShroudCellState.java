package com.gustavaopere.enshrouded.shroud.state;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

public record ShroudCellState(ShroudCellPos position, double intensity, ShroudSeverity severity) {
    public ShroudCellState {
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(severity, "severity");
        if (!Double.isFinite(intensity) || intensity < 0.0D || intensity > 1.0D) {
            throw new IllegalArgumentException("intensity must be finite and within [0, 1]");
        }
    }
}
