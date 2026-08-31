package com.gustavaopere.enshrouded.config;

import com.gustavaopere.enshrouded.client.accessibility.AccessibilityPresetController;
import com.gustavaopere.enshrouded.client.accessibility.AccessibilityProfile;
import net.neoforged.neoforge.common.ModConfigSpec;

/** Single presentation-only client configuration seam for Stage 07. */
public final class EnshroudedClientConfig {
    public static final double DEFAULT_HUD_SCALE = 1.0D;
    public static final double MIN_HUD_SCALE = 0.50D;
    public static final double MAX_HUD_SCALE = 2.00D;
    public static final double DEFAULT_FOG_INTENSITY = 1.0D;
    public static final double MIN_FOG_INTENSITY = 0.0D;
    public static final double MAX_FOG_INTENSITY = 1.0D;
    public static final double DEFAULT_AUDIO_VOLUME = 0.65D;
    public static final double MIN_AUDIO_VOLUME = 0.0D;
    public static final double MAX_AUDIO_VOLUME = 1.0D;
    public static final double DEFAULT_MADNESS_AUDIO_INTENSITY = 0.75D;
    public static final double MIN_MADNESS_AUDIO_INTENSITY = 0.0D;
    public static final double MAX_MADNESS_AUDIO_INTENSITY = 1.0D;
    public static final int DEFAULT_PARTICLE_COUNT = 8;
    public static final int MIN_PARTICLE_COUNT = 0;
    public static final int MAX_PARTICLE_COUNT = 16;
    public static final double DEFAULT_PARTICLE_DISTANCE = 10.0D;
    public static final double MIN_PARTICLE_DISTANCE = 2.0D;
    public static final double MAX_PARTICLE_DISTANCE = 16.0D;
    public static final double DEFAULT_DISTORTION_INTENSITY = 1.0D;
    public static final double MIN_DISTORTION_INTENSITY = 0.0D;
    public static final double MAX_DISTORTION_INTENSITY = 1.0D;

    public static final ModConfigSpec CLIENT_SPEC;

