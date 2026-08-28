package com.gustavaopere.enshrouded.api.story;

import net.minecraft.core.BlockPos;

import java.util.Objects;
import java.util.UUID;

public record EncounterContext(UUID encounterId, BlockPos origin, int manifestationLevel, long seed) {
    public EncounterContext {
        Objects.requireNonNull(encounterId, "encounterId");
        origin = Objects.requireNonNull(origin, "origin").immutable();
        if (manifestationLevel < 1) {
            throw new IllegalArgumentException("manifestationLevel must be >= 1");
        }
    }

    public EncounterContext(UUID encounterId, int manifestationLevel, long seed) {
        this(encounterId, BlockPos.ZERO, manifestationLevel, seed);
    }
}
