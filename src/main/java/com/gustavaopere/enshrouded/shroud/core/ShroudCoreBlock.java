package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Physical Shroud Core anchor.
 *
 * <p>The block remains mechanically identical to the Stage 01 core. Stage 10 only changes its
 * presentation path to a GeckoLib BlockEntity renderer and gives the existing BlockEntity a
 * bounded server ticker for a read-only presentation profile.</p>
 */
public final class ShroudCoreBlock extends Block implements EntityBlock {
    public ShroudCoreBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShroudCoreBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide() || type != ModBlockEntities.SHROUD_CORE.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) ->
                ShroudCoreBlockEntity.serverTick(tickerLevel, pos, tickerState, (ShroudCoreBlockEntity) blockEntity);
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof ShroudCoreBlockEntity coreBlockEntity) {
            coreBlockEntity.retirePhysicalCore(serverLevel);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
