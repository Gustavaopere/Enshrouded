package com.gustavaopere.enshrouded.integration.epicfight;

/** Compatibility marker only. Epic Fight remains the animation/combat presentation owner. */
public record EpicFightAdapter(boolean loaded) {
    public boolean ownsDamagePipeline() {
        return false;
    }
}
