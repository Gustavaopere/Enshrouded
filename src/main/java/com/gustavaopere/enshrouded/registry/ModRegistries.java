package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.exposure.ShroudExposureAttachment;
import com.gustavaopere.enshrouded.shroud.worldgen.ShroudCoreWorldgenRegistry;
import net.neoforged.bus.api.IEventBus;

import java.util.Objects;

public final class ModRegistries {
    private ModRegistries() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        ModFluids.register(modBus);
        ModBlocks.BLOCKS.register(modBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        ShroudExposureAttachment.register(modBus);
        ShroudCoreWorldgenRegistry.register(modBus);
    }
}
