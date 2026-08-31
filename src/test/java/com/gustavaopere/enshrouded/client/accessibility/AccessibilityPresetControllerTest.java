package com.gustavaopere.enshrouded.client.accessibility;

import com.gustavaopere.enshrouded.api.shroud.ShroudSeverity;
import com.gustavaopere.enshrouded.client.hud.ExposureHudModel;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.ExposureSnapshot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class AccessibilityPresetControllerTest {
    private static AccessibilityPresetController.SettingsBundle rawSettings() {
        return new AccessibilityPresetController.SettingsBundle(
                new EnshroudedClientConfig.HudSettings(false, 0.75D, EnshroudedClientConfig.HudAnchor.BOTTOM_RIGHT),
                new EnshroudedClientConfig.FogSettings(true, 0.90D),
                new EnshroudedClientConfig.AudioSettings(true, 0.90D),
                new EnshroudedClientConfig.MadnessAudioSettings(true, 0.90D),
                new EnshroudedClientConfig.ParticleSettings(true, 12, 15.0D),
                new EnshroudedClientConfig.AccessibilitySettings(0.90D, false)
        );
    }

    @Test
    void customProfilePreservesValidatedUserSettings() {
        AccessibilityPresetController.SettingsBundle raw = rawSettings();
        assertEquals(raw, AccessibilityPresetController.resolve(AccessibilityProfile.CUSTOM, raw));
    }

    @Test
    void reducedSensoryProfileCapsIntensityWithoutCreatingAnotherConfigOwner() {
        AccessibilityPresetController.SettingsBundle effective =
                AccessibilityPresetController.resolve(AccessibilityProfile.REDUCED_SENSORY, rawSettings());

        assertTrue(effective.hud().visible());
        assertEquals(1.0D, effective.hud().scale(), 0.0001D);
        assertTrue(effective.fog().enabled());
        assertEquals(0.35D, effective.fog().intensity(), 0.0001D);
        assertTrue(effective.audio().enabled());
        assertEquals(0.45D, effective.audio().volume(), 0.0001D);
        assertTrue(effective.madnessAudio().enabled());
        assertEquals(0.35D, effective.madnessAudio().intensity(), 0.0001D);
        assertTrue(effective.particles().enabled());
        assertEquals(4, effective.particles().maxCount());
        assertEquals(8.0D, effective.particles().maxDistance(), 0.0001D);
        assertEquals(0.25D, effective.accessibility().distortionIntensity(), 0.0001D);
        assertTrue(effective.accessibility().reduceScreenFlashes());
    }

    @Test
    void minimalProfileEliminatesOptionalSensoryEffectsButKeepsReadableHud() {
        AccessibilityPresetController.SettingsBundle effective =
                AccessibilityPresetController.resolve(AccessibilityProfile.MINIMAL, rawSettings());

        assertTrue(effective.hud().visible());
        assertEquals(1.0D, effective.hud().scale(), 0.0001D);
        assertFalse(effective.fog().enabled());
        assertEquals(0.0D, effective.fog().intensity(), 0.0001D);
        assertFalse(effective.audio().enabled());
        assertEquals(0.0D, effective.audio().volume(), 0.0001D);
        assertFalse(effective.madnessAudio().enabled());
        assertEquals(0.0D, effective.madnessAudio().intensity(), 0.0001D);
        assertFalse(effective.particles().enabled());
        assertEquals(0, effective.particles().maxCount());
        assertEquals(2.0D, effective.particles().maxDistance(), 0.0001D);
        assertEquals(0.0D, effective.accessibility().distortionIntensity(), 0.0001D);
        assertTrue(effective.accessibility().reduceScreenFlashes());
    }

    @Test
    void lowestEffectsProfileStillLeavesDeadlyWarningColorIndependent() {
        AccessibilityPresetController.SettingsBundle effective =
                AccessibilityPresetController.resolve(AccessibilityProfile.MINIMAL, rawSettings());
        assertTrue(effective.hud().visible());

        ExposureSnapshot deadly = new ExposureSnapshot(
                ExposureSchema.CURRENT_VERSION,
                200,
                1200,
                1.0F,
                ShroudSeverity.DEADLY,
                false,
                true
        );
        ExposureHudModel model = ExposureHudModel.fromSnapshot(deadly, 0);

        assertTrue(model.visible());
        assertEquals(ExposureHudModel.ZoneKind.DEADLY, model.zoneKind());
        assertEquals("hud.enshrouded.deadly_shroud", model.zoneTranslationKey());
        assertTrue(model.passageWarning());
        assertEquals("hud.enshrouded.passage_blocked", model.warningTranslationKey());
    }

    @Test
    void distortionIntensityIsClampedLikeOtherPresentationOnlySettings() {
        assertEquals(0.0D, EnshroudedClientConfig.clampDistortionIntensity(-4.0D), 0.0001D);
        assertEquals(1.0D, EnshroudedClientConfig.clampDistortionIntensity(4.0D), 0.0001D);
        assertEquals(1.0D, EnshroudedClientConfig.clampDistortionIntensity(Double.NaN), 0.0001D);
        assertEquals(0.40D, EnshroudedClientConfig.clampDistortionIntensity(0.40D), 0.0001D);
    }
}
