package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.client.state.ClientShroudState;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Objects;

/**
 * Client-only Sanctuary language driven exclusively by the synchronized authoritative Shroud sample.
 * Latent Shroud remains visible; the controller only adds bounded clean upward motes while the
 * server-authored sample reports that a real Sanctuary ward is suppressing exposure.
 */
public final class SanctuaryPresentationController {
    static final int PULSE_INTERVAL_TICKS = 6;
    static final int MAX_MOTES_PER_PULSE = 6;
    private static final float LATENT_SHROUD_EPSILON = 0.001F;
    private static final double MAX_LOCAL_RADIUS = 3.0D;

    private static long clientTick;

    private SanctuaryPresentationController() {}

    public static void register(IEventBus gameBus) {
        Objects.requireNonNull(gameBus, "gameBus");
        gameBus.addListener(SanctuaryPresentationController::onClientTick);
    }

    public static void reset() {
        clientTick = 0L;
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            reset();
            return;
        }

        clientTick++;
        if (clientTick % PULSE_INTERVAL_TICKS != 0L) {
            return;
        }

        ShroudSample sample = ClientShroudState.INSTANCE.sample();
        if (!sample.sanctuarySuppressed() || sample.intensity() <= LATENT_SHROUD_EPSILON) {
            return;
        }

        EnshroudedClientConfig.ParticleSettings particles = EnshroudedClientConfig.particleSettings();
        if (!particles.enabled() || particles.maxCount() <= 0) {
            return;
        }

        int count = Math.min(MAX_MOTES_PER_PULSE, particles.maxCount());
        double radius = Math.min(MAX_LOCAL_RADIUS, particles.maxDistance());
        for (int index = 0; index < count; index++) {
            double angle = minecraft.level.random.nextDouble() * Math.PI * 2.0D;
            double radial = 0.45D + minecraft.level.random.nextDouble() * Math.max(0.0D, radius - 0.45D);
            double x = minecraft.player.getX() + Math.cos(angle) * radial;
            double y = minecraft.player.getY() + 0.15D + minecraft.level.random.nextDouble() * 1.85D;
            double z = minecraft.player.getZ() + Math.sin(angle) * radial;
            double upwardVelocity = 0.018D + minecraft.level.random.nextDouble() * 0.022D;
            minecraft.level.addParticle(ModParticles.SANCTUARY_MOTE.get(), x, y, z, 0.0D, upwardVelocity, 0.0D);
        }
    }
}
