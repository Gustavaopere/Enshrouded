package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.story.boss.NativeShroudLichEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(Registries.ENTITY_TYPE, Enshrouded.MOD_ID);

    private static final ResourceLocation SHROUD_LICH_ID =
            ResourceLocation.fromNamespaceAndPath(Enshrouded.MOD_ID, "shroud_lich");

    public static final DeferredHolder<EntityType<?>, EntityType<NativeShroudLichEntity>> SHROUD_LICH =
            ENTITY_TYPES.register(
                    "shroud_lich",
                    () -> EntityType.Builder.of(NativeShroudLichEntity::new, MobCategory.MONSTER)
                            .sized(0.8F, 2.4F)
                            .clientTrackingRange(10)
                            .updateInterval(2)
                            .build(SHROUD_LICH_ID.toString())
            );

    private ModEntities() {
    }

    public static void register(IEventBus modBus) {
        ENTITY_TYPES.register(modBus);
        modBus.addListener(ModEntities::createDefaultAttributes);
    }

    private static void createDefaultAttributes(EntityAttributeCreationEvent event) {
        event.put(SHROUD_LICH.get(), NativeShroudLichEntity.createAttributes().build());
    }
}
