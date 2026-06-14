package cn.kuzuanpa.organapi.api.body;

import net.minecraft.resources.ResourceLocation;

public final class BodyPartIds {
    public static final ResourceLocation HEAD = ResourceLocation.fromNamespaceAndPath("organapi", "head");
    public static final ResourceLocation CHEST = ResourceLocation.fromNamespaceAndPath("organapi", "chest");
    public static final ResourceLocation ABDOMEN = ResourceLocation.fromNamespaceAndPath("organapi", "abdomen");
    public static final ResourceLocation LEFT_ARM = ResourceLocation.fromNamespaceAndPath("organapi", "left_arm");
    public static final ResourceLocation RIGHT_ARM = ResourceLocation.fromNamespaceAndPath("organapi", "right_arm");
    public static final ResourceLocation LEFT_LEG = ResourceLocation.fromNamespaceAndPath("organapi", "left_leg");
    public static final ResourceLocation RIGHT_LEG = ResourceLocation.fromNamespaceAndPath("organapi", "right_leg");

    private BodyPartIds() {
    }
}
