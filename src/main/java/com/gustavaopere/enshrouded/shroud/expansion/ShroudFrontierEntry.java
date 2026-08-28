package com.gustavaopere.enshrouded.shroud.expansion;

import com.gustavaopere.enshrouded.shroud.state.ShroudCellPos;

import java.util.Objects;

/**
 * Immutable unit of pending logical Shroud expansion work.
 */
public record ShroudFrontierEntry(ShroudCellPos position, long expansionEpoch, long sequence) {
    public ShroudFrontierEntry {
        Objects.requireNonNull(position, "position");
        if (expansionEpoch < 0L) {
            throw new IllegalArgumentException("expansionEpoch must be >= 0");
        }
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
    }
}
