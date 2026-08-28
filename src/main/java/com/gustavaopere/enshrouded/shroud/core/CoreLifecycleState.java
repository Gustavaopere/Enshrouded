package com.gustavaopere.enshrouded.shroud.core;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public enum CoreLifecycleState {
    DORMANT("dormant"),
    ACTIVE("active"),
    DESTROYED("destroyed"),
    PURIFIED("purified");

    private final String id;

    CoreLifecycleState(String id) {
        this.id = id;
    }

    public String id() {
        return id;
    }

    public boolean canTransitionTo(CoreLifecycleState target) {
        Objects.requireNonNull(target, "target");
        return switch (this) {
            case DORMANT -> target == ACTIVE;
            case ACTIVE -> target == DESTROYED;
            case DESTROYED -> target == PURIFIED;
            case PURIFIED -> false;
        };
    }

    public CoreLifecycleState transitionTo(CoreLifecycleState target) {
        Objects.requireNonNull(target, "target");
        if (!canTransitionTo(target)) {
            throw new IllegalStateException("illegal Shroud core lifecycle transition: " + id + " -> " + target.id);
        }
        return target;
    }

    public boolean expansionEligible() {
        return this == ACTIVE;
    }

    public static Optional<CoreLifecycleState> fromId(String id) {
        if (id == null) {
            return Optional.empty();
        }
        return Arrays.stream(values())
                .filter(state -> state.id.equals(id))
                .findFirst();
    }
}
