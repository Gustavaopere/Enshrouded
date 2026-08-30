package com.gustavaopere.enshrouded.flame.ward;

import net.minecraft.core.BlockPos;

import java.util.Objects;

/** Immutable active ward snapshot indexed from one loaded Flame Altar. */
public record FlameWardState(BlockPos center, int radius) {
    public FlameWardState {
        center = Objects.requireNonNull(center, "center").immutable();
        if (radius <= 0) {
            throw new IllegalArgumentException("radius must be > 0");
        }
    }
}
