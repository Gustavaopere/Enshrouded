package com.gustavaopere.enshrouded.ecology.state;

import net.minecraft.world.entity.LivingEntity;

/**
 * Test-source-only access bridge for the package-private corruption tick seam.
 * Production visibility stays unchanged.
 */
public final class EntityCorruptionGameTestAccess {
    private EntityCorruptionGameTestAccess() {
    }

    public static void advanceNow(LivingEntity entity) {
        EntityCorruptionRuntime.advanceNow(entity);
    }
}
