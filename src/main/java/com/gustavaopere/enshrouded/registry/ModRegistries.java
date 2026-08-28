package com.gustavaopere.enshrouded.registry;

import net.neoforged.bus.api.IEventBus;

import java.util.Objects;

public final class ModRegistries {
    private ModRegistries() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
    }
}
