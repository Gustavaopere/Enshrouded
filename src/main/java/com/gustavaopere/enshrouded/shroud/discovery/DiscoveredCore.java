package com.gustavaopere.enshrouded.shroud.discovery;

import com.gustavaopere.enshrouded.shroud.core.CoreLifecycleState;
import net.minecraft.core.BlockPos;

import java.util.Objects;
import java.util.UUID;

/** Owner-scoped knowledge of a Shroud core that is safe to expose to client presentation. */
public record DiscoveredCore(
        UUID coreId,
        String dimensionId,
        BlockPos pos,
        CoreLifecycleState lifecycle) {

    public DiscoveredCore {
        Objects.requireNonNull(coreId, "coreId");
        Objects.requireNonNull(dimensionId, "dimensionId");
        Objects.requireNonNull(pos, "pos");
        Objects.requireNonNull(lifecycle, "lifecycle");
        if (dimensionId.isBlank() || !dimensionId.equals(dimensionId.strip())) {
            throw new IllegalArgumentException("dimension id must be a non-blank canonical string");
        }
        if (lifecycle != CoreLifecycleState.ACTIVE && lifecycle != CoreLifecycleState.PURIFIED) {
            throw new IllegalArgumentException("only active or purified cores may be exposed as discovered knowledge");
        }
        pos = pos.immutable();
    }
}
