package com.gustavaopere.enshrouded.presentation;

import com.gustavaopere.enshrouded.network.AdvancedVfxPayload;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

/**
 * Emits one-shot authoritative presentation cues to nearby players in the same ServerLevel.
 * No chunk loading, world scan, particle spawning or gameplay mutation occurs here.
 */
public final class AdvancedVfxServerEmitter {
    private AdvancedVfxServerEmitter() {}

    public static int emit(ServerLevel level, BlockPos origin, AdvancedVfxCue cue) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(origin, "origin");
        Objects.requireNonNull(cue, "cue");
        if (!cue.serverAuthored()) {
            throw new IllegalArgumentException("server emitter requires a server-authored cue: " + cue.id());
        }

        double x = origin.getX() + 0.5D;
        double y = origin.getY() + 0.5D;
        double z = origin.getZ() + 0.5D;
        double maximumDistanceSquared = cue.maxDistance() * cue.maxDistance();
        AdvancedVfxPayload payload = AdvancedVfxPayload.of(cue, origin);
        int sent = 0;
        for (var player : level.players()) {
            if (player.distanceToSqr(x, y, z) > maximumDistanceSquared) {
                continue;
            }
            PacketDistributor.sendToPlayer(player, payload);
            sent++;
        }
        return sent;
    }
}
