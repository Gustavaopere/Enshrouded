package com.gustavaopere.enshrouded.client.hud;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.client.state.ClientExposureState;
import com.gustavaopere.enshrouded.exposure.ExposurePayload;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class ExposureHudModelTest {
    @AfterEach
    void resetClientState() {
        ClientExposureState.INSTANCE.reset();
    }

    @Test
    void formatsFiveMinutesAndUsesServerAuthoredOrdinaryState() {
        ExposureSnapshot snapshot = snapshot(
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                ShroudSeverity.SHROUD,
                false,
                false
        );

        ExposureHudModel model = ExposureHudModel.fromSnapshot(snapshot, 0);

        assertTrue(model.visible());
        assertEquals("05:00", model.countdownText());
        assertEquals(ExposureHudModel.ZoneKind.ORDINARY, model.zoneKind());
        assertEquals(snapshot.madnessStage(), model.madnessStage());
        assertFalse(model.passageWarning());
    }

    @Test
    void deadlyBarrierHasDistinctSemanticTreatmentIndependentOfColor() {
        ExposureSnapshot snapshot = snapshot(1200, ShroudSeverity.DEADLY, false, true);

        ExposureHudModel model = ExposureHudModel.fromSnapshot(snapshot, 0);

        assertTrue(model.visible());
        assertEquals(ExposureHudModel.ZoneKind.DEADLY, model.zoneKind());
        assertTrue(model.passageWarning());
        assertEquals("hud.enshrouded.deadly_shroud", model.zoneTranslationKey());
        assertEquals("hud.enshrouded.passage_blocked", model.warningTranslationKey());
    }

    @Test
    void safeOrSanctuarySuppressedStateHidesOverlay() {
        assertFalse(ExposureHudModel.fromSnapshot(
                snapshot(ExposureSchema.DEFAULT_MAX_RESERVE_TICKS, ShroudSeverity.CLEAR, false, false), 0
        ).visible());
        assertFalse(ExposureHudModel.fromSnapshot(
                snapshot(3000, ShroudSeverity.SHROUD, true, false), 0
        ).visible());
    }

    @Test
    void synchronizedReturnToSafeStateHidesPreviouslyVisibleOverlay() {
        assertTrue(ClientExposureState.INSTANCE.accept(ExposurePayload.fromSnapshot(
                10L,
                snapshot(3000, ShroudSeverity.SHROUD, false, false)
        )));
        assertTrue(ExposureHudModel.fromSnapshot(ClientExposureState.INSTANCE.snapshot(), 0).visible());

        assertTrue(ClientExposureState.INSTANCE.accept(ExposurePayload.fromSnapshot(
                11L,
                snapshot(ExposureSchema.DEFAULT_MAX_RESERVE_TICKS, ShroudSeverity.CLEAR, false, false)
        )));
        assertFalse(ExposureHudModel.fromSnapshot(ClientExposureState.INSTANCE.snapshot(), 0).visible());
    }

    @Test
    void madnessThresholdsMirrorAuthoritativeSnapshotStagesWithoutClientReducer() {
        int max = ExposureSchema.DEFAULT_MAX_RESERVE_TICKS;
        assertEquals(MadnessStage.STABLE, snapshot(max, ShroudSeverity.SHROUD, false, false).madnessStage());
        assertEquals(MadnessStage.UNEASY, snapshot(max / 2, ShroudSeverity.SHROUD, false, false).madnessStage());
        assertEquals(MadnessStage.DISTORTED, snapshot(max / 4, ShroudSeverity.SHROUD, false, false).madnessStage());
        assertEquals(MadnessStage.CRITICAL, snapshot(max / 10, ShroudSeverity.SHROUD, false, false).madnessStage());
        assertEquals(MadnessStage.FATAL, snapshot(0, ShroudSeverity.SHROUD, false, false).madnessStage());

        assertEquals(MadnessStage.UNEASY,
                ExposureHudModel.fromSnapshot(snapshot(max / 2, ShroudSeverity.SHROUD, false, false), 0).madnessStage());
        assertEquals(MadnessStage.DISTORTED,
                ExposureHudModel.fromSnapshot(snapshot(max / 4, ShroudSeverity.SHROUD, false, false), 0).madnessStage());
        assertEquals(MadnessStage.CRITICAL,
                ExposureHudModel.fromSnapshot(snapshot(max / 10, ShroudSeverity.SHROUD, false, false), 0).madnessStage());
    }

    @Test
    void interpolationNeverPredictsDeathBeforeAuthoritativeZero() {
        ExposureSnapshot nearlyExhausted = snapshot(5, ShroudSeverity.SHROUD, false, false);
        ExposureHudModel interpolated = ExposureHudModel.fromSnapshot(nearlyExhausted, 200);
        ExposureHudModel authoritativeZero = ExposureHudModel.fromSnapshot(
                snapshot(0, ShroudSeverity.SHROUD, false, false), 0
        );

        assertEquals(1, interpolated.presentedRemainingTicks(),
                "client smoothing may approach zero but cannot predict the server death boundary");
        assertEquals("00:01", interpolated.countdownText());
        assertEquals(0, authoritativeZero.presentedRemainingTicks());
        assertEquals("00:00", authoritativeZero.countdownText());
    }

    @Test
    void newerServerSnapshotOverridesOlderInterpolationAndStalePacketsAreRejected() {
        ExposureSnapshot first = snapshot(200, ShroudSeverity.SHROUD, false, false);
        ExposureSnapshot newer = snapshot(180, ShroudSeverity.DEADLY, false, true);

        assertTrue(ClientExposureState.INSTANCE.accept(ExposurePayload.fromSnapshot(4L, first)));
        ExposureHudModel oldInterpolated = ExposureHudModel.fromSnapshot(ClientExposureState.INSTANCE.snapshot(), 100);
        assertEquals(100, oldInterpolated.presentedRemainingTicks());

        assertTrue(ClientExposureState.INSTANCE.accept(ExposurePayload.fromSnapshot(5L, newer)));
        assertFalse(ClientExposureState.INSTANCE.accept(ExposurePayload.fromSnapshot(4L, first)));

        ExposureHudModel snapped = ExposureHudModel.fromSnapshot(ClientExposureState.INSTANCE.snapshot(), 0);
        assertEquals(180, snapped.presentedRemainingTicks());
        assertEquals(ExposureHudModel.ZoneKind.DEADLY, snapped.zoneKind());
        assertTrue(snapped.passageWarning());
    }

    private static ExposureSnapshot snapshot(
            int remainingTicks,
            ShroudSeverity severity,
            boolean sanctuarySuppressed,
            boolean deadlyBarrierActive) {
        return new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                remainingTicks,
                ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                severity == ShroudSeverity.CLEAR ? 0.0F : 0.6F,
                severity,
                sanctuarySuppressed,
                deadlyBarrierActive
        );
    }
}
