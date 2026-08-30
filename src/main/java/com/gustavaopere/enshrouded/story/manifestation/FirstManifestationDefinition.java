package com.gustavaopere.enshrouded.story.manifestation;

/** Immutable Level-1 encounter tuning shared by provider-neutral manifestation services. */
public record FirstManifestationDefinition(
        int manifestationIndex,
        int arenaRadius,
        float arenaIntensity,
        float phaseTwoHealthFraction,
        float phaseThreeHealthFraction,
        long phaseTwoElapsedTicks,
        long phaseThreeElapsedTicks,
        int phaseTwoEventPressure,
        int phaseThreeEventPressure) {

    public FirstManifestationDefinition {
        if (manifestationIndex < 1) {
            throw new IllegalArgumentException("manifestationIndex must be >= 1");
        }
        if (arenaRadius <= 0) {
            throw new IllegalArgumentException("arenaRadius must be > 0");
        }
        if (!Float.isFinite(arenaIntensity) || arenaIntensity <= 0.0F || arenaIntensity > 1.0F) {
            throw new IllegalArgumentException("arenaIntensity must be finite and within (0, 1]");
        }
        if (!Float.isFinite(phaseTwoHealthFraction)
                || phaseTwoHealthFraction <= 0.0F
                || phaseTwoHealthFraction >= 1.0F) {
            throw new IllegalArgumentException("phaseTwoHealthFraction must be finite and within (0, 1)");
        }
        if (!Float.isFinite(phaseThreeHealthFraction)
                || phaseThreeHealthFraction < 0.0F
                || phaseThreeHealthFraction >= phaseTwoHealthFraction) {
            throw new IllegalArgumentException(
                    "phaseThreeHealthFraction must be finite, >= 0 and lower than phaseTwoHealthFraction");
        }
        if (phaseTwoElapsedTicks <= 0L) {
            throw new IllegalArgumentException("phaseTwoElapsedTicks must be > 0");
        }
        if (phaseThreeElapsedTicks <= phaseTwoElapsedTicks) {
            throw new IllegalArgumentException("phaseThreeElapsedTicks must be greater than phaseTwoElapsedTicks");
        }
        if (phaseTwoEventPressure < 1) {
            throw new IllegalArgumentException("phaseTwoEventPressure must be >= 1");
        }
        if (phaseThreeEventPressure <= phaseTwoEventPressure) {
            throw new IllegalArgumentException("phaseThreeEventPressure must be greater than phaseTwoEventPressure");
        }
    }

    public static FirstManifestationDefinition levelOne() {
        return new FirstManifestationDefinition(
                1,
                12,
                0.65F,
                0.70F,
                0.35F,
                1_200L,
                2_400L,
                1,
                2
        );
    }
}
