package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.registry.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
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
 * <p>Disk-loaded block entities are lazy in Minecraft 1.21.1: a chunk may contain persisted block
 * entity NBT without constructing that BlockEntity until somebody asks for it. On chunk load we
 * therefore inspect only that chunk's public block-entity position metadata, and materialize only a
 * Flame Altar whose pending NBT carries a valid persisted FORMED recovery intent. The normal
 * NeoForge BlockEntity lifecycle then invokes {@code onLoad()} with fully deserialized NBT.</p>
 *
 * <p>After materialization the recovery queues are ephemeral, per-level and never gameplay
 * authority. Initial validation is deferred to a post-server-tick boundary. If validation reports
 * an unavailable footprint chunk, the altar is moved to the exact chunk index until that chunk
 * loads. No path requests or force-loads a chunk, and unrelated chunk loads perform no block/world
 * scan.</p>
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
                System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE server_tick_consume positions=" + waiting);
                recoverWaiting(level, waiting);
            }
        }
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)
                || !(event.getChunk() instanceof LevelChunk levelChunk)) {
            return;
        }

        materializePersistedFormationControllers(levelChunk);

        Set<BlockPos> waiting = consumeWaiting(serverLevel, levelChunk.getPos());
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
        System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE onload_deferred pos=" + altarPos);
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

    private static void materializePersistedFormationControllers(LevelChunk chunk) {
        if (chunk.getBlockEntitiesPos().isEmpty()) {
            return;
        }

        // getBlockEntitiesPos() returns a copy containing both live and still-packed positions, so
        // promoting a matching pending entry below cannot invalidate this iteration.
        for (BlockPos pos : chunk.getBlockEntitiesPos()) {
            if (!chunk.getBlockState(pos).is(ModBlocks.FLAME_ALTAR.get())) {
                continue;
            }

            CompoundTag pendingTag = chunk.getBlockEntityNbt(pos);
            if (pendingTag == null || !FlameAltarBlockEntity.hasPersistedFormationIntent(pendingTag)) {
                continue;
            }

            System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE chunk_pending_found pos=" + pos);
            // This is the only deliberate lazy-BE promotion. It loads no chunk and creates no new
            // authority; NeoForge subsequently calls FlameAltarBlockEntity#onLoad normally.
            var promoted = chunk.getBlockEntity(pos);
            System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE chunk_promoted pos=" + pos
                    + " type=" + (promoted == null ? "null" : promoted.getClass().getName())
                    + " pending=" + (promoted instanceof FlameAltarBlockEntity altar
                    && altar.hasPendingFormationRecovery()));
        }
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
                System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE retry_skipped_unloaded pos=" + altarPos);
                continue;
            }
            if (level.getBlockEntity(altarPos) instanceof FlameAltarBlockEntity altar
                    && altar.hasPendingFormationRecovery()) {
                System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE retry_invoked pos=" + altarPos);
                altar.retryPendingFormationRecovery(level);
                System.out.println("ENSHROUDED_FLAME_ALTAR_RECOVERY_STAGE retry_finished pos=" + altarPos
                        + " formed=" + altar.isFormed()
                        + " pending=" + altar.hasPendingFormationRecovery()
                        + " phase=" + altar.formationPhase());
            }
        }
    }
}