package com.gustavaopere.enshrouded.shroud.core;

import com.gustavaopere.enshrouded.shroud.state.ShroudCoreState;
import com.gustavaopere.enshrouded.shroud.state.ShroudSavedData;
import com.gustavaopere.enshrouded.shroud.state.ShroudWorldState;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Thread-safe handoff between physical core loading/worldgen and canonical dimension-local
 * Shroud state. Enqueue never touches SavedData; mutation happens only while draining from the
 * server tick.
 */
public final class ShroudCoreRegistrationQueue {
    private static final int MAX_PENDING_PER_DIMENSION = 4096;
    private static final AtomicBoolean RUNTIME_REGISTERED = new AtomicBoolean();
    private static final ConcurrentMap<ResourceKey<Level>, ConcurrentMap<BlockPos, RegistrationRequest>> PENDING =
            new ConcurrentHashMap<>();

    private ShroudCoreRegistrationQueue() {
    }

    public static void registerRuntime() {
        if (RUNTIME_REGISTERED.compareAndSet(false, true)) {
            NeoForge.EVENT_BUS.addListener(ShroudCoreRegistrationQueue::onServerTickPost);
            NeoForge.EVENT_BUS.addListener(ShroudCoreRegistrationQueue::onServerStopping);
        }
    }

    public static void enqueue(
            ServerLevel level,
            BlockPos position,
            UUID coreId,
            UUID regionId,
            int tier,
            int maxInfluenceRadius,
            long expansionSeed,
            boolean activateAfterRegistration) {
        Objects.requireNonNull(level, "level");
        Objects.requireNonNull(position, "position");
        Objects.requireNonNull(coreId, "coreId");
        Objects.requireNonNull(regionId, "regionId");

        ConcurrentMap<BlockPos, RegistrationRequest> dimensionQueue =
                PENDING.computeIfAbsent(level.dimension(), ignored -> new ConcurrentHashMap<>());
        BlockPos immutablePosition = position.immutable();
        RegistrationRequest incoming = new RegistrationRequest(
                immutablePosition,
                coreId,
                regionId,
                tier,
                maxInfluenceRadius,
                expansionSeed,
                activateAfterRegistration
        );

        dimensionQueue.compute(immutablePosition, (ignored, existing) -> {
            if (existing == null) {
                if (dimensionQueue.size() >= MAX_PENDING_PER_DIMENSION) {
                    throw new IllegalStateException("Shroud core registration queue capacity exceeded in " + level.dimension().location());
                }
                return incoming;
            }
            if (!existing.sameIdentity(incoming)) {
                return incoming;
            }
            return existing.withActivation(existing.activateAfterRegistration() || incoming.activateAfterRegistration());
        });
    }

    public static int pendingCount(ResourceKey<Level> dimension) {
        ConcurrentMap<BlockPos, RegistrationRequest> queue = PENDING.get(Objects.requireNonNull(dimension, "dimension"));
        return queue == null ? 0 : queue.size();
    }

    static int drain(ServerLevel level) {
        Objects.requireNonNull(level, "level");
        ConcurrentMap<BlockPos, RegistrationRequest> queue = PENDING.get(level.dimension());
        if (queue == null || queue.isEmpty()) {
            return 0;
        }

        int completed = 0;
        for (Map.Entry<BlockPos, RegistrationRequest> entry : new ArrayList<>(queue.entrySet())) {
            BlockPos position = entry.getKey();
            RegistrationRequest request = entry.getValue();
            if (!level.hasChunkAt(position)) {
                continue;
            }

            if (!(level.getBlockEntity(position) instanceof ShroudCoreBlockEntity coreBlockEntity)
                    || !coreBlockEntity.matchesIdentity(request.coreId(), request.regionId())) {
                if (queue.remove(position, request)) {
                    completed++;
                }
                continue;
            }

            ShroudSavedData savedData = ShroudSavedData.get(level);
            CoreMutationResult registration = ShroudCoreService.registerDormant(
                    savedData.state(),
                    request.coreId(),
                    request.regionId(),
                    request.position(),
                    request.tier(),
                    request.maxInfluenceRadius(),
                    request.expansionSeed()
            );
            ShroudWorldState nextState = registration.state();

            if (request.activateAfterRegistration()) {
                ShroudCoreState registeredCore = nextState.cores().get(request.coreId());
                if (registeredCore != null && registeredCore.lifecycleState() == CoreLifecycleState.DORMANT) {
                    nextState = ShroudCoreService.activate(nextState, request.coreId()).state();
                }
            }

            savedData.replace(nextState);
            if (queue.remove(position, request)) {
                completed++;
            }
        }

        if (queue.isEmpty()) {
            PENDING.remove(level.dimension(), queue);
        }
        return completed;
    }

    static void clear() {
        PENDING.clear();
    }

    private static void onServerTickPost(ServerTickEvent.Post event) {
        for (ServerLevel level : event.getServer().getAllLevels()) {
            drain(level);
        }
    }

    private static void onServerStopping(ServerStoppingEvent event) {
        clear();
    }

    private record RegistrationRequest(
            BlockPos position,
            UUID coreId,
            UUID regionId,
            int tier,
            int maxInfluenceRadius,
            long expansionSeed,
            boolean activateAfterRegistration) {
        private RegistrationRequest {
            Objects.requireNonNull(position, "position");
            Objects.requireNonNull(coreId, "coreId");
            Objects.requireNonNull(regionId, "regionId");
            if (tier <= 0) {
                throw new IllegalArgumentException("tier must be > 0");
            }
            if (maxInfluenceRadius <= 0) {
                throw new IllegalArgumentException("maxInfluenceRadius must be > 0");
            }
        }

        private boolean sameIdentity(RegistrationRequest other) {
            return coreId.equals(other.coreId)
                    && regionId.equals(other.regionId)
                    && tier == other.tier
                    && maxInfluenceRadius == other.maxInfluenceRadius
                    && expansionSeed == other.expansionSeed;
        }

        private RegistrationRequest withActivation(boolean activate) {
            return new RegistrationRequest(position, coreId, regionId, tier, maxInfluenceRadius, expansionSeed, activate);
        }
    }
}
