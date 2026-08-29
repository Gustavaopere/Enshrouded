package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.level.Level;

/** Registry key and runtime source factory for authoritative Madness death. */
public final class ModDamageTypes {
    public static final ResourceKey<DamageType> MADNESS = ResourceKey.create(
            Registries.DAMAGE_TYPE,
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "madness")
    );

    private ModDamageTypes() {
    }

    public static DamageSource madness(Level level) {
        return level.damageSources().source(MADNESS);
    }
}
