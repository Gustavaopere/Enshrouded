package com.gustavaopere.enshrouded.story.reward;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/** Registry-independent persistent identity encoded inside the Lich Skull item component. */
public record LichSkullIdentity(UUID encounterId, int manifestationIndex) {
    public static final int FORMAT_VERSION = 1;
    private static final String FORMAT_KEY = "Format";
    private static final String ENCOUNTER_KEY = "EncounterId";
    private static final String MANIFESTATION_KEY = "ManifestationIndex";

    public LichSkullIdentity {
        encounterId = Objects.requireNonNull(encounterId, "encounterId");
        if (manifestationIndex < 1) {
            throw new IllegalArgumentException("manifestationIndex must be >= 1");
        }
    }

    public CompoundTag encode() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(FORMAT_KEY, FORMAT_VERSION);
        tag.putUUID(ENCOUNTER_KEY, encounterId);
        tag.putInt(MANIFESTATION_KEY, manifestationIndex);
        return tag;
    }

    public static Optional<LichSkullIdentity> decode(CompoundTag tag) {
        if (tag == null
                || tag.getInt(FORMAT_KEY) != FORMAT_VERSION
                || !tag.contains(ENCOUNTER_KEY, Tag.TAG_INT_ARRAY)
                || !tag.contains(MANIFESTATION_KEY, Tag.TAG_INT)) {
            return Optional.empty();
        }
        try {
            int manifestationIndex = tag.getInt(MANIFESTATION_KEY);
            if (manifestationIndex < 1) {
                return Optional.empty();
            }
            return Optional.of(new LichSkullIdentity(tag.getUUID(ENCOUNTER_KEY), manifestationIndex));
        } catch (RuntimeException malformed) {
            return Optional.empty();
        }
    }
}
