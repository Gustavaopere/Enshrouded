package com.gustavaopere.enshrouded.client.ambient;

import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.Objects;

/**
 * Client-only bounded ambient presentation derived exclusively from synchronized Exposure state.
 */
public final class ShroudAmbientController {
    private static final BudgetState BUDGET = new BudgetState();
    private static long clientTick;

    private ShroudAmbientController() {
    }

    public static void register(IEventBus gameBus) {
        Objects.requireNonNull(gameBus, "gameBus");
        gameBus.addListener(ShroudAmbientController::onClientTick);
    }

    public static void reset() {
        BUDGET.reset();
        clientTick = 0L;
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }

        clientTick++;
        ExposureSnapshot snapshot = ClientExposureState.INSTANCE.snapshot();
        ShroudSoundProfile soundProfile = ShroudSoundProfile.forState(
                snapshot.severity(), snapshot.sanctuarySuppressed());
        ShroudParticleProfile particleProfile = ShroudParticleProfile.forState(
                snapshot.severity(), snapshot.sanctuarySuppressed());
        EnshroudedClientConfig.AudioSettings audio = EnshroudedClientConfig.audioSettings();
        EnshroudedClientConfig.ParticleSettings particles = EnshroudedClientConfig.particleSettings();

        EmissionPlan plan = plan(soundProfile, particleProfile, audio, particles, clientTick, BUDGET);
        if (plan.playSound()) {
            emitSound(minecraft, soundProfile, (float) (soundProfile.baseVolume() * audio.volume()));
        }
        if (plan.particleCount() > 0) {
            emitParticles(minecraft, particleProfile, plan.particleCount());
        }
    }

    static EmissionPlan plan(
            ShroudSoundProfile soundProfile,
            ShroudParticleProfile particleProfile,
            EnshroudedClientConfig.AudioSettings audio,
            EnshroudedClientConfig.ParticleSettings particles,
            long tick,
            BudgetState state) {
        Objects.requireNonNull(soundProfile, "soundProfile");
        Objects.requireNonNull(particleProfile, "particleProfile");
        Objects.requireNonNull(audio, "audio");
        Objects.requireNonNull(particles, "particles");
        Objects.requireNonNull(state, "state");

        boolean playSound = false;
        if (soundProfile != ShroudSoundProfile.NONE
                && audio.enabled()
                && audio.volume() > 0.0D
                && tick >= state.nextSoundTick) {
            playSound = true;
            state.nextSoundTick = saturatingAdd(tick, soundProfile.cooldownTicks());
        }

        int particleCount = 0;
        if (particleProfile != ShroudParticleProfile.NONE
                && particles.enabled()
                && particles.maxCount() > 0
                && tick >= state.nextParticleTick) {
            particleCount = Math.min(particleProfile.baseCount(), particles.maxCount());
            state.nextParticleTick = saturatingAdd(tick, particleProfile.intervalTicks());
        }

        return new EmissionPlan(playSound, particleCount);
    }

    private static long saturatingAdd(long tick, int delta) {
        if (delta <= 0 || tick > Long.MAX_VALUE - delta) {
            return Long.MAX_VALUE;
        }
        return tick + delta;
    }

    private static void emitSound(Minecraft minecraft, ShroudSoundProfile profile, float volume) {
        SoundEvent sound = profile == ShroudSoundProfile.DEADLY
                ? SoundEvents.ENDERMAN_STARE.value()
                : SoundEvents.AMBIENT_CAVE.value();
        minecraft.level.playLocalSound(
                minecraft.player.getX(),
                minecraft.player.getY(),
                minecraft.player.getZ(),
                sound,
                SoundSource.AMBIENT,
                volume,
                1.0F,
                false
        );
    }

    private static void emitParticles(Minecraft minecraft, ShroudParticleProfile profile, int count) {
        ParticleOptions particle = profile == ShroudParticleProfile.DEADLY
                ? ParticleTypes.REVERSE_PORTAL
                : ParticleTypes.ASH;
        for (int index = 0; index < count; index++) {
            double x = minecraft.player.getX() + (minecraft.level.random.nextDouble() - 0.5D) * 4.0D;
            double y = minecraft.player.getY() + 0.25D + minecraft.level.random.nextDouble() * 2.0D;
            double z = minecraft.player.getZ() + (minecraft.level.random.nextDouble() - 0.5D) * 4.0D;
            minecraft.level.addParticle(particle, x, y, z, 0.0D, 0.005D, 0.0D);
        }
    }

    public static final class BudgetState {
        private long nextSoundTick = Long.MIN_VALUE;
        private long nextParticleTick = Long.MIN_VALUE;

        public void reset() {
            nextSoundTick = Long.MIN_VALUE;
            nextParticleTick = Long.MIN_VALUE;
        }
    }

    public record EmissionPlan(boolean playSound, int particleCount) {
        public EmissionPlan {
            if (particleCount < 0) {
                throw new IllegalArgumentException("particleCount must be >= 0");
            }
        }
    }
}
