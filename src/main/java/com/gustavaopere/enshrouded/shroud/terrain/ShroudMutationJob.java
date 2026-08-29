package com.gustavaopere.enshrouded.shroud.terrain;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

/** One bounded unit of deferred loaded-world Shroud materialization work. */
public record ShroudMutationJob(
        BlockPos pos,
        ResourceLocation ruleId,
        ResourceLocation expectedSourceBlock) {

    public ShroudMutationJob {
        pos = Objects.requireNonNull(pos, "pos").immutable();
        Objects.requireNonNull(ruleId, "ruleId");
        Objects.requireNonNull(expectedSourceBlock, "expectedSourceBlock");
    }
}
