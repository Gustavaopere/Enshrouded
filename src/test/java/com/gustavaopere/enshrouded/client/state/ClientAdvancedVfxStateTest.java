package com.gustavaopere.enshrouded.client.state;

import com.gustavaopere.enshrouded.network.AdvancedVfxPayload;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import net.minecraft.core.BlockPos;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

final class ClientAdvancedVfxStateTest {
    @AfterEach
    void resetState() {
        ClientAdvancedVfxState.INSTANCE.reset();
    }

    @Test
    void acceptsOnlyServerAuthoredDiscreteCues() {
        var payload = AdvancedVfxPayload.of(AdvancedVfxCue.CORE_DESTROYED, new BlockPos(4, 70, -3));
        ClientAdvancedVfxState.INSTANCE.accept(payload);
        assertEquals(List.of(payload), ClientAdvancedVfxState.INSTANCE.drain());

        assertThrows(IllegalArgumentException.class,
                () -> AdvancedVfxPayload.of(AdvancedVfxCue.CLEAR_TO_SHROUD, BlockPos.ZERO));
    }

    @Test
    void queueIsBoundedAndDrainIsExactlyOnce() {
        for (int index = 0; index < ClientAdvancedVfxState.MAX_PENDING_CUES + 5; index++) {
            ClientAdvancedVfxState.INSTANCE.accept(AdvancedVfxPayload.of(
                    AdvancedVfxCue.FLAME_RITUAL_SUCCESS,
                    new BlockPos(index, 64, 0)));
        }

        List<AdvancedVfxPayload> drained = ClientAdvancedVfxState.INSTANCE.drain();
        assertEquals(ClientAdvancedVfxState.MAX_PENDING_CUES, drained.size());
        assertEquals(5, drained.getFirst().origin().getX());
        assertEquals(List.of(), ClientAdvancedVfxState.INSTANCE.drain());
    }
}
