package com.gustavaopere.enshrouded.api.shroud;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

/** Central terrain-safety gate for all Enshrouded-owned block mutation. */
@FunctionalInterface
public interface MutationAuthority {
    boolean canMutate(ServerLevel level, BlockPos pos, MutationKind kind);
}
