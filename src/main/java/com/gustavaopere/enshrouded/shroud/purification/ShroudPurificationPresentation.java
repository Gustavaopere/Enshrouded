package com.gustavaopere.enshrouded.shroud.purification;

import com.gustavaopere.enshrouded.registry.ModParticles;
import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;

import java.util.Objects;

/**
 * Event-bounded Stage 10 presentation downstream of the canonical DESTROYED -> PURIFIED transition.
 * This class never advances regression, mutates Shroud state or supplies Sanctuary authority.
 */
public final class ShroudPurificationPresentation {
    static final int MAX_RELEASE_PARTICLES = 24;
    private static final double HORIZONTAL_SPREAD = 1.75D;
    private static final double VERTICAL_SPREAD = 1.25D;
    private static final double RELEASE_SPEED = 0.045D;

    private ShroudPurificationPresentation() {}

    public static void onPurified(ServerLevel level, ShroudCoreState core) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(core, "core");
        BlockPos center = core.center();
        if (!level.hasChunkAt(center)) {
            return;
        }
        level.sendParticles(
                ModParticles.SANCTUARY_MOTE.get(),
                center.getX() + 0.5D,
                center.getY() + 1.0D,
                center.getZ() + 0.5D,
                MAX_RELEASE_PARTICLES,
                HORIZONTAL_SPREAD,
                VERTICAL_SPREAD,
                HORIZONTAL_SPREAD,
                RELEASE_SPEED
        );
    }
}
