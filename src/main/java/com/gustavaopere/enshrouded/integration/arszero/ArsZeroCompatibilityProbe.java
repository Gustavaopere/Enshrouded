package com.gustavaopere.enshrouded.integration.arszero;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.fml.ModList;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

/** One-shot compatibility inventory for the exact Ars Zero Lich registry contract. */
public final class ArsZeroCompatibilityProbe {
    public static final String MOD_ID = "ars_zero";
    public static final ResourceLocation LICH_ID = ResourceLocation.fromNamespaceAndPath(MOD_ID, "lich");

    private final Status status;
    private final Optional<EntityType<?>> lichType;

    private ArsZeroCompatibilityProbe(Status status, Optional<EntityType<?>> lichType) {
        this.status = Objects.requireNonNull(status, "status");
        this.lichType = Objects.requireNonNull(lichType, "lichType");
    }

    public static ArsZeroCompatibilityProbe detect() {
        return inspect(
                ModList.get().isLoaded(MOD_ID),
                id -> BuiltInRegistries.ENTITY_TYPE.containsKey(id)
                        ? Optional.of(BuiltInRegistries.ENTITY_TYPE.get(id))
                        : Optional.empty()
        );
    }

    static ArsZeroCompatibilityProbe inspect(
            boolean modLoaded,
            Function<ResourceLocation, Optional<EntityType<?>>> resolver
    ) {
        Objects.requireNonNull(resolver, "resolver");
        if (!modLoaded) {
            return new ArsZeroCompatibilityProbe(Status.MOD_ABSENT, Optional.empty());
        }

        Optional<EntityType<?>> resolved = Objects.requireNonNull(
                resolver.apply(LICH_ID),
                "resolver result"
        );
        if (resolved.isEmpty() || resolved.orElseThrow().getCategory() != MobCategory.MONSTER) {
            return new ArsZeroCompatibilityProbe(Status.INCOMPATIBLE, Optional.empty());
        }
        return new ArsZeroCompatibilityProbe(Status.READY, resolved);
    }

    public Status status() {
        return status;
    }

    public boolean available() {
        return status == Status.READY && lichType.isPresent();
    }

    public Optional<EntityType<?>> lichType() {
        return lichType;
    }

    public enum Status {
        MOD_ABSENT,
        INCOMPATIBLE,
        READY
    }
}
