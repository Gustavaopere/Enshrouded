package com.gustavaopere.enshrouded.datafix;

import net.minecraft.nbt.CompoundTag;

import java.util.Objects;

/** One deterministic migration step between two adjacent persistent schema versions. */
@FunctionalInterface
public interface SchemaMigration {
    CompoundTag migrate(CompoundTag input);

    static CompoundTag copyOf(CompoundTag input) {
        return Objects.requireNonNull(input, "input").copy();
    }
}
