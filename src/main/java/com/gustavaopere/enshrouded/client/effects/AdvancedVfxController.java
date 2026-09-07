package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.client.state.ClientAdvancedVfxState;
import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.network.AdvancedVfxPayload;
import com.gustavaopere.enshrouded.performance.PerformanceCounters;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import com.gustavaopere.enshrouded.registry.ModParticles;
import com.gustavaopere.enshrouded.registry.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

/**
 * Physical-client-only Stage 10.08 sequence renderer.
 *
 * <p>Snapshot transitions are derived only when a new synchronized exposure sequence arrives.
 * The first snapshot after connection establishes a baseline and never replays a transition.
 * Discrete world events arrive through the bounded clientbound inbox. This class only renders.</p>
 */
public final class AdvancedVfxController {
    private static final double GOLDEN_ANGLE = Math.PI * (3.0D - Math.sqrt(5.0D));
    private static final EnumMap<AdvancedVfxCue, Long> NEXT_ALLOWED_TICK = new EnumMap<>(AdvancedVfxCue.class);
    private static final List<ActiveSequence> ACTIVE = new ArrayList<>();

    private static long clientTick;
    private static long observedExposureSequence = Long.MIN_VALUE;
    private static AdvancedVfxTransitionPlanner.Frame previousFrame;

    private AdvancedVfxController() {}

    public static void register(IEventBus gameBus) {
        Objects.requireNonNull(gameBus, "gameBus");
        gameBus.addListener(AdvancedVfxController::onClientTick);
    }

    public static void reset() {
        clientTick = 0L;
        observedExposureSequence = Long.MIN_VALUE;
        previousFrame = null;
        NEXT_ALLOWED_TICK.clear();
        ACTIVE.clear();
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        clientTick++;
        consumeExposureTransitions(minecraft);
        consumeServerCues(minecraft);
        renderActiveSequences(minecraft);
    }

    private static void consumeExposureTransitions(Minecraft minecraft) {
        long sequence = ClientExposureState.INSTANCE.lastSequence();
        if (sequence < 0L || sequence == observedExposureSequence) {
            return;
        }

        AdvancedVfxTransitionPlanner.Frame current =
                AdvancedVfxTransitionPlanner.fromSnapshot(ClientExposureState.INSTANCE.snapshot());
        if (previousFrame != null) {
            for (AdvancedVfxCue cue : AdvancedVfxTransitionPlanner.plan(previousFrame, current)) {
                schedule(cue, minecraft.player.blockPosition());
            }
        }
        previousFrame = current;
        observedExposureSequence = sequence;
    }

    private static void consumeServerCues(Minecraft minecraft) {
        for (AdvancedVfxPayload payload : ClientAdvancedVfxState.INSTANCE.drain()) {
            if (!minecraft.level.hasChunkAt(payload.origin())) {
                continue;
            }
            AdvancedVfxCue cue = payload.cue();
            double maximumDistanceSquared = cue.maxDistance() * cue.maxDistance();
            double distanceSquared = minecraft.player.distanceToSqr(
                    payload.origin().getX() + 0.5D,
                    payload.origin().getY() + 0.5D,
                    payload.origin().getZ() + 0.5D);
            if (distanceSquared <= maximumDistanceSquared) {
                schedule(cue, payload.origin());
            }
        }
    }

    private static void schedule(AdvancedVfxCue cue, BlockPos origin) {
        long nextAllowed = NEXT_ALLOWED_TICK.getOrDefault(cue, Long.MIN_VALUE);
        if (clientTick < nextAllowed) {
            return;
        }
        NEXT_ALLOWED_TICK.put(cue, saturatingAdd(clientTick, cue.cooldownTicks()));
        ACTIVE.add(new ActiveSequence(cue, origin.immutable(), clientTick));
        playCueAudio(Minecraft.getInstance(), cue);
    }

