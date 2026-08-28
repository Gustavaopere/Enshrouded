package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.shroud.core.ShroudCoreBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, Enshrouded.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ShroudCoreBlockEntity>> SHROUD_CORE =
            BLOCK_ENTITY_TYPES.register(
                    "shroud_core",
                    () -> BlockEntityType.Builder.of(ShroudCoreBlockEntity::new, ModBlocks.SHROUD_CORE.get()).build(null)
            );

    private ModBlockEntities() {
    }
}
