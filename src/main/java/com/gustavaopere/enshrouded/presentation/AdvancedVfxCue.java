package com.gustavaopere.enshrouded.presentation;

import java.util.Arrays;
import java.util.Optional;

/** Bounded Stage 10.08 presentation cue metadata. Never owns gameplay state. */
public enum AdvancedVfxCue {
    CLEAR_TO_SHROUD("clear_to_shroud", 6, 20, 16.0D, 40, false),
    SHROUD_TO_DEADLY("shroud_to_deadly", 10, 24, 18.0D, 60, false),
    SANCTUARY_ENTER("sanctuary_enter", 6, 18, 16.0D, 40, false),
    CORE_DESTROYED("core_destroyed", 16, 28, 32.0D, 80, true),
    FLAME_RITUAL_SUCCESS("flame_ritual_success", 12, 28, 32.0D, 80, true),
    LICH_MANIFESTATION("lich_manifestation", 18, 30, 48.0D, 100, true),
    MADNESS_ESCALATION("madness_escalation", 6, 20, 16.0D, 60, false);

    private final String id;
    private final int maxParticles;
    private final int lifetimeTicks;
    private final double maxDistance;
    private final int cooldownTicks;
    private final boolean serverAuthored;

    AdvancedVfxCue(String id, int maxParticles, int lifetimeTicks, double maxDistance, int cooldownTicks, boolean serverAuthored) {
        this.id = id;
        this.maxParticles = maxParticles;
        this.lifetimeTicks = lifetimeTicks;
        this.maxDistance = maxDistance;
        this.cooldownTicks = cooldownTicks;
        this.serverAuthored = serverAuthored;
    }

    public String id() { return id; }
    public int maxParticles() { return maxParticles; }
    public int lifetimeTicks() { return lifetimeTicks; }
    public double maxDistance() { return maxDistance; }
    public int cooldownTicks() { return cooldownTicks; }
    public boolean serverAuthored() { return serverAuthored; }

    public int particleBudget(boolean enabled, int clientMaxCount) {
        if (!enabled || clientMaxCount <= 0) return 0;
        return Math.min(maxParticles, clientMaxCount);
    }

    public static Optional<AdvancedVfxCue> fromId(String id) {
        if (id == null) return Optional.empty();
        return Arrays.stream(values()).filter(value -> value.id.equals(id)).findFirst();
    }
}
