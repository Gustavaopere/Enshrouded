package com.gustavaopere.enshrouded.ecology.purification;

import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

/** Server-side cleanup surface for Enshrouded-owned corrupted-ecology state. */
public final class EntityPurificationService {
    private EntityPurificationService() {
    }

    public static void purify(LivingEntity entity) {
        Objects.requireNonNull(entity, "entity");
    }
}
