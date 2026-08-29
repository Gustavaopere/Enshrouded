package com.gustavaopere.enshrouded.content.fluid;

import com.gustavaopere.enshrouded.exposure.redsludge.RedSludgeExposureHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;

/** Physical Red Sludge block; logical Shroud ownership remains outside the fluid. */
public final class RedSludgeBlock extends LiquidBlock {
    public RedSludgeBlock(FlowingFluid fluid, BlockBehaviour.Properties properties) {
        super(fluid, properties);
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        super.entityInside(state, level, pos, entity);
        RedSludgeExposureHandler.onContact(entity);
    }
}
