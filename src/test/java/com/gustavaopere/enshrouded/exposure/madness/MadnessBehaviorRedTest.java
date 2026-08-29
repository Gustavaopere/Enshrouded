package com.gustavaopere.enshrouded.exposure.madness;

import com.gustavaopere.enshrouded.config.EnshroudedConfig;
import com.gustavaopere.enshrouded.exposure.ExposurePayload;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MadnessBehaviorRedTest {
    @Test
    void levelOneThresholdBandsUseOnlyExistingExposureReserve() throws Exception {
        Method levelOne = MadnessService.class.getDeclaredMethod("levelOne");
        Object service = levelOne.invoke(null);
        Method stage = MadnessService.class.getDeclaredMethod("stage", int.class, int.class);

        assertEquals(MadnessStage.STABLE, stage.invoke(service, 1000, 1000));
        assertEquals(MadnessStage.STABLE, stage.invoke(service, 501, 1000));
        assertEquals(MadnessStage.UNEASY, stage.invoke(service, 500, 1000));
        assertEquals(MadnessStage.UNEASY, stage.invoke(service, 251, 1000));
        assertEquals(MadnessStage.DISTORTED, stage.invoke(service, 250, 1000));
        assertEquals(MadnessStage.DISTORTED, stage.invoke(service, 101, 1000));
        assertEquals(MadnessStage.CRITICAL, stage.invoke(service, 100, 1000));
        assertEquals(MadnessStage.CRITICAL, stage.invoke(service, 1, 1000));
        assertEquals(MadnessStage.FATAL, stage.invoke(service, 0, 1000));
    }

    @Test
    void stagesExposePresentationAndRestrainedServerPenaltyFlags() throws Exception {
        Method visual = MadnessStage.class.getDeclaredMethod("visualHallucinations");
        Method audio = MadnessStage.class.getDeclaredMethod("audioHallucinations");
        Method sprintLocked = MadnessStage.class.getDeclaredMethod("sprintLocked");
        Method fatal = MadnessStage.class.getDeclaredMethod("fatal");

        assertFalse((boolean) visual.invoke(MadnessStage.UNEASY));
        assertTrue((boolean) visual.invoke(MadnessStage.DISTORTED));
        assertFalse((boolean) audio.invoke(MadnessStage.DISTORTED));
        assertTrue((boolean) audio.invoke(MadnessStage.CRITICAL));
        assertTrue((boolean) sprintLocked.invoke(MadnessStage.CRITICAL));
        assertFalse((boolean) sprintLocked.invoke(MadnessStage.DISTORTED));
        assertTrue((boolean) fatal.invoke(MadnessStage.FATAL));
        assertFalse((boolean) fatal.invoke(MadnessStage.CRITICAL));
    }

    @Test
    void authoritativeSnapshotAndPayloadCarryMadnessStage() {
        assertTrue(Arrays.stream(ExposureSnapshot.class.getRecordComponents())
                .anyMatch(component -> component.getName().equals("madnessStage")
                        && component.getType() == MadnessStage.class));
        assertTrue(Arrays.stream(ExposurePayload.class.getRecordComponents())
                .anyMatch(component -> component.getName().equals("madnessStage")
                        && component.getType() == MadnessStage.class));
    }

    @Test
    void criticalSprintPenaltyHasServerConfigToggle() throws Exception {
        Method accessor = EnshroudedConfig.class.getDeclaredMethod("madnessPreventSprintingAtCritical");
        assertEquals(boolean.class, accessor.getReturnType());
    }
}
