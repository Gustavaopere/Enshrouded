package com.gustavaopere.enshrouded.performance;

import java.util.Objects;

/**
 * Shared validation helper for Stage 09 performance budgets and benchmark descriptors.
 *
 * <p>This class does not own any gameplay budget. Runtime owners keep their existing config and
 * safety-limit authorities; this helper only provides one fail-fast rule when a Stage 09 surface
 * needs to express a positive request under an explicit hard ceiling.</p>
 */
public final class PerformanceBudgetMatrix {
    private PerformanceBudgetMatrix() {
    }

    public static int requirePositiveBounded(String name, int requested, int hardMaximum) {
        String nonBlankName = Objects.requireNonNull(name, "name").trim();
        if (nonBlankName.isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        if (requested <= 0) {
            throw new IllegalArgumentException(nonBlankName + " requested budget must be > 0");
        }
        if (hardMaximum <= 0) {
            throw new IllegalArgumentException(nonBlankName + " hard maximum must be > 0");
        }
        return Math.min(requested, hardMaximum);
    }
}
