package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Bounded structural lifecycle routing for first-party Flame Altar shell parts. */
final class FlameAltarStructureLifecycle {
    private FlameAltarStructureLifecycle() {
    }

    static void onRequiredShellRemoved(ServerLevel level, BlockPos shellPos) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) {
                    continue;
                }

                BlockPos candidate = shellPos.offset(dx, 0, dz);
                if (!level.getChunkSource().hasChunk(candidate.getX() >> 4, candidate.getZ() >> 4)) {
                    continue;
                }
                if (level.getBlockEntity(candidate) instanceof FlameAltarBlockEntity altar && altar.isFormed()) {
                    altar.unform(level);
                }
            }
        }
    }
}
