package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
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
        ModItems.ITEMS.register(modBus);
        ModEntities.register(modBus);
        ModBlockEntities.BLOCK_ENTITY_TYPES.register(modBus);
        ModMenus.MENUS.register(modBus);
        ModSounds.register(modBus);
        ShroudExposureAttachment.register(modBus);
        EntityCorruptionAttachment.register(modBus);
        ShroudCoreWorldgenRegistry.register(modBus);
    }
}
