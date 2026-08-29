package com.gustavaopere.enshrouded.exposure;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Minimal persistent player exposure state. The authoritative runtime stores only the
 * versioned reserve; effective zone data is sampled from the canonical Shroud query.
 */
public record ShroudExposureAttachment(int schemaVersion, int remainingTicks) {
    private static final MapCodec<SerializedExposure> RAW_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("schema_version").forGetter(SerializedExposure::schemaVersion),
            Codec.INT.fieldOf("remaining_ticks").forGetter(SerializedExposure::remainingTicks)
    ).apply(instance, SerializedExposure::new));

    public static final MapCodec<ShroudExposureAttachment> MAP_CODEC = RAW_MAP_CODEC.flatXmap(
            ShroudExposureAttachment::decode,
            state -> DataResult.success(new SerializedExposure(state.schemaVersion(), state.remainingTicks()))
    );
    public static final Codec<ShroudExposureAttachment> CODEC = MAP_CODEC.codec();

    public ShroudExposureAttachment {
        if (schemaVersion != ExposureSchema.CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported exposure schema version: " + schemaVersion);
        }
        if (remainingTicks < 0) {
            throw new IllegalArgumentException("remainingTicks must be >= 0");
        }
    }

    public static ShroudExposureAttachment full(int maxReserveTicks) {
        if (maxReserveTicks <= 0) {
            throw new IllegalArgumentException("maxReserveTicks must be > 0");
        }
        return new ShroudExposureAttachment(ExposureSchema.CURRENT_VERSION, maxReserveTicks);
    }

    private static DataResult<ShroudExposureAttachment> decode(SerializedExposure serialized) {
        if (serialized.schemaVersion() != ExposureSchema.CURRENT_VERSION) {
            return DataResult.error(() -> "unsupported exposure schema version: " + serialized.schemaVersion()
                    + " (current=" + ExposureSchema.CURRENT_VERSION + ")");
        }
        if (serialized.remainingTicks() < 0) {
            return DataResult.error(() -> "remaining_ticks must be >= 0");
        }
        return DataResult.success(new ShroudExposureAttachment(serialized.schemaVersion(), serialized.remainingTicks()));
    }

    private record SerializedExposure(int schemaVersion, int remainingTicks) {
    }
}
