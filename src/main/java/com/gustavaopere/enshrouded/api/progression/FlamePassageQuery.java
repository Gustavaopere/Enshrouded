package com.gustavaopere.enshrouded.api.progression;

import java.util.Objects;

/**
 * Read-only boundary for querying the Flame passage level of a progression owner.
 * Foundation supplies the standalone Level 1 fallback; Stage 05 may provide a persisted implementation.
 */
@FunctionalInterface
public interface FlamePassageQuery {
    int passageLevel(ProgressionOwner owner);

    static FlamePassageQuery levelOneFallback() {
        return owner -> {
            Objects.requireNonNull(owner, "owner");
            return 1;
        };
    }
}
