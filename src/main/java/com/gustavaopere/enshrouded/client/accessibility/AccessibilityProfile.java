package com.gustavaopere.enshrouded.client.accessibility;

/** Coordinated presentation-only profiles layered over the single Stage 07 client config. */
public enum AccessibilityProfile {
    /** Preserve the user's individual validated presentation values. */
    CUSTOM,
    /** Cap sensory intensity while keeping all individually enabled presentation channels available. */
    REDUCED_SENSORY,
    /** Remove optional sensory effects while preserving a readable hazard/timer HUD. */
    MINIMAL
}
