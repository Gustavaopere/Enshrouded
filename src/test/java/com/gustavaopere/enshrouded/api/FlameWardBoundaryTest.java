package com.gustavaopere.enshrouded.api;

import com.gustavaopere.enshrouded.api.shroud.FlameWardQuery;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class FlameWardBoundaryTest {
    @Test
    void noWardFallbackNeverInventsSuppression() {
        assertTrue(FlameWardQuery.class.isAnnotationPresent(FunctionalInterface.class));
        assertFalse(FlameWardQuery.none().suppresses(null, null));
    }
}