    private static void renderActiveSequences(Minecraft minecraft) {
        EnshroudedClientConfig.ParticleSettings particles = EnshroudedClientConfig.particleSettings();
        int emittedThisTick = 0;
        Iterator<ActiveSequence> iterator = ACTIVE.iterator();
        while (iterator.hasNext()) {
            ActiveSequence sequence = iterator.next();
            int elapsed = (int) Math.max(0L, Math.min(Integer.MAX_VALUE, clientTick - sequence.startTick + 1L));
            int target = AdvancedVfxSequenceBudget.targetEmitted(sequence.cue, particles, elapsed);
            int amount = Math.max(0, target - sequence.emitted);
            if (amount > 0) {
                if (!sequence.cue.serverAuthored() || minecraft.level.hasChunkAt(sequence.origin)) {
                    for (int index = 0; index < amount; index++) {
                        emitParticle(minecraft, sequence, sequence.emitted + index, elapsed);
                    }
                    emittedThisTick += amount;
                }
                sequence.emitted = target;
            }
            if (elapsed >= sequence.cue.lifetimeTicks()) {
                iterator.remove();
            }
        }
        if (emittedThisTick > 0) {
            PerformanceCounters.global().recordClientEffects(0, emittedThisTick);
        }
    }

    private static void emitParticle(Minecraft minecraft, ActiveSequence sequence, int ordinal, int elapsed) {
        AdvancedVfxCue cue = sequence.cue;
        boolean followPlayer = !cue.serverAuthored();
        double centerX = followPlayer ? minecraft.player.getX() : sequence.origin.getX() + 0.5D;
        double centerY = followPlayer ? minecraft.player.getY() : sequence.origin.getY() + 0.5D;
        double centerZ = followPlayer ? minecraft.player.getZ() : sequence.origin.getZ() + 0.5D;
        double progress = Math.min(1.0D, elapsed / (double) cue.lifetimeTicks());
        double angle = ordinal * GOLDEN_ANGLE + progress * Math.PI * 2.0D;

        ParticleOptions type = particleFor(cue);
        double radius;
        double yOffset;
        double radialVelocity;
        double verticalVelocity;

        switch (cue) {
            case CLEAR_TO_SHROUD -> {
                radius = 2.35D;
                yOffset = 0.35D + (ordinal % 5) * 0.34D;
                radialVelocity = -0.010D;
                verticalVelocity = 0.008D;
            }
            case SHROUD_TO_DEADLY -> {
                radius = 2.70D;
                yOffset = 0.45D + (ordinal % 4) * 0.42D;
                radialVelocity = -0.020D;
                verticalVelocity = 0.006D;
            }
            case SANCTUARY_ENTER -> {
                radius = 1.25D;
                yOffset = 0.10D + (ordinal % 3) * 0.18D;
                radialVelocity = 0.002D;
                verticalVelocity = 0.035D;
            }
            case CORE_DESTROYED -> {
                boolean inward = progress < 0.50D;
                double phase = inward ? progress * 2.0D : (progress - 0.50D) * 2.0D;
                radius = inward ? 1.80D - 1.20D * phase : 0.60D + 1.65D * phase;
                yOffset = 0.55D + (ordinal % 4) * 0.30D;
                radialVelocity = inward ? -0.035D : 0.040D;
                verticalVelocity = inward ? -0.005D : 0.020D;
            }
            case FLAME_RITUAL_SUCCESS -> {
                radius = 1.45D;
                yOffset = 0.85D + Math.sin(angle * 2.0D) * 0.22D;
                radialVelocity = 0.018D;
                verticalVelocity = 0.045D;
            }
            case LICH_MANIFESTATION -> {
                radius = 1.70D;
                yOffset = 1.05D + (ordinal % 3) * 0.38D;
                radialVelocity = 0.022D;
                verticalVelocity = 0.022D;
            }
            case MADNESS_ESCALATION -> {
                radius = 2.55D;
                yOffset = 0.45D + (ordinal % 4) * 0.42D;
                radialVelocity = -0.004D;
                verticalVelocity = 0.004D;
            }
            default -> throw new IllegalStateException("unhandled VFX cue: " + cue);
        }

        double cos = Math.cos(angle);
        double sin = Math.sin(angle);
        minecraft.level.addParticle(
                type,
                centerX + cos * radius,
                centerY + yOffset,
                centerZ + sin * radius,
                cos * radialVelocity,
                verticalVelocity,
                sin * radialVelocity);
    }

