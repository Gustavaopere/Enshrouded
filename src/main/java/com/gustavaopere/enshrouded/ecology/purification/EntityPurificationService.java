package com.gustavaopere.enshrouded.ecology.purification;

import com.gustavaopere.enshrouded.ecology.combat.CorruptedCombatRuntime;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import net.minecraft.world.entity.LivingEntity;

import java.util.Objects;

/** Server-side cleanup surface for Enshrouded-owned corrupted-ecology state. */
public final class EntityPurificationService {
    private EntityPurificationService() {
    }

    public static void purify(LivingEntity entity) {
        Objects.requireNonNull(entity, "entity");
        CorruptedCombatRuntime.clearIfActive(entity);
        if (entity.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION) != null) {
            entity.removeData(EntityCorruptionAttachment.ENTITY_CORRUPTION);
        }
    }
}
