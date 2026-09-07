package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/**
 * Ephemeral server-to-client Stage 10.08 presentation cue.
 *
 * <p>This packet carries only an already-authorized discrete cue and its visual origin. It cannot
 * mutate gameplay state and deliberately rejects snapshot-derived/local transition cues.</p>
 */
public record AdvancedVfxPayload(
        int payloadVersion,
        AdvancedVfxCue cue,
        BlockPos origin) implements CustomPacketPayload {

    public static final int CURRENT_VERSION = 1;
    public static final Type<AdvancedVfxPayload> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "advanced_vfx"));
    public static final StreamCodec<RegistryFriendlyByteBuf, AdvancedVfxPayload> STREAM_CODEC =
            StreamCodec.ofMember(AdvancedVfxPayload::encode, AdvancedVfxPayload::decode);

    public AdvancedVfxPayload {
        if (payloadVersion != CURRENT_VERSION) {
            throw new IllegalArgumentException("unsupported advanced VFX payload version: " + payloadVersion);
        }
        Objects.requireNonNull(cue, "cue");
        Objects.requireNonNull(origin, "origin");
        if (!cue.serverAuthored()) {
            throw new IllegalArgumentException("advanced VFX payload accepts server-authored cues only: " + cue.id());
        }
        origin = origin.immutable();
    }

    public static AdvancedVfxPayload of(AdvancedVfxCue cue, BlockPos origin) {
        return new AdvancedVfxPayload(CURRENT_VERSION, cue, origin);
    }

    private void encode(RegistryFriendlyByteBuf buffer) {
        buffer.writeVarInt(payloadVersion);
        buffer.writeUtf(cue.id(), 48);
        buffer.writeLong(origin.asLong());
    }

    private static AdvancedVfxPayload decode(RegistryFriendlyByteBuf buffer) {
        int payloadVersion = buffer.readVarInt();
        String cueId = buffer.readUtf(48);
        AdvancedVfxCue cue = AdvancedVfxCue.fromId(cueId)
                .orElseThrow(() -> new IllegalArgumentException("unknown advanced VFX cue id: " + cueId));
        BlockPos origin = BlockPos.of(buffer.readLong());
        return new AdvancedVfxPayload(payloadVersion, cue, origin);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
