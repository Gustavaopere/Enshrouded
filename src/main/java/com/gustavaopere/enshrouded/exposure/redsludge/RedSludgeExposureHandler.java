package com.gustavaopere.enshrouded.exposure.redsludge;

import net.minecraft.world.entity.Entity;

import java.util.Objects;

/** Server-authoritative contact seam for the Red Sludge hazard. */
public final class RedSludgeExposureHandler {
    private RedSludgeExposureHandler() {
    }

    public static void onContact(Entity entity) {
        Objects.requireNonNull(entity, "entity");
        // Behavioral ownership is added only after the dedicated RED contract is observed.
    }
}
