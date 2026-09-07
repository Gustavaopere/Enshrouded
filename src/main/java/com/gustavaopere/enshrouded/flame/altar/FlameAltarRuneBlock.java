package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

/** Presentation-only corner shell component for the Flame Altar multiblock. */
public final class FlameAltarRuneBlock extends Block {
    public static final BooleanProperty FORMED = BooleanProperty.create("formed");

    public FlameAltarRuneBlock(Properties properties) {
        super(properties);
        registerDefaultState(stateDefinition.any().setValue(FORMED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FORMED);
    }
}
