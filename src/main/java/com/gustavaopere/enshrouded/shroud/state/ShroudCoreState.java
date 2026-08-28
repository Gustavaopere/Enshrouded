package com.gustavaopere.enshrouded.shroud.state;

import net.minecraft.core.BlockPos;

import java.util.Objects;
import java.util.UUID;

public record ShroudCoreState(
        UUID id,
        BlockPos center,
        int tier,
        String lifecycleState,
        int maxInfluenceRadius,
        long expansionSeed,
        long expansionEpoch,
        UUID regionId) {

    public ShroudCoreState {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(center, "center");
        Objects.requireNonNull(lifecycleState, "lifecycleState");
        Objects.requireNonNull(regionId, "regionId");
        if (tier < 1) {
            throw new IllegalArgumentException("tier must be >= 1");
        }
        if (lifecycleState.isBlank()) {
            throw new IllegalArgumentException("lifecycleState must not be blank");
        }
        if (maxInfluenceRadius <= 0) {
            throw new IllegalArgumentException("maxInfluenceRadius must be > 0");
        }
        if (expansionEpoch < 0L) {
            throw new IllegalArgumentException("expansionEpoch must be >= 0");
        }
    }
}
