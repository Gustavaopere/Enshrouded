package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.performance.PerformanceCounters;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import com.gustavaopere.enshrouded.registry.ModParticles;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.PlayerCloudParticle;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

import java.util.Objects;

/**
 * Bounded client-only source sampler for Core, corruption-growth and Red Sludge particles.
 * It never scans entities, forces chunks or broadcasts server packets.
 */
public final class ShroudParticleController {
    static final int PULSE_INTERVAL_TICKS = 4;
    static final int MAX_SAMPLES_PER_PULSE = 192;

    private static long clientTick;
    private static int sampleCursor;

    private ShroudParticleController() {}

    public static void register(IEventBus gameBus) {
        Objects.requireNonNull(gameBus, "gameBus");
        gameBus.addListener(ShroudParticleController::onClientTick);
    }

    public static void registerParticleProviders(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(ModParticles.SHROUD_CORE.get(), PlayerCloudParticle.Provider::new);
        event.registerSpriteSet(ModParticles.SHROUD_GROWTH.get(), PlayerCloudParticle.Provider::new);
        event.registerSpriteSet(ModParticles.RED_SLUDGE.get(), PlayerCloudParticle.Provider::new);
    }

    public static void reset() {
        clientTick = 0L;
        sampleCursor = 0;
    }

    private static void onClientTick(ClientTickEvent.Post event) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.level == null || minecraft.player == null) return;
        clientTick++;
        if (clientTick % PULSE_INTERVAL_TICKS != 0L) return;

        EnshroudedClientConfig.ParticleSettings particles = EnshroudedClientConfig.particleSettings();
        if (!particles.enabled() || particles.maxCount() <= 0) return;

        int radius = (int) Math.ceil(particles.maxDistance());
        int side = radius * 2 + 1;
        int totalPositions = side * side * side;
        int samples = Math.min(MAX_SAMPLES_PER_PULSE, totalPositions);
        int start = Math.floorMod(sampleCursor, totalPositions);
        int remainingBudget = particles.maxCount();
        int visitedSamples = 0;
        int emittedParticles = 0;
        BlockPos center = minecraft.player.blockPosition();

        for (int sample = 0; sample < samples && remainingBudget > 0; sample++) {
            visitedSamples++;
            int flat = (start + sample) % totalPositions;
            int dx = flat % side - radius;
            int yz = flat / side;
            int dz = yz % side - radius;
            int dy = yz / side - radius;
            BlockPos pos = center.offset(dx, dy, dz);

            if (!minecraft.level.hasChunkAt(pos)) continue;
            double distanceSquared = minecraft.player.distanceToSqr(
                    pos.getX() + 0.5D,
                    pos.getY() + 0.5D,
                    pos.getZ() + 0.5D);
            ShroudSourceParticlePlanner.SourceKind kind = sourceKind(minecraft.level.getBlockState(pos));
            if (kind == null) continue;

            int count = Math.min(remainingBudget, ShroudSourceParticlePlanner.emissionCount(kind, particles, distanceSquared));
            if (count <= 0) continue;
            ParticleOptions type = particleFor(kind);
            emitAtSource(minecraft, type, pos, count);
            emittedParticles += count;
            remainingBudget -= count;
        }

        PerformanceCounters.global().recordClientEffects(visitedSamples, emittedParticles);
        sampleCursor = (start + samples) % totalPositions;
    }

    private static ShroudSourceParticlePlanner.SourceKind sourceKind(BlockState state) {
        if (state.getBlock() == ModBlocks.SHROUD_CORE.get()) {
            return ShroudSourceParticlePlanner.SourceKind.CORE;
        }
        if (state.getBlock() == ModBlocks.SHROUD_GROWTH.get()
                || state.getBlock() == ModBlocks.SHROUD_VEIN.get()
                || state.getBlock() == ModBlocks.WITHERED_GROWTH.get()) {
            return ShroudSourceParticlePlanner.SourceKind.GROWTH;
        }
        if (state.getBlock() == ModBlocks.RED_SLUDGE.get()) {
            return ShroudSourceParticlePlanner.SourceKind.RED_SLUDGE;
        }
        return null;
    }

    private static ParticleOptions particleFor(ShroudSourceParticlePlanner.SourceKind kind) {
        return switch (kind) {
            case CORE -> ModParticles.SHROUD_CORE.get();
            case GROWTH -> ModParticles.SHROUD_GROWTH.get();
            case RED_SLUDGE -> ModParticles.RED_SLUDGE.get();
        };
    }

    private static void emitAtSource(Minecraft minecraft, ParticleOptions type, BlockPos pos, int count) {
        for (int index = 0; index < count; index++) {
            double x = pos.getX() + 0.25D + minecraft.level.random.nextDouble() * 0.50D;
            double y = pos.getY() + 0.35D + minecraft.level.random.nextDouble() * 0.70D;
            double z = pos.getZ() + 0.25D + minecraft.level.random.nextDouble() * 0.50D;
            minecraft.level.addParticle(type, x, y, z, 0.0D, 0.01D, 0.0D);
        }
    }
}