    private static final ModConfigSpec.BooleanValue HUD_VISIBLE;
    private static final ModConfigSpec.DoubleValue HUD_SCALE;
    private static final ModConfigSpec.EnumValue<HudAnchor> HUD_ANCHOR;
    private static final ModConfigSpec.BooleanValue FOG_ENABLED;
    private static final ModConfigSpec.DoubleValue FOG_INTENSITY;
    private static final ModConfigSpec.BooleanValue AUDIO_ENABLED;
    private static final ModConfigSpec.DoubleValue AUDIO_VOLUME;
    private static final ModConfigSpec.BooleanValue MADNESS_AUDIO_ENABLED;
    private static final ModConfigSpec.DoubleValue MADNESS_AUDIO_INTENSITY;
    private static final ModConfigSpec.BooleanValue PARTICLES_ENABLED;
    private static final ModConfigSpec.IntValue PARTICLE_MAX_COUNT;
    private static final ModConfigSpec.DoubleValue PARTICLE_MAX_DISTANCE;
    private static final ModConfigSpec.EnumValue<AccessibilityProfile> ACCESSIBILITY_PROFILE;
    private static final ModConfigSpec.DoubleValue DISTORTION_INTENSITY;
    private static final ModConfigSpec.BooleanValue REDUCE_SCREEN_FLASHES;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Client-only presentation settings. These values never alter server gameplay.");
        builder.push("hud");
        HUD_VISIBLE = builder.comment("Show the Shroud exposure HUD while synchronized server state is hazardous.").define("visible", true);
        HUD_SCALE = builder.comment("Visual scale of the Shroud exposure HUD.").defineInRange("scale", DEFAULT_HUD_SCALE, MIN_HUD_SCALE, MAX_HUD_SCALE);
        HUD_ANCHOR = builder.comment("Screen corner used to anchor the Shroud exposure HUD.").defineEnum("anchor", HudAnchor.TOP_LEFT);
        builder.pop();
        builder.push("fog");
        FOG_ENABLED = builder.comment("Enable enhanced client-side Shroud fog. Disabling this never changes gameplay state.").define("enabled", true);
        FOG_INTENSITY = builder.comment("Strength of enhanced Shroud fog color and distance treatment.").defineInRange("intensity", DEFAULT_FOG_INTENSITY, MIN_FOG_INTENSITY, MAX_FOG_INTENSITY);
        builder.pop();
        builder.push("audio");
        AUDIO_ENABLED = builder.comment("Enable local Shroud ambience. Disabling audio does not alter particles or gameplay.").define("enabled", true);
        AUDIO_VOLUME = builder.comment("Volume multiplier for Enshrouded ambient Shroud sounds.").defineInRange("volume", DEFAULT_AUDIO_VOLUME, MIN_AUDIO_VOLUME, MAX_AUDIO_VOLUME);
        builder.pop();
        builder.push("madnessAudio");
        MADNESS_AUDIO_ENABLED = builder.comment("Enable client-side Madness auditory cues independently of ordinary Shroud ambience.").define("enabled", true);
        MADNESS_AUDIO_INTENSITY = builder.comment("Intensity multiplier for server-authored Madness auditory cues.").defineInRange("intensity", DEFAULT_MADNESS_AUDIO_INTENSITY, MIN_MADNESS_AUDIO_INTENSITY, MAX_MADNESS_AUDIO_INTENSITY);
        builder.pop();
        builder.push("particles");
        PARTICLES_ENABLED = builder.comment("Enable local Shroud particles. Disabling particles does not alter audio or gameplay.").define("enabled", true);
        PARTICLE_MAX_COUNT = builder.comment("Hard client-side cap on particles emitted by one Enshrouded pulse.").defineInRange("maxCount", DEFAULT_PARTICLE_COUNT, MIN_PARTICLE_COUNT, MAX_PARTICLE_COUNT);
        PARTICLE_MAX_DISTANCE = builder.comment("Maximum distance in blocks for source-local Core, growth and Red Sludge particle sampling.").defineInRange("maxDistance", DEFAULT_PARTICLE_DISTANCE, MIN_PARTICLE_DISTANCE, MAX_PARTICLE_DISTANCE);
        builder.pop();
        builder.push("accessibility");
        ACCESSIBILITY_PROFILE = builder.comment("Coordinated presentation preset. CUSTOM preserves the individual values above.")
                .defineEnum("profile", AccessibilityProfile.CUSTOM);
        DISTORTION_INTENSITY = builder.comment("Maximum client-side hallucination/distortion intensity available to Enshrouded presentation effects.")
                .defineInRange("distortionIntensity", DEFAULT_DISTORTION_INTENSITY, MIN_DISTORTION_INTENSITY, MAX_DISTORTION_INTENSITY);
        REDUCE_SCREEN_FLASHES = builder.comment("Reduce or suppress Enshrouded-owned screen-flash presentation effects when such effects are active.")
                .define("reduceScreenFlashes", false);
        builder.pop();
        CLIENT_SPEC = builder.build();
    }

    private EnshroudedClientConfig() {}

    public static HudSettings hudSettings() {
        return resolvedSettings().hud();
    }

    public static FogSettings fogSettings() {
        return resolvedSettings().fog();
    }

    public static AudioSettings audioSettings() {
        return resolvedSettings().audio();
    }

    public static MadnessAudioSettings madnessAudioSettings() {
        return resolvedSettings().madnessAudio();
    }

    public static ParticleSettings particleSettings() {
        return resolvedSettings().particles();
    }

    public static AccessibilitySettings accessibilitySettings() {
        return resolvedSettings().accessibility();
    }

    public static AccessibilityProfile accessibilityProfile() {
        AccessibilityProfile profile = ACCESSIBILITY_PROFILE.get();
        return profile == null ? AccessibilityProfile.CUSTOM : profile;
    }

    private static AccessibilityPresetController.SettingsBundle resolvedSettings() {
        return AccessibilityPresetController.resolve(
                accessibilityProfile(),
                new AccessibilityPresetController.SettingsBundle(
                        rawHudSettings(),
                        rawFogSettings(),
                        rawAudioSettings(),
                        rawMadnessAudioSettings(),
                        rawParticleSettings(),
                        rawAccessibilitySettings()
                )
        );
    }

    private static HudSettings rawHudSettings() {
        return new HudSettings(HUD_VISIBLE.getAsBoolean(), HUD_SCALE.getAsDouble(), HUD_ANCHOR.get());
    }

    private static FogSettings rawFogSettings() {
        return new FogSettings(FOG_ENABLED.getAsBoolean(), FOG_INTENSITY.getAsDouble());
    }

    private static AudioSettings rawAudioSettings() {
        return new AudioSettings(AUDIO_ENABLED.getAsBoolean(), AUDIO_VOLUME.getAsDouble());
    }

    private static MadnessAudioSettings rawMadnessAudioSettings() {
        return new MadnessAudioSettings(MADNESS_AUDIO_ENABLED.getAsBoolean(), MADNESS_AUDIO_INTENSITY.getAsDouble());
    }

    private static ParticleSettings rawParticleSettings() {
        return new ParticleSettings(PARTICLES_ENABLED.getAsBoolean(), PARTICLE_MAX_COUNT.getAsInt(), PARTICLE_MAX_DISTANCE.getAsDouble());
    }

    private static AccessibilitySettings rawAccessibilitySettings() {
        return new AccessibilitySettings(DISTORTION_INTENSITY.getAsDouble(), REDUCE_SCREEN_FLASHES.getAsBoolean());
    }

    public static double clampHudScale(double value) {
        if (!Double.isFinite(value)) return DEFAULT_HUD_SCALE;
        return Math.max(MIN_HUD_SCALE, Math.min(MAX_HUD_SCALE, value));
    }

    public static double clampFogIntensity(double value) {
        if (!Double.isFinite(value)) return DEFAULT_FOG_INTENSITY;
        return Math.max(MIN_FOG_INTENSITY, Math.min(MAX_FOG_INTENSITY, value));
    }

    public static double clampAudioVolume(double value) {
        if (!Double.isFinite(value)) return DEFAULT_AUDIO_VOLUME;
        return Math.max(MIN_AUDIO_VOLUME, Math.min(MAX_AUDIO_VOLUME, value));
    }

    public static double clampMadnessAudioIntensity(double value) {
        if (!Double.isFinite(value)) return DEFAULT_MADNESS_AUDIO_INTENSITY;
        return Math.max(MIN_MADNESS_AUDIO_INTENSITY, Math.min(MAX_MADNESS_AUDIO_INTENSITY, value));
    }

    public static int clampParticleCount(int value) {
        return Math.max(MIN_PARTICLE_COUNT, Math.min(MAX_PARTICLE_COUNT, value));
    }

    public static double clampParticleDistance(double value) {
        if (!Double.isFinite(value)) return DEFAULT_PARTICLE_DISTANCE;
        return Math.max(MIN_PARTICLE_DISTANCE, Math.min(MAX_PARTICLE_DISTANCE, value));
    }

    public static double clampDistortionIntensity(double value) {
        if (!Double.isFinite(value)) return DEFAULT_DISTORTION_INTENSITY;
        return Math.max(MIN_DISTORTION_INTENSITY, Math.min(MAX_DISTORTION_INTENSITY, value));
    }

    public enum HudAnchor { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    public record HudSettings(boolean visible, double scale, HudAnchor anchor) {
        public HudSettings {
            scale = clampHudScale(scale);
            if (anchor == null) anchor = HudAnchor.TOP_LEFT;
        }
        public static HudSettings defaults() { return new HudSettings(true, DEFAULT_HUD_SCALE, HudAnchor.TOP_LEFT); }
    }

    public record FogSettings(boolean enabled, double intensity) {
        public FogSettings { intensity = clampFogIntensity(intensity); }
        public static FogSettings defaults() { return new FogSettings(true, DEFAULT_FOG_INTENSITY); }
    }

    public record AudioSettings(boolean enabled, double volume) {
        public AudioSettings { volume = clampAudioVolume(volume); }
        public static AudioSettings defaults() { return new AudioSettings(true, DEFAULT_AUDIO_VOLUME); }
    }

    public record MadnessAudioSettings(boolean enabled, double intensity) {
        public MadnessAudioSettings { intensity = clampMadnessAudioIntensity(intensity); }
        public static MadnessAudioSettings defaults() { return new MadnessAudioSettings(true, DEFAULT_MADNESS_AUDIO_INTENSITY); }
    }

    public record ParticleSettings(boolean enabled, int maxCount, double maxDistance) {
        public ParticleSettings {
            maxCount = clampParticleCount(maxCount);
            maxDistance = clampParticleDistance(maxDistance);
        }
        public ParticleSettings(boolean enabled, int maxCount) { this(enabled, maxCount, DEFAULT_PARTICLE_DISTANCE); }
        public static ParticleSettings defaults() { return new ParticleSettings(true, DEFAULT_PARTICLE_COUNT, DEFAULT_PARTICLE_DISTANCE); }
    }

    public record AccessibilitySettings(double distortionIntensity, boolean reduceScreenFlashes) {
        public AccessibilitySettings {
            distortionIntensity = clampDistortionIntensity(distortionIntensity);
        }
        public static AccessibilitySettings defaults() {
            return new AccessibilitySettings(DEFAULT_DISTORTION_INTENSITY, false);
        }
    }
}
