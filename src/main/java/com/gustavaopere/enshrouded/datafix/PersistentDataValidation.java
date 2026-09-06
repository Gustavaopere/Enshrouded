package com.gustavaopere.enshrouded.datafix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;

/** Strict structural validation helpers for versioned persistence decode paths. */
public final class PersistentDataValidation {
    private PersistentDataValidation() {
    }

    /**
     * Returns a required list only when the field exists as a list and its non-empty element type
     * matches the codec contract. Missing, mistyped and wrong-element lists fail closed instead of
     * being converted to an empty list by {@link CompoundTag#getList(String, int)}.
     */
    public static ListTag requireList(
            CompoundTag parent,
            String field,
            int expectedElementType,
            PersistentSubsystem subsystem) {
        Objects.requireNonNull(parent, "parent");
        Objects.requireNonNull(field, "field");
        Objects.requireNonNull(subsystem, "subsystem");

        Tag raw = parent.get(field);
        if (!(raw instanceof ListTag list)) {
            throw new PersistentDataFormatException(subsystem, "missing or mistyped required list " + field);
        }
        if (!list.isEmpty() && list.getElementType() != expectedElementType) {
            throw new PersistentDataFormatException(
                    subsystem,
                    "required list " + field + " has element type " + list.getElementType()
                            + " instead of " + expectedElementType
            );
        }
        return list;
    }
}
