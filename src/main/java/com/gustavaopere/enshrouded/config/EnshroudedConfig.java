package com.gustavaopere.enshrouded.config;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.exposure.ExposureSchema;
import com.gustavaopere.enshrouded.exposure.deadly.PassageRequirement;
import com.gustavaopere.enshrouded.protection.MutationSafetyMode;
import com.gustavaopere.enshrouded.shroud.core.CoreSafetyLimits;
import com.gustavaopere.enshrouded.shroud.query.ShroudSeverityThresholds;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class EnshroudedConfig {
    public static final String CONFIG_NAMESPACE = Enshrouded.MOD_ID;
    public static final ModConfigSpec SERVER_SPEC;

    private static final ModConfigSpec.IntValue CORE_MAX_INFLUENCE_RADIUS;
    private static final ModConfigSpec.IntValue CORE_GROWTH_WORK_PER_TICK;
    private static final ModConfigSpec.IntValue CORE_REGRESSION_WORK_PER_TICK;
    private static final ModConfigSpec.IntValue PURIFICATION_CLEANUP_WORK_PER_TICK;
    private static final ModConfigSpec.DoubleValue SHROUD_DEADLY_INTENSITY_THRESHOLD;
    private static final ModConfigSpec.IntValue EXPOSURE_MAX_RESERVE_TICKS;
    private static final ModConfigSpec.IntValue EXPOSURE_EMERGENCY_WINDOW_TICKS;
    private static final ModConfigSpec.IntValue DEADLY_REQUIRED_PASSAGE_LEVEL;
    private static final ModConfigSpec.BooleanValue MADNESS_PREVENT_SPRINTING_AT_CRITICAL;
    private static final ModConfigSpec.EnumValue<MutationSafetyMode> TERRAIN_MUTATION_MODE;
    private static final ModConfigSpec.BooleanValue TERRAIN_ALLOW_INDETERMINATE_PROTECTION;
    private static final ModConfigSpec.BooleanValue TERRAIN_ALLOW_BLOCK_ENTITY_MUTATION;

    static {
        ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        builder.push("shroudCore");
        CORE_MAX_INFLUENCE_RADIUS = builder
                .comment("Maximum Level 1 Shroud core influence radius in blocks.")
                .defineInRange(
                        "maxInfluenceRadius",
                        CoreSafetyLimits.DEFAULT_MAX_INFLUENCE_RADIUS,
                        CoreSafetyLimits.MIN_MAX_INFLUENCE_RADIUS,
                        CoreSafetyLimits.MAX_MAX_INFLUENCE_RADIUS
                );
        CORE_GROWTH_WORK_PER_TICK = builder
                .comment("Maximum logical Shroud frontier work units processed per active core each server tick.")
                .defineInRange(
                        "growthWorkPerTick",
                        CoreSafetyLimits.DEFAULT_GROWTH_WORK_PER_TICK,
                        CoreSafetyLimits.MIN_GROWTH_WORK_PER_TICK,
                        CoreSafetyLimits.MAX_GROWTH_WORK_PER_TICK
                );
        CORE_REGRESSION_WORK_PER_TICK = builder
                .comment("Maximum logical Shroud regression work units processed per destroyed core each server tick.")
                .defineInRange(
                        "regressionWorkPerTick",
                        CoreSafetyLimits.DEFAULT_REGRESSION_WORK_PER_TICK,
                        CoreSafetyLimits.MIN_REGRESSION_WORK_PER_TICK,
                        CoreSafetyLimits.MAX_REGRESSION_WORK_PER_TICK
                );
        PURIFICATION_CLEANUP_WORK_PER_TICK = builder
                .comment("Maximum loaded-world visual cleanup attempts processed by Shroud purification each server tick.")
                .defineInRange(
                        "purificationCleanupWorkPerTick",
                        CoreSafetyLimits.DEFAULT_CLEANUP_WORK_PER_TICK,
                        CoreSafetyLimits.MIN_CLEANUP_WORK_PER_TICK,
                        CoreSafetyLimits.MAX_CLEANUP_WORK_PER_TICK
                );
        builder.pop();

        builder.push("shroudQuery");
        SHROUD_DEADLY_INTENSITY_THRESHOLD = builder
                .comment("Intensity at or above which the authoritative Level 1 Shroud query classifies a logical cell as DEADLY.")
                .defineInRange(
                        "deadlyIntensityThreshold",
                        (double) ShroudSeverityThresholds.DEFAULT_DEADLY_AT_OR_ABOVE,
                        0.01D,
                        1.0D
                );
        builder.pop();

        builder.push("exposure");
        EXPOSURE_MAX_RESERVE_TICKS = builder
                .comment("Maximum ordinary Shroud survival reserve in server ticks. Default is 300 seconds.")
                .defineInRange(
                        "maxReserveTicks",
                        ExposureSchema.DEFAULT_MAX_RESERVE_TICKS,
                        20,
                        30 * 60 * 20
                );
        EXPOSURE_EMERGENCY_WINDOW_TICKS = builder
                .comment("Maximum survival window applied by the Level 1 Deadly Shroud barrier, in server ticks.")
                .defineInRange(
                        "emergencyWindowTicks",
                        ExposureSchema.DEFAULT_EMERGENCY_WINDOW_TICKS,
                        1,
                        5 * 60 * 20
                );
        DEADLY_REQUIRED_PASSAGE_LEVEL = builder
                .comment("Minimum Flame passage level required to traverse DEADLY Shroud without the emergency barrier.")
                .defineInRange(
                        "requiredDeadlyPassageLevel",
                        PassageRequirement.DEFAULT_REQUIRED_LEVEL,
                        PassageRequirement.MIN_REQUIRED_LEVEL,
                        PassageRequirement.MAX_REQUIRED_LEVEL
                );
        builder.pop();

        builder.push("madness");
        MADNESS_PREVENT_SPRINTING_AT_CRITICAL = builder
                .comment("When enabled, server-authoritative CRITICAL/FATAL Madness prevents sprinting. No persistent attribute or potion effect is applied.")
                .define("preventSprintingAtCritical", true);
        builder.pop();

        builder.push("terrainSafety");
        TERRAIN_MUTATION_MODE = builder
                .comment("Terrain mutation policy. SAFE mutates only explicitly safe-tagged terrain; AGGRESSIVE additionally permits aggressive-tagged terrain.")
                .defineEnum("mutationMode", MutationSafetyMode.SAFE);
        TERRAIN_ALLOW_INDETERMINATE_PROTECTION = builder
                .comment("Expert override: allow mutation when an installed protection adapter cannot determine protection. Fail-closed by default.")
                .define("allowIndeterminateProtection", false);
        TERRAIN_ALLOW_BLOCK_ENTITY_MUTATION = builder
                .comment("Expert override: allow tagged block entities/containers to be mutated. Disabled by default.")
                .define("allowBlockEntityMutation", false);
        builder.pop();

        SERVER_SPEC = builder.build();
    }

    private EnshroudedConfig() {
    }

    public static int coreMaxInfluenceRadius() {
        return CoreSafetyLimits.clampMaxInfluenceRadius(CORE_MAX_INFLUENCE_RADIUS.getAsInt());
    }

    public static int coreGrowthWorkPerTick() {
        return CoreSafetyLimits.clampGrowthWorkPerTick(CORE_GROWTH_WORK_PER_TICK.getAsInt());
    }

    public static int coreRegressionWorkPerTick() {
        return CoreSafetyLimits.clampRegressionWorkPerTick(CORE_REGRESSION_WORK_PER_TICK.getAsInt());
    }

    public static int purificationCleanupWorkPerTick() {
        return CoreSafetyLimits.clampCleanupWorkPerTick(PURIFICATION_CLEANUP_WORK_PER_TICK.getAsInt());
    }

    public static float shroudDeadlyIntensityThreshold() {
        return SHROUD_DEADLY_INTENSITY_THRESHOLD.get().floatValue();
    }

    public static int exposureMaxReserveTicks() {
        return EXPOSURE_MAX_RESERVE_TICKS.getAsInt();
    }

    public static int exposureEmergencyWindowTicks() {
        return Math.min(EXPOSURE_EMERGENCY_WINDOW_TICKS.getAsInt(), exposureMaxReserveTicks());
    }

    public static int deadlyRequiredPassageLevel() {
        return DEADLY_REQUIRED_PASSAGE_LEVEL.getAsInt();
    }

    public static boolean madnessPreventSprintingAtCritical() {
        return MADNESS_PREVENT_SPRINTING_AT_CRITICAL.getAsBoolean();
    }

    public static MutationSafetyMode terrainMutationMode() {
        return TERRAIN_MUTATION_MODE.get();
    }

    public static boolean terrainAllowIndeterminateProtection() {
        return TERRAIN_ALLOW_INDETERMINATE_PROTECTION.get();
    }

    public static boolean terrainAllowBlockEntityMutation() {
        return TERRAIN_ALLOW_BLOCK_ENTITY_MUTATION.get();
    }
}
