package com.gustavaopere.enshrouded.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class EnshroudedClientConfigTest {
    @Test
    void sharedClientSpecExistsWithHudAndFogDefaults() {
        assertNotNull(EnshroudedClientConfig.CLIENT_SPEC);

        EnshroudedClientConfig.HudSettings hudDefaults = EnshroudedClientConfig.HudSettings.defaults();
        assertTrue(hudDefaults.visible());
        assertEquals(1.0D, hudDefaults.scale(), 0.0001D);
        assertEquals(EnshroudedClientConfig.HudAnchor.TOP_LEFT, hudDefaults.anchor());

        EnshroudedClientConfig.FogSettings fogDefaults = EnshroudedClientConfig.FogSettings.defaults();
        assertTrue(fogDefaults.enabled());
        assertEquals(1.0D, fogDefaults.intensity(), 0.0001D);
    }

    @Test
    void hudScaleSanitizationIsPresentationOnlyAndBounded() {
        assertEquals(0.50D, EnshroudedClientConfig.clampHudScale(0.10D), 0.0001D);
        assertEquals(2.00D, EnshroudedClientConfig.clampHudScale(3.00D), 0.0001D);
        assertEquals(1.00D, EnshroudedClientConfig.clampHudScale(Double.NaN), 0.0001D);
        assertEquals(1.25D, EnshroudedClientConfig.clampHudScale(1.25D), 0.0001D);
    }

    @Test
    void fogIntensitySanitizationIsPresentationOnlyAndBounded() {
        assertEquals(0.0D, EnshroudedClientConfig.clampFogIntensity(-1.0D), 0.0001D);
        assertEquals(1.0D, EnshroudedClientConfig.clampFogIntensity(4.0D), 0.0001D);
        assertEquals(1.0D, EnshroudedClientConfig.clampFogIntensity(Double.NaN), 0.0001D);
        assertEquals(0.65D, EnshroudedClientConfig.clampFogIntensity(0.65D), 0.0001D);

        assertEquals(0.0D, new EnshroudedClientConfig.FogSettings(true, -2.0D).intensity(), 0.0001D);
        assertEquals(1.0D, new EnshroudedClientConfig.FogSettings(false, 2.0D).intensity(), 0.0001D);
    }
}
