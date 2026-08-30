package com.gustavaopere.enshrouded.client.ecology;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.ecology.state.EntityCorruptionAttachment;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Objects;

/**
 * Client-only visual projection of the synced canonical entity-corruption attachment.
 *
 * <p>No renderer replacement or parallel corruption state is created. Unsupported/custom entity
 * renderers therefore degrade to bounded vanilla particles instead of requiring per-mob assets.</p>
 */
@EventBusSubscriber(modid = Enshrouded.MOD_ID, value = Dist.CLIENT)
public final class CorruptionVisualRuntime {
    private CorruptionVisualRuntime() {
    }

    @SubscribeEvent
    public static void onEntityTickPost(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity living) || !living.level().isClientSide) {
            return;
        }

        EntityCorruptionAttachment corruption =
                living.getExistingDataOrNull(EntityCorruptionAttachment.ENTITY_CORRUPTION);
        if (corruption == null) {
            return;
        }

        CorruptionVisualState visual = CorruptionVisualState.fromIntensity(corruption.intensity());
        if (!shouldEmitAtTick(visual, living.tickCount)) {
            return;
        }

        var particle = switch (visual.cue()) {
            case TAINTED -> ParticleTypes.WITCH;
            case CORRUPTED -> ParticleTypes.REVERSE_PORTAL;
            case NONE -> null;
        };
        if (particle == null) {
            return;
        }

        for (int index = 0; index < visual.particleCount(); index++) {
            living.level().addParticle(
                    particle,
                    living.getRandomX(0.7D),
                    living.getRandomY(),
                    living.getRandomZ(0.7D),
                    0.0D,
                    0.01D,
                    0.0D
            );
        }
    }

    static boolean shouldEmitAtTick(CorruptionVisualState visual, int tickCount) {
        Objects.requireNonNull(visual, "visual");
        return visual.visible()
                && visual.particleCount() > 0
                && visual.particleIntervalTicks() > 0
                && Math.floorMod(tickCount, visual.particleIntervalTicks()) == 0;
    }
}
