package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** GameTest-only bridge to the package-private bounded chunk-recovery callback. */
public final class FlameAltarRecoveryGameTestAccess {
    private FlameAltarRecoveryGameTestAccess() {
    }

    public static void recoverAltarsIntersecting(ServerLevel level, ChunkPos loadedPos) {
        FlameAltarChunkRecoveryEvents.recoverAltarsIntersecting(level, loadedPos);
    }
}
