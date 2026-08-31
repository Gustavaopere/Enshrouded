package com.gustavaopere.enshrouded.config;

import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * Single presentation-only client configuration seam for Stage 07.
 *
 * <p>Later fog, audio/particle and accessibility tasks extend this container rather than
 * registering parallel client configs. Values in this class must never alter server-authoritative
 * exposure, damage, progression or passage rules.</p>
 */
public final class EnshroudedClientConfig {
    public static final double DEFAULT_HUD_SCALE = 1.0D;
    public static final double MIN_HUD_SCALE = 0.50D;
    public static final double MAX_HUD_SCALE = 2.00D;
    public static final double DEFAULT_FOG_INTENSITY = 1.0D;
    public static final double MIN_FOG_INTENSITY = 0.0D;
    public static final double MAX_FOG_INTENSITY = 1.0D;

    public static final ModConfigSpec CLIENT_SPEC;

    private static final ModConfigSpec.BooleanValue HUD_VISIBLE;
    private static final ModConfigSpec.DoubleValue HUD_SCALE;
    private static final ModConfigSpec.EnumValue<HudAnchor> HUD_ANCHOR;
    private static final ModConfigSpec.BooleanValue FOG_ENABLED;
    private static final ModConfigSpec.DoubleValue FOG_INTENSITY;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.comment("Client-only presentation settings. These values never alter server gameplay.");
        builder.push("hud");
        HUD_VISIBLE = builder
                .comment("Show the Shroud exposure HUD while the synchronized server state is hazardous.")
                .define("visible", true);
        HUD_SCALE = builder
                .comment("Visual scale of the Shroud exposure HUD.")
                .defineInRange("scale", DEFAULT_HUD_SCALE, MIN_HUD_SCALE, MAX_HUD_SCALE);
        HUD_ANCHOR = builder
                .comment("Screen corner used to anchor the Shroud exposure HUD.")
                .defineEnum("anchor", HudAnchor.TOP_LEFT);
        builder.pop();
        builder.push("fog");
        FOG_ENABLED = builder
                .comment("Enable enhanced client-side Shroud fog. Disabling this never changes gameplay state.")
                .define("enabled", true);
        FOG_INTENSITY = builder
                .comment("Strength of enhanced Shroud fog color and distance treatment.")
                .defineInRange("intensity", DEFAULT_FOG_INTENSITY, MIN_FOG_INTENSITY, MAX_FOG_INTENSITY);
        builder.pop();
        CLIENT_SPEC = builder.build();
    }

    private EnshroudedClientConfig() {
    }

    public static HudSettings hudSettings() {
        return new HudSettings(
                HUD_VISIBLE.getAsBoolean(),
                clampHudScale(HUD_SCALE.getAsDouble()),
                HUD_ANCHOR.get()
        );
    }

    public static FogSettings fogSettings() {
        return new FogSettings(
                FOG_ENABLED.getAsBoolean(),
                clampFogIntensity(FOG_INTENSITY.getAsDouble())
        );
    }

    public static double clampHudScale(double value) {
        if (!Double.isFinite(value)) {
            return DEFAULT_HUD_SCALE;
        }
        return Math.max(MIN_HUD_SCALE, Math.min(MAX_HUD_SCALE, value));
    }

    public static double clampFogIntensity(double value) {
        if (!Double.isFinite(value)) {
            return DEFAULT_FOG_INTENSITY;
        }
        return Math.max(MIN_FOG_INTENSITY, Math.min(MAX_FOG_INTENSITY, value));
    }

    public enum HudAnchor {
        TOP_LEFT,
        TOP_RIGHT,
        BOTTOM_LEFT,
        BOTTOM_RIGHT
    }

    public record HudSettings(boolean visible, double scale, HudAnchor anchor) {
        public HudSettings {
            scale = clampHudScale(scale);
            if (anchor == null) {
                anchor = HudAnchor.TOP_LEFT;
            }
        }

        public static HudSettings defaults() {
            return new HudSettings(true, DEFAULT_HUD_SCALE, HudAnchor.TOP_LEFT);
        }
    }

    public record FogSettings(boolean enabled, double intensity) {
        public FogSettings {
            intensity = clampFogIntensity(intensity);
        }

        public static FogSettings defaults() {
            return new FogSettings(true, DEFAULT_FOG_INTENSITY);
        }
    }
}
