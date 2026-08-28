package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public final class ShroudCoreBlockEntity extends BlockEntity {
    public ShroudCoreBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SHROUD_CORE.get(), pos, state);
    }
}
