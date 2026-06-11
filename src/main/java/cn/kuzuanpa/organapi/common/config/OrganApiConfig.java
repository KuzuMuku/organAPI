package cn.kuzuanpa.organapi.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class OrganApiConfig {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.DoubleValue SLAUGHTER_HEALTH_THRESHOLD_RATIO;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_RESTRICTION_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_SLOWNESS_AMPLIFIER;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_WEAKNESS_AMPLIFIER;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();

        builder.push("slaughter");
        SLAUGHTER_HEALTH_THRESHOLD_RATIO = builder
                .comment("Current health ratio at or below which chest opening is allowed. 0.30 = 30% health.")
                .defineInRange("health_threshold_ratio", 0.30D, 0.0D, 1.0D);
        SLAUGHTER_RESTRICTION_DURATION_TICKS = builder
                .comment("Duration in ticks for post-opening restriction effects. 20 ticks = 1 second.")
                .defineInRange("restriction_duration_ticks", 600, 0, 72000);
        SLAUGHTER_SLOWNESS_AMPLIFIER = builder
                .comment("Amplifier for slowness applied after opening. 0 = Slowness I.")
                .defineInRange("slowness_amplifier", 4, 0, 255);
        SLAUGHTER_WEAKNESS_AMPLIFIER = builder
                .comment("Amplifier for weakness applied after opening. 0 = Weakness I.")
                .defineInRange("weakness_amplifier", 2, 0, 255);
        builder.pop();

        COMMON_SPEC = builder.build();
    }

    private OrganApiConfig() {
    }

    public static float getSlaughterHealthThresholdRatio() {
        return SLAUGHTER_HEALTH_THRESHOLD_RATIO.get().floatValue();
    }

    public static int getSlaughterRestrictionDurationTicks() {
        return SLAUGHTER_RESTRICTION_DURATION_TICKS.get();
    }

    public static int getSlaughterSlownessAmplifier() {
        return SLAUGHTER_SLOWNESS_AMPLIFIER.get();
    }

    public static int getSlaughterWeaknessAmplifier() {
        return SLAUGHTER_WEAKNESS_AMPLIFIER.get();
    }
}
