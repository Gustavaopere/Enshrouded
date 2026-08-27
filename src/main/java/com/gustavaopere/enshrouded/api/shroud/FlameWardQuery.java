package com.gustavaopere.enshrouded.api.shroud;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/**
 * Read-only Sanctuary boundary used by Shroud query and terrain-safety consumers.
 * Stage 05 supplies the indexed altar-backed implementation; Foundation provides
 * a total no-ward fallback so earlier stages never depend on Stage 05 classes.
 */
@FunctionalInterface
public interface FlameWardQuery {
    boolean suppresses(ServerLevel level, BlockPos pos);

    static FlameWardQuery none() {
        return (level, pos) -> false;
    }
}
