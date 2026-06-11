package cn.kuzuanpa.organapi.common.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class OrganDataKeys {
    public static final int MAX_VISIBLE_SLOTS = 36;
    public static final ResourceLocation DEFAULT_BODY_PART = ResourceLocation.fromNamespaceAndPath("organapi", "head");
    public static final ItemStack EMPTY = ItemStack.EMPTY;

    private OrganDataKeys() {
    }
}
