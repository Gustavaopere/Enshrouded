package com.gustavaopere.enshrouded.ecology.state;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.datafix.EnshroudedDataFixer;
import com.gustavaopere.enshrouded.datafix.PersistentSubsystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;
import java.util.function.Supplier;

/** Persistent, version-aware corruption state for non-player living entities. */
public record EntityCorruptionAttachment(int schemaVersion, float intensity) {
    private static final MapCodec<SerializedState> RAW_MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("schema_version").forGetter(SerializedState::schemaVersion),
            Codec.FLOAT.fieldOf("intensity").forGetter(SerializedState::intensity)
    ).apply(instance, SerializedState::new));

    public static final MapCodec<EntityCorruptionAttachment> MAP_CODEC = RAW_MAP_CODEC.flatXmap(
            EntityCorruptionAttachment::decode,
            state -> DataResult.success(new SerializedState(state.schemaVersion(), state.intensity()))
    );
    public static final Codec<EntityCorruptionAttachment> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<ByteBuf, EntityCorruptionAttachment> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_INT,
            EntityCorruptionAttachment::schemaVersion,
            ByteBufCodecs.FLOAT,
            EntityCorruptionAttachment::intensity,
            EntityCorruptionAttachment::new
    );

    public static final Supplier<AttachmentType<EntityCorruptionAttachment>> ENTITY_CORRUPTION =
            () -> RegistryHolder.ENTITY_CORRUPTION.get();

    public EntityCorruptionAttachment {
        if (schemaVersion != EntityCorruptionSchema.CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported entity corruption schema version: " + schemaVersion);
        }
        if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
            throw new IllegalArgumentException("intensity must be finite and within [0, 1]");
        }
    }

    public static EntityCorruptionAttachment clean() {
        return new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, 0.0F);
    }

    public CorruptionStage stage() {
        return CorruptionStage.fromIntensity(intensity);
    }

    public static void register(IEventBus modBus) {
        RegistryHolder.ATTACHMENT_TYPES.register(Objects.requireNonNull(modBus, "modBus"));
    }

    private static DataResult<EntityCorruptionAttachment> decode(SerializedState serialized) {
        CompoundTag raw = new CompoundTag();
        raw.putInt(EnshroudedDataFixer.SCHEMA_VERSION_TAG, serialized.schemaVersion());
        raw.putFloat("intensity", serialized.intensity());

        final CompoundTag migrated;
        try {
            migrated = EnshroudedDataFixer.migrate(PersistentSubsystem.ENTITY_CORRUPTION, raw);
        } catch (IllegalArgumentException failure) {
            return DataResult.error(failure::getMessage);
        }
        float intensity = migrated.getFloat("intensity");
        if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
            return DataResult.error(() -> "intensity must be finite and within [0, 1]");
        }
        return DataResult.success(new EntityCorruptionAttachment(EntityCorruptionSchema.CURRENT_VERSION, intensity));
    }

    private record SerializedState(int schemaVersion, float intensity) {
    }

    private static final class RegistryHolder {
        private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
                DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Enshrouded.MOD_ID);

        private static final Supplier<AttachmentType<EntityCorruptionAttachment>> ENTITY_CORRUPTION = ATTACHMENT_TYPES.register(
                "entity_corruption",
                () -> AttachmentType.builder(EntityCorruptionAttachment::clean)
                        .serialize(CODEC)
                        .sync(STREAM_CODEC)
                        .build()
        );

        private RegistryHolder() {
        }
    }
}
