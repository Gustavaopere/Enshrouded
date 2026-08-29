package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.Enshrouded;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

/**
 * Minimal persistent player exposure state. The authoritative runtime stores only the
 * versioned reserve; effective zone data is sampled from the canonical Shroud query.
 */
public record ShroudExposureAttachment(int schemaVersion, int remainingTicks) {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Enshrouded.MOD_ID);

    private static final MapCodec<SerializedExposure> RAW_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("schema_version").forGetter(SerializedExposure::schemaVersion),
            Codec.INT.fieldOf("remaining_ticks").forGetter(SerializedExposure::remainingTicks)
    ).apply(instance, SerializedExposure::new));

    public static final MapCodec<ShroudExposureAttachment> MAP_CODEC = RAW_MAP_CODEC.flatXmap(
            ShroudExposureAttachment::decode,
            state -> DataResult.success(new SerializedExposure(state.schemaVersion(), state.remainingTicks()))
    );
    public static final Codec<ShroudExposureAttachment> CODEC = MAP_CODEC.codec();

    public static final Supplier<AttachmentType<ShroudExposureAttachment>> PLAYER_EXPOSURE = ATTACHMENT_TYPES.register(
            "player_exposure",
            () -> AttachmentType.builder(() -> full(ExposureSchema.DEFAULT_MAX_RESERVE_TICKS))
                    .serialize(CODEC)
                    .build()
    );

    public ShroudExposureAttachment {
        if (schemaVersion != ExposureSchema.CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported exposure schema version: " + schemaVersion);
        }
        if (remainingTicks < 0) {
            throw new IllegalArgumentException("remainingTicks must be >= 0");
        }
    }

    public static void register(IEventBus modBus) {
        ATTACHMENT_TYPES.register(Objects.requireNonNull(modBus, "modBus"));
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
