/*
 * This class was created by <kuzuanpa>. It is distributed as
 * part of the organAPI Mod. Get the Source Code in github:
 * https://github.com/KuzuMuku/organAPI
 *
 * organAPI is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Affero General Public License for more details.

 * organAPI is Open Source and distributed under the
 * AGPLv3 License: https://www.gnu.org/licenses/agpl-3.0.txt
 *
 */

package cn.kuzuanpa.organapi.common.config;

//? if forge {
import net.minecraftforge.common.ForgeConfigSpec;
//?} else {
/*import net.neoforged.neoforge.common.ModConfigSpec;
*///?}

/**
 * Common config. The spec type is the only loader difference: Forge names it
 * {@code ForgeConfigSpec}, NeoForge 1.21.1 renames it to {@code ModConfigSpec}
 * with an identical builder API, so it is handled with {@code //?} rather than a
 * platform seam. The {@code COMMON_SPEC} object is handed to the loader entry,
 * which registers it against its own {@code ModContainer}.
 */
public final class OrganApiConfig {
    //? if forge {
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec.DoubleValue SLAUGHTER_HEALTH_THRESHOLD_RATIO;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_RESTRICTION_DURATION_TICKS;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_SLOWNESS_AMPLIFIER;
    public static final ForgeConfigSpec.IntValue SLAUGHTER_WEAKNESS_AMPLIFIER;
    //?} else {
    /*public static final ModConfigSpec COMMON_SPEC;
    public static final ModConfigSpec.DoubleValue SLAUGHTER_HEALTH_THRESHOLD_RATIO;
    public static final ModConfigSpec.IntValue SLAUGHTER_RESTRICTION_DURATION_TICKS;
    public static final ModConfigSpec.IntValue SLAUGHTER_SLOWNESS_AMPLIFIER;
    public static final ModConfigSpec.IntValue SLAUGHTER_WEAKNESS_AMPLIFIER;
    *///?}

    static {
        //? if forge {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        //?} else {
        /*ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
        *///?}

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
