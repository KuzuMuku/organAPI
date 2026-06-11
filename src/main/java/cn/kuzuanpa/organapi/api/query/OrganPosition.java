package cn.kuzuanpa.organapi.api.query;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record OrganPosition(
        ResourceLocation bodyPartId,
        int slotIndex,
        ItemStack organ
) {
    public OrganPosition {
        organ = organ.copy();
    }
}
