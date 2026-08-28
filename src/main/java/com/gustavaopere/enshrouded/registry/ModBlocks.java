package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Enshrouded.MOD_ID);

    public static final DeferredBlock<Block> SHROUD_CORE = BLOCKS.registerSimpleBlock(
            "shroud_core",
            () -> BlockBehaviour.Properties.of()
                    .strength(5.0F, 1200.0F)
    );

    private ModBlocks() {
    }
}
