package com.gustavaopere.enshrouded.flame.altar;

import com.gustavaopere.enshrouded.Enshrouded;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.TickTask;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.LogicalSide;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.LogicalSidedProvider;
import net.neoforged.neoforge.event.level.ChunkEvent;

/**
 * Restores persisted Flame Altar formation only after relevant chunks have completed their load
 * boundary. The scan is limited to at most nine already-loaded chunks and never requests a chunk.
 */
@EventBusSubscriber(modid = Enshrouded.MOD_ID)
public final class FlameAltarChunkRecoveryEvents {
    private FlameAltarChunkRecoveryEvents() {
    }

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        ChunkPos loadedPos = event.getChunk().getPos();
        var server = serverLevel.getServer();
        LogicalSidedProvider.WORKQUEUE.get(LogicalSide.SERVER).tell(new TickTask(
                server.getTickCount() + 1,
                () -> recoverAltarsIntersecting(serverLevel, loadedPos)
        ));
    }

    private static void recoverAltarsIntersecting(ServerLevel level, ChunkPos loadedPos) {
        for (int chunkDx = -1; chunkDx <= 1; chunkDx++) {
            for (int chunkDz = -1; chunkDz <= 1; chunkDz++) {
                var chunk = level.getChunk(
                        loadedPos.x + chunkDx,
                        loadedPos.z + chunkDz,
                        ChunkStatus.FULL,
                        false
                );
                if (!(chunk instanceof LevelChunk levelChunk)) {
                    continue;
                }

                for (BlockEntity blockEntity : List.copyOf(levelChunk.getBlockEntities().values())) {
                    if (blockEntity instanceof FlameAltarBlockEntity altar
                            && altar.hasPendingFormationRecovery()
                            && footprintIntersectsLoadedChunk(altar.getBlockPos(), loadedPos)) {
                        altar.retryPendingFormationRecovery(level);
                    }
                }
            }
        }
    }

    private static boolean footprintIntersectsLoadedChunk(BlockPos center, ChunkPos loadedPos) {
        int minX = loadedPos.x << 4;
        int minZ = loadedPos.z << 4;
        int maxX = minX + 15;
        int maxZ = minZ + 15;
        return center.getX() + 1 >= minX
                && center.getX() - 1 <= maxX
                && center.getZ() + 1 >= minZ
                && center.getZ() - 1 <= maxZ;
    }
}
