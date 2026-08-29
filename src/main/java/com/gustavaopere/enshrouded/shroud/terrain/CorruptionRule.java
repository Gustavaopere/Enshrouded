package com.gustavaopere.enshrouded.shroud.terrain;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
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

    public static final Codec<CorruptionRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CorruptionRule::id),
            ResourceLocation.CODEC.fieldOf("source_tag").forGetter(CorruptionRule::sourceTag),
            ResourceLocation.CODEC.fieldOf("result").forGetter(CorruptionRule::resultBlock),
            ResourceLocation.CODEC.fieldOf("reversal").forGetter(CorruptionRule::reversalBlock),
            Codec.floatRange(0.0F, 1.0F).fieldOf("min_intensity").forGetter(CorruptionRule::minIntensity),
            CorruptionSafetyClass.CODEC.fieldOf("safety").forGetter(CorruptionRule::safetyClass)
    ).apply(instance, CorruptionRule::new));

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
