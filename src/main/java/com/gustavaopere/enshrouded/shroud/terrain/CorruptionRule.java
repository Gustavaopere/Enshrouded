package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** Immutable, explicitly reversible terrain conversion rule. */
public record CorruptionRule(
        ResourceLocation id,
        ResourceLocation sourceTag,
        ResourceLocation resultBlock,
        ResourceLocation reversalBlock,
        float minIntensity,
        CorruptionSafetyClass safetyClass) {

    public CorruptionRule {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sourceTag, "sourceTag");
        Objects.requireNonNull(resultBlock, "resultBlock");
        Objects.requireNonNull(reversalBlock, "reversalBlock");
        Objects.requireNonNull(safetyClass, "safetyClass");
        if (!Float.isFinite(minIntensity) || minIntensity < 0.0F || minIntensity > 1.0F) {
            throw new IllegalArgumentException("minIntensity must be finite and within [0, 1]");
        }
        if (resultBlock.equals(reversalBlock)) {
            throw new IllegalArgumentException("resultBlock and reversalBlock must differ");
        }
    }
}
