package com.gustavaopere.enshrouded.shroud.worldgen;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ShroudCoreWorldgenRegistry {
    public static final int CELL_SIZE_BLOCKS = 512;
    public static final int MINIMUM_SPACING_BLOCKS = 128;

    public static final DeferredRegister<Feature<?>> FEATURES =
            DeferredRegister.create(Registries.FEATURE, Enshrouded.MOD_ID);

    public static final DeferredHolder<Feature<?>, ShroudCoreFeature> SHROUD_CORE = FEATURES.register(
            "shroud_core",
            () -> new ShroudCoreFeature(
                    NoneFeatureConfiguration.CODEC,
                    new ShroudCoreCandidateField(CELL_SIZE_BLOCKS, MINIMUM_SPACING_BLOCKS)
            )
    );

    private ShroudCoreWorldgenRegistry() {
    }

    public static void register(IEventBus modBus) {
        FEATURES.register(modBus);
    }
}
