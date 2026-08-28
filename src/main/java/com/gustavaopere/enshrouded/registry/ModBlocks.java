package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Enshrouded.MOD_ID);

    public static final DeferredBlock<ShroudCoreBlock> SHROUD_CORE = BLOCKS.register(
            "shroud_core",
            () -> new ShroudCoreBlock(
                    BlockBehaviour.Properties.of()
                            .strength(5.0F, 1200.0F)
                            .pushReaction(PushReaction.BLOCK)
            )
    );

    private ModBlocks() {
    }
}
