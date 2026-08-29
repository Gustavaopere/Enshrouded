package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.content.fluid.RedSludgeFluid;
import com.gustavaopere.enshrouded.content.fluid.RedSludgeFluidType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FlowingFluid;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.fluids.FluidType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.Objects;

public final class ModFluids {
    public static final DeferredRegister<FluidType> FLUID_TYPES =
            DeferredRegister.create(NeoForgeRegistries.Keys.FLUID_TYPES, Enshrouded.MOD_ID);
    public static final DeferredRegister<Fluid> FLUIDS =
            DeferredRegister.create(BuiltInRegistries.FLUID, Enshrouded.MOD_ID);

    public static final DeferredHolder<FluidType, FluidType> RED_SLUDGE_TYPE = FLUID_TYPES.register(
            "red_sludge",
            () -> new RedSludgeFluidType(FluidType.Properties.create()
                    .density(1800)
                    .viscosity(2400)
                    .canSwim(false)
                    .canDrown(false))
    );

    public static final DeferredHolder<Fluid, FlowingFluid> RED_SLUDGE = FLUIDS.register(
            "red_sludge",
            RedSludgeFluid.Source::new
    );

    public static final DeferredHolder<Fluid, FlowingFluid> FLOWING_RED_SLUDGE = FLUIDS.register(
            "flowing_red_sludge",
            RedSludgeFluid.Flowing::new
    );

    private ModFluids() {
    }

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        FLUID_TYPES.register(modBus);
        FLUIDS.register(modBus);
    }
}
