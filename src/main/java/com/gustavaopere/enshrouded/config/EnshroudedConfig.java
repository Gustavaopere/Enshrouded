package com.gustavaopere.enshrouded.config;

import com.gustavaopere.enshrouded.Enshrouded;
import com.gustavaopere.enshrouded.shroud.core.CoreSafetyLimits;
import com.gustavaopere.enshrouded.shroud.query.ShroudSeverityThresholds;
import net.neoforged.neoforge.common.ModConfigSpec;

public final class EnshroudedConfig {
    public static final String CONFIG_NAMESPACE = Enshrouded.MOD_ID;
    public static final ModConfigSpec SERVER_SPEC;

    private static final ModConfigSpec.IntValue CORE_MAX_INFLUENCE_RADIUS;
    private static final ModConfigSpec.IntValue CORE_GROWTH_WORK_PER_TICK;
    private static final ModConfigSpec.DoubleValue SHROUD_DEADLY_INTENSITY_THRESHOLD;

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

    public static float shroudDeadlyIntensityThreshold() {
        return SHROUD_DEADLY_INTENSITY_THRESHOLD.get().floatValue();
    }
}
