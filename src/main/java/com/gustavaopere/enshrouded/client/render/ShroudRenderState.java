package com.gustavaopere.enshrouded.client.render;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;

import java.util.Objects;

/**
 * Small presentation-only interpolation state for the Stage 07 fog hooks.
 * It consumes server-authored severity and never writes gameplay state.
 */
public final class ShroudRenderState {
    static final float TRANSITION_TICKS = 8.0F;

    private float ordinaryWeight;
    private float deadlyWeight;

    public void advance(ShroudSeverity severity, boolean sanctuarySuppressed, float deltaTicks) {
        ShroudSeverity effective = sanctuarySuppressed ? ShroudSeverity.CLEAR : Objects.requireNonNullElse(severity, ShroudSeverity.CLEAR);
        float targetOrdinary = effective == ShroudSeverity.SHROUD ? 1.0F : 0.0F;
        float targetDeadly = effective == ShroudSeverity.DEADLY ? 1.0F : 0.0F;
        float maxStep = clamp01(deltaTicks / TRANSITION_TICKS);

        ordinaryWeight = approach(ordinaryWeight, targetOrdinary, maxStep);
        deadlyWeight = approach(deadlyWeight, targetDeadly, maxStep);
    }

    public float ordinaryWeight() {
        return ordinaryWeight;
    }

    public float deadlyWeight() {
        return deadlyWeight;
    }

    public boolean active() {
        return ordinaryWeight > 0.0001F || deadlyWeight > 0.0001F;
    }

    public ShroudColorProfile colorProfile(double intensity) {
        return ShroudColorProfile.blend(ordinaryWeight, deadlyWeight, intensity);
    }

    public void reset() {
        ordinaryWeight = 0.0F;
        deadlyWeight = 0.0F;
    }

    private static float approach(float current, float target, float maxStep) {
        if (current < target) {
            return Math.min(target, current + maxStep);
        }
        if (current > target) {
            return Math.max(target, current - maxStep);
        }
        return target;
    }

    private static float clamp01(float value) {
        if (!Float.isFinite(value)) {
            return 0.0F;
        }
        return Math.max(0.0F, Math.min(1.0F, value));
    }
}
