package com.gustavaopere.enshrouded.registry;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.story.boss.NativeShroudLichEntity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public final class ModEntities {
    public static final DeferredRegister.Entities ENTITY_TYPES = DeferredRegister.createEntities(Enshrouded.MOD_ID);

    public static final Supplier<EntityType<NativeShroudLichEntity>> SHROUD_LICH = ENTITY_TYPES.registerEntityType(
            "shroud_lich",
            NativeShroudLichEntity::new,
            MobCategory.MONSTER,
            builder -> builder
                    .sized(0.8F, 2.4F)
                    .clientTrackingRange(10)
                    .updateInterval(2)
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