    private static ParticleOptions particleFor(AdvancedVfxCue cue) {
        return switch (cue) {
            case CLEAR_TO_SHROUD, MADNESS_ESCALATION -> ModParticles.SHROUD_GROWTH.get();
            case SHROUD_TO_DEADLY -> ModParticles.RED_SLUDGE.get();
            case SANCTUARY_ENTER, FLAME_RITUAL_SUCCESS -> ModParticles.SANCTUARY_MOTE.get();
            case CORE_DESTROYED -> ModParticles.SHROUD_CORE.get();
            case LICH_MANIFESTATION -> ModParticles.LICH_ARCANA.get();
        };
    }

    private static void playCueAudio(Minecraft minecraft, AdvancedVfxCue cue) {
        if (minecraft.level == null || minecraft.player == null) {
            return;
        }
        if (cue == AdvancedVfxCue.MADNESS_ESCALATION) {
            EnshroudedClientConfig.MadnessAudioSettings settings = EnshroudedClientConfig.madnessAudioSettings();
            if (settings.enabled() && settings.intensity() > 0.0D) {
                playLocal(minecraft, ModSounds.MADNESS_WHISPER.get(), SoundSource.AMBIENT,
                        (float) (0.65D * settings.intensity()), 0.92F);
            }
            return;
        }

        EnshroudedClientConfig.AudioSettings settings = EnshroudedClientConfig.audioSettings();
        if (!settings.enabled() || settings.volume() <= 0.0D) {
            return;
        }
        float volume = (float) settings.volume();
        switch (cue) {
            case CLEAR_TO_SHROUD -> playLocal(minecraft, ModSounds.SHROUD_AMBIENT.get(), SoundSource.AMBIENT,
                    volume * 0.55F, 1.10F);
            case SHROUD_TO_DEADLY -> playLocal(minecraft, ModSounds.DEADLY_SHROUD_AMBIENT.get(), SoundSource.AMBIENT,
                    volume * 0.70F, 0.88F);
            case CORE_DESTROYED -> playLocal(minecraft, ModSounds.DEADLY_SHROUD_AMBIENT.get(), SoundSource.AMBIENT,
                    volume * 0.75F, 0.70F);
            case LICH_MANIFESTATION -> playLocal(minecraft, SoundEvents.SOUL_ESCAPE.value(), SoundSource.HOSTILE,
                    volume * 0.85F, 0.72F);
            case SANCTUARY_ENTER, FLAME_RITUAL_SUCCESS, MADNESS_ESCALATION -> {
                // Existing altar/ward animation and ambient layers already provide non-authoritative feedback.
            }
        }
    }

    private static void playLocal(
            Minecraft minecraft,
            SoundEvent sound,
            SoundSource source,
            float volume,
            float pitch) {
        minecraft.level.playLocalSound(
                minecraft.player.getX(), minecraft.player.getY(), minecraft.player.getZ(),
                sound, source, volume, pitch, false);
    }

    private static long saturatingAdd(long value, int delta) {
        if (delta <= 0 || value > Long.MAX_VALUE - delta) {
            return Long.MAX_VALUE;
        }
        return value + delta;
    }

    private static final class ActiveSequence {
        private final AdvancedVfxCue cue;
        private final BlockPos origin;
        private final long startTick;
        private int emitted;

        private ActiveSequence(AdvancedVfxCue cue, BlockPos origin, long startTick) {
            this.cue = Objects.requireNonNull(cue, "cue");
            this.origin = Objects.requireNonNull(origin, "origin");
            this.startTick = startTick;
        }
    }
}
