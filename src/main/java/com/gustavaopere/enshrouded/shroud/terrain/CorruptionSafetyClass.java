package com.gustavaopere.enshrouded.shroud.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

import java.util.Locale;

/** Rule-level safety classification; this does not replace Foundation MutationKind. */
public enum CorruptionSafetyClass {
    SAFE,
    AGGRESSIVE;

    public static final Codec<CorruptionSafetyClass> CODEC = Codec.STRING.comapFlatMap(
            CorruptionSafetyClass::decode,
            CorruptionSafetyClass::serializedName
    );

    public String serializedName() {
        return name().toLowerCase(Locale.ROOT);
    }

    private static DataResult<CorruptionSafetyClass> decode(String value) {
        if (value == null) {
            return DataResult.error(() -> "Corruption safety class cannot be null");
        }
        return switch (value) {
            case "safe" -> DataResult.success(SAFE);
            case "aggressive" -> DataResult.success(AGGRESSIVE);
            default -> DataResult.error(() -> "Unknown corruption safety class: " + value);
        };
    }
}
