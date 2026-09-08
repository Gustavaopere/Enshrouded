package com.gustavaopere.enshrouded.flame.altar;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameAltarRecoveryIndexTest {
    @Test
    void registrationIsIdempotentAndMovesWhenMissingChunkChanges() {
        FlameAltarRecoveryIndex index = new FlameAltarRecoveryIndex();
        BlockPos altar = new BlockPos(31, 80, 31);
        ChunkPos firstMissing = new ChunkPos(2, 1);
        ChunkPos secondMissing = new ChunkPos(1, 2);

        index.waitFor(altar, firstMissing);
        index.waitFor(altar, firstMissing);
        index.waitFor(altar, secondMissing);

        assertTrue(index.consume(firstMissing).isEmpty(),
                "Re-registering an altar for another missing chunk must remove the stale subscription");
        assertEquals(Set.of(altar), index.consume(secondMissing),
                "Only the current missing chunk may wake the pending altar");
        assertTrue(index.isEmpty(),
                "Consuming the current subscription must remove both forward and reverse index state");
    }

    @Test
    void explicitRemovalPreventsLaterChunkLoadWakeup() {
        FlameAltarRecoveryIndex index = new FlameAltarRecoveryIndex();
        BlockPos altar = new BlockPos(15, 80, 15);
        ChunkPos missing = new ChunkPos(1, 0);

        index.waitFor(altar, missing);
        index.remove(altar);

        assertTrue(index.consume(missing).isEmpty(),
                "Unformed or removed controllers must not leave stale chunk recovery subscriptions");
        assertTrue(index.isEmpty());
    }
}
