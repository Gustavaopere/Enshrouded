package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.api.progression.ProgressionOwner;
import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** Complete authorized marker snapshot for one resolved progression owner. */
public record ShroudDiscoveryPayload(
        int payloadVersion,
        long sequence,
        String ownerStableKey,
        List<DiscoveredCore> cores) implements CustomPacketPayload {

    public static final int CURRENT_VERSION = 1;
    public static final Type<ShroudDiscoveryPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_discovery"));
    public static final StreamCodec<RegistryFriendlyByteBuf, ShroudDiscoveryPayload> STREAM_CODEC =
            StreamCodec.ofMember(ShroudDiscoveryPayload::encode, ShroudDiscoveryPayload::decode);

    private static final Comparator<DiscoveredCore> CORE_ORDER = Comparator
            .comparing(DiscoveredCore::dimensionId)
            .thenComparing(core -> core.coreId().toString());

    public ShroudDiscoveryPayload {
        if (payloadVersion != CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported Shroud discovery payload version: " + payloadVersion);
        }
        if (sequence < 0L) {
            throw new IllegalArgumentException("sequence must be >= 0");
        }
        if (ProgressionOwner.parse(ownerStableKey).isEmpty()) {
            throw new IllegalArgumentException("invalid discovery owner stable key: " + ownerStableKey);
        }
        cores = canonicalize(cores);
    }

    static List<DiscoveredCore> canonicalize(List<DiscoveredCore> cores) {
        Objects.requireNonNull(cores, "cores");
        ArrayList<DiscoveredCore> sorted = new ArrayList<>(cores.size());
        HashSet<UUID> seen = new HashSet<>();
        for (DiscoveredCore core : cores) {
            Objects.requireNonNull(core, "core");
            if (!core.markerVisible()) {
                throw new IllegalArgumentException("non-visible core lifecycle must not cross the discovery payload boundary: "
                        + core.lifecycle());
            }
            if (!seen.add(core.coreId())) {
                throw new IllegalArgumentException("duplicate discovery payload core id: " + core.coreId());
            }
            sorted.add(core);
        }
        sorted.sort(CORE_ORDER);
        return List.copyOf(sorted);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(payloadVersion);
        buffer.writeVarLong(sequence);
        buffer.writeUtf(ownerStableKey, 256);
        buffer.writeVarInt(cores.size());
        for (DiscoveredCore core : cores) {
            buffer.writeUUID(core.coreId());
            buffer.writeUtf(core.dimensionId(), 256);
            buffer.writeBlockPos(core.pos());
            buffer.writeUtf(core.lifecycle().id(), 32);
        }
    }

    private static ShroudDiscoveryPayload decode(RegistryFriendlyByteBuf buffer) {
        int version = buffer.readVarInt();
        long sequence = buffer.readVarLong();
        String owner = buffer.readUtf(256);
        int size = buffer.readVarInt();
        if (size < 0 || size > 4096) {
            throw new IllegalArgumentException("invalid discovery payload core count: " + size);
        }
        ArrayList<DiscoveredCore> cores = new ArrayList<>(size);
        for (int index = 0; index < size; index++) {
            UUID coreId = buffer.readUUID();
            String dimension = buffer.readUtf(256);
            BlockPos pos = buffer.readBlockPos();
            String lifecycleId = buffer.readUtf(32);
            CoreLifecycleState lifecycle = CoreLifecycleState.fromId(lifecycleId)
                    .orElseThrow(() -> new IllegalArgumentException("unknown discovery payload lifecycle: " + lifecycleId));
            cores.add(new DiscoveredCore(coreId, dimension, pos, lifecycle));
        }
        return new ShroudDiscoveryPayload(version, sequence, owner, cores);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
