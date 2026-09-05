package com.gustavaopere.enshrouded.network;

import com.gustavaopere.enshrouded.api.shroud.ShroudSample;
import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.client.state.ClientShroudState;
import com.gustavaopere.enshrouded.performance.PerformanceCounters;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class ShroudSampleSyncRedTest {
    private static final UUID PLAYER = new UUID(9L, 9L);
    private static final UUID SOURCE = new UUID(7L, 7L);

    @Test
    void clientAcceptsOrderedClearShroudDeadlyClearAndRejectsStalePayloads() {
        ClientShroudState state = new ClientShroudState();
        ShroudSample clear = ShroudSample.clear();
        ShroudSample shroud = new ShroudSample(0.40f, ShroudSeverity.SHROUD, Optional.of(SOURCE), false);
        ShroudSample deadly = new ShroudSample(0.90f, ShroudSeverity.DEADLY, Optional.of(SOURCE), false);

        assertTrue(state.accept(ShroudSamplePayload.fromSample(0L, clear)));
        assertTrue(state.accept(ShroudSamplePayload.fromSample(1L, shroud)));
        assertTrue(state.accept(ShroudSamplePayload.fromSample(2L, deadly)));
        assertFalse(state.accept(ShroudSamplePayload.fromSample(1L, clear)));
        assertEquals(deadly, state.sample());
        assertTrue(state.accept(ShroudSamplePayload.fromSample(3L, clear)));
        assertEquals(clear, state.sample());
    }

    @Test
    void payloadValidationRejectsMalformedVersionSequenceAndSample() {
        assertThrows(IllegalArgumentException.class, () -> new ShroudSamplePayload(
                99, 0L, 0.0f, ShroudSeverity.CLEAR, Optional.empty(), false));
        assertThrows(IllegalArgumentException.class, () -> ShroudSamplePayload.fromSample(-1L, ShroudSample.clear()));
        assertThrows(IllegalArgumentException.class, () -> new ShroudSamplePayload(
                ShroudSamplePayload.CURRENT_VERSION,
                0L,
                Float.NaN,
                ShroudSeverity.SHROUD,
                Optional.of(SOURCE),
                false));
    }

    @Test
    void trackerIsChangeDrivenRateLimitedAndStillSendsClearTransitions() {
        PerformanceCounters.global().reset();
        ShroudPlayerSyncTracker tracker = new ShroudPlayerSyncTracker(5L);
        ShroudSample clear = ShroudSample.clear();
        ShroudSample shroud = new ShroudSample(0.40f, ShroudSeverity.SHROUD, Optional.of(SOURCE), false);
        ShroudSample deadly = new ShroudSample(0.90f, ShroudSeverity.DEADLY, Optional.of(SOURCE), false);

        assertEquals(0L, tracker.update(PLAYER, 100L, clear).orElseThrow().sequence());
        assertTrue(tracker.update(PLAYER, 101L, clear).isEmpty());
        assertTrue(tracker.update(PLAYER, 102L, shroud).isEmpty());
        assertEquals(1L, tracker.update(PLAYER, 105L, shroud).orElseThrow().sequence());
        assertTrue(tracker.update(PLAYER, 106L, deadly).isEmpty());
        assertEquals(2L, tracker.update(PLAYER, 110L, deadly).orElseThrow().sequence());
        assertTrue(tracker.update(PLAYER, 111L, clear).isEmpty());
        assertEquals(3L, tracker.update(PLAYER, 115L, clear).orElseThrow().sequence());
        assertTrue(tracker.update(PLAYER, 116L, clear).isEmpty());

        assertEquals(4L, PerformanceCounters.global().snapshot().clientPayloadsSent());
        PerformanceCounters.global().reset();
    }

    @Test
    void trackerRejectsTimeRegressionAndForgetResetsPerPlayerSequence() {
        ShroudPlayerSyncTracker tracker = new ShroudPlayerSyncTracker(5L);
        assertEquals(0L, tracker.update(PLAYER, 10L, ShroudSample.clear()).orElseThrow().sequence());
        assertThrows(IllegalArgumentException.class, () -> tracker.update(PLAYER, 9L, ShroudSample.clear()));

        tracker.forget(PLAYER);
        assertEquals(0L, tracker.update(PLAYER, 20L, ShroudSample.clear()).orElseThrow().sequence());
    }
}
