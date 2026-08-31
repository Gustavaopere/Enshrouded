package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Objects;

/** Canonical particle type registry for Stage 07 presentation effects. */
public final class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES =
            DeferredRegister.create(BuiltInRegistries.PARTICLE_TYPE, Enshrouded.MOD_ID);

    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHROUD_CORE = register("shroud_core");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> SHROUD_GROWTH = register("shroud_growth");
    public static final DeferredHolder<ParticleType<?>, SimpleParticleType> RED_SLUDGE = register("red_sludge");

    private ModParticles() {}

    public static void register(IEventBus modBus) {
        PARTICLE_TYPES.register(Objects.requireNonNull(modBus, "modBus"));
    }

    private static DeferredHolder<ParticleType<?>, SimpleParticleType> register(String name) {
        return PARTICLE_TYPES.register(name, () -> new SimpleParticleType(false));
    }
}
