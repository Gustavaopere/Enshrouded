package com.gustavaopere.enshrouded.content.fluid;

import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.registry.ModFluids;
import net.neoforged.neoforge.fluids.BaseFlowingFluid;

/**
 * Red Sludge fluid family. It deliberately has no bucket supplier in Level 1;
 * physical relocation never owns or creates logical Shroud state.
 */
public final class RedSludgeFluid {
    private RedSludgeFluid() {
    }

    private static BaseFlowingFluid.Properties properties() {
        return new BaseFlowingFluid.Properties(
                ModFluids.RED_SLUDGE_TYPE,
                ModFluids.RED_SLUDGE,
                ModFluids.FLOWING_RED_SLUDGE
        ).block(ModBlocks.RED_SLUDGE);
    }

    public static final class Source extends BaseFlowingFluid.Source {
        public Source() {
            super(properties());
        }
    }

    public static final class Flowing extends BaseFlowingFluid.Flowing {
        public Flowing() {
            super(properties());
        }
    }
}
