package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

final class AdvancedVfxTransitionPlannerTest {
    @Test void clearToShroudProducesOneEntryCue() {
        var previous = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.CLEAR, 0.0F, false, MadnessStage.STABLE);
        var current = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.55F, false, MadnessStage.STABLE);
        assertEquals(List.of(AdvancedVfxCue.CLEAR_TO_SHROUD), AdvancedVfxTransitionPlanner.plan(previous, current));
    }
    @Test void directClearToDeadlyUsesOnlyTheStrongerDeadlyCue() {
        var previous = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.CLEAR, 0.0F, false, MadnessStage.STABLE);
        var current = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.DEADLY, 1.0F, false, MadnessStage.STABLE);
        assertEquals(List.of(AdvancedVfxCue.SHROUD_TO_DEADLY), AdvancedVfxTransitionPlanner.plan(previous, current));
    }
    @Test void sanctuaryCueRequiresARealLatentShroudSuppressionTransition() {
        var clear = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.CLEAR, 0.0F, false, MadnessStage.STABLE);
        var fakeSanctuary = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.CLEAR, 0.0F, true, MadnessStage.STABLE);
        assertTrue(AdvancedVfxTransitionPlanner.plan(clear, fakeSanctuary).isEmpty());
        var shroud = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.6F, false, MadnessStage.STABLE);
        var sanctuary = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.6F, true, MadnessStage.STABLE);
        assertEquals(List.of(AdvancedVfxCue.SANCTUARY_ENTER), AdvancedVfxTransitionPlanner.plan(shroud, sanctuary));
    }
    @Test void madnessOnlySignalsEscalationNotRecoveryOrStableRepeats() {
        var stable = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.5F, false, MadnessStage.STABLE);
        var distorted = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.5F, false, MadnessStage.DISTORTED);
        var critical = new AdvancedVfxTransitionPlanner.Frame(ShroudSeverity.SHROUD, 0.5F, false, MadnessStage.CRITICAL);
        assertEquals(List.of(AdvancedVfxCue.MADNESS_ESCALATION), AdvancedVfxTransitionPlanner.plan(stable, distorted));
        assertEquals(List.of(AdvancedVfxCue.MADNESS_ESCALATION), AdvancedVfxTransitionPlanner.plan(distorted, critical));
        assertTrue(AdvancedVfxTransitionPlanner.plan(critical, distorted).isEmpty());
        assertTrue(AdvancedVfxTransitionPlanner.plan(stable, stable).isEmpty());
    }
}
