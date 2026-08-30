package com.gustavaopere.enshrouded.story.manifestation;

import java.util.Objects;

/**
 * Provider-neutral classifier for Level-1 Lich encounter escalation.
 *
 * <p>The director never mutates boss AI or provider state. It only derives the strongest phase
 * justified by authoritative encounter signals so native and optional providers can consume the
 * same stable contract without Enshrouded replacing their combat implementation.</p>
 */
public final class LichPhaseDirector {
    private final FirstManifestationDefinition definition;

    public LichPhaseDirector(FirstManifestationDefinition definition) {
        this.definition = Objects.requireNonNull(definition, "definition");
    }

    public Phase phase(float healthFraction, long elapsedTicks, int eventPressure) {
        if (!Float.isFinite(healthFraction) || healthFraction < 0.0F || healthFraction > 1.0F) {
            throw new IllegalArgumentException("healthFraction must be finite and within [0, 1]");
        }
        if (elapsedTicks < 0L) {
            throw new IllegalArgumentException("elapsedTicks must be >= 0");
        }
        if (eventPressure < 0) {
            throw new IllegalArgumentException("eventPressure must be >= 0");
        }

        if (healthFraction <= definition.phaseThreeHealthFraction()
                || elapsedTicks >= definition.phaseThreeElapsedTicks()
                || eventPressure >= definition.phaseThreeEventPressure()) {
            return Phase.PHASE_THREE;
        }
        if (healthFraction <= definition.phaseTwoHealthFraction()
                || elapsedTicks >= definition.phaseTwoElapsedTicks()
                || eventPressure >= definition.phaseTwoEventPressure()) {
            return Phase.PHASE_TWO;
        }
        return Phase.PHASE_ONE;
    }

    public enum Phase {
        PHASE_ONE,
        PHASE_TWO,
        PHASE_THREE
    }
}
