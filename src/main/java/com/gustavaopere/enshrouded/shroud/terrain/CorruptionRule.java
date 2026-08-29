package com.gustavaopere.enshrouded.shroud.terrain;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
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
        ShroudSeverity minimumSeverity,
        CorruptionSafetyClass safetyClass) {

    private static final Codec<ShroudSeverity> SEVERITY_CODEC = Codec.STRING.comapFlatMap(
            id -> ShroudSeverity.fromId(id)
                    .map(DataResult::success)
                    .orElseGet(() -> DataResult.error(() -> "Unknown Shroud severity: " + id)),
            ShroudSeverity::id
    );

    public static final Codec<CorruptionRule> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(CorruptionRule::id),
            ResourceLocation.CODEC.fieldOf("source_tag").forGetter(CorruptionRule::sourceTag),
            ResourceLocation.CODEC.fieldOf("result").forGetter(CorruptionRule::resultBlock),
            ResourceLocation.CODEC.fieldOf("reversal").forGetter(CorruptionRule::reversalBlock),
            Codec.floatRange(0.0F, 1.0F).fieldOf("min_intensity").forGetter(CorruptionRule::minIntensity),
            SEVERITY_CODEC.optionalFieldOf("min_severity", ShroudSeverity.SHROUD)
                    .forGetter(CorruptionRule::minimumSeverity),
            CorruptionSafetyClass.CODEC.fieldOf("safety").forGetter(CorruptionRule::safetyClass)
    ).apply(instance, CorruptionRule::new));

    /** Preserves the Stage-02 six-argument rule surface with ordinary SHROUD semantics. */
    public CorruptionRule(
            ResourceLocation id,
            ResourceLocation sourceTag,
            ResourceLocation resultBlock,
            ResourceLocation reversalBlock,
            float minIntensity,
            CorruptionSafetyClass safetyClass) {
        this(id, sourceTag, resultBlock, reversalBlock, minIntensity, ShroudSeverity.SHROUD, safetyClass);
    }

    public CorruptionRule {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(sourceTag, "sourceTag");
        Objects.requireNonNull(resultBlock, "resultBlock");
        Objects.requireNonNull(reversalBlock, "reversalBlock");
        Objects.requireNonNull(minimumSeverity, "minimumSeverity");
        Objects.requireNonNull(safetyClass, "safetyClass");
        if (!Float.isFinite(minIntensity) || minIntensity < 0.0F || minIntensity > 1.0F) {
            throw new IllegalArgumentException("minIntensity must be finite and within [0, 1]");
        }
        if (minimumSeverity == ShroudSeverity.CLEAR) {
            throw new IllegalArgumentException("corruption rules cannot target CLEAR severity");
        }
        if (resultBlock.equals(reversalBlock)) {
            throw new IllegalArgumentException("resultBlock and reversalBlock must differ");
        }
    }

    /** Canonical intensity/severity eligibility test shared by enqueue and apply-time revalidation. */
    public boolean appliesTo(ShroudSample sample) {
        Objects.requireNonNull(sample, "sample");
        return !sample.sanctuarySuppressed()
                && sample.severity() != ShroudSeverity.CLEAR
                && sample.intensity() > 0.0F
                && sample.intensity() >= minIntensity
                && sample.severity().ordinal() >= minimumSeverity.ordinal();
    }
}
