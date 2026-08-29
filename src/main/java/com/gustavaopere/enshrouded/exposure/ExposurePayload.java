package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Server-authored exposure presentation snapshot. This payload is clientbound-only and exposes
 * no mutation or command input surface.
 */
public record ExposurePayload(
        int payloadVersion,
        long sequence,
        int schemaVersion,
        int remainingTicks,
        int maxReserveTicks,
        float intensity,
        ShroudSeverity severity,
        boolean sanctuarySuppressed,
        boolean deadlyBarrierActive) implements CustomPacketPayload {

    public static final int CURRENT_VERSION = 1;
    public static final Type<ExposurePayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "exposure"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ExposurePayload> STREAM_CODEC =
            StreamCodec.ofMember(ExposurePayload::encode, ExposurePayload::decode);

    public ExposurePayload {
        if (payloadVersion != CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported exposure payload version: " + payloadVersion);
        }
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
        new ExposureSnapshot(
                schemaVersion,
                remainingTicks,
                maxReserveTicks,
                intensity,
                Objects.requireNonNull(severity, "severity"),
                sanctuarySuppressed,
                deadlyBarrierActive
        );
    }

    public static ExposurePayload fromSnapshot(long sequence, ExposureSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        return new ExposurePayload(
                CURRENT_VERSION,
                sequence,
                snapshot.schemaVersion(),
                snapshot.remainingTicks(),
                snapshot.maxReserveTicks(),
                snapshot.intensity(),
                snapshot.severity(),
                snapshot.sanctuarySuppressed(),
                snapshot.deadlyBarrierActive()
        );
    }

    public ExposureSnapshot snapshot() {
        return new ExposureSnapshot(
                schemaVersion,
                remainingTicks,
                maxReserveTicks,
                intensity,
                severity,
                sanctuarySuppressed,
                deadlyBarrierActive
        );
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(payloadVersion);
        buffer.writeVarLong(sequence);
        buffer.writeVarInt(schemaVersion);
        buffer.writeVarInt(remainingTicks);
        buffer.writeVarInt(maxReserveTicks);
        buffer.writeFloat(intensity);
        buffer.writeUtf(severity.id(), 16);
        buffer.writeBoolean(sanctuarySuppressed);
        buffer.writeBoolean(deadlyBarrierActive);
    }

    private static ExposurePayload decode(RegistryFriendlyByteBuf buffer) {
        int payloadVersion = buffer.readVarInt();
        long sequence = buffer.readVarLong();
        int schemaVersion = buffer.readVarInt();
        int remainingTicks = buffer.readVarInt();
        int maxReserveTicks = buffer.readVarInt();
        float intensity = buffer.readFloat();
        String severityId = buffer.readUtf(16);
        ShroudSeverity severity = ShroudSeverity.fromId(severityId)
                .orElseThrow(() -> new IllegalArgumentException("unknown Shroud severity id: " + severityId));
        boolean sanctuarySuppressed = buffer.readBoolean();
        boolean deadlyBarrierActive = buffer.readBoolean();
        return new ExposurePayload(
                payloadVersion,
                sequence,
                schemaVersion,
                remainingTicks,
                maxReserveTicks,
                intensity,
                severity,
                sanctuarySuppressed,
                deadlyBarrierActive
        );
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
