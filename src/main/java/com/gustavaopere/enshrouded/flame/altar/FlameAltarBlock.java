package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

/** Physical server-authoritative interface for Flame rituals. */
public final class FlameAltarBlock extends Block implements EntityBlock {
    public FlameAltarBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new FlameAltarBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level,
            BlockState state,
            BlockEntityType<T> type) {
        if (level.isClientSide || type != ModBlockEntities.FLAME_ALTAR.get()) {
            return null;
        }
        return (tickerLevel, pos, tickerState, blockEntity) -> {
            if (tickerLevel instanceof ServerLevel serverLevel
                    && blockEntity instanceof FlameAltarBlockEntity altar) {
                FlameAltarBlockEntity.serverTick(serverLevel, altar);
            }
        };
    }

    @Override
    protected InteractionResult useWithoutItem(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            BlockHitResult hitResult) {
        if (level instanceof ServerLevel serverLevel
                && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof FlameAltarBlockEntity altar) {
            if (!altar.isFormed()) {
                altar.requestFormation(serverLevel);
            } else {
                serverPlayer.openMenu(altar);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston) {
        if (state.getBlock() != newState.getBlock()
                && level instanceof ServerLevel serverLevel
                && level.getBlockEntity(pos) instanceof FlameAltarBlockEntity altar) {
            altar.unform(serverLevel);
            altar.dropContents(serverLevel);
        }
        super.onRemove(state, level, pos, newState, movedByPiston);
    }
}
