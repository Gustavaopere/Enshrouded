package com.gustavaopere.enshrouded.api.shroud;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Read-only authoritative Shroud lookup. Implementations must not mutate world state
 * and must never force-load a chunk in order to answer a sample request.
 */
@FunctionalInterface
public interface ShroudQuery {
    ShroudSample sample(ServerLevel level, BlockPos pos, @Nullable Entity entity);
}
