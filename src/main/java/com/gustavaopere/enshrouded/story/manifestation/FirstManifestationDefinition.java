package com.gustavaopere.enshrouded.story.manifestation;

/** Immutable Level-1 encounter tuning shared by provider-neutral manifestation services. */
public record FirstManifestationDefinition(
        int manifestationIndex,
        int arenaRadius,
        float arenaIntensity) {

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
    }

    public static FirstManifestationDefinition levelOne() {
        return new FirstManifestationDefinition(1, 12, 0.65F);
    }
}
