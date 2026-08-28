package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Minimal server-to-client presentation snapshot. It intentionally contains no world field,
 * core list or mutable server command surface.
 */
public record ShroudSamplePayload(
        int payloadVersion,
        long sequence,
        float intensity,
        ShroudSeverity severity,
        Optional<UUID> sourceId,
        boolean sanctuarySuppressed) implements CustomPacketPayload {

    public static final int CURRENT_VERSION = 1;
    public static final Type<ShroudSamplePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_sample"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShroudSamplePayload> STREAM_CODEC =
            StreamCodec.ofMember(ShroudSamplePayload::encode, ShroudSamplePayload::decode);

    public ShroudSamplePayload {
        if (payloadVersion != CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported Shroud sample payload version: " + payloadVersion);
        }
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
        Objects.requireNonNull(severity, "severity");
        Objects.requireNonNull(sourceId, "sourceId");
        new ShroudSample(intensity, severity, sourceId, sanctuarySuppressed);
    }

    public static ShroudSamplePayload fromSample(long sequence, ShroudSample sample) {
        Objects.requireNonNull(sample, "sample");
        return new ShroudSamplePayload(
                CURRENT_VERSION,
                sequence,
                sample.intensity(),
                sample.severity(),
                sample.sourceId(),
                sample.sanctuarySuppressed());
    }

    public ShroudSample sample() {
        return new ShroudSample(intensity, severity, sourceId, sanctuarySuppressed);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(payloadVersion);
        buffer.writeVarLong(sequence);
        buffer.writeFloat(intensity);
        buffer.writeUtf(severity.id(), 16);
        buffer.writeBoolean(sourceId.isPresent());
        sourceId.ifPresent(buffer::writeUUID);
        buffer.writeBoolean(sanctuarySuppressed);
    }

    private static ShroudSamplePayload decode(RegistryFriendlyByteBuf buffer) {
        int payloadVersion = buffer.readVarInt();
        long sequence = buffer.readVarLong();
        float intensity = buffer.readFloat();
        String severityId = buffer.readUtf(16);
        ShroudSeverity severity = ShroudSeverity.fromId(severityId)
                .orElseThrow(() -> new IllegalArgumentException("unknown Shroud severity id: " + severityId));
        Optional<UUID> sourceId = buffer.readBoolean()
                ? Optional.of(buffer.readUUID())
                : Optional.empty();
        boolean sanctuarySuppressed = buffer.readBoolean();
        return new ShroudSamplePayload(
                payloadVersion,
                sequence,
                intensity,
                severity,
                sourceId,
                sanctuarySuppressed);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
