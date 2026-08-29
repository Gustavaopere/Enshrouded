package com.gustavaopere.enshrouded.exposure;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExposurePayloadSyncRedTest {
    @Test
    void payloadIsStrictlyPresentationSnapshotWithStableSequence() {
        ExposureSnapshot snapshot = new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                5800,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                0.65F,
                ShroudSeverity.SHROUD,
                false,
                false
        );

        ExposurePayload payload = ExposurePayload.fromSnapshot(7L, snapshot);
        assertTrue(payload instanceof CustomPacketPayload);
        assertEquals(ExposurePayload.CURRENT_VERSION, payload.payloadVersion());
        assertEquals(7L, payload.sequence());
        assertEquals(snapshot, payload.snapshot());
        assertEquals(ExposurePayload.TYPE, payload.type());
    }

    @Test
    void trackerSendsFirstAndChangedSnapshotsButNeverIdenticalState() {
        ExposurePlayerSyncTracker tracker = new ExposurePlayerSyncTracker();
        UUID playerId = UUID.fromString("92cf69d1-65f2-4c65-a77b-2901d543af37");

        ExposureSnapshot initial = snapshot(5800, ShroudSeverity.SHROUD, false);
        ExposurePayload first = tracker.update(playerId, initial).orElseThrow();
        assertEquals(0L, first.sequence());
        assertFalse(tracker.update(playerId, initial).isPresent(),
                "identical exposure snapshots must not resend");

        ExposureSnapshot changed = snapshot(5780, ShroudSeverity.SHROUD, false);
        ExposurePayload second = tracker.update(playerId, changed).orElseThrow();
        assertEquals(1L, second.sequence());
        assertEquals(changed, second.snapshot());

        tracker.forget(playerId);
        ExposurePayload afterForget = tracker.update(playerId, changed).orElseThrow();
        assertEquals(0L, afterForget.sequence(),
                "logout forgets only ephemeral sync sequence, never the persisted attachment");
    }

    private static ExposureSnapshot snapshot(int remainingTicks, ShroudSeverity severity, boolean sanctuarySuppressed) {
        return new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                remainingTicks,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                severity == ShroudSeverity.CLEAR ? 0.0F : 0.5F,
                severity,
                sanctuarySuppressed,
                false
        );
    }
}
