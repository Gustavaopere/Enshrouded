package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

/** GameTest-only bridge to the package-private indexed chunk-recovery boundary. */
public final class FlameAltarRecoveryGameTestAccess {
    private FlameAltarRecoveryGameTestAccess() {
    }

    public static void waitForChunk(ServerLevel level, BlockPos altarPos, ChunkPos missingChunk) {
        FlameAltarChunkRecoveryEvents.waitForChunk(level, altarPos, missingChunk);
    }

    public static void recoverWaitingForLoadedChunk(ServerLevel level, ChunkPos loadedChunk) {
        FlameAltarChunkRecoveryEvents.recoverWaitingForLoadedChunk(level, loadedChunk);
    }
}
