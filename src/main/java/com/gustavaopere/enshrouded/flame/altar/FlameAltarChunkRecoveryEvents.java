package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.Enshrouded;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

/**
 * Wakes only Flame Altars that explicitly require formation recovery.
 *
 * <p>The recovery queues are ephemeral, per-level and never gameplay authority. Initial persisted
 * recovery is deferred to a real post-server-tick boundary so BlockEntity load ordering cannot make
 * validation observe a half-finalized chunk. If validation reports an unavailable footprint chunk,
 * the altar is moved to the exact chunk index until that chunk loads. No path requests or
 * force-loads a chunk, and unrelated chunk loads perform no world scan.</p>
 */
@EventBusSubscriber(modid = Enshrouded.MOD_ID)
public final class FlameAltarChunkRecoveryEvents {
    private static final Map<ServerLevel, Set<BlockPos>> INITIAL_RECOVERY_BY_LEVEL = new WeakHashMap<>();
    private static final Map<ServerLevel, FlameAltarRecoveryIndex> RECOVERY_BY_LEVEL = new WeakHashMap<>();

    private FlameAltarChunkRecoveryEvents() {
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        if (INITIAL_RECOVERY_BY_LEVEL.isEmpty()) {
            return;
        }

        for (ServerLevel level : List.copyOf(INITIAL_RECOVERY_BY_LEVEL.keySet())) {
            if (level.getServer() != event.getServer()) {
                continue;
            }

            Set<BlockPos> waiting = INITIAL_RECOVERY_BY_LEVEL.remove(level);
            if (waiting != null && !waiting.isEmpty()) {
                recoverWaiting(level, waiting);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        Set<BlockPos> waiting = consumeWaiting(serverLevel, event.getChunk().getPos());
        if (waiting.isEmpty()) {
            return;
        }

        serverLevel.getServer().executeIfPossible(() -> recoverWaiting(serverLevel, waiting));
    }

    @SubscribeEvent
    public static void onLevelUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel) {
            INITIAL_RECOVERY_BY_LEVEL.remove(serverLevel);
            RECOVERY_BY_LEVEL.remove(serverLevel);
        }
    }

    static void deferInitialRecovery(ServerLevel level, BlockPos altarPos) {
        INITIAL_RECOVERY_BY_LEVEL
                .computeIfAbsent(level, ignored -> new LinkedHashSet<>())
                .add(altarPos.immutable());
    }

    static void waitForChunk(ServerLevel level, BlockPos altarPos, ChunkPos missingChunk) {
        clearInitialWaiting(level, altarPos);
        RECOVERY_BY_LEVEL
                .computeIfAbsent(level, ignored -> new FlameAltarRecoveryIndex())
                .waitFor(altarPos, missingChunk);
    }

    static void clearWaiting(ServerLevel level, BlockPos altarPos) {
        clearInitialWaiting(level, altarPos);

        FlameAltarRecoveryIndex index = RECOVERY_BY_LEVEL.get(level);
        if (index == null) {
            return;
        }
        index.remove(altarPos);
        if (index.isEmpty()) {
            RECOVERY_BY_LEVEL.remove(level);
        }
    }

    /** Package-private deterministic seam used by GameTests for the indexed load boundary. */
    static void recoverWaitingForLoadedChunk(ServerLevel level, ChunkPos loadedChunk) {
        recoverWaiting(level, consumeWaiting(level, loadedChunk));
    }

    private static void clearInitialWaiting(ServerLevel level, BlockPos altarPos) {
        Set<BlockPos> waiting = INITIAL_RECOVERY_BY_LEVEL.get(level);
        if (waiting == null) {
            return;
        }
        waiting.remove(altarPos);
        if (waiting.isEmpty()) {
            INITIAL_RECOVERY_BY_LEVEL.remove(level);
        }
    }

    private static Set<BlockPos> consumeWaiting(ServerLevel level, ChunkPos loadedChunk) {
        FlameAltarRecoveryIndex index = RECOVERY_BY_LEVEL.get(level);
        if (index == null) {
            return Set.of();
        }

        Set<BlockPos> waiting = index.consume(loadedChunk);
        if (index.isEmpty()) {
            RECOVERY_BY_LEVEL.remove(level);
        }
        return waiting;
    }

    private static void recoverWaiting(ServerLevel level, Set<BlockPos> waiting) {
        for (BlockPos altarPos : waiting) {
            if (!level.getChunkSource().hasChunk(altarPos.getX() >> 4, altarPos.getZ() >> 4)) {
                continue;
            }
            if (level.getBlockEntity(altarPos) instanceof FlameAltarBlockEntity altar
                    && altar.hasPendingFormationRecovery()) {
                altar.retryPendingFormationRecovery(level);
            }
        }
    }
}
