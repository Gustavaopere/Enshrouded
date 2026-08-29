package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.content.block.ShroudGrowthBlock;
import com.gustavaopere.enshrouded.content.block.ShroudVeinBlock;
import com.gustavaopere.enshrouded.content.block.WitheredGrowthBlock;
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

    public static final DeferredBlock<ShroudGrowthBlock> SHROUD_GROWTH = BLOCKS.register(
            "shroud_growth",
            () -> new ShroudGrowthBlock(growthProperties())
    );

    public static final DeferredBlock<ShroudVeinBlock> SHROUD_VEIN = BLOCKS.register(
            "shroud_vein",
            () -> new ShroudVeinBlock(growthProperties())
    );

    public static final DeferredBlock<WitheredGrowthBlock> WITHERED_GROWTH = BLOCKS.register(
            "withered_growth",
            () -> new WitheredGrowthBlock(growthProperties())
    );

    private ModBlocks() {
    }

    private static BlockBehaviour.Properties growthProperties() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .noOcclusion()
                .instabreak()
                .replaceable()
                .pushReaction(PushReaction.DESTROY);
    }
}
