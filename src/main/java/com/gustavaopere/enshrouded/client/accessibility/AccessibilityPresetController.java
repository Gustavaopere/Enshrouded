package com.gustavaopere.enshrouded.client.accessibility;

import com.gustavaopere.enshrouded.client.ambient.ShroudAmbientController;
import com.gustavaopere.enshrouded.client.effects.ShroudParticleController;
import com.gustavaopere.enshrouded.client.render.ShroudFogController;
import com.gustavaopere.enshrouded.config.EnshroudedClientConfig;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.config.ModConfigEvent;

import java.util.Objects;

/**
 * Owns Stage 07 accessibility preset semantics and client-config reload hygiene.
 *
 * <p>This class never registers or persists another configuration source. It resolves effective
 * presentation values from the single {@link EnshroudedClientConfig#CLIENT_SPEC} seam.</p>
 */
public final class AccessibilityPresetController {
    private static final double MIN_READABLE_HUD_SCALE = 1.0D;

    private AccessibilityPresetController() {}

    public static void register(IEventBus modBus) {
        Objects.requireNonNull(modBus, "modBus");
        modBus.addListener(AccessibilityPresetController::onConfigLoading);
        modBus.addListener(AccessibilityPresetController::onConfigReloading);
    }

    public static SettingsBundle resolve(AccessibilityProfile profile, SettingsBundle raw) {
        Objects.requireNonNull(raw, "raw");
        AccessibilityProfile effectiveProfile = profile == null ? AccessibilityProfile.CUSTOM : profile;
        return switch (effectiveProfile) {
            case CUSTOM -> raw;
            case REDUCED_SENSORY -> reducedSensory(raw);
            case MINIMAL -> minimal(raw);
        };
    }

    private static SettingsBundle reducedSensory(SettingsBundle raw) {
        return new SettingsBundle(
                readableHud(raw.hud()),
                new EnshroudedClientConfig.FogSettings(
                        raw.fog().enabled(),
                        Math.min(raw.fog().intensity(), 0.35D)),
                new EnshroudedClientConfig.AudioSettings(
                        raw.audio().enabled(),
                        Math.min(raw.audio().volume(), 0.45D)),
                new EnshroudedClientConfig.MadnessAudioSettings(
                        raw.madnessAudio().enabled(),
                        Math.min(raw.madnessAudio().intensity(), 0.35D)),
                new EnshroudedClientConfig.ParticleSettings(
                        raw.particles().enabled(),
                        Math.min(raw.particles().maxCount(), 4),
                        Math.min(raw.particles().maxDistance(), 8.0D)),
                new EnshroudedClientConfig.AccessibilitySettings(
                        Math.min(raw.accessibility().distortionIntensity(), 0.25D),
                        true)
        );
    }

    private static SettingsBundle minimal(SettingsBundle raw) {
        return new SettingsBundle(
                readableHud(raw.hud()),
                new EnshroudedClientConfig.FogSettings(false, 0.0D),
                new EnshroudedClientConfig.AudioSettings(false, 0.0D),
                new EnshroudedClientConfig.MadnessAudioSettings(false, 0.0D),
                new EnshroudedClientConfig.ParticleSettings(false, 0, EnshroudedClientConfig.MIN_PARTICLE_DISTANCE),
                new EnshroudedClientConfig.AccessibilitySettings(0.0D, true)
        );
    }

    private static EnshroudedClientConfig.HudSettings readableHud(EnshroudedClientConfig.HudSettings raw) {
        return new EnshroudedClientConfig.HudSettings(
                true,
                Math.max(MIN_READABLE_HUD_SCALE, raw.scale()),
                raw.anchor());
    }

    private static void onConfigLoading(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == EnshroudedClientConfig.CLIENT_SPEC) {
            resetPresentationState();
        }
    }

    private static void onConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == EnshroudedClientConfig.CLIENT_SPEC) {
            resetPresentationState();
        }
    }

    static void resetPresentationState() {
        ShroudFogController.reset();
        ShroudAmbientController.reset();
        ShroudParticleController.reset();
    }

    public record SettingsBundle(
            EnshroudedClientConfig.HudSettings hud,
            EnshroudedClientConfig.FogSettings fog,
            EnshroudedClientConfig.AudioSettings audio,
            EnshroudedClientConfig.MadnessAudioSettings madnessAudio,
            EnshroudedClientConfig.ParticleSettings particles,
            EnshroudedClientConfig.AccessibilitySettings accessibility) {
        public SettingsBundle {
            Objects.requireNonNull(hud, "hud");
            Objects.requireNonNull(fog, "fog");
            Objects.requireNonNull(audio, "audio");
            Objects.requireNonNull(madnessAudio, "madnessAudio");
            Objects.requireNonNull(particles, "particles");
            Objects.requireNonNull(accessibility, "accessibility");
        }
    }
}
