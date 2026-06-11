package cn.kuzuanpa.organapi.common.config;

import net.minecraftforge.common.ForgeConfigSpec;

public final class OrganApiConfig {
    public static final ForgeConfigSpec COMMON_SPEC;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON_SPEC = builder.build();
    }

    private OrganApiConfig() {
    }
}
