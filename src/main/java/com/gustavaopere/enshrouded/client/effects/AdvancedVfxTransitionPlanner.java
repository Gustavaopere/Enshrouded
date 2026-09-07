package com.gustavaopere.enshrouded.client.effects;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import com.gustavaopere.enshrouded.exposure.madness.MadnessStage;
import com.gustavaopere.enshrouded.presentation.AdvancedVfxCue;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** Pure transition planner over already synchronized authoritative presentation state. */
public final class AdvancedVfxTransitionPlanner {
    private static final float LATENT_EPSILON = 0.0001F;

    private AdvancedVfxTransitionPlanner() {}

    public static List<AdvancedVfxCue> plan(Frame previous, Frame current) {
        Objects.requireNonNull(previous, "previous");
        Objects.requireNonNull(current, "current");
        List<AdvancedVfxCue> cues = new ArrayList<>(3);

        if (current.severity() == ShroudSeverity.DEADLY && previous.severity() != ShroudSeverity.DEADLY) {
            cues.add(AdvancedVfxCue.SHROUD_TO_DEADLY);
        } else if (previous.severity() == ShroudSeverity.CLEAR && current.severity() == ShroudSeverity.SHROUD) {
            cues.add(AdvancedVfxCue.CLEAR_TO_SHROUD);
        }

        if (!previous.sanctuarySuppressed() && current.sanctuarySuppressed() && current.intensity() > LATENT_EPSILON) {
            cues.add(AdvancedVfxCue.SANCTUARY_ENTER);
        }
        if (current.madnessStage().ordinal() > previous.madnessStage().ordinal()) {
            cues.add(AdvancedVfxCue.MADNESS_ESCALATION);
        }
        return List.copyOf(cues);
    }

    public static Frame fromSnapshot(ExposureSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot");
        return new Frame(snapshot.severity(), snapshot.intensity(), snapshot.sanctuarySuppressed(), snapshot.madnessStage());
    }

    public record Frame(ShroudSeverity severity, float intensity, boolean sanctuarySuppressed, MadnessStage madnessStage) {
        public Frame {
            Objects.requireNonNull(severity, "severity");
            Objects.requireNonNull(madnessStage, "madnessStage");
            if (!Float.isFinite(intensity) || intensity < 0.0F || intensity > 1.0F) {
                throw new IllegalArgumentException("intensity must be finite and within [0,1]");
            }
        }
    }
}
