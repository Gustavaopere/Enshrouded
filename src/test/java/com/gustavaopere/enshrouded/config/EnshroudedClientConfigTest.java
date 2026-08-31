package com.gustavaopere.enshrouded.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EnshroudedClientConfigTest {
    @Test
    void sharedClientSpecExistsWithHudDefaults() {
        assertNotNull(EnshroudedClientConfig.CLIENT_SPEC);

        EnshroudedClientConfig.HudSettings defaults = EnshroudedClientConfig.HudSettings.defaults();
        assertTrue(defaults.visible());
        assertEquals(1.0D, defaults.scale(), 0.0001D);
        assertEquals(EnshroudedClientConfig.HudAnchor.TOP_LEFT, defaults.anchor());
    }

    @Test
    void hudScaleSanitizationIsPresentationOnlyAndBounded() {
        assertEquals(0.50D, EnshroudedClientConfig.clampHudScale(0.10D), 0.0001D);
        assertEquals(2.00D, EnshroudedClientConfig.clampHudScale(3.00D), 0.0001D);
        assertEquals(1.00D, EnshroudedClientConfig.clampHudScale(Double.NaN), 0.0001D);
        assertEquals(1.25D, EnshroudedClientConfig.clampHudScale(1.25D), 0.0001D);
    }
}
